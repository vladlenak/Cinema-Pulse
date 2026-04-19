# Cinema Pulse
Cinema Pulse is a user-friendly Android application designed for discovering and exploring current movies.

## Key Features:
- Current Movie Ratings: a list of the most popular films, updated in real-time. Each film in the list displays brief information such as title, poster, rating, and release year.
- Search Functionality: with the integrated search feature, users can quickly find movies by keywords such as title, actors, or directors.

## Stack
- Language: Kotlin.
- UI: Compose, Navigation, StateFlow.
- Architecture: Clean Architecture, MVI, Single Activity.
- Multithreading: Coroutines.
- Dependency injection: Hilt.

## How to use
1. Get your API key from The Movie Database:  
   https://www.themoviedb.org/settings/api

2. Add the API key to your `local.properties` file (this file is not committed to VCS):
   apikey=YOUR_TMDB_API_KEY_HERE