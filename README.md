# CookBook - Your Personal Recipe Management App

CookBook is a modern Android application that helps you manage, discover, and share your favorite recipes. Built with Material Design principles and Firebase integration, it provides a seamless cooking experience.

## Features

### 1. User Authentication
- Secure sign-up and login using Firebase Authentication
- Google Sign-In integration
- User profile management

### 2. Recipe Management
- Create and save your own recipes
- Add detailed ingredients and instructions
- Upload recipe images
- Categorize recipes for easy organization

### 3. Recipe Discovery
- Browse through a collection of recipes
- Search functionality to find specific recipes
- Filter recipes by categories or ingredients

### 4. Favorites System
- Save your favorite recipes for quick access
- Organize recipes in custom collections
- Offline access to saved recipes

### 5. Modern UI/UX
- Material Design 3 implementation
- Smooth animations and transitions
- Responsive layout for various screen sizes
- Dark mode support

## Technical Details

### Built With
- Android SDK 34
- Java 21
- Firebase Services
  - Authentication
  - Firestore
  - Storage
- Android Jetpack Components
  - Room: Local database for offline caching and data persistence
  - ViewModel: Manages UI-related data and handles configuration changes
  - LiveData: Observable data holder for reactive UI updates
  - Navigation Component: Handles in-app navigation and deep linking
  - ViewBinding: Type-safe view access
  - Lifecycle Components: Manages Android activity and fragment lifecycles
  - WorkManager: Handles background tasks like image uploads
- Glide for image loading and caching

### Architecture
- MVVM (Model-View-ViewModel) architecture
- Repository pattern for data management
- Clean separation of concerns
- Single Activity Architecture using Navigation Component

## Getting Started

### Prerequisites
- Android Studio Arctic Fox or newer
- JDK 21
- Android SDK 34
- Google Play Services

### Installation
1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Add your `google-services.json` file to the app directory
5. Build and run the application

### Configuration
1. Set up a Firebase project
2. Enable Authentication and Firestore
3. Download and add the `google-services.json` file
4. Configure your Firebase security rules

## Contributing
Contributions are welcome! Please feel free to submit a Pull Request.

## Acknowledgments
- Material Design Components
- Firebase
- Android Jetpack Libraries 
