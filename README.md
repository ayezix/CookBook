# CookBook Android App

A modern Android application for managing and sharing recipes, built with Java and Firebase.

## Features

- **Recipe Management**
  - Create, read, update, and delete recipes
  - Upload recipe images
  - Categorize recipes
  - Add ingredients with quantities and units
  - Detailed cooking instructions

- **User Features**
  - User authentication (email/password)
  - Favorite recipes
  - Search functionality
  - Share recipes with others

- **Recipe Categories**
  - Breakfast
  - Lunch
  - Dinner
  - Desserts
  - Snacks
  - Vegan
  - Vegetarian
  - Gluten-free

## Technical Stack

- **Frontend**
  - Java
  - Android SDK
  - Material Design Components
  - ViewBinding
  - Glide (Image loading)

- **Backend**
  - Firebase Authentication
  - Firebase Firestore
  - Firebase Storage
  - TheMealDB API (Recipe search)
  - ImgBB API (Image hosting)

## Setup Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/ayezix/CookBook.git
   ```

2. Open the project in Android Studio

3. Create a `local.properties` file in the root directory with your API keys:
   ```properties
   IMGBB_API_KEY=your_imgbb_api_key
   ```

4. Build and run the project

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/cookbook/
│   │   │   ├── api/           # API related classes
│   │   │   ├── model/         # Data models
│   │   │   ├── ui/            # UI components
│   │   │   │   ├── home/      # Home screen
│   │   │   │   ├── recipe/    # Recipe screens
│   │   │   │   └── favorites/ # Favorites screen
│   │   │   └── util/          # Utility classes
│   │   └── res/               # Resources
│   └── test/                  # Test files
```

## Contributing

1. Fork the repository
2. Create a new branch for your feature
3. Make your changes
4. Submit a pull request

## Security

- API keys are stored securely in `local.properties`
- User authentication is handled through Firebase
- Image uploads are processed through secure APIs
- TheMealDB API is free to use and doesn't require authentication

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact

For any questions or suggestions, please open an issue in the GitHub repository. 