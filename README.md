# GWeather

### Features

- User Registration and Sign-In
- Display current location (City and Country)
- Show current temperature in Celsius
- Display sunrise and sunset times
- Show weather icons provided by OpenWeatherMap API
- Two Tabs:
    1. Current weather
    2. List of weather fetched every time the app opens

### Technologies / Tools Used

- Kotlin
- Hilt (Dependency Injection)
- Retrofit (API calls)
- OpenWeatherMap API (fetching weather data)
- Glide (Weather icons)
- Jetpack Components: ViewModel, LiveData, Navigation, TabLayout
- SQLite 

### How It Works

1. User registers or signs in to access the app.
2. App gets the current location of the user.
3. App fetches current weather data from OpenWeatherMap API.
4. Displays the data on the first tab (current weather).
5. Updates a list of weather on the second tab every time the app opens.

## API Key Configuration

This app requires an OpenWeatherMap API key. To use the app:

1. In the root of the project, create a file named local.properties (if it doesn’t exist).
2. Add the following line: OPENWEATHER_API_KEY=86f96889bfdc33c7576bbc0737f1dece
