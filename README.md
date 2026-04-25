# Cinema Pulse

![CI](https://github.com/vladlenak/Cinema-Pulse/actions/workflows/ci.yml/badge.svg)

An Android app for discovering and exploring movies, powered by [The Movie Database (TMDB)](https://www.themoviedb.org/).

## Features

- **Popular Movies** — infinite scroll with automatic pagination
- **Movie Details** — full info: poster, overview, genres, rating, vote count, release date, language
- **Search** — find movies by title with debounced live search
- **Error handling** — retry button on all error states
- **Network timeouts** — clear error messages instead of infinite loading
- **Cinema theme** — custom dark/light color scheme with gold accent

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material3 |
| Architecture | Clean Architecture, MVI, Single Activity |
| Navigation | Jetpack Navigation Compose |
| DI | Hilt |
| Async | Coroutines, StateFlow |
| Networking | Retrofit, OkHttp |
| Image Loading | Coil |
| Testing | JUnit4, MockK, Coroutines Test |
| CI/CD | GitHub Actions |

## Architecture

```
app/          → UI: screens, components, ViewModels, navigation
data/         → API, mappers, repository implementation
domain/       → use cases, repository interface, models
```

## Getting Started

1. Get a free API key from [TMDB](https://www.themoviedb.org/settings/api)

2. Add it to your `local.properties` (this file is not committed to git):
   ```
   apikey=YOUR_TMDB_API_KEY_HERE
   ```

3. Build and run the project in Android Studio

## Running Tests

```bash
# Domain layer (use cases)
./gradlew :domain:test

# App layer (ViewModels)
./gradlew :app:test

# All tests
./gradlew :domain:test :app:test
```
