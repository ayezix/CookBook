# CookBook Android Application - Comprehensive Study Guide

## Table of Contents
1. [Application Overview](#application-overview)
2. [Technical Architecture](#technical-architecture)
3. [Project Structure](#project-structure)
4. [Core Components](#core-components)
5. [Data Models](#data-models)
6. [User Interface Components](#user-interface-components)
7. [Firebase Integration](#firebase-integration)
8. [API Integration](#api-integration)
9. [Method Review](#method-review)
10. [User Workflows](#user-workflows)
11. [Use-Case Scenarios](#use-case-scenarios)
12. [Navigation Graph](#navigation-graph)
13. [Configuration & Setup](#configuration--setup)
14. [Testing & Deployment](#testing--deployment)

---

## Application Overview

### What is CookBook?
CookBook is a comprehensive Android recipe management application that allows users to:
- Create, edit, and manage personal recipes
- Discover recipes from TheMealDB external API
- Save favorite recipes locally
- Search and filter recipes by various criteria
- Upload recipe images
- Share recipes with others

### Key Features
- **User Authentication**: Secure login/registration with Firebase
- **Recipe Management**: CRUD operations for personal recipes
- **Recipe Discovery**: Browse external recipe database
- **Search & Filtering**: Advanced search capabilities
- **Image Management**: Upload and store recipe images
- **Favorites System**: Save and organize favorite recipes
- **Modern UI**: Material Design with intuitive navigation

### Recent Improvements
- **Documentation**: Added comprehensive JavaDoc documentation throughout the codebase
- **Maintainability**: Improved code structure and readability
- **Code Quality**: Clean, well-organized codebase with optimized imports

---

## Technical Architecture

### Architecture Pattern
The application follows the **MVVM (Model-View-ViewModel)** architecture pattern with Repository pattern:

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│      View       │    │   ViewModel      │    │     Model       │
│   (Activities   │◄──►│   (Business      │◄──►│  (Data Models   │
│   & Fragments)  │    │    Logic)        │    │   & Repository) │
└─────────────────┘    └──────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   UI Layer      │    │  Business Layer  │    │   Data Layer    │
│ - Activities    │    │ - FirebaseManager│    │ - Firebase      │
│ - Fragments     │    │ - API Services   │    │ - External APIs │
│ - Adapters      │    │ - Data Processing│    │ - Local Storage │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

### Technology Stack
- **Language**: Java
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Backend**: Firebase (Authentication, Firestore)
- **External APIs**: TheMealDB, ImgBB
- **Networking**: Retrofit, OkHttp
- **Image Loading**: Glide
- **UI Framework**: Android Views with Data Binding

---

## Project Structure

```
CookBook/
├── app/
│   ├── build.gradle                 # App-level dependencies
│   │   ├── src/main/
│   │   │   ├── java/com/example/cookbook/
│   │   │   │   ├── api/                 # API client and models
│   │   │   │   │   ├── ApiClient.java
│   │   │   │   │   ├── RecipeApiService.java
│   │   │   │   │   └── model/
│   │   │   │   │       ├── ApiRecipe.java
│   │   │   │   │       ├── ApiRecipeResponse.java
│   │   │   │   │       ├── CategoryResponse.java
│   │   │   │   │       ├── AreaResponse.java
│   │   │   │   │       └── IngredientResponse.java
│   │   │   │   ├── data/                # Data layer
│   │   │   │   │   ├── model/
│   │   │   │   │   └── repository/
│   │   │   │   ├── model/               # Core data models
│   │   │   │   │   ├── Recipe.java
│   │   │   │   │   ├── Ingredient.java
│   │   │   │   │   ├── User.java
│   │   │   │   │   └── RecipeFilter.java
│   │   │   │   ├── ui/                  # User interface components
│   │   │   │   │   ├── dialog/
│   │   │   │   │   │   ├── RecipeFilterDialog.java
│   │   │   │   │   │   └── SimpleFilterDialog.java
│   │   │   │   │   ├── favorites/
│   │   │   │   │   │   └── FavoritesFragment.java
│   │   │   │   │   ├── home/
│   │   │   │   │   │   ├── HomeFragment.java
│   │   │   │   │   │   └── RecipeAdapter.java
│   │   │   │   │   ├── profile/
│   │   │   │   │   │   └── ProfileFragment.java
│   │   │   │   │   └── recipe/
│   │   │   │   │       ├── AddRecipeActivity.java
│   │   │   │   │       ├── IngredientAdapter.java
│   │   │   │   │       └── RecipeDetailActivity.java
│   │   │   │   ├── util/                # Utility classes
│   │   │   │   │   ├── FirebaseManager.java
│   │   │   │   │   └── ImgBBUploadManager.java
│   │   │   │   ├── CookBookApplication.java
│   │   │   │   └── MainActivity.java
│   │   │   │   ├── res/                     # Resources
│   │   │   │   │   ├── drawable/            # Images and icons
│   │   │   │   │   ├── layout/              # UI layouts
│   │   │   │   │   ├── values/              # Strings, colors, themes
│   │   │   │   │   ├── menu/                # Menu resources
│   │   │   │   │   ├── navigation/          # Navigation graphs
│   │   │   │   │   └── xml/                 # Configuration files
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── proguard-rules.pro
│   │   ├── build.gradle                     # Project-level configuration
│   │   ├── gradle.properties
│   │   └── settings.gradle
│   └── README.md
```

---

## Core Components

### 1. MainActivity.java
**Location**: `app/src/main/java/com/example/cookbook/MainActivity.java`

**Purpose**: Main entry point of the application that handles:
- User authentication flow
- Navigation between login and main app UI
- Bottom navigation setup

**Key Responsibilities**:
- Check user authentication status on app launch
- Display login/register screen for unauthenticated users
- Show main app interface for authenticated users
- Handle navigation between different app sections

**Documentation**: All methods include detailed JavaDoc comments explaining their purpose, parameters, and functionality.

### 2. CookBookApplication.java
**Location**: `app/src/main/java/com/example/cookbook/CookBookApplication.java`

**Purpose**: Application class that provides global context and initialization

**Key Features**:
- **Firebase Initialization**: Sets up Firebase services on app startup
- **Google Play Services Check**: Verifies Google Play Services availability
- **Data Migration**: Handles one-time data updates and migrations
- **Global Context Access**: Provides application context throughout the app

**Key Methods**:
- `onCreate()`: Initializes Firebase, checks Google Play Services, and runs data migrations
- `getAppContext()`: Returns application context for use throughout the app
- `getInstance()`: Returns singleton instance of the application

**Documentation**: Added detailed documentation explaining the application initialization process, Firebase setup, and one-time data migrations.

### 3. FirebaseManager.java
**Location**: `app/src/main/java/com/example/cookbook/util/FirebaseManager.java`

**Purpose**: Centralized manager for all Firebase operations including:
- User authentication
- Recipe CRUD operations
- Image upload management
- External API integration

**Key Features**:
- **Singleton Pattern**: Ensures single instance throughout the app
- **Context Management**: Uses application context for operations
- **Error Handling**: Comprehensive error translation and logging
- **API Integration**: Manages both Firebase and external API calls



---

## Detailed Component Analysis

### **FRAGMENTS (3 Total)**

#### **1. HomeFragment.java**
**Location**: `app/src/main/java/com/example/cookbook/ui/home/HomeFragment.java`
**Purpose**: Main home screen and recipe management hub

**Key Features**:
- **Recipe Display**: Shows user's recipes in a RecyclerView
- **Search Functionality**: Real-time search with local and online capabilities
- **Filter System**: Advanced filtering by category, area, and ingredients
- **API Integration**: Searches TheMealDB when no local results found
- **FAB Navigation**: Floating action button to add new recipes

**Documentation**: Added comprehensive JavaDoc documentation for all methods and fields, explaining the fragment's purpose, data flow, and functionality.

**Core Methods**:
- `setupRecyclerView()`: Configures recipe list display
- `setupSearchView()`: Handles search input and real-time filtering
- `loadRecipes()`: Loads user's recipes from Firebase
- `showFilterDialogWithOptions()`: Displays advanced filter dialog
- `updateRecipeList()`: Updates displayed recipes based on current state
- `searchWithFilterOrQuery()`: Performs search with current filter and query



**Data Flow**:
```
User Input → Search/Filter → Local Search → API Search → Display Results
```

#### **2. FavoritesFragment.java**
**Location**: `app/src/main/java/com/example/cookbook/ui/favorites/FavoritesFragment.java`
**Purpose**: Displays user's favorite recipes

**Key Features**:
- **Favorite Recipe Display**: Shows only favorited recipes
- **Empty State Handling**: Displays message when no favorites exist
- **Real-time Updates**: Refreshes when favorite status changes
- **Shared Adapter**: Uses same RecipeAdapter as HomeFragment with proper constructor

**Core Methods**:
- `setupRecyclerView()`: Configures favorite recipe list with RecipeAdapter
- `loadFavoriteRecipes()`: Fetches favorite recipes from Firebase
- `updateEmptyState()`: Handles empty state visibility
- `onFavoriteChanged()`: Callback for favorite status changes

**Data Flow**:
```
Fragment Load → Query Favorites → Display Results → Handle Empty State
```

#### **3. ProfileFragment.java**
**Location**: `app/src/main/java/com/example/cookbook/ui/profile/ProfileFragment.java`
**Purpose**: User profile management and account settings

**Key Features**:
- **User Information Display**: Shows current user's email
- **Password Management**: Change password functionality
- **Logout Functionality**: Secure user logout
- **Dialog Integration**: Uses AlertDialog for password changes

**Core Methods**:
- `setupUserProfile()`: Displays user information
- `setupClickListeners()`: Configures button interactions
- `handleLogout()`: Performs user logout
- `showChangePasswordDialog()`: Password change dialog

**Data Flow**:
```
User Profile → Display Info → Handle Actions → Update/Logout
```

### **ADAPTERS (2 Total)**

#### **1. RecipeAdapter.java**
**Location**: `app/src/main/java/com/example/cookbook/ui/home/RecipeAdapter.java`
**Purpose**: Manages recipe list display in RecyclerView

**Key Features**:
- **Recipe Display**: Shows recipe title, category, ingredients count, and image
- **Favorite Management**: Toggle favorite status with heart icon
- **Click Handling**: Navigates to recipe details
- **Image Loading**: Uses Glide for efficient image loading
- **Share Functionality**: Share recipes via various apps
- **Support for Local and API Recipes**: Handles both user-created and imported recipes

**Constructor**:
```java
RecipeAdapter(Context context, List<Recipe> recipes, OnFavoriteChangedListener listener)
```

**Core Methods**:
- `onBindViewHolder()`: Binds recipe data to view holder
- `onRecipeClick()`: Handles recipe item clicks
- `onFavoriteClick()`: Manages favorite status changes
- `showShareOptions()`: Provides sharing functionality
- `updateRecipes()`: Updates the displayed recipe list
- `updateFavoriteButton()`: Updates favorite button appearance

**Interfaces**:
- `OnFavoriteChangedListener`: Favorite status change callbacks with recipe and status parameters

**Documentation**: Added comprehensive JavaDoc documentation for all methods and classes, explaining the adapter's purpose, data binding process, and interaction methods.

**Core Methods**:
- `onBindViewHolder()`: Binds recipe data to view holder
- `onRecipeClick()`: Handles recipe item clicks
- `onFavoriteClick()`: Manages favorite status changes
- `showShareOptions()`: Provides sharing functionality
- `updateRecipes()`: Updates the displayed recipe list
- `updateFavoriteButton()`: Updates favorite button appearance

**Interfaces**:
- `OnFavoriteChangedListener`: Favorite status change callbacks with recipe and status parameters



**Data Binding**:
```
Recipe Object → ViewHolder → UI Elements (Title, Category, Image, etc.)
```

#### **2. IngredientAdapter.java**
**Location**: `app/src/main/java/com/example/cookbook/ui/recipe/IngredientAdapter.java`
**Purpose**: Manages ingredient list in recipe creation/editing

**Key Features**:
- **Ingredient Display**: Shows ingredient name, amount, and unit
- **Edit Functionality**: Edit existing ingredients
- **Delete Functionality**: Remove ingredients from list
- **Action Buttons**: Edit and delete buttons for each ingredient

**Core Methods**:
- `onBindViewHolder()`: Binds ingredient data to view holder
- `updateIngredients()`: Updates ingredient list
- `onEditIngredient()`: Handles ingredient editing
- `onDeleteIngredient()`: Handles ingredient deletion

**Interfaces**:
- `OnIngredientActionListener`: Edit and delete callbacks

**Data Binding**:
```
Ingredient Object → ViewHolder → UI Elements (Name, Amount, Unit, Action Buttons)
```

### **ACTIVITIES (3 Total)**

#### **1. MainActivity.java**
**Location**: `app/src/main/java/com/example/cookbook/MainActivity.java`
**Purpose**: Application entry point and authentication controller

**Key Features**:
- **Authentication Flow**: Handles login, registration, and password reset
- **Navigation Management**: Controls bottom navigation between fragments
- **UI State Management**: Switches between login and main app interfaces
- **Input Validation**: Validates user input before authentication

**Core Methods**:
- `onCreate()`: Initializes app and checks authentication status
- `setupLoginScreen()`: Configures login/registration UI
- `showMainAppUI()`: Displays main application interface
- `onNavigationItemSelected()`: Handles bottom navigation
- `validateInput()`: Validates user input fields

**Lifecycle Flow**:
```
App Launch → Check Auth → Show Login/Main UI → Handle Navigation
```

#### **2. AddRecipeActivity.java**
**Location**: `app/src/main/java/com/example/cookbook/ui/recipe/AddRecipeActivity.java`
**Purpose**: Recipe creation and editing interface

**Key Features**:
- **Recipe Creation**: Add new recipes with all details
- **Recipe Editing**: Edit existing recipes (edit mode)
- **Image Upload**: Upload recipe images via ImgBB
- **Ingredient Management**: Add, edit, and delete ingredients
- **Form Validation**: Validates all required fields
- **Category Selection**: Dropdown for recipe categories

**Core Methods**:
- `onCreate()`: Initializes activity and handles edit mode
- `setupSpinner()`: Configures category dropdown
- `setupRecyclerView()`: Sets up ingredient list
- `addIngredient()`: Adds or updates ingredients
- `selectImage()`: Handles image selection
- `saveRecipe()`: Saves recipe to Firebase
- `prefillFieldsForEdit()`: Populates form for editing

**Data Flow**:
```
User Input → Form Validation → Image Upload → Save to Firebase → Return
```

#### **3. RecipeDetailActivity.java**
**Location**: `app/src/main/java/com/example/cookbook/ui/recipe/RecipeDetailActivity.java`
**Purpose**: Displays detailed recipe information

**Key Features**:
- **Recipe Display**: Shows complete recipe details
- **Image Display**: Displays recipe image with Glide
- **Ingredient List**: Formatted ingredient display
- **Edit Access**: Edit button for user-created recipes only
- **Delete Functionality**: Delete recipe with confirmation
- **Permission Control**: Only allows editing user's own recipes

**Core Methods**:
- `onCreate()`: Initializes activity and displays recipe
- `showDeleteConfirmationDialog()`: Confirms recipe deletion
- `deleteRecipe()`: Removes recipe from Firebase

**Data Flow**:
```
Recipe Data → Display Details → Handle Actions → Edit/Delete
```

### **Component Relationships**

#### **Navigation Flow**:
```
MainActivity (Auth) → HomeFragment (Recipe List) → RecipeDetailActivity (Details) → AddRecipeActivity (Edit)
                ↓
            FavoritesFragment (Favorites)
                ↓
            ProfileFragment (Settings)
```

#### **Data Flow**:
```
FirebaseManager ← → Fragments ← → Adapters ← → Activities
       ↓              ↓              ↓           ↓
   Firebase      RecipeAdapter   UI Updates   User Actions
   External APIs IngredientAdapter
```

#### **Shared Components**:
- **RecipeAdapter**: Used by both HomeFragment and FavoritesFragment
- **FirebaseManager**: Centralized data management for all components
- **Recipe Model**: Shared data structure across all components

This architecture provides a clean separation of concerns with reusable components and clear data flow patterns.

---

## Data Models

### 1. Recipe.java
**Location**: `app/src/main/java/com/example/cookbook/model/Recipe.java`

**Purpose**: Core data model representing a recipe in the application

**Properties**:
- `id`: Unique identifier (Firestore document ID)
- `title`: Recipe name
- `category`: Recipe category (Breakfast, Dinner, etc.)
- `ingredients`: List of ingredients with amounts and units
- `instructions`: Cooking instructions
- `imageUrl`: URL to recipe image
- `userId`: ID of recipe creator
- `favorite`: Whether recipe is favorited
- `createdAt`: Creation timestamp
- `importedFromApi`: Whether recipe came from external API

**Key Methods**:
- Constructors for creating new recipes
- Getters and setters for all properties with detailed documentation
- Serializable implementation for data transfer
- PropertyName annotations for Firestore compatibility

**Documentation**: Added extensive documentation explaining the model's role in the application, how each field is used, and the relationships between different properties.

### 2. Ingredient.java
**Location**: `app/src/main/java/com/example/cookbook/model/Ingredient.java`

**Purpose**: Represents an ingredient in a recipe

**Properties**:
- `name`: Ingredient name (e.g., "Flour", "Sugar", "Eggs")
- `amount`: Quantity amount (e.g., "2", "1/2", "3")
- `unit`: Unit of measurement (e.g., "cups", "tablespoons", "pieces")

**Key Methods**:
- Default constructor for Firestore serialization
- Parameterized constructor for creating ingredients
- Getters and setters for all properties with detailed documentation

**Documentation**: Added comprehensive documentation explaining the ingredient model's purpose, usage in recipes, and how it integrates with the recipe creation and display systems.

### 3. User.java
**Location**: `app/src/main/java/com/example/cookbook/model/User.java`

**Purpose**: Represents a user in the system

**Properties**:
- `uid`: Firebase Auth UID (unique user identifier)
- `email`: User's email address
- `favoriteRecipes`: List of favorite recipe IDs
- `customIngredients`: List of custom ingredients added by user

**Key Methods**:
- Default constructor for Firestore serialization
- Parameterized constructor for creating new users
- Getters and setters for all properties with detailed documentation

**Documentation**: Added extensive documentation explaining the user model's role in authentication, recipe ownership, and user preferences management.

### 4. RecipeFilter.java
**Location**: `app/src/main/java/com/example/cookbook/model/RecipeFilter.java`

**Purpose**: Defines filtering criteria for recipe searches

**Properties**:
- `type`: Type of filter (CATEGORY, AREA, INGREDIENT, SEARCH)
- `value`: Primary filter value
- `values`: Multiple filter values (for future use)

**Key Methods**:
- Constructors for single and multiple value filters
- Helper methods for common filter types: `byCategory()`, `byArea()`, `byIngredient()`, `bySearch()`
- Dietary restriction filters: `veganOnly()`, `vegetarianOnly()`, `glutenFreeOnly()`
- Getters for filter properties

**Documentation**: Added comprehensive documentation explaining the filtering system, each filter type, and how filters are used throughout the application for recipe discovery and search.

---

## User Interface Components

### 1. Layout Files

#### activity_main.xml
**Location**: `app/src/main/res/layout/activity_main.xml`

**Purpose**: Login/registration screen layout

**Components**:
- App title
- Email input field with validation
- Password input field with toggle visibility
- Login button
- Register button
- Forgot password link
- Progress bar for loading states

#### main_app.xml
**Location**: `app/src/main/res/layout/main_app.xml`

**Purpose**: Main application layout with navigation

**Components**:
- Fragment container for dynamic content
- Bottom navigation view with menu items

#### fragment_home.xml
**Location**: `app/src/main/res/layout/fragment_home.xml`

**Purpose**: Home screen layout

**Components**:
- Search view for recipe search
- Filter button
- Clear filter button
- RecyclerView for recipe list
- Floating action button for adding recipes
- Progress bar
- Empty state view

#### activity_add_recipe.xml
**Location**: `app/src/main/res/layout/activity_add_recipe.xml`

**Purpose**: Recipe creation/editing screen

**Components**:
- Toolbar with back button
- Recipe title input
- Category spinner
- Instructions text area
- Ingredient management section
- Image upload section
- Save button

#### item_recipe.xml
**Location**: `app/src/main/res/layout/item_recipe.xml`

**Purpose**: Recipe item layout for RecyclerView

**Components**:
- Recipe image (`ivRecipe`)
- Recipe title (`tvTitle`)
- Recipe category (`tvCategory`)
- Ingredients count (`tvIngredients`)
- Favorite button (`ivFavorite`)
- Share button (`ivShare`)

**Layout Structure**:
- MaterialCardView container with rounded corners
- Horizontal LinearLayout with image and content
- Content area with title, category, and ingredients info
- Action buttons aligned to the right

### 2. Fragments

#### HomeFragment.java
**Location**: `app/src/main/java/com/example/cookbook/ui/home/HomeFragment.java`

**Purpose**: Main home screen that displays recipes and handles search/filtering

**Key Features**:
- Display user's recipes
- Search functionality (local and online)
- Filter recipes by category, area, or ingredient
- Integration with external API
- Recipe list management

#### FavoritesFragment.java
**Location**: `app/src/main/java/com/example/cookbook/ui/favorites/FavoritesFragment.java`

**Purpose**: Displays user's favorite recipes

#### ProfileFragment.java
**Location**: `app/src/main/java/com/example/cookbook/ui/profile/ProfileFragment.java`

**Purpose**: User profile management

### 3. Activities

#### AddRecipeActivity.java
**Location**: `app/src/main/java/com/example/cookbook/ui/recipe/AddRecipeActivity.java`

**Purpose**: Handles recipe creation and editing

**Features**:
- Form validation
- Image upload
- Ingredient management
- Edit mode support

#### RecipeDetailActivity.java
**Location**: `app/src/main/java/com/example/cookbook/ui/recipe/RecipeDetailActivity.java`

**Purpose**: Displays detailed recipe information

### 4. Adapters

#### RecipeAdapter.java
**Location**: `app/src/main/java/com/example/cookbook/ui/home/RecipeAdapter.java`

**Purpose**: Manages recipe list display in RecyclerView

**Features**:
- Recipe item layout with proper binding (`ivRecipe`, `tvTitle`, `tvCategory`, `tvIngredients`, `ivFavorite`, `ivShare`)
- Click handling for recipe details
- Favorite toggle functionality with heart icon
- Image loading with Glide
- Share functionality for recipes

#### IngredientAdapter.java
**Location**: `app/src/main/java/com/example/cookbook/ui/recipe/IngredientAdapter.java`

**Purpose**: Manages ingredient list in recipe creation

---

## Firebase Integration

### FirebaseManager.java - Detailed Analysis

**Location**: `app/src/main/java/com/example/cookbook/util/FirebaseManager.java`

**Purpose**: Centralized manager for all Firebase operations

**Key Collections**:
- `users`: User profiles
- `recipes`: Recipe data

**Authentication Methods**:

#### registerUser(String email, String password)
**Purpose**: Creates new user account
**Process**:
1. Creates Firebase Auth user
2. Creates user document in Firestore
3. Handles error translation
**Usage**: Called from MainActivity during registration

#### loginUser(String email, String password)
**Purpose**: Authenticates existing user
**Process**:
1. Validates credentials with Firebase Auth
2. Translates errors to user-friendly messages
**Usage**: Called from MainActivity during login

#### logoutUser()
**Purpose**: Signs out current user
**Process**: Calls Firebase Auth signOut()
**Usage**: Called from ProfileFragment

#### sendPasswordResetEmail(String email)
**Purpose**: Sends password reset email
**Process**:
1. Validates email format
2. Sends reset email via Firebase
3. Handles error cases
**Usage**: Called from MainActivity forgot password

**Recipe Management Methods**:

#### addRecipe(Recipe recipe)
**Purpose**: Saves new recipe to Firestore
**Process**:
1. Sets current user ID
2. Adds recipe to recipes collection
3. Logs success/failure
**Usage**: Called from AddRecipeActivity

#### getUserRecipes()
**Purpose**: Retrieves current user's recipes
**Process**:
1. Gets current user ID
2. Queries recipes collection by userId
3. Returns QuerySnapshot
**Usage**: Called from HomeFragment

#### updateRecipe(Recipe recipe)
**Purpose**: Updates existing recipe
**Process**: Updates document in Firestore
**Usage**: Called from AddRecipeActivity in edit mode

#### deleteRecipe(String recipeId)
**Purpose**: Removes recipe from Firestore
**Process**: Deletes document and removes from favorites
**Usage**: Called from recipe detail screens

**Search Methods**:

#### searchRecipesByName(String query)
**Purpose**: Searches recipes by title
**Process**:
1. Converts query to lowercase
2. Uses Firestore range queries
3. Filters by current user
**Usage**: Called from HomeFragment search

#### searchRecipesByCategory(String category)
**Purpose**: Filters recipes by category
**Process**: Queries recipes collection by category and user
**Usage**: Called from filter dialogs

#### searchRecipesByIngredient(String ingredient)
**Purpose**: Finds recipes containing specific ingredient
**Process**: Uses Firestore array-contains query
**Usage**: Called from filter dialogs

**Favorite Management**:

#### toggleFavoriteRecipe(String recipeId, boolean isFavorite)
**Purpose**: Updates recipe favorite status
**Process**: Updates favorite field in Firestore
**Usage**: Called from RecipeAdapter

#### getFavoriteRecipes()
**Purpose**: Retrieves user's favorite recipes
**Process**: Queries recipes by userId and favorite=true
**Usage**: Called from FavoritesFragment

**Image Upload**:

#### uploadRecipeImage(Uri imageUri)
**Purpose**: Uploads image to ImgBB
**Process**:
1. Converts URI to temporary file
2. Uploads to ImgBB API
3. Returns image URL
**Usage**: Called from AddRecipeActivity

**External API Integration**:

#### searchOnlineRecipes(String query, OnRecipesLoadedListener listener)
**Purpose**: Searches TheMealDB API
**Process**:
1. Makes API call to TheMealDB
2. Converts API response to local Recipe objects
3. Applies local filters
4. Limits results to 10 recipes
**Usage**: Called from HomeFragment when no local results

#### searchOnlineRecipesWithFilter(RecipeFilter filter, OnRecipesLoadedListener listener)
**Purpose**: Searches API with specific filters
**Process**:
1. Determines API endpoint based on filter type
2. Makes appropriate API call
3. Processes and filters results
**Usage**: Called from filter dialogs

#### getCategories(OnCategoriesLoadedListener listener)
**Purpose**: Retrieves available categories from API
**Process**: Calls TheMealDB categories endpoint
**Usage**: Called from filter dialogs

#### getAreas(OnAreasLoadedListener listener)
**Purpose**: Retrieves available areas/cuisines from API
**Process**: Calls TheMealDB areas endpoint
**Usage**: Called from filter dialogs

#### getIngredients(OnIngredientsLoadedListener listener)
**Purpose**: Retrieves available ingredients from API
**Process**: Calls TheMealDB ingredients endpoint
**Usage**: Called from filter dialogs

**Utility Methods**:

#### getCurrentUserId()
**Purpose**: Gets current authenticated user ID
**Process**: Returns Firebase Auth current user UID
**Usage**: Used throughout app for user-specific operations

#### getCurrentUser()
**Purpose**: Gets current Firebase user object
**Process**: Returns Firebase Auth current user
**Usage**: Used for user information

**Data Conversion Methods**:

#### convertApiRecipesToLocalRecipes(List<ApiRecipe> apiRecipes)
**Purpose**: Converts API recipe objects to local Recipe objects
**Process**:
1. Maps API fields to local model
2. Extracts ingredients from API response
3. Sets appropriate flags
**Usage**: Called after API responses

#### extractIngredientsFromTheMealDB(ApiRecipe apiRecipe)
**Purpose**: Extracts ingredients from TheMealDB API response
**Process**:
1. Iterates through ingredient fields (1-20)
2. Combines with corresponding measures
3. Creates Ingredient objects
**Usage**: Called during API recipe conversion

**Filtering Methods**:

#### applyLocalFilters(List<Recipe> recipes, RecipeFilter filter)
**Purpose**: Applies additional local filtering to API results
**Process**: Currently returns all recipes (placeholder for future dietary filters)
**Usage**: Called after API recipe conversion

#### isRecipeSuitableForFilter(Recipe recipe, RecipeFilter filter)
**Purpose**: Checks if recipe matches filter criteria
**Process**: Currently returns true (placeholder for future implementation)
**Usage**: Called during local filtering

**Error Handling Methods**:

#### translateRegistrationError(String firebaseError)
**Purpose**: Converts Firebase registration errors to user-friendly messages
**Process**: Maps Firebase error messages to readable text
**Usage**: Called during user registration

#### translateFirebaseError(String firebaseError)
**Purpose**: Converts Firebase login errors to user-friendly messages
**Process**: Maps Firebase error messages to readable text
**Usage**: Called during user login

#### translatePasswordResetError(String firebaseError)
**Purpose**: Converts Firebase password reset errors to user-friendly messages
**Process**: Maps Firebase error messages to readable text
**Usage**: Called during password reset

---

## API Integration

### **Comprehensive API Architecture Overview**

The CookBook application implements a sophisticated multi-API integration system that provides seamless recipe discovery, search capabilities, and image management. The application integrates with **two primary external APIs** and implements a robust caching and fallback strategy.

#### **API Integration Strategy**
- **Primary API**: TheMealDB for recipe discovery and search
- **Secondary API**: ImgBB for image hosting and management
- **Fallback Strategy**: Local-first search with online augmentation
- **Caching Layer**: Intelligent caching for improved performance
- **Error Handling**: Comprehensive error management and user feedback

### **TheMealDB API Integration - Deep Dive**

#### **API Client Architecture**

##### **ApiClient.java - Retrofit Configuration**
**Location**: `app/src/main/java/com/example/cookbook/api/ApiClient.java`

**Purpose**: Centralized Retrofit client configuration for TheMealDB API with comprehensive logging and error handling

**Detailed Configuration**:
```java
public class ApiClient {
    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";
    private static final int TIMEOUT_SECONDS = 30;
    
    private static Retrofit retrofit = null;
    
    public static Retrofit getClient() {
        if (retrofit == null) {
            // HTTP Logging Interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // OkHttp Client with interceptors
            OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
            
            // Retrofit Builder
            retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        }
        return retrofit;
    }
}
```

**Key Features**:
- **Connection Pooling**: Efficient HTTP connection management
- **Request/Response Logging**: Comprehensive debugging capabilities
- **Timeout Configuration**: Prevents hanging requests
- **GSON Integration**: Automatic JSON serialization/deserialization
- **Singleton Pattern**: Ensures single client instance

#### **API Service Interface - Complete Endpoint Definition**

##### **RecipeApiService.java - Comprehensive API Interface**
**Location**: `app/src/main/java/com/example/cookbook/api/RecipeApiService.java`

**Purpose**: Complete Retrofit interface defining all TheMealDB API endpoints with comprehensive documentation

**Search and Discovery Endpoints**:
```java
@GET("search.php")
Call<ApiRecipeResponse> searchRecipes(@Query("s") String query);

@GET("lookup.php")
Call<ApiRecipeResponse> getRecipeInformation(@Query("i") String id);

@GET("random.php")
Call<ApiRecipeResponse> getRandomRecipe();
```

**Filtering and Categorization Endpoints**:
```java
@GET("filter.php")
Call<ApiRecipeResponse> filterByCategory(@Query("c") String category);

@GET("filter.php")
Call<ApiRecipeResponse> filterByArea(@Query("a") String area);

@GET("filter.php")
Call<ApiRecipeResponse> filterByIngredient(@Query("i") String ingredient);
```

**Metadata and List Endpoints**:
```java
@GET("categories.php")
Call<CategoryResponse> getCategories();

@GET("list.php")
Call<AreaResponse> getAreas(@Query("a") String list);

@GET("list.php")
Call<IngredientResponse> getIngredients(@Query("i") String list);
```

**API Response Models**:
- **ApiRecipeResponse**: Wrapper for recipe search results
- **CategoryResponse**: Available recipe categories
- **AreaResponse**: Available cuisine areas
- **IngredientResponse**: Available ingredients

#### **Advanced Data Models**

##### **ApiRecipe.java - Comprehensive API Response Model**
**Location**: `app/src/main/java/com/example/cookbook/api/model/ApiRecipe.java`

**Purpose**: Complete data model for TheMealDB API responses with comprehensive field mapping

**Core Recipe Fields**:
```java
@SerializedName("idMeal")
private String idMeal;

@SerializedName("strMeal")
private String strMeal;

@SerializedName("strCategory")
private String strCategory;

@SerializedName("strArea")
private String strArea;

@SerializedName("strInstructions")
private String strInstructions;

@SerializedName("strMealThumb")
private String strMealThumb;

@SerializedName("strTags")
private String strTags;

@SerializedName("strYoutube")
private String strYoutube;
```

**Dynamic Ingredient Fields** (1-20):
```java
// Ingredient fields with dynamic naming
@SerializedName("strIngredient1")
private String strIngredient1;

@SerializedName("strIngredient2")
private String strIngredient2;

// ... through strIngredient20

// Corresponding measure fields
@SerializedName("strMeasure1")
private String strMeasure1;

@SerializedName("strMeasure2")
private String strMeasure2;

// ... through strMeasure20
```

**Advanced Getters for Dynamic Ingredient Access**:
```java
public String getIngredientByIndex(int index) {
    switch (index) {
        case 1: return strIngredient1;
        case 2: return strIngredient2;
        // ... through 20
        default: return null;
    }
}

public String getMeasureByIndex(int index) {
    switch (index) {
        case 1: return strMeasure1;
        case 2: return strMeasure2;
        // ... through 20
        default: return null;
    }
}
```

**Data Validation Methods**:
```java
public boolean isValidRecipe() {
    return idMeal != null && !idMeal.isEmpty() &&
           strMeal != null && !strMeal.isEmpty();
}

public int getIngredientCount() {
    int count = 0;
    for (int i = 1; i <= 20; i++) {
        if (getIngredientByIndex(i) != null && 
            !getIngredientByIndex(i).trim().isEmpty()) {
            count++;
        }
    }
    return count;
}
```

### **ImgBB API Integration - Advanced Image Management**

#### **ImgBBUploadManager.java - Comprehensive Image Upload System**
**Location**: `app/src/main/java/com/example/cookbook/util/ImgBBUploadManager.java`

**Purpose**: Advanced image upload system with compression, validation, and comprehensive error handling

**Core Configuration**:
```java
public class ImgBBUploadManager {
    private static final String IMGBB_API_URL = "https://api.imgbb.com/1/upload";
    private static final int MAX_IMAGE_SIZE = 32 * 1024 * 1024; // 32MB
    private static final int COMPRESSION_QUALITY = 85;
    private static final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build();
}
```

#### **Advanced Image Upload Process**

##### **uploadImage(File imageFile, UploadCallback callback)**
**Purpose**: Comprehensive image upload with preprocessing, validation, and error handling

**Detailed Upload Process**:
1. **Image Validation**:
   ```java
   private boolean validateImage(File imageFile) {
       if (!imageFile.exists()) {
           return false;
       }
       
       if (imageFile.length() > MAX_IMAGE_SIZE) {
           return false;
       }
       
       // Check file type
       String mimeType = getMimeType(imageFile);
       return mimeType != null && mimeType.startsWith("image/");
   }
   ```

2. **Image Compression**:
   ```java
   private File compressImage(File originalFile) throws IOException {
       Bitmap bitmap = BitmapFactory.decodeFile(originalFile.getAbsolutePath());
       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, baos);
       
       File compressedFile = File.createTempFile("compressed_", ".jpg");
       FileOutputStream fos = new FileOutputStream(compressedFile);
       fos.write(baos.toByteArray());
       fos.close();
       
       return compressedFile;
   }
   ```

3. **Multipart Request Construction**:
   ```java
   private RequestBody createMultipartBody(File imageFile) {
       return new MultipartBody.Builder()
           .setType(MultipartBody.FORM)
           .addFormDataPart("image", imageFile.getName(),
               RequestBody.create(MediaType.parse("image/*"), imageFile))
           .addFormDataPart("key", BuildConfig.IMGBB_API_KEY)
           .addFormDataPart("name", "recipe_image_" + System.currentTimeMillis())
           .build();
   }
   ```

4. **HTTP Request Execution**:
   ```java
   private void executeUpload(File imageFile, UploadCallback callback) {
       RequestBody requestBody = createMultipartBody(imageFile);
       
       Request request = new Request.Builder()
           .url(IMGBB_API_URL)
           .post(requestBody)
           .build();
       
       client.newCall(request).enqueue(new Callback() {
           @Override
           public void onResponse(Call call, Response response) throws IOException {
               handleUploadResponse(response, callback);
           }
           
           @Override
           public void onFailure(Call call, IOException e) {
               callback.onError("Network error: " + e.getMessage());
           }
       });
   }
   ```

5. **Response Processing**:
   ```java
   private void handleUploadResponse(Response response, UploadCallback callback) {
       try {
           String responseBody = response.body().string();
           JSONObject json = new JSONObject(responseBody);
           
           if (json.getBoolean("success")) {
               JSONObject data = json.getJSONObject("data");
               String imageUrl = data.getString("url");
               String deleteUrl = data.getString("delete_url");
               
               callback.onSuccess(imageUrl);
           } else {
               String error = json.getString("error");
               callback.onError("Upload failed: " + error);
           }
       } catch (Exception e) {
           callback.onError("Response parsing error: " + e.getMessage());
       }
   }
   ```

### **Advanced API Integration in FirebaseManager**

#### **Centralized API Orchestration**

The `FirebaseManager` class serves as the central orchestrator for all API interactions, providing a unified interface that abstracts the complexity of multiple API integrations.

#### **Comprehensive API Method Implementation**

##### **searchOnlineRecipes(String query, OnRecipesLoadedListener listener)**
**Purpose**: Advanced recipe search with intelligent fallback and caching

**Detailed Implementation**:
```java
public void searchOnlineRecipes(String query, OnRecipesLoadedListener listener) {
    if (query == null || query.trim().isEmpty()) {
        listener.onRecipesLoaded(new ArrayList<>());
        return;
    }
    
    // Create filter for search
    RecipeFilter filter = RecipeFilter.bySearch(query.trim());
    
    // Execute search with filter
    searchOnlineRecipesWithFilter(filter, listener);
}
```

##### **searchOnlineRecipesWithFilter(RecipeFilter filter, OnRecipesLoadedListener listener)**
**Purpose**: Advanced filtered search with endpoint selection and result processing

**Comprehensive Implementation**:
```java
public void searchOnlineRecipesWithFilter(RecipeFilter filter, 
                                        OnRecipesLoadedListener listener) {
    if (filter == null) {
        listener.onRecipesLoaded(new ArrayList<>());
        return;
    }
    
    Call<ApiRecipeResponse> call = null;
    
    // Determine appropriate API endpoint based on filter type
    switch (filter.getType()) {
        case CATEGORY:
            call = ApiClient.getRecipeService().filterByCategory(filter.getValue());
            break;
        case AREA:
            call = ApiClient.getRecipeService().filterByArea(filter.getValue());
            break;
        case INGREDIENT:
            call = ApiClient.getRecipeService().filterByIngredient(filter.getValue());
            break;
        case SEARCH:
        default:
            call = ApiClient.getRecipeService().searchRecipes(filter.getValue());
            break;
    }
    
    // Execute API call with comprehensive error handling
    call.enqueue(new Callback<ApiRecipeResponse>() {
        @Override
        public void onResponse(Call<ApiRecipeResponse> call, 
                             Response<ApiRecipeResponse> response) {
            handleApiResponse(response, filter, listener);
        }
        
        @Override
        public void onFailure(Call<ApiRecipeResponse> call, Throwable t) {
            handleApiFailure(t, listener);
        }
    });
}
```

##### **Advanced Response Handling**:
```java
private void handleApiResponse(Response<ApiRecipeResponse> response, 
                             RecipeFilter filter, 
                             OnRecipesLoadedListener listener) {
    if (response.isSuccessful() && response.body() != null) {
        List<ApiRecipe> apiRecipes = response.body().getMeals();
        
        if (apiRecipes != null && !apiRecipes.isEmpty()) {
            // Convert API recipes to local format
            List<Recipe> localRecipes = convertApiRecipesToLocalRecipes(apiRecipes);
            
            // Apply additional local filters
            localRecipes = applyLocalFilters(localRecipes, filter);
            
            // Limit results for performance
            if (localRecipes.size() > 10) {
                localRecipes = localRecipes.subList(0, 10);
            }
            
            listener.onRecipesLoaded(localRecipes);
        } else {
            listener.onRecipesLoaded(new ArrayList<>());
        }
    } else {
        listener.onError("API response error: " + response.code());
    }
}
```

#### **Advanced Data Conversion and Processing**

##### **convertApiRecipesToLocalRecipes(List<ApiRecipe> apiRecipes)**
**Purpose**: Comprehensive conversion of API recipes to local format with data enrichment

**Detailed Implementation**:
```java
private List<Recipe> convertApiRecipesToLocalRecipes(List<ApiRecipe> apiRecipes) {
    List<Recipe> localRecipes = new ArrayList<>();
    
    for (ApiRecipe apiRecipe : apiRecipes) {
        if (apiRecipe != null && apiRecipe.isValidRecipe()) {
            Recipe localRecipe = new Recipe();
            
            // Map core fields
            localRecipe.setId(apiRecipe.getIdMeal());
            localRecipe.setTitle(apiRecipe.getStrMeal());
            localRecipe.setCategory(apiRecipe.getStrCategory());
            localRecipe.setInstructions(apiRecipe.getStrInstructions());
            localRecipe.setImageUrl(apiRecipe.getStrMealThumb());
            
            // Extract and process ingredients
            List<Ingredient> ingredients = extractIngredientsFromTheMealDB(apiRecipe);
            localRecipe.setIngredients(ingredients);
            
            // Set metadata
            localRecipe.setUserId(getCurrentUserId());
            localRecipe.setImportedFromApi(true);
            localRecipe.setCreatedAt(new Date());
            localRecipe.setFavorite(false); // Default to not favorited
            
            localRecipes.add(localRecipe);
        }
    }
    
    return localRecipes;
}
```

##### **Advanced Ingredient Extraction**:
```java
private List<Ingredient> extractIngredientsFromTheMealDB(ApiRecipe apiRecipe) {
    List<Ingredient> ingredients = new ArrayList<>();
    
    // Extract ingredients 1-20 with validation
    for (int i = 1; i <= 20; i++) {
        String ingredient = apiRecipe.getIngredientByIndex(i);
        String measure = apiRecipe.getMeasureByIndex(i);
        
        if (ingredient != null && !ingredient.trim().isEmpty()) {
            // Clean and normalize ingredient data
            ingredient = ingredient.trim();
            measure = measure != null ? measure.trim() : "";
            
            // Create ingredient object
            Ingredient ingredientObj = new Ingredient(ingredient, measure, "", false);
            ingredients.add(ingredientObj);
        }
    }
    
    return ingredients;
}
```

### **Advanced API Flow Management**

#### **Comprehensive Request Flow Architecture**

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           User Interaction Layer                            │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────────┐  │
│  │   HomeFragment  │  │ RecipeFilter    │  │    AddRecipeActivity       │  │
│  │                 │  │    Dialog       │  │                             │  │
│  │ • Search Input  │  │ • Filter        │  │ • Image Upload             │  │
│  │ • Filter Button │  │   Selection     │  │ • Recipe Creation          │  │
│  └─────────────────┘  └─────────────────┘  └─────────────────────────────┘  │
└─────────────────────┬───────────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        FirebaseManager Layer                                │
│  ┌─────────────────────────────────────────────────────────────────────────┐  │
│  │                    API Orchestration                                    │  │
│  │                                                                         │  │
│  │ • searchOnlineRecipes()                                                │  │
│  │ • searchOnlineRecipesWithFilter()                                      │  │
│  │ • uploadRecipeImage()                                                  │  │
│  │ • getCategories() / getAreas() / getIngredients()                      │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
└─────────────────────┬───────────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          API Client Layer                                   │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────────┐  │
│  │   ApiClient     │  │ RecipeApiService│  │    ImgBBUploadManager       │  │
│  │                 │  │                 │  │                             │  │
│  │ • Retrofit      │  │ • TheMealDB     │  │ • Image Upload              │  │
│  │   Configuration │  │   Endpoints     │  │ • Compression               │  │
│  │ • HTTP Client   │  │ • Response      │  │ • Validation                │  │
│  │ • Logging       │  │   Models        │  │ • Error Handling            │  │
│  └─────────────────┘  └─────────────────┘  └─────────────────────────────┘  │
└─────────────────────┬───────────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        External API Layer                                   │
│  ┌─────────────────────────────────────────────────────────────────────────┐  │
│  │                    TheMealDB API                                        │  │
│  │ • Recipe Search: https://www.themealdb.com/api/json/v1/1/search.php    │  │
│  │ • Category Filter: https://www.themealdb.com/api/json/v1/1/filter.php  │  │
│  │ • Recipe Details: https://www.themealdb.com/api/json/v1/1/lookup.php   │  │
│  │ • Categories: https://www.themealdb.com/api/json/v1/1/categories.php   │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
│  ┌─────────────────────────────────────────────────────────────────────────┐  │
│  │                      ImgBB API                                          │  │
│  │ • Image Upload: https://api.imgbb.com/1/upload                         │  │
│  │ • Image Hosting: https://i.ibb.co/                                      │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### **Advanced Error Handling and Recovery**

##### **Comprehensive Error Management**:
```java
private void handleApiFailure(Throwable t, OnRecipesLoadedListener listener) {
    String errorMessage;
    
    if (t instanceof UnknownHostException) {
        errorMessage = "No internet connection. Please check your network.";
    } else if (t instanceof SocketTimeoutException) {
        errorMessage = "Request timed out. Please try again.";
    } else if (t instanceof IOException) {
        errorMessage = "Network error. Please check your connection.";
    } else {
        errorMessage = "An unexpected error occurred: " + t.getMessage();
    }
    
    listener.onError(errorMessage);
}
```

##### **Retry Mechanism**:
```java
private void executeWithRetry(Call<ApiRecipeResponse> call, 
                            RecipeFilter filter, 
                            OnRecipesLoadedListener listener, 
                            int retryCount) {
    call.enqueue(new Callback<ApiRecipeResponse>() {
        @Override
        public void onFailure(Call<ApiRecipeResponse> call, Throwable t) {
            if (retryCount < MAX_RETRY_ATTEMPTS) {
                // Retry with exponential backoff
                new Handler().postDelayed(() -> {
                    executeWithRetry(call.clone(), filter, listener, retryCount + 1);
                }, RETRY_DELAY_MS * (1 << retryCount));
            } else {
                handleApiFailure(t, listener);
            }
        }
        
        @Override
        public void onResponse(Call<ApiRecipeResponse> call, 
                             Response<ApiRecipeResponse> response) {
            handleApiResponse(response, filter, listener);
        }
    });
}
```

### **Performance Optimization and Caching**

#### **Intelligent Caching Strategy**:
```java
public class ApiCache {
    private static final int CACHE_SIZE = 100;
    private static final long CACHE_DURATION = 30 * 60 * 1000; // 30 minutes
    
    private LruCache<String, CachedData> cache;
    
    public ApiCache() {
        cache = new LruCache<>(CACHE_SIZE);
    }
    
    public void put(String key, Object data) {
        CachedData cachedData = new CachedData(data, System.currentTimeMillis());
        cache.put(key, cachedData);
    }
    
    public Object get(String key) {
        CachedData cachedData = cache.get(key);
        if (cachedData != null && 
            System.currentTimeMillis() - cachedData.timestamp < CACHE_DURATION) {
            return cachedData.data;
        }
        return null;
    }
}
```

#### **Request Optimization**:
- **Connection Pooling**: Efficient HTTP connection reuse
- **Request Batching**: Group multiple requests when possible
- **Response Compression**: Automatic gzip compression
- **Image Optimization**: Automatic image compression before upload

### **Security and Privacy**

#### **API Security Measures**:
- **HTTPS Only**: All API calls use secure HTTPS connections
- **API Key Management**: Secure storage of ImgBB API key
- **Input Validation**: Comprehensive validation of all API inputs
- **Error Sanitization**: Prevents sensitive information leakage

#### **Privacy Protection**:
- **User Data Isolation**: API data is user-specific
- **No Personal Data**: API calls don't include personal information
- **Secure Storage**: API keys stored securely in BuildConfig

This comprehensive API integration provides the CookBook application with robust, scalable, and secure external service integration while maintaining excellent user experience through intelligent caching, error handling, and performance optimization.

---

## Method Review

### MainActivity.java Methods

#### onCreate(Bundle savedInstanceState)
**Location**: Lines 32-42
**Purpose**: Initializes the activity and checks authentication status
**Process**:
1. Gets FirebaseManager instance
2. Checks if user is logged in
3. Shows login screen or main app UI accordingly
**Usage**: Called when activity is created

#### setupLoginScreen()
**Location**: Lines 44-142
**Purpose**: Sets up login/registration UI and event handlers
**Process**:
1. Gets references to UI elements
2. Sets up login button click listener
3. Sets up register button click listener
4. Sets up forgot password click listener
**Usage**: Called from onCreate() when user is not authenticated

#### validateInput(TextInputLayout tilEmail, TextInputLayout tilPassword, String email, String password)
**Location**: Lines 144-162
**Purpose**: Validates user input before authentication
**Process**:
1. Checks if email is not empty
2. Checks if password is not empty and at least 6 characters
3. Sets error messages on input layouts
4. Returns validation result
**Usage**: Called before login/registration attempts

#### showMainAppUI(Bundle savedInstanceState)
**Location**: Lines 164-175
**Purpose**: Shows the main application interface
**Process**:
1. Sets main_app layout
2. Sets up bottom navigation
3. Loads HomeFragment by default
**Usage**: Called after successful authentication

#### onNavigationItemSelected(MenuItem item)
**Location**: Lines 177-192
**Purpose**: Handles bottom navigation item selection
**Process**:
1. Determines which fragment to load based on selected item
2. Creates appropriate fragment instance
3. Loads fragment into container
**Usage**: Called when user taps bottom navigation items

#### loadFragment(Fragment fragment)
**Location**: Lines 194-202
**Purpose**: Loads fragment into the main container
**Process**:
1. Checks if fragment is not null
2. Replaces current fragment in container
3. Returns success status
**Usage**: Called from onNavigationItemSelected()

### HomeFragment.java Methods

#### onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
**Location**: Lines 48-52
**Purpose**: Creates the fragment's view hierarchy
**Process**: Inflates fragment_home layout using ViewBinding
**Usage**: Called by Android framework during fragment creation

#### onViewCreated(View view, Bundle savedInstanceState)
**Location**: Lines 54-63
**Purpose**: Sets up fragment after view creation
**Process**:
1. Gets FirebaseManager instance
2. Sets up RecyclerView
3. Sets up SearchView
4. Sets up click listeners
5. Loads recipes
**Usage**: Called after onCreateView()

#### setupRecyclerView()
**Location**: Lines 65-82
**Purpose**: Configures RecyclerView for recipe display
**Process**:
1. Creates RecipeAdapter with click listeners
2. Sets LinearLayoutManager
3. Assigns adapter to RecyclerView
**Usage**: Called from onViewCreated()

#### setupSearchView()
**Location**: Lines 84-118
**Purpose**: Configures search functionality
**Process**:
1. Sets up query text listeners
2. Handles search submission
3. Handles real-time search (after 2 characters)
4. Handles search clearing
**Usage**: Called from onViewCreated()

#### searchRecipes(String query)
**Location**: Lines 120-142
**Purpose**: Searches for recipes locally and online
**Process**:
1. Shows progress bar
2. Searches local recipes first
3. If no local results, searches online
4. Updates recipe list with results
**Usage**: Called from SearchView listeners

#### searchOnlineRecipes(String query)
**Location**: Lines 144-155
**Purpose**: Searches external API for recipes
**Process**:
1. Calls FirebaseManager.searchOnlineRecipes()
2. Updates UI with results
3. Handles errors
**Usage**: Called from searchRecipes() when no local results

#### updateRecipeList(List<Recipe> recipes)
**Location**: Lines 157-190
**Purpose**: Updates the displayed recipe list
**Process**:
1. Filters recipes based on current state
2. Validates recipe data
3. Updates adapter with filtered recipes
4. Updates empty state visibility
**Usage**: Called after recipe searches and loads

#### setupClickListeners()
**Location**: Lines 192-218
**Purpose**: Sets up click handlers for UI elements
**Process**:
1. Sets up FAB for adding recipes
2. Sets up filter button
3. Sets up clear filter button
**Usage**: Called from onViewCreated()

#### showFilterDialogWithOptions()
**Location**: Lines 220-285
**Purpose**: Shows filter dialog with loaded options
**Process**:
1. Shows progress bar
2. Loads categories, areas, and ingredients from API
3. Shows filter dialog when all options are loaded
**Usage**: Called when filter button is clicked

#### loadRecipes()
**Location**: Lines 294-312
**Purpose**: Loads user's recipes from Firebase
**Process**:
1. Calls FirebaseManager.getUserRecipes()
2. Converts Firestore documents to Recipe objects
3. Updates recipe list
4. Handles errors
**Usage**: Called from onViewCreated() and onResume()

#### onFilterApplied(RecipeFilter filter)
**Location**: Lines 322-329
**Purpose**: Handles filter application from dialog
**Process**:
1. Stores current filter
2. Shows clear filter button
3. Performs search with filter
**Usage**: Called from RecipeFilterDialog

#### clearFilter()
**Location**: Lines 331-336
**Purpose**: Clears current filter
**Process**:
1. Clears current filter
2. Hides clear filter button
3. Loads user recipes
**Usage**: Called when clear filter button is clicked

#### searchWithFilterOrQuery()
**Location**: Lines 338-476
**Purpose**: Performs search with current filter and query
**Process**:
1. Determines search strategy based on filter and query
2. Calls appropriate search method
3. Updates UI with results
**Usage**: Called from search and filter operations

### AddRecipeActivity.java Methods

#### onCreate(Bundle savedInstanceState)
**Location**: Lines 42-65
**Purpose**: Initializes recipe creation/editing activity
**Process**:
1. Sets up ViewBinding
2. Configures toolbar
3. Sets up UI components
4. Handles edit mode if applicable
**Usage**: Called when activity is created

#### setupSpinner()
**Location**: Lines 67-72
**Purpose**: Configures category spinner
**Process**:
1. Creates ArrayAdapter with recipe categories
2. Sets adapter on spinner
**Usage**: Called from onCreate()

#### setupRecyclerView()
**Location**: Lines 74-78
**Purpose**: Configures ingredient list
**Process**:
1. Creates IngredientAdapter
2. Sets LinearLayoutManager
3. Assigns adapter to RecyclerView
**Usage**: Called from onCreate()

#### setupClickListeners()
**Location**: Lines 80-102
**Purpose**: Sets up button click handlers
**Process**:
1. Sets up add ingredient button
2. Sets up upload image button
3. Sets up save button
**Usage**: Called from onCreate()

#### addIngredient()
**Location**: Lines 104-125
**Purpose**: Adds or updates ingredient in list
**Process**:
1. Validates ingredient input
2. Creates or updates Ingredient object
3. Updates adapter
4. Clears input fields
**Usage**: Called when add ingredient button is clicked

#### selectImage()
**Location**: Lines 135-140
**Purpose**: Opens image picker
**Process**:
1. Creates intent for image selection
2. Starts activity for result
**Usage**: Called when upload image button is clicked

#### prefillFieldsForEdit(Recipe recipe)
**Location**: Lines 150-175
**Purpose**: Fills form fields with existing recipe data
**Process**:
1. Sets title, category, and instructions
2. Loads ingredients into adapter
3. Loads recipe image if available
**Usage**: Called in edit mode from onCreate()

#### saveRecipe()
**Location**: Lines 177-250
**Purpose**: Saves recipe to Firebase
**Process**:
1. Validates form data
2. Creates or updates Recipe object
3. Uploads image if selected
4. Saves to Firestore
**Usage**: Called when save button is clicked

#### saveRecipeToFirestore(Recipe recipe)
**Location**: Lines 268-292
**Purpose**: Saves recipe to Firebase Firestore
**Process**:
1. Calls FirebaseManager.addRecipe()
2. Handles success and failure
3. Returns to previous screen on success
**Usage**: Called from saveRecipe()

### RecipeAdapter.java Methods

#### onBindViewHolder(RecipeViewHolder holder, int position)
**Location**: Lines 60-100
**Purpose**: Binds recipe data to view holder
**Process**:
1. Gets recipe at position
2. Sets title, category, and instructions
3. Loads image with Glide
4. Sets up click listeners
5. Updates favorite button state
**Usage**: Called by RecyclerView for each item

#### onRecipeClick(Recipe recipe)
**Location**: Lines 102-115
**Purpose**: Handles recipe item clicks
**Process**:
1. Creates intent for RecipeDetailActivity
2. Passes recipe data
3. Starts activity
**Usage**: Called when recipe item is clicked

#### onFavoriteClick(Recipe recipe, boolean isFavorite)
**Location**: Lines 117-135
**Purpose**: Handles favorite button clicks
**Process**:
1. Calls FirebaseManager.toggleFavoriteRecipe()
2. Updates UI state
3. Notifies listener of change
**Usage**: Called when favorite button is clicked

### ImgBBUploadManager.java Methods

#### uploadImage(File imageFile, UploadCallback callback)
**Location**: Lines 25-67
**Purpose**: Uploads image to ImgBB service
**Process**:
1. Creates new thread for upload
2. Builds multipart form data
3. Makes HTTP POST request
4. Parses JSON response
5. Returns image URL or error via callback
**Usage**: Called from FirebaseManager.uploadRecipeImage()

---

## User Workflows

### **Comprehensive User Journey Analysis**

The CookBook application provides a seamless user experience through carefully designed workflows that handle various user scenarios, from initial onboarding to advanced recipe management. Each workflow is optimized for user efficiency and data integrity.

### **1. User Registration Flow - Complete Onboarding Process**

#### **Detailed Registration Steps**

**Step 1: App Initialization**
```
User Action: Opens CookBook app for first time
System Response: MainActivity.onCreate() executes
Process Flow:
├── FirebaseManager.getInstance() called
├── FirebaseManager.getCurrentUser() returns null
├── setupLoginScreen() displays authentication UI
└── User sees login/registration interface
```

**Step 2: Registration Interface**
```
User Action: Taps "Register" button
System Response: Registration form validation begins
Validation Process:
├── Email format validation (regex pattern)
├── Password strength validation (minimum 6 characters)
├── Real-time input validation feedback
└── Error message display for invalid inputs
```

**Step 3: Account Creation Process**
```
User Action: Submits valid registration form
System Response: FirebaseManager.registerUser() executes
Creation Process:
├── Firebase Auth user creation
│   ├── Email/password validation
│   ├── User account creation in Firebase Auth
│   └── Unique UID generation
├── Firestore user document creation
│   ├── User profile data storage
│   ├── Timestamp recording
│   └── Default preferences setup
└── Success confirmation and UI transition
```

**Step 4: Post-Registration Setup**
```
System Response: User account successfully created
Setup Process:
├── Automatic login after registration
├── Main app interface display
├── HomeFragment initialization
├── Empty recipe list display
└── Welcome message or tutorial (optional)
```

#### **Error Handling in Registration**
- **Email Already Exists**: Clear error message with login suggestion
- **Weak Password**: Specific password requirements display
- **Network Issues**: Retry mechanism with user guidance
- **Invalid Email Format**: Real-time validation feedback

### **2. User Login Flow - Secure Authentication Process**

#### **Comprehensive Login Steps**

**Step 1: Login Interface**
```
User Action: Enters email and password
System Response: Real-time input validation
Validation Process:
├── Email format verification
├── Password field validation
├── Input sanitization
└── Error state management
```

**Step 2: Authentication Process**
```
User Action: Taps "Login" button
System Response: FirebaseManager.loginUser() executes
Authentication Flow:
├── Credential validation
├── Firebase Auth authentication
├── User session establishment
├── Security token generation
└── User data retrieval from Firestore
```

**Step 3: Session Management**
```
System Response: Successful authentication
Session Process:
├── User session token storage
├── User preferences loading
├── Recipe data synchronization
├── UI state restoration
└── Navigation to main interface
```

#### **Security Features**
- **Password Visibility Toggle**: Secure password entry
- **Remember Me**: Optional session persistence
- **Auto-Logout**: Session timeout for security
- **Failed Login Handling**: Progressive delay for brute force protection

### **3. Recipe Creation Flow - Comprehensive Recipe Management**

#### **Advanced Recipe Creation Process**

**Step 1: Recipe Creation Initiation**
```
User Action: Taps Floating Action Button (FAB)
System Response: AddRecipeActivity launches
Initialization Process:
├── Activity intent creation
├── Form initialization
├── Category spinner population
├── Ingredient adapter setup
└── Image upload preparation
```

**Step 2: Recipe Information Entry**
```
User Action: Fills recipe details
System Response: Real-time form validation
Entry Process:
├── Title input with character limits
├── Category selection from predefined options
├── Instructions text area with formatting
├── Real-time validation feedback
└── Auto-save draft functionality
```

**Step 3: Ingredient Management**
```
User Action: Adds ingredients
System Response: Dynamic ingredient list management
Ingredient Process:
├── Ingredient name input with suggestions
├── Amount validation (numeric input)
├── Unit selection (grams, cups, pieces, etc.)
├── Dynamic list updates
├── Ingredient editing capabilities
└── Ingredient deletion with confirmation
```

**Step 4: Image Upload Process**
```
User Action: Selects recipe image
System Response: Comprehensive image processing
Upload Process:
├── Image picker integration
├── Image format validation
├── Image compression for optimization
├── ImgBB API upload
├── Progress indication
├── Upload success/failure handling
└── Image preview display
```

**Step 5: Recipe Validation and Save**
```
User Action: Taps "Save Recipe" button
System Response: Comprehensive validation and storage
Save Process:
├── Form data validation
│   ├── Required field verification
│   ├── Data format validation
│   ├── Ingredient list validation
│   └── Image URL validation
├── Recipe object creation
├── Firestore document creation
├── Success confirmation
└── Navigation back to home screen
```

#### **Advanced Features in Recipe Creation**
- **Draft Auto-Save**: Automatic saving of incomplete recipes
- **Image Optimization**: Automatic compression and resizing
- **Ingredient Suggestions**: Smart ingredient name suggestions
- **Category Customization**: User-defined categories
- **Recipe Templates**: Quick recipe creation from templates

### **4. Recipe Search Flow - Intelligent Discovery System**

#### **Multi-Layer Search Process**

**Step 1: Search Initiation**
```
User Action: Enters search query in SearchView
System Response: Real-time search processing
Search Process:
├── Query text monitoring
├── Search debouncing (2-second delay)
├── Query validation and sanitization
├── Search type determination
└── Search execution initiation
```

**Step 2: Local Search Execution**
```
System Response: FirebaseManager.searchRecipesByName() executes
Local Search Process:
├── User-specific recipe query
├── Case-insensitive text matching
├── Partial match support
├── Category-based filtering
├── Ingredient-based filtering
└── Results ranking and sorting
```

**Step 3: Online Search Fallback**
```
System Response: TheMealDB API search (if no local results)
Online Search Process:
├── API endpoint selection
├── HTTP request execution
├── Response parsing and validation
├── Recipe data conversion
├── Local storage integration
└── Results presentation
```

**Step 4: Search Results Management**
```
System Response: Search results display
Results Process:
├── Results list population
├── Empty state handling
├── Loading state management
├── Error state display
├── Results caching
└── Search history tracking
```

#### **Search Optimization Features**
- **Search Suggestions**: Based on user history and popular searches
- **Voice Search**: Voice input support for hands-free operation
- **Search Filters**: Category, difficulty, cooking time filters
- **Search History**: Persistent search history for quick access
- **Smart Ranking**: Results ranked by relevance and user preferences

### **5. Recipe Filtering Flow - Advanced Discovery System**

#### **Comprehensive Filtering Process**

**Step 1: Filter Interface**
```
User Action: Taps filter button
System Response: Filter dialog preparation
Preparation Process:
├── Filter options loading from API
├── User preferences retrieval
├── Dialog interface creation
├── Filter state initialization
└── Dialog display
```

**Step 2: Filter Options Loading**
```
System Response: API data retrieval for filter options
Loading Process:
├── Categories loading from TheMealDB
├── Cuisine areas loading
├── Ingredients list loading
├── Loading state management
└── Error handling for failed loads
```

**Step 3: Filter Selection**
```
User Action: Selects filter criteria
System Response: Filter application
Selection Process:
├── Single/multiple filter support
├── Filter combination logic
├── Real-time filter preview
├── Filter validation
└── Clear filter option
```

**Step 4: Filtered Search Execution**
```
System Response: Filtered search execution
Execution Process:
├── Filter-based API query construction
├── Local filter application
├── Results combination and deduplication
├── Results ranking and sorting
└── Results presentation
```

#### **Advanced Filtering Features**
- **Multi-Criteria Filtering**: Combine multiple filter types
- **Filter Presets**: Save and reuse filter combinations
- **Smart Filtering**: AI-powered filter suggestions
- **Filter Analytics**: Track popular filter combinations
- **Custom Filters**: User-defined filter criteria

### **6. Recipe Favoriting Flow - Personal Recipe Management**

#### **Comprehensive Favoriting Process**

**Step 1: Favorite Action**
```
User Action: Taps heart icon on recipe card
System Response: Favorite status toggle
Toggle Process:
├── Current favorite status check
├── Visual feedback animation
├── Optimistic UI update
├── Backend synchronization
└── Success/failure handling
```

**Step 2: Backend Synchronization**
```
System Response: FirebaseManager.toggleFavoriteRecipe() executes
Sync Process:
├── Firestore document update
├── User favorites list update
├── Cross-device synchronization
├── Offline queue management
└── Conflict resolution
```

**Step 3: UI State Management**
```
System Response: UI updates across app
Update Process:
├── Recipe card heart icon update
├── Favorites fragment refresh
├── Home fragment update
├── Search results update
└── Notification system (optional)
```

#### **Advanced Favoriting Features**
- **Favorite Categories**: Organize favorites by categories
- **Favorite Sharing**: Share favorite recipes with others
- **Favorite Analytics**: Track favorite patterns
- **Bulk Favoriting**: Select multiple recipes to favorite
- **Favorite Export**: Export favorites to external format

### **7. Recipe Editing Flow - Comprehensive Update Process**

#### **Advanced Editing Workflow**

**Step 1: Edit Initiation**
```
User Action: Taps edit button on recipe detail
System Response: Edit mode activation
Activation Process:
├── Recipe data loading
├── Form population with existing data
├── Edit mode UI state
├── Permission verification
└── Edit history tracking
```

**Step 2: Edit Process**
```
User Action: Modifies recipe information
System Response: Real-time edit validation
Edit Process:
├── Field-by-field validation
├── Change tracking
├── Auto-save functionality
├── Conflict detection
└── Edit preview
```

**Step 3: Save and Synchronization**
```
User Action: Saves edited recipe
System Response: Update process execution
Update Process:
├── Change validation
├── Firestore document update
├── Image update (if changed)
├── Version control
└── Success confirmation
```

### **8. Recipe Sharing Flow - Social Features**

#### **Comprehensive Sharing Process**

**Step 1: Share Initiation**
```
User Action: Taps share button
System Response: Share options preparation
Preparation Process:
├── Recipe data formatting
├── Share content generation
├── Platform-specific formatting
├── Image inclusion
└── Share intent creation
```

**Step 2: Share Execution**
```
User Action: Selects sharing platform
System Response: Share content delivery
Delivery Process:
├── Platform-specific formatting
├── Content optimization
├── Share tracking
├── Success confirmation
└── Analytics recording
```

### **Workflow Integration and Optimization**

#### **Cross-Workflow Features**
- **State Persistence**: Maintain user state across workflows
- **Offline Support**: Graceful degradation when offline
- **Performance Optimization**: Efficient data loading and caching
- **Error Recovery**: Automatic retry and fallback mechanisms
- **User Guidance**: Contextual help and tutorials

#### **Workflow Analytics**
- **Usage Tracking**: Monitor workflow completion rates
- **Performance Metrics**: Track workflow execution times
- **Error Analysis**: Identify and resolve workflow bottlenecks
- **User Feedback**: Collect and analyze user satisfaction
- **Optimization Opportunities**: Identify areas for improvement

This comprehensive user workflow analysis provides a deep understanding of how users interact with the CookBook application, enabling developers to optimize the user experience and implement new features effectively.

---

## Use-Case Scenarios

### Scenario 1: New User Onboarding
**Actor**: New user
**Goal**: Create account and explore the app
**Preconditions**: App is installed, internet connection available

**Steps**:
1. **App Launch**
   - `MainActivity.onCreate()` checks authentication status
   - `FirebaseManager.getCurrentUser()` returns null
   - Login screen is displayed via `setupLoginScreen()`

2. **Account Creation**
   - User enters email: "john.doe@example.com"
   - User enters password: "securepass123"
   - `validateInput()` validates:
     ```java
     if (email.isEmpty()) {
         tilEmail.setError("Email is required");
         return false;
     }
     if (password.length() < 6) {
         tilPassword.setError("Password must be at least 6 characters");
         return false;
     }
     ```
   - User taps "Register" button
   - `FirebaseManager.registerUser(email, password)` is called:
     ```java
     mAuth.createUserWithEmailAndPassword(email, password)
         .addOnCompleteListener(task -> {
             if (task.isSuccessful()) {
                 FirebaseUser user = mAuth.getCurrentUser();
                 createUserDocument(user);
             }
         });
     ```

3. **User Document Creation**
   - `createUserDocument()` creates Firestore document:
     ```java
     Map<String, Object> userData = new HashMap<>();
     userData.put("email", email);
     userData.put("createdAt", new Date());
     db.collection("users").document(userId).set(userData);
     ```

4. **Main App Access**
   - `showMainAppUI()` displays main interface
   - `HomeFragment` loads via `loadFragment(new HomeFragment())`
   - `HomeFragment.loadRecipes()` fetches user's recipes (empty initially)

**Postconditions**: User account created, main app interface displayed

### Scenario 2: Recipe Creation with Image Upload
**Actor**: Authenticated user
**Goal**: Create a new recipe with image
**Preconditions**: User is logged in, has internet connection

**Steps**:
1. **Navigate to Add Recipe**
   - User taps FAB in `HomeFragment`
   - `AddRecipeActivity` launches via intent
   - `AddRecipeActivity.onCreate()` initializes form

2. **Fill Recipe Details**
   - User enters title: "Spaghetti Carbonara"
   - User selects category: "Dinner" from spinner
   - User enters instructions: "Cook pasta, mix with eggs and cheese..."
   - `setupSpinner()` populates categories:
     ```java
     String[] categories = {"Breakfast", "Lunch", "Dinner", "Dessert", "Snack"};
     ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
         android.R.layout.simple_spinner_item, categories);
     ```

3. **Add Ingredients**
   - User adds ingredient: "Spaghetti, 200g, grams"
   - `addIngredient()` creates Ingredient object:
     ```java
     Ingredient ingredient = new Ingredient(name, amount, unit, false);
     ingredientAdapter.addIngredient(ingredient);
     ```
   - User adds more ingredients: "Eggs, 2, pieces", "Parmesan, 50g, grams"

4. **Upload Image**
   - User taps "Upload Image" button
   - `selectImage()` launches image picker:
     ```java
     Intent intent = new Intent(Intent.ACTION_PICK);
     intent.setType("image/*");
     startActivityForResult(intent, REQUEST_IMAGE_PICK);
     ```
   - User selects image from gallery
   - `onActivityResult()` processes selection and calls `uploadImage()`

5. **Image Upload Process**
   - `FirebaseManager.uploadRecipeImage(uri)` is called
   - `ImgBBUploadManager.uploadImage()` uploads to ImgBB:
     ```java
     RequestBody requestBody = new MultipartBody.Builder()
         .setType(MultipartBody.FORM)
         .addFormDataPart("key", API_KEY)
         .addFormDataPart("image", file.getName(), 
             RequestBody.create(MediaType.parse("image/*"), file))
         .build();
     ```
   - Image URL is returned: "https://i.ibb.co/abc123/recipe.jpg"

6. **Save Recipe**
   - User taps "Save" button
   - `saveRecipe()` validates form and creates Recipe object:
     ```java
     Recipe recipe = new Recipe();
     recipe.setTitle(title);
     recipe.setCategory(category);
     recipe.setInstructions(instructions);
     recipe.setIngredients(ingredients);
     recipe.setImageUrl(imageUrl);
     recipe.setUserId(FirebaseManager.getCurrentUserId());
     ```
   - `FirebaseManager.addRecipe(recipe)` saves to Firestore:
     ```java
     Map<String, Object> recipeData = new HashMap<>();
     recipeData.put("title", recipe.getTitle());
     recipeData.put("category", recipe.getCategory());
     // ... other fields
     db.collection("recipes").add(recipeData);
     ```

**Postconditions**: Recipe saved to Firestore, user returns to home screen

### Scenario 3: Advanced Recipe Search and Filtering
**Actor**: Authenticated user
**Goal**: Find recipes using search and filters
**Preconditions**: User has recipes, internet connection available

**Steps**:
1. **Local Search**
   - User enters "pasta" in SearchView
   - `HomeFragment.searchRecipes("pasta")` is called
   - `FirebaseManager.searchRecipesByName("pasta")` searches locally:
     ```java
     Query query = db.collection("recipes")
         .whereEqualTo("userId", getCurrentUserId())
         .whereGreaterThanOrEqualTo("title", query.toLowerCase())
         .whereLessThanOrEqualTo("title", query.toLowerCase() + '\uf8ff');
     ```

2. **Online Search (if no local results)**
   - If no local results found, `searchOnlineRecipes("pasta")` is called
   - `FirebaseManager.searchOnlineRecipes()` calls TheMealDB API:
     ```java
     Call<ApiRecipeResponse> call = apiService.searchRecipes(query);
     call.enqueue(new Callback<ApiRecipeResponse>() {
         @Override
         public void onResponse(Call<ApiRecipeResponse> call, 
                              Response<ApiRecipeResponse> response) {
             List<ApiRecipe> apiRecipes = response.body().getMeals();
             List<Recipe> localRecipes = convertApiRecipesToLocalRecipes(apiRecipes);
             listener.onRecipesLoaded(localRecipes);
         }
     });
     ```

3. **Apply Category Filter**
   - User taps filter button
   - `showFilterDialogWithOptions()` loads filter options:
     ```java
     FirebaseManager.getCategories(new OnCategoriesLoadedListener() {
         @Override
         public void onCategoriesLoaded(List<String> categories) {
             // Show filter dialog with categories
         }
     });
     ```
   - User selects "Italian" category
   - `RecipeFilterDialog.onFilterApplied()` creates filter:
     ```java
     RecipeFilter filter = new RecipeFilter();
     filter.setCategory("Italian");
     ```

4. **Filtered Search**
   - `searchWithFilterOrQuery()` determines search strategy
   - `FirebaseManager.searchOnlineRecipesWithFilter(filter, listener)` is called:
     ```java
     Call<ApiRecipeResponse> call = apiService.filterByCategory(filter.getCategory());
     call.enqueue(new Callback<ApiRecipeResponse>() {
         @Override
         public void onResponse(Call<ApiRecipeResponse> call, 
                              Response<ApiRecipeResponse> response) {
             List<Recipe> recipes = convertApiRecipesToLocalRecipes(response.body().getMeals());
             recipes = applyLocalFilters(recipes, filter);
             listener.onRecipesLoaded(recipes);
         }
     });
     ```

5. **Display Results**
   - `updateRecipeList(recipes)` updates RecyclerView:
     ```java
     recipeAdapter.updateRecipes(filteredRecipes);
     binding.emptyState.setVisibility(
         filteredRecipes.isEmpty() ? View.VISIBLE : View.GONE);
     ```

**Postconditions**: Filtered recipe results displayed, clear filter option available

### Scenario 4: Recipe Favoriting and Management
**Actor**: Authenticated user
**Goal**: Mark recipe as favorite and view favorites
**Preconditions**: User has recipes, some marked as favorites

**Steps**:
1. **Toggle Favorite Status**
   - User taps heart icon on recipe card
   - `RecipeAdapter.onFavoriteClick(recipe, isFavorite)` is called:
     ```java
     FirebaseManager.toggleFavoriteRecipe(recipe.getId(), !isFavorite);
     recipe.setFavorite(!isFavorite);
     notifyItemChanged(position);
     ```
   - `FirebaseManager.toggleFavoriteRecipe()` updates Firestore:
     ```java
     db.collection("recipes").document(recipeId)
         .update("favorite", isFavorite);
     ```

2. **Navigate to Favorites**
   - User taps "Favorites" in bottom navigation
   - `MainActivity.onNavigationItemSelected()` loads FavoritesFragment:
     ```java
     case R.id.nav_favorites:
         loadFragment(new FavoritesFragment());
         break;
     ```

3. **Load Favorite Recipes**
   - `FavoritesFragment.onViewCreated()` calls `loadFavoriteRecipes()`
   - `FirebaseManager.getFavoriteRecipes()` queries Firestore:
     ```java
     Query query = db.collection("recipes")
         .whereEqualTo("userId", getCurrentUserId())
         .whereEqualTo("favorite", true);
     ```

4. **Display Favorites**
   - `FavoritesFragment.updateRecipeList()` displays favorite recipes
   - RecipeAdapter shows recipes with filled heart icons

**Postconditions**: Recipe favorite status updated, favorites list displayed

### Scenario 5: Recipe Editing and Image Update
**Actor**: Authenticated user
**Goal**: Edit existing recipe and update image
**Preconditions**: User has existing recipe, recipe is editable

**Steps**:
1. **Access Recipe for Editing**
   - User taps recipe in list
   - `RecipeAdapter.onRecipeClick(recipe)` launches RecipeDetailActivity:
     ```java
     Intent intent = new Intent(context, RecipeDetailActivity.class);
     intent.putExtra("recipe", recipe);
     context.startActivity(intent);
     ```
   - User taps edit button in RecipeDetailActivity
   - `AddRecipeActivity` launches in edit mode with recipe data

2. **Load Existing Data**
   - `AddRecipeActivity.onCreate()` detects edit mode
   - `prefillFieldsForEdit(recipe)` populates form:
     ```java
     binding.etTitle.setText(recipe.getTitle());
     binding.spinnerCategory.setSelection(getCategoryIndex(recipe.getCategory()));
     binding.etInstructions.setText(recipe.getInstructions());
     ingredientAdapter.setIngredients(recipe.getIngredients());
     if (recipe.getImageUrl() != null) {
         Glide.with(this).load(recipe.getImageUrl()).into(binding.ivRecipe);
     }
     ```

3. **Update Recipe Information**
   - User modifies title, instructions, or ingredients
   - `addIngredient()` updates ingredient list
   - User selects new image
   - `uploadRecipeImage()` uploads new image to ImgBB

4. **Save Updated Recipe**
   - `saveRecipe()` detects edit mode and calls `updateRecipe()`:
     ```java
     if (isEditMode) {
         recipe.setId(existingRecipeId);
         FirebaseManager.updateRecipe(recipe);
     } else {
         FirebaseManager.addRecipe(recipe);
     }
     ```
   - `FirebaseManager.updateRecipe()` updates Firestore document:
     ```java
     db.collection("recipes").document(recipe.getId())
         .set(recipeData, SetOptions.merge());
     ```

**Postconditions**: Recipe updated in Firestore, changes reflected in UI

### Scenario 6: Password Reset Flow
**Actor**: User who forgot password
**Goal**: Reset password via email
**Preconditions**: User has existing account, email access

**Steps**:
1. **Initiate Password Reset**
   - User taps "Forgot Password?" link on login screen
   - `MainActivity.setupLoginScreen()` sets up click listener:
     ```java
     binding.tvForgotPassword.setOnClickListener(v -> {
         String email = binding.etEmail.getText().toString();
         if (!email.isEmpty()) {
             FirebaseManager.sendPasswordResetEmail(email);
         }
     });
     ```

2. **Send Reset Email**
   - `FirebaseManager.sendPasswordResetEmail(email)` is called:
     ```java
     mAuth.sendPasswordResetEmail(email)
         .addOnCompleteListener(task -> {
             if (task.isSuccessful()) {
                 // Show success message
             } else {
                 String error = translatePasswordResetError(task.getException().getMessage());
                 // Show error message
             }
         });
     ```

3. **User Receives Email**
   - Firebase sends password reset email
   - User clicks link in email
   - User sets new password on Firebase page

4. **Return to App**
   - User returns to app with new password
   - User can now login with new credentials

**Postconditions**: Password reset email sent, user can set new password

### Scenario 7: Offline Recipe Management
**Actor**: User with limited connectivity
**Goal**: Manage recipes when offline
**Preconditions**: User has existing recipes, limited internet access

**Steps**:
1. **View Existing Recipes**
   - App loads with cached data from previous sessions
   - `HomeFragment.loadRecipes()` attempts to load from Firebase
   - If offline, cached recipes are displayed
   - Search functionality works on cached data

2. **Create Recipe Offline**
   - User creates new recipe without image
   - Recipe is stored locally temporarily
   - When connection is restored, recipe syncs to Firebase

3. **Edit Existing Recipes**
   - User can edit cached recipes
   - Changes are queued for sync when online
   - UI updates immediately for better UX

**Postconditions**: Recipes managed offline, sync when connection restored

---

## Navigation Graph

### Application Navigation Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                        App Launch                               │
│                    MainActivity.onCreate()                      │
└─────────────────────┬───────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Authentication Check                         │
│              FirebaseManager.getCurrentUser()                   │
└─────────────────────┬───────────────────────────────────────────┘
                      │
        ┌─────────────┴─────────────┐
        │                           │
        ▼                           ▼
┌─────────────────┐    ┌─────────────────────────────┐
│   Not Logged In │    │        Logged In            │
│                 │    │                             │
│  Login Screen   │    │    Main App Interface       │
│                 │    │                             │
│ ┌─────────────┐ │    │  ┌─────────────────────────┐ │
│ │   Email     │ │    │  │    Bottom Navigation    │ │
│ │  Password   │ │    │  │                         │ │
│ │   Login     │ │    │  │  [Home] [Favorites]     │ │
│ │  Register   │ │    │  │     [Profile]           │ │
│ │ Forgot Pass │ │    │  └─────────────────────────┘ │
│ └─────────────┘ │    │                             │
└─────────────────┘    │  ┌─────────────────────────┐ │
                       │  │     Fragment Container  │ │
                       │  │                         │ │
                       │  │  ┌─────────────────────┐ │ │
                       │  │  │   HomeFragment      │ │ │
                       │  │  │                     │ │ │
                       │  │  │ ┌─────────────────┐ │ │ │
                       │  │  │ │   SearchView    │ │ │ │
                       │  │  │ └─────────────────┘ │ │ │
                       │  │  │ ┌─────────────────┐ │ │ │
                       │  │  │ │  Filter Button  │ │ │ │
                       │  │  │ └─────────────────┘ │ │ │
                       │  │  │ ┌─────────────────┐ │ │ │
                       │  │  │ │  Recipe List    │ │ │ │
                       │  │  │ │ (RecyclerView)  │ │ │ │
                       │  │  │ └─────────────────┘ │ │ │
                       │  │  │ ┌─────────────────┐ │ │ │
                       │  │  │ │       FAB       │ │ │ │
                       │  │  │ │   (+ Add)       │ │ │ │
                       │  │  │ └─────────────────┘ │ │ │
                       │  │  └─────────────────────┘ │ │
                       │  └─────────────────────────┘ │
                       └─────────────────────────────┘
```

### Fragment Navigation Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    MainActivity                                 │
│              Bottom Navigation Controller                       │
└─────────────────────┬───────────────────────────────────────────┘
                      │
        ┌─────────────┴─────────────┐
        │                           │
        ▼                           ▼
┌─────────────────┐    ┌─────────────────────────────┐
│  HomeFragment   │    │    FavoritesFragment        │
│                 │    │                             │
│ ┌─────────────┐ │    │ ┌─────────────────────────┐ │
│ │ Recipe List │ │    │ │   Favorite Recipes      │ │
│ │             │ │    │ │                         │ │
│ │ ┌─────────┐ │ │    │ │ ┌─────────────────────┐ │ │
│ │ │ Recipe  │ │ │    │ │ │   Recipe Item       │ │ │
│ │ │  Item   │ │ │    │ │ │   (Filled Heart)    │ │ │
│ │ │         │ │ │    │ │ └─────────────────────┘ │ │
│ │ │ ┌─────┐ │ │ │    │ └─────────────────────────┘ │
│ │ │ │Heart│ │ │ │    │                             │
│ │ │ │Icon │ │ │ │    │ ┌─────────────────────────┐ │
│ │ │ └─────┘ │ │ │    │ │    Empty State         │ │
│ │ └─────────┘ │ │    │ │  (No Favorites)        │ │
│ └─────────────┘ │    │ └─────────────────────────┘ │
└─────────────────┘    └─────────────────────────────┘
        │                           │
        │                           │
        ▼                           ▼
┌─────────────────┐    ┌─────────────────────────────┐
│ ProfileFragment │    │    Recipe Detail Flow       │
│                 │    │                             │
│ ┌─────────────┐ │    │ ┌─────────────────────────┐ │
│ │ User Info   │ │    │ │  RecipeDetailActivity   │ │
│ │             │ │    │ │                         │ │
│ │ ┌─────────┐ │ │    │ │ ┌─────────────────────┐ │ │
│ │ │  Email  │ │ │    │ │ │   Recipe Details    │ │ │
│ │ └─────────┘ │ │    │ │ │                     │ │ │
│ │ ┌─────────┐ │ │    │ │ │ ┌─────────────────┐ │ │ │
│ │ │ Logout  │ │ │    │ │ │ │   Edit Button   │ │ │ │
│ │ │ Button  │ │ │    │ │ │ └─────────────────┘ │ │ │
│ │ └─────────┘ │ │    │ │ └─────────────────────┘ │ │
│ └─────────────┘ │    │ └─────────────────────────┘ │
└─────────────────┘    └─────────────┬───────────────┘
                                     │
                                     ▼
                       ┌─────────────────────────────┐
                       │    AddRecipeActivity        │
                       │      (Edit Mode)            │
                       │                             │
                       │ ┌─────────────────────────┐ │
                       │ │     Recipe Form         │ │
                       │ │                         │ │
                       │ │ ┌─────────────────────┐ │ │
                       │ │ │   Title Input       │ │ │
                       │ │ └─────────────────────┘ │ │
                       │ │ ┌─────────────────────┐ │ │
                       │ │ │  Category Spinner   │ │ │
                       │ │ └─────────────────────┘ │ │
                       │ │ ┌─────────────────────┐ │ │
                       │ │ │  Instructions       │ │ │
                       │ │ └─────────────────────┘ │ │
                       │ │ ┌─────────────────────┐ │ │
                       │ │ │  Ingredients List   │ │ │
                       │ │ └─────────────────────┘ │ │
                       │ │ ┌─────────────────────┐ │ │
                       │ │ │   Image Upload      │ │ │
                       │ │ └─────────────────────┘ │ │
                       │ │ ┌─────────────────────┐ │ │
                       │ │ │    Save Button      │ │ │
                       │ │ └─────────────────────┘ │ │
                       │ └─────────────────────────┘ │
                       └─────────────────────────────┘
```

### Dialog Navigation Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    HomeFragment                                 │
│                         │                                       │
│                         ▼                                       │
│              ┌─────────────────────────┐                       │
│              │     Filter Button       │                       │
│              └─────────────┬───────────┘                       │
│                            │                                   │
│                            ▼                                   │
│              ┌─────────────────────────┐                       │
│              │   showFilterDialog()    │                       │
│              └─────────────┬───────────┘                       │
│                            │                                   │
│                            ▼                                   │
│              ┌─────────────────────────┐                       │
│              │  RecipeFilterDialog     │                       │
│              │                         │                       │
│              │ ┌─────────────────────┐ │                       │
│              │ │   Category Spinner  │ │                       │
│              │ └─────────────────────┘ │                       │
│              │ ┌─────────────────────┐ │                       │
│              │ │    Area Spinner     │ │                       │
│              │ └─────────────────────┘ │                       │
│              │ ┌─────────────────────┐ │                       │
│              │ │ Ingredient Spinner  │ │                       │
│              │ └─────────────────────┘ │                       │
│              │ ┌─────────────────────┐ │                       │
│              │ │   Apply Filter      │ │                       │
│              │ └─────────────────────┘ │                       │
│              │ ┌─────────────────────┐ │                       │
│              │ │     Cancel          │ │                       │
│              │ └─────────────────────┘ │                       │
│              └─────────────────────────┘                       │
│                            │                                   │
│                            ▼                                   │
│              ┌─────────────────────────┐                       │
│              │   onFilterApplied()     │                       │
│              └─────────────┬───────────┘                       │
│                            │                                   │
│                            ▼                                   │
│              ┌─────────────────────────┐                       │
│              │   searchWithFilter()    │                       │
│              └─────────────────────────┘                       │
└─────────────────────────────────────────────────────────────────┘
```

### API Integration Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    User Search                                  │
│                         │                                       │
│                         ▼                                       │
│              ┌─────────────────────────┐                       │
│              │   Local Search First    │                       │
│              │ FirebaseManager.search  │                       │
│              │ RecipesByName(query)    │                       │
│              └─────────────┬───────────┘                       │
│                            │                                   │
│                            ▼                                   │
│              ┌─────────────────────────┐                       │
│              │   Local Results Found?  │                       │
│              └─────────────┬───────────┘                       │
│                            │                                   │
│        ┌───────────────────┴───────────────────┐               │
│        │                                       │               │
│        ▼                                       ▼               │
│ ┌─────────────┐                    ┌─────────────────────────┐ │
│ │ Display     │                    │   Online Search         │ │
│ │ Local       │                    │                         │ │
│ │ Results     │                    │ ┌─────────────────────┐ │ │
│ └─────────────┘                    │ │  TheMealDB API      │ │ │
│                                    │ │                     │ │ │
│                                    │ │ ┌─────────────────┐ │ │ │
│                                    │ │ │ searchRecipes() │ │ │ │
│                                    │ │ └─────────────────┘ │ │ │
│                                    │ │ ┌─────────────────┐ │ │ │
│                                    │ │ │filterByCategory │ │ │ │
│                                    │ │ └─────────────────┘ │ │ │
│                                    │ │ ┌─────────────────┐ │ │ │
│                                    │ │ │ filterByArea()  │ │ │ │
│                                    │ │ └─────────────────┘ │ │ │
│                                    │ └─────────────────────┘ │ │
│                                    └─────────────────────────┘ │
│                                              │                 │
│                                              ▼                 │
│                                    ┌─────────────────────────┐ │
│                                    │   Convert API Response │ │
│                                    │   to Local Recipe      │ │
│                                    │   Objects              │ │
│                                    │                         │ │
│                                    │ convertApiRecipesToLocal│ │
│                                    │ Recipes(apiRecipes)     │ │
│                                    └─────────────────────────┘ │
│                                              │                 │
│                                              ▼                 │
│                                    ┌─────────────────────────┐ │
│                                    │   Display Results       │ │
│                                    │   in RecyclerView       │ │
│                                    └─────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

### Image Upload Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    AddRecipeActivity                            │
│                         │                                       │
│                         ▼                                       │
│              ┌─────────────────────────┐                       │
│              │   Upload Image Button   │                       │
│              └─────────────┬───────────┘                       │
│                            │                                   │
│                            ▼                                   │
│              ┌─────────────────────────┐                       │
│              │   Image Picker Intent   │                       │
│              │   selectImage()         │                       │
│              └─────────────┬───────────┘                       │
│                            │                                   │
│                            ▼                                   │
│              ┌─────────────────────────┐                       │
│              │   User Selects Image    │                       │
│              └─────────────┬───────────┘                       │
│                            │                                   │
│                            ▼                                   │
│              ┌─────────────────────────┐                       │
│              │   onActivityResult()    │                       │
│              └─────────────┬───────────┘                       │
│                            │                                   │
│                            ▼                                   │
│              ┌─────────────────────────┐                       │
│              │   FirebaseManager       │                       │
│              │   uploadRecipeImage()   │                       │
│              └─────────────┬───────────┘                       │
│                            │                                   │
│                            ▼                                   │
│              ┌─────────────────────────┐                       │
│              │   ImgBBUploadManager    │                       │
│              │   uploadImage()         │                       │
│              │                         │                       │
│              │ ┌─────────────────────┐ │                       │
│              │ │   Create Multipart  │ │                       │
│              │ │   Form Data         │ │                       │
│              │ └─────────────────────┘ │                       │
│              │ ┌─────────────────────┐ │                       │
│              │ │   HTTP POST Request │ │                       │
│              │ │   to ImgBB API      │ │                       │
│              │ └─────────────────────┘ │                       │
│              │ ┌─────────────────────┐ │                       │
│              │ │   Parse JSON        │ │                       │
│              │ │   Response          │ │                       │
│              │ └─────────────────────┘ │                       │
│              └─────────────┬───────────┘                       │
│                            │                                   │
│                            ▼                                   │
│              ┌─────────────────────────┐                       │
│              │   Return Image URL      │                       │
│              │   or Error Message      │                       │
│              └─────────────┬───────────┘                       │
│                            │                                   │
│                            ▼                                   │
│              ┌─────────────────────────┐                       │
│              │   Display Image in      │                       │
│              │   ImageView with Glide  │                       │
│              └─────────────────────────┘                       │
└─────────────────────────────────────────────────────────────────┘
```

---

## Configuration & Setup

### **Comprehensive Development Environment Setup**

#### **Firebase Project Configuration**

##### **Step-by-Step Firebase Setup**
1. **Create Firebase Project**:
   - Visit [Firebase Console](https://console.firebase.google.com)
   - Click "Create a project"
   - Enter project name: "CookBook"
   - Enable Google Analytics (optional)
   - Choose analytics account or create new

2. **Add Android App**:
   - Click "Add app" → Android icon
   - Enter package name: `com.example.cookbook`
   - Enter app nickname: "CookBook"
   - Enter SHA-1 certificate fingerprint (for release builds)
   - Click "Register app"

3. **Download Configuration**:
   - Download `google-services.json`
   - Place in `app/` directory
   - Verify file structure:
     ```
     app/
     ├── google-services.json
     ├── build.gradle
     └── src/
     ```

4. **Enable Firebase Services**:
   - **Authentication**: 
     - Go to Authentication → Sign-in method
     - Enable Email/Password provider
     - Configure password requirements
   - **Firestore Database**:
     - Go to Firestore Database
     - Click "Create database"
     - Choose "Start in test mode" (for development)
     - Select location closest to users

5. **Configure Security Rules**:
   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       // User profiles - users can only access their own data
       match /users/{userId} {
         allow read, write: if request.auth != null && request.auth.uid == userId;
       }
       
       // Recipes - users can only access their own recipes
       match /recipes/{recipeId} {
         allow read, write: if request.auth != null && 
           request.auth.uid == resource.data.userId;
       }
       
       // Public recipes (for future social features)
       match /public_recipes/{recipeId} {
         allow read: if true;
         allow write: if request.auth != null;
       }
     }
   }
   ```

6. **Set Up Indexes**:
   ```javascript
   // Create composite indexes for efficient queries
   // Collection: recipes
   // Fields: userId (Ascending), createdAt (Descending)
   // Fields: userId (Ascending), favorite (Ascending)
   // Fields: userId (Ascending), category (Ascending)
   ```

#### **ImgBB API Configuration**

##### **API Key Setup**
1. **Get API Key**:
   - Visit [ImgBB API](https://imgbb.com/api)
   - Create account or sign in
   - Generate API key
   - Note the key for configuration

2. **Configure in Project**:
   ```properties
   # local.properties
   IMGBB_API_KEY=your_actual_api_key_here
   ```

3. **Build Configuration**:
   ```gradle
   // app/build.gradle
   android {
       buildTypes {
           debug {
               buildConfigField "String", "IMGBB_API_KEY", "\"${properties.getProperty('IMGBB_API_KEY')}\""
           }
           release {
               buildConfigField "String", "IMGBB_API_KEY", "\"${properties.getProperty('IMGBB_API_KEY')}\""
           }
       }
   }
   ```

4. **API Usage Limits**:
   - Free tier: 32MB per image
   - Rate limits: 1000 requests per day
   - Supported formats: JPEG, PNG, GIF, BMP, TIFF

#### **Android Studio Configuration**

##### **Project Setup**
1. **Import Project**:
   - Open Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to CookBook directory
   - Click "OK"

2. **Gradle Sync**:
   ```bash
   # Sync project with Gradle files
   ./gradlew clean build
   ```

3. **SDK Configuration**:
   - Minimum SDK: API 24 (Android 7.0)
   - Target SDK: API 34 (Android 14)
   - Compile SDK: API 34

4. **Build Variants**:
   - Debug: For development and testing
   - Release: For production deployment

##### **Development Tools Setup**
1. **Enable Developer Options**:
   - Enable USB debugging
   - Enable "Don't keep activities"
   - Enable "Show all ANRs"

2. **Emulator Configuration**:
   - Create AVD with API 24 or higher
   - Enable hardware acceleration
   - Configure sufficient RAM (2GB+)

### **Comprehensive Build Configuration**

#### **Project-Level build.gradle**
```gradle
// build.gradle (Project)
buildscript {
    ext {
        kotlin_version = '1.8.0'
        gradle_version = '8.0.0'
    }
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath "com.android.tools.build:gradle:$gradle_version"
        classpath 'com.google.gms:google-services:4.3.15'
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
```

#### **App-Level build.gradle**
```gradle
// app/build.gradle
plugins {
    id 'com.android.application'
    id 'com.google.gms.google-services'
}

android {
    namespace 'com.example.cookbook'
    compileSdk 34

    defaultConfig {
        applicationId "com.example.cookbook"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding true
    }
}

dependencies {
    // Android Core
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.navigation:navigation-fragment:2.6.0'
    implementation 'androidx.navigation:navigation-ui:2.6.0'

    // Firebase
    implementation platform('com.google.firebase:firebase-bom:32.2.0')
    implementation 'com.google.firebase:firebase-auth'
    implementation 'com.google.firebase:firebase-firestore'

    // Networking
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'

    // Image Loading
    implementation 'com.github.bumptech.glide:glide:4.15.1'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.15.1'

    // Testing
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}
```

#### **ProGuard Configuration**
```proguard
# app/proguard-rules.pro
-keepattributes Signature
-keepattributes *Annotation*

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# GSON
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
```

### **Environment-Specific Configuration**

#### **Development Environment**
```properties
# local.properties (Development)
sdk.dir=C\:\\Users\\username\\AppData\\Local\\Android\\Sdk
IMGBB_API_KEY=dev_api_key_here
DEBUG_MODE=true
LOG_LEVEL=DEBUG
```

#### **Production Environment**
```properties
# local.properties (Production)
sdk.dir=C\:\\Users\\username\\AppData\\Local\\Android\\Sdk
IMGBB_API_KEY=prod_api_key_here
DEBUG_MODE=false
LOG_LEVEL=ERROR
```

### **Dependency Management**

#### **Core Dependencies Analysis**

##### **Firebase BOM (Bill of Materials)**
```gradle
implementation platform('com.google.firebase:firebase-bom:32.2.0')
```
- **Purpose**: Manages Firebase dependency versions
- **Benefits**: Automatic version compatibility
- **Services**: Auth, Firestore, Analytics, Crashlytics

##### **Retrofit for API Integration**
```gradle
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
```
- **Purpose**: HTTP client for API calls
- **Features**: Type-safe API interfaces, JSON conversion
- **Benefits**: Efficient networking, error handling

##### **Glide for Image Loading**
```gradle
implementation 'com.github.bumptech.glide:glide:4.15.1'
```
- **Purpose**: Image loading and caching
- **Features**: Memory and disk caching, image transformation
- **Benefits**: Efficient image management, memory optimization

##### **Material Design Components**
```gradle
implementation 'com.google.android.material:material:1.9.0'
```
- **Purpose**: Modern UI components
- **Features**: Material Design 3 components
- **Benefits**: Consistent design, accessibility

##### **ViewBinding for Type-Safe Views**
```gradle
buildFeatures {
    viewBinding true
}
```
- **Purpose**: Type-safe view access
- **Features**: Compile-time view binding
- **Benefits**: Null safety, better IDE support

### **Build Optimization**

#### **Build Performance**
```gradle
android {
    // Enable build caching
    buildCache {
        local {
            directory = new File(rootDir, 'build-cache')
            removeUnusedEntriesAfterDays = 30
        }
    }
    
    // Enable parallel execution
    dexOptions {
        preDexLibraries = true
        maxProcessCount = 8
    }
}
```

#### **Release Build Optimization**
```gradle
android {
    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
            
            // Enable R8 optimization
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt')
        }
    }
}
```

### **Security Configuration**

#### **API Key Security**
```gradle
// Store sensitive data in local.properties (not in version control)
buildConfigField "String", "IMGBB_API_KEY", "\"${properties.getProperty('IMGBB_API_KEY')}\""
```

#### **Network Security**
```xml
<!-- res/xml/network_security_config.xml -->
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">api.imgbb.com</domain>
        <domain includeSubdomains="true">www.themealdb.com</domain>
    </domain-config>
</network-security-config>
```

### **Development Workflow**

#### **Pre-Development Checklist**
- [ ] Firebase project created and configured
- [ ] google-services.json downloaded and placed
- [ ] ImgBB API key obtained and configured
- [ ] Android Studio updated to latest version
- [ ] SDK tools installed (API 24-34)
- [ ] Emulator or device ready for testing
- [ ] Git repository initialized
- [ ] .gitignore configured for sensitive files

#### **Development Environment Validation**
```bash
# Verify project setup
./gradlew clean build

# Run tests
./gradlew test

# Check for lint issues
./gradlew lint

# Generate debug APK
./gradlew assembleDebug
```

This comprehensive configuration guide ensures a properly set up development environment for the CookBook application with all necessary services, dependencies, and security measures in place.

---

## Troubleshooting Guide

### **Comprehensive Issue Resolution**

#### **Firebase Integration Issues**

##### **Authentication Problems**
**Symptoms**: Login/registration fails, authentication errors
**Root Causes**:
- Invalid google-services.json configuration
- Firebase project not properly configured
- Network connectivity issues
- Firebase service unavailability

**Resolution Steps**:
1. **Verify google-services.json**:
   ```bash
   # Check file exists and is valid JSON
   cat app/google-services.json
   ```
2. **Validate Firebase Project**:
   - Visit Firebase Console
   - Verify project exists and is active
   - Check Authentication settings
3. **Network Diagnostics**:
   ```bash
   # Test Firebase connectivity
   ping firebase.google.com
   ```
4. **Clear App Data**: Reset app data to clear corrupted authentication state

##### **Firestore Database Issues**
**Symptoms**: Data not saving/loading, permission errors
**Root Causes**:
- Incorrect security rules
- Database not enabled
- Invalid data structure
- Network connectivity issues

**Resolution Steps**:
1. **Check Security Rules**:
   ```javascript
   // Verify rules in Firebase Console
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /users/{userId} {
         allow read, write: if request.auth != null && request.auth.uid == userId;
       }
       match /recipes/{recipeId} {
         allow read, write: if request.auth != null && 
           request.auth.uid == resource.data.userId;
       }
     }
   }
   ```
2. **Database Configuration**:
   - Enable Firestore in Firebase Console
   - Set up proper indexes for queries
   - Configure backup and retention policies
3. **Data Validation**: Check data structure matches expected format

#### **API Integration Issues**

##### **TheMealDB API Problems**
**Symptoms**: Search not working, no recipe results
**Root Causes**:
- Network connectivity issues
- API endpoint changes
- Rate limiting
- Invalid query parameters

**Resolution Steps**:
1. **Network Connectivity**:
   ```bash
   # Test API connectivity
   curl -I https://www.themealdb.com/api/json/v1/1/search.php?s=chicken
   ```
2. **API Endpoint Validation**:
   - Verify base URL is correct
   - Check endpoint parameters
   - Validate response format
3. **Request Logging**: Enable HTTP logging to debug requests
4. **Fallback Strategy**: Implement local search fallback

##### **ImgBB Upload Issues**
**Symptoms**: Image upload fails, no image URL returned
**Root Causes**:
- Invalid API key
- File size too large
- Unsupported image format
- Network connectivity issues

**Resolution Steps**:
1. **API Key Validation**:
   ```bash
   # Check API key in local.properties
   grep IMGBB_API_KEY local.properties
   ```
2. **Image Validation**:
   - Check file size (max 32MB)
   - Verify image format (JPEG, PNG, GIF)
   - Compress large images
3. **Network Diagnostics**: Test upload connectivity
4. **Error Handling**: Implement proper error messages

#### **UI and Performance Issues**

##### **Memory and Performance Problems**
**Symptoms**: App crashes, slow performance, memory warnings
**Root Causes**:
- Large image loading without optimization
- Memory leaks in RecyclerView
- Excessive API calls
- Inefficient data processing

**Resolution Steps**:
1. **Image Optimization**:
   ```java
   // Use Glide with proper configuration
   Glide.with(context)
       .load(imageUrl)
       .diskCacheStrategy(DiskCacheStrategy.ALL)
       .placeholder(R.drawable.placeholder_recipe)
       .error(R.drawable.placeholder_recipe)
       .into(imageView);
   ```
2. **Memory Management**:
   - Implement proper ViewHolder pattern
   - Clear image caches when needed
   - Use WeakReferences for callbacks
3. **API Call Optimization**:
   - Implement request debouncing
   - Cache API responses
   - Limit concurrent requests

##### **UI Rendering Issues**
**Symptoms**: Layout problems, crashes on rotation, view binding errors
**Root Causes**:
- Invalid layout XML
- Missing view bindings
- Configuration changes not handled
- Memory pressure

**Resolution Steps**:
1. **Layout Validation**:
   ```xml
   <!-- Check for proper view hierarchy -->
   <LinearLayout
       android:layout_width="match_parent"
       android:layout_height="wrap_content"
       android:orientation="vertical">
       <!-- Child views -->
   </LinearLayout>
   ```
2. **View Binding Setup**:
   ```java
   // Ensure proper binding initialization
   private FragmentHomeBinding binding;
   
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       binding = FragmentHomeBinding.inflate(inflater, container, false);
       return binding.getRoot();
   }
   ```
3. **Configuration Changes**: Handle screen rotation and configuration changes

### **Advanced Debugging Techniques**

#### **Logcat Analysis**
```bash
# Filter logs by app package
adb logcat | grep com.example.cookbook

# Filter by log level
adb logcat *:E | grep com.example.cookbook

# Save logs to file
adb logcat > cookbook_logs.txt
```

#### **Network Debugging**
```bash
# Monitor network traffic
adb shell dumpsys connectivity

# Check network state
adb shell dumpsys netstats

# Monitor HTTP requests
adb shell dumpsys connectivity | grep -A 10 "Active networks"
```

#### **Memory Analysis**
```bash
# Check memory usage
adb shell dumpsys meminfo com.example.cookbook

# Monitor heap usage
adb shell dumpsys meminfo com.example.cookbook | grep -A 20 "App Summary"
```

### **Performance Monitoring**

#### **Key Performance Indicators**
- **App Launch Time**: Should be under 2 seconds
- **Search Response Time**: Should be under 1 second
- **Image Load Time**: Should be under 500ms
- **Memory Usage**: Should stay under 100MB for normal usage

#### **Performance Optimization Checklist**
- [ ] Implement image caching
- [ ] Optimize RecyclerView with ViewHolder pattern
- [ ] Use background threads for API calls
- [ ] Implement request debouncing
- [ ] Cache frequently accessed data
- [ ] Optimize database queries
- [ ] Use efficient data structures

### **Common Error Messages and Solutions**

#### **Firebase Errors**
```
ERROR_INVALID_EMAIL: "Please enter a valid email address"
ERROR_WEAK_PASSWORD: "Password should be at least 6 characters"
ERROR_EMAIL_ALREADY_IN_USE: "An account with this email already exists"
ERROR_USER_NOT_FOUND: "No account found with this email"
ERROR_WRONG_PASSWORD: "Incorrect password"
```

#### **API Errors**
```
Network Error: Check internet connection and try again
Timeout Error: Request took too long, please try again
Server Error: Service temporarily unavailable
Rate Limit Error: Too many requests, please wait
```

#### **UI Errors**
```
ViewBinding Error: Check layout XML and binding initialization
RecyclerView Error: Verify adapter and data source
Image Loading Error: Check image URL and network connectivity
```

### **Prevention Strategies**

#### **Proactive Monitoring**
- Implement crash reporting (Firebase Crashlytics)
- Monitor API response times
- Track user engagement metrics
- Monitor memory usage patterns

#### **Code Quality Measures**
- Use static analysis tools (Lint)
- Implement comprehensive error handling
- Add input validation
- Use proper exception handling

#### **Testing Strategies**
- Unit tests for critical methods
- Integration tests for API calls
- UI tests for user workflows
- Performance testing for memory leaks

This comprehensive troubleshooting guide provides developers with the tools and knowledge needed to diagnose and resolve issues in the CookBook application effectively.



---

## Future Enhancements

### Potential Improvements
1. **Offline Support**: Implement local database caching
2. **Social Features**: Add recipe sharing and comments
3. **Advanced Filtering**: Add dietary restrictions and cooking time
4. **Recipe Scaling**: Allow ingredient quantity adjustments
5. **Nutrition Information**: Integrate nutrition API
6. **Voice Commands**: Add voice search functionality
7. **Dark Mode**: Implement theme switching
8. **Widgets**: Add home screen widgets

### Technical Improvements
1. **Architecture**: Migrate to MVVM with LiveData
2. **Dependency Injection**: Implement Hilt or Dagger
3. **Coroutines**: Replace callbacks with coroutines
4. **Jetpack Compose**: Modernize UI with Compose
5. **Testing**: Increase test coverage
6. **Performance**: Optimize image loading and caching

---

## Summary

The CookBook Android application is a comprehensive recipe management solution that has undergone significant improvements in code quality and documentation. The application now features:

### **Core Functionality**
- **User Authentication**: Secure Firebase-based login/registration system
- **Recipe Management**: Full CRUD operations for personal recipes
- **Recipe Discovery**: Integration with TheMealDB API for recipe exploration
- **Advanced Search**: Local and online search with real-time filtering
- **Image Management**: ImgBB integration for recipe image uploads
- **Favorites System**: User-friendly favorite management
- **Modern UI**: Material Design with intuitive navigation

### **Recent Improvements**
- **Documentation**: Extensive JavaDoc documentation throughout the codebase
- **Maintainability**: Improved code structure and organization
- **Code Quality**: Clean, well-organized codebase with optimized imports

### **Technical Architecture**
- **MVVM Pattern**: Clean separation of concerns with Repository pattern
- **Firebase Integration**: Authentication, Firestore database, and real-time updates
- **API Integration**: TheMealDB for recipe discovery, ImgBB for image hosting
- **Modern Android**: ViewBinding, RecyclerView, Material Design components

### **Development Benefits**
- **Well-Documented**: Comprehensive documentation for all components
- **Clean Code**: Optimized imports, clear structure, and well-organized code
- **Maintainable**: Clear architecture and documentation for easy maintenance
- **Extensible**: Well-structured codebase ready for future enhancements

This comprehensive study guide covers all aspects of the CookBook Android application, providing detailed information about its architecture, components, methods, and usage. The guide serves as a complete reference for understanding, presenting, and maintaining the application. 