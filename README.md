# WeatherApp

## Overview
WeatherApp is an Android application that provides real-time weather information based on the user's current location. The app fetches weather data using the OpenWeather API and displays it in a user-friendly interface. The app is designed using the MVVM architecture and utilizes several libraries and tools to ensure a smooth and efficient user experience.

## Features
- **Real-time Weather Data**: Fetches and displays the current weather based on the user's location.
- **Detailed Weather Information**: Shows detailed weather data including:
  - Climate
  - Temperature
  - Wind Speed
  - Minimum Temperature
  - Maximum Temperature
  - Sunrise Time
  - Sunset Time
  - Humidity
- **Offline Support**: Stores weather data locally using Room Database for offline access.
- **Location Services**: Automatically detects the user's location using location services.
- **Permissions Management**: Utilizes Dexter for handling runtime permissions.
- **User-Friendly Interface**: Designed with a clean and intuitive UI.

## Libraries and Tools Used
- **OpenWeather API**: For fetching weather data.
- **Retrofit**: For network requests.
- **Gson**: For JSON parsing.
- **Dexter**: For managing runtime permissions.
- **Location Services**: For retrieving the user's current location.
- **MVVM Architecture**: For a clean and maintainable code structure.
- **Room Database**: For storing weather data locally.

## Getting Started
### Prerequisites
- Android Studio
- An OpenWeather API key

### Installation
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/weatherapp.git
2. **Open in Android Studio**: Open the cloned project in Android Studio.
3. **Add API Key**:
* Obtain your API key from OpenWeather.
* Add your API key in the gradle.properties file:
 OpenWeatherApiKey="YOUR_API_KEY"
4. **Build the Project**: Sync the project with Gradle files and build the project.

  ## Project Structure
- `MainActivity.kt`: The main activity that initializes the app and handles navigation.
- `WeatherViewModel.kt`: The ViewModel that handles data logic and interacts with the repository.
- `WeatherRepository.kt`: The repository that manages data from both the API and the local database.
- `WeatherDao.kt`: The Data Access Object (DAO) for Room Database.
- `WeatherDatabase.kt`: The Room Database setup.
- `LocationService.kt`: Handles location services to get the user's current location.
- `WeatherApiService.kt`: The Retrofit interface for the OpenWeather API.
- `models/`: Contains data models for the weather data.
- `views/`: Contains the UI components and layouts.

## Usage
1. **Launch the App**: Open the app on your Android device or emulator.
2. **Grant Permissions**: Grant location permissions when prompted.
3. **View Weather Data**: The app will automatically detect your location and display the current weather data.

## Contact
For any inquiries or issues, please contact kanhaiyashukla0.01@gmail.com

## Screenshots

![wi2](https://github.com/KanhaiyaKumarShukla/weather-app/assets/148223010/54fbed4d-b58b-44b1-a3d9-54b231c78a80)
![wi1](https://github.com/KanhaiyaKumarShukla/weather-app/assets/148223010/69ad58be-0c93-42aa-bbfd-0ac3157ef017)


