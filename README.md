# Cinema Pulse

![CI](https://github.com/vladlenak/Cinema-Pulse/actions/workflows/ci.yml/badge.svg)

An Android app for discovering and exploring movies, powered by [The Movie Database (TMDB)](https://www.themoviedb.org/).

## Features

- **Movie Categories** - browse Popular, Top Rated, Upcoming, and Now Playing movies with tab navigation
- **Infinite Scroll** - automatic pagination for all movie categories
- **Movie Details** - full movie information: poster, overview, genres, rating, vote count, release date, and original language
- **Search** - find movies by title with debounced live search
- **Offline Support** - cached category pages and movie details are available when the network fails
- **Error Handling** - retry actions on the main and details screens, plus clear error states for search
- **Network Timeouts** - clear error messages instead of infinite loading
- **Cinema Theme** - custom dark/light color scheme with gold accent

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material3 |
| Architecture | Clean Architecture, MVI-style state management, Single Activity |
| Navigation | Jetpack Navigation Compose |
| Dependency Injection | Hilt |
| Async | Coroutines, StateFlow |
| Networking | Retrofit, OkHttp |
| Local Storage | Room |
| Image Loading | Coil |
| Testing | JUnit4, MockK, Coroutines Test |
| CI/CD | GitHub Actions |

## Architecture

```text
app/          -> UI: screens, components, ViewModels, navigation
data/         -> API, Room database, mappers, repository implementation
domain/       -> use cases, repository interface, models
```

## Project Highlights

- Multi-module project structure with separate `app`, `data`, and `domain` layers
- TMDB API integration with authenticated requests through OkHttp interceptor
- Category-based movie browsing with pagination
- Debounced movie search using Kotlin Flow
- Room cache fallback for movie lists and movie details
- ViewModel-driven UI state with loading, success, and error states
- Unit tests for domain use cases, repository logic, mappers, and ViewModels
- GitHub Actions workflow for lint, unit tests, and debug APK build

## Getting Started

1. Get a free API key from [TMDB](https://www.themoviedb.org/settings/api).

2. Add it to your `local.properties` file:

   ```properties
   apikey=YOUR_TMDB_API_KEY_HERE
   ```

3. Build and run the project in Android Studio.

## Running Tests

```bash
# Domain layer
./gradlew :domain:test

# App layer
./gradlew :app:test

# Data layer
./gradlew :data:test

# All unit tests
./gradlew :domain:test :app:test :data:test
```

## CI

The project uses GitHub Actions to run lint, unit tests, and build a debug APK on pushes and pull requests to `main`.
