package t.me.octopusapps.domain.errors

public sealed class MovieError(
    public val userMessage: String,
    cause: Throwable? = null
) : Exception(userMessage, cause) {

    public class Network public constructor(
        cause: Throwable? = null
    ) : MovieError(
        userMessage = "Network error. Please check your connection.",
        cause = cause
    )

    public class Remote public constructor(
        public val code: Int,
        cause: Throwable? = null
    ) : MovieError(
        userMessage = "Movie service error. Please try again later.",
        cause = cause
    )

    public class NotFound public constructor(
        public val movieId: Int? = null,
        cause: Throwable? = null
    ) : MovieError(
        userMessage = "Movie not found.",
        cause = cause
    )

    public class Storage public constructor(
        cause: Throwable? = null
    ) : MovieError(
        userMessage = "Local storage error. Please try again.",
        cause = cause
    )

    public class Unknown public constructor(
        cause: Throwable? = null
    ) : MovieError(
        userMessage = "An unexpected error occurred.",
        cause = cause
    )
}
