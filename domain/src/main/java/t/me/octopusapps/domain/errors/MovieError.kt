package t.me.octopusapps.domain.errors

public sealed class MovieError(
    cause: Throwable? = null
) : Exception(cause) {

    public class Network public constructor(
        cause: Throwable? = null
    ) : MovieError(cause = cause)

    public class Remote public constructor(
        public val code: Int,
        cause: Throwable? = null
    ) : MovieError(cause = cause)

    public class NotFound public constructor(
        public val movieId: Int? = null,
        cause: Throwable? = null
    ) : MovieError(cause = cause)

    public class Storage public constructor(
        cause: Throwable? = null
    ) : MovieError(cause = cause)

    public class Unknown public constructor(
        cause: Throwable? = null
    ) : MovieError(cause = cause)
}
