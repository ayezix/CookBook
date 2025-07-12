# CookBook - Android Recipe Management App

A comprehensive Android application for managing, discovering, and sharing recipes. Built with modern Android development practices and Firebase integration.

## 🍳 Features

### Core Functionality
- **Recipe Management**: Create, edit, and delete your own recipes
- **Recipe Discovery**: Search and browse recipes from TheMealDB API
- **Favorites System**: Save and organize your favorite recipes
- **User Authentication**: Secure login and registration system
- **Image Upload**: Add photos to your recipes using ImgBB integration

### Search & Filtering
- **Text Search**: Search recipes by name, ingredients, or instructions
- **Category Filtering**: Filter by recipe categories (Breakfast, Dinner, Dessert, etc.)
- **Cuisine Filtering**: Filter by cuisine/area (Italian, Mexican, Asian, etc.)
- **Ingredient Filtering**: Find recipes containing specific ingredients

### User Experience
- **Modern UI**: Material Design with intuitive navigation
- **Offline Support**: View your saved recipes without internet connection
- **Share Recipes**: Share recipes via WhatsApp or other apps
- **Responsive Design**: Optimized for various screen sizes

## 🛠️ Technical Stack

### Frontend
- **Language**: Java
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Architecture**: MVVM with Repository pattern
- **UI Framework**: Android Views with Data Binding

### Backend & Services
- **Database**: Firebase Firestore
- **Authentication**: Firebase Authentication
- **Image Storage**: ImgBB API
- **External API**: TheMealDB (recipe database)
- **Networking**: Retrofit for API calls

### Libraries & Dependencies
- **Firebase**: Authentication, Firestore, Storage
- **Retrofit**: HTTP client for API calls
- **Glide**: Image loading and caching
- **Material Design**: UI components
- **ViewBinding**: Type-safe view binding

## 📱 Screenshots

*[Screenshots would be added here]*

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK API 24+
- Google account for Firebase setup
- ImgBB API key for image uploads

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/ayezix/CookBook.git
   cd CookBook
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Open the project folder
   - Wait for Gradle sync to complete

3. **Configure Firebase**
   - Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Add an Android app to your Firebase project
   - Download `google-services.json` and place it in the `app/` directory
   - Enable Authentication (Email/Password) in Firebase Console
   - Enable Firestore Database in Firebase Console

4. **Configure ImgBB API**
   - Get an API key from [ImgBB](https://imgbb.com/api)
   - Add the API key to your project configuration

5. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

## 📖 Usage Guide

### Creating an Account
1. Launch the app
2. Tap "Register" on the login screen
3. Enter your email and password
4. Tap "Register" to create your account

### Adding Your Own Recipe
1. Navigate to the Home tab
2. Tap the floating action button (+)
3. Fill in the recipe details:
   - Title
   - Category
   - Ingredients (tap "Add Ingredient" for each)
   - Instructions
   - Optional: Upload an image
4. Tap "Save Recipe"

### Discovering Recipes
1. Use the search bar to find recipes by name
2. Tap the filter button to filter by:
   - Category (Dessert, Side, Starter, Breakfast, Goat)
   - Cuisine/Area
   - Ingredients
3. Tap on any recipe to view full details

### Managing Favorites
1. Tap the heart icon on any recipe to favorite/unfavorite
2. View your favorites in the Favorites tab
3. Remove recipes from favorites by tapping the heart icon again

### Sharing Recipes
1. Open any recipe
2. Tap the share icon
3. Choose your preferred sharing method

## 🏗️ Project Structure

```
app/
├── src/main/
│   ├── java/com/example/cookbook/
│   │   ├── api/                    # API client and models
│   │   ├── data/                   # Data layer
│   │   ├── model/                  # Data models
│   │   ├── ui/                     # UI components
│   │   │   ├── dialog/            # Dialog fragments
│   │   │   ├── favorites/         # Favorites screen
│   │   │   ├── home/              # Home screen
│   │   │   ├── profile/           # Profile screen
│   │   │   └── recipe/            # Recipe management
│   │   └── util/                  # Utility classes
│   └── res/                       # Resources
│       ├── drawable/              # Images and icons
│       ├── layout/                # UI layouts
│       ├── values/                # Strings, colors, themes
│       └── xml/                   # Configuration files
```

## 🔧 Configuration

### Firebase Setup
1. Enable Email/Password authentication
2. Create Firestore database in test mode
3. Set up security rules for your collections

### API Keys
- **ImgBB**: Required for image upload functionality
- **TheMealDB**: Free API, no key required

## 🐛 Known Issues

- Image upload requires internet connection
- Some API recipes may have incomplete data
- Offline mode limited to saved recipes only

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 🙏 Acknowledgments

- [TheMealDB](https://www.themealdb.com/) for providing the recipe API
- [ImgBB](https://imgbb.com/) for image hosting
- [Firebase](https://firebase.google.com/) for backend services
- [Material Design](https://material.io/) for UI guidelines

---

**CookBook** - Making recipe management simple and enjoyable! 🍽️ 