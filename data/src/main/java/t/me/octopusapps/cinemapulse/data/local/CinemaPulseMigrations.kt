package t.me.octopusapps.cinemapulse.data.local

import androidx.room.migration.Migration

internal object CinemaPulseMigrations {

    val MIGRATION_2_3 = Migration(2, 3) { database ->
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `favorite_movies` (
                `id` INTEGER NOT NULL,
                `title` TEXT NOT NULL,
                `overview` TEXT NOT NULL,
                `popularity` REAL NOT NULL,
                `releaseDate` TEXT NOT NULL,
                `voteAverage` REAL NOT NULL,
                `voteCount` INTEGER NOT NULL,
                `posterPath` TEXT,
                `backdropPath` TEXT,
                `genreIds` TEXT,
                `adult` INTEGER NOT NULL,
                `originalLanguage` TEXT NOT NULL,
                `originalTitle` TEXT NOT NULL,
                `video` INTEGER NOT NULL,
                `addedAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent(),
        )
    }

    val MIGRATION_3_4 = Migration(3, 4) { database ->
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `watched_movies` (
                `id` INTEGER NOT NULL,
                `title` TEXT NOT NULL,
                `overview` TEXT NOT NULL,
                `popularity` REAL NOT NULL,
                `releaseDate` TEXT NOT NULL,
                `voteAverage` REAL NOT NULL,
                `voteCount` INTEGER NOT NULL,
                `posterPath` TEXT,
                `backdropPath` TEXT,
                `genreIds` TEXT,
                `adult` INTEGER NOT NULL,
                `originalLanguage` TEXT NOT NULL,
                `originalTitle` TEXT NOT NULL,
                `video` INTEGER NOT NULL,
                `watchedAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent(),
        )
    }

    val MIGRATION_4_5 = Migration(4, 5) { database ->
        database.execSQL(
            """
            DELETE FROM `movies`
            WHERE `rowId` NOT IN (
                SELECT MAX(`rowId`)
                FROM `movies`
                GROUP BY `category`, `page`, `id`
            )
            """.trimIndent(),
        )
        database.execSQL(
            """
            CREATE UNIQUE INDEX IF NOT EXISTS `index_movies_category_page_id`
            ON `movies` (`category`, `page`, `id`)
            """.trimIndent(),
        )
    }

    val MIGRATION_5_6 = Migration(5, 6) { database ->
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `movie_details` (
                `id` INTEGER NOT NULL,
                `title` TEXT NOT NULL,
                `overview` TEXT NOT NULL,
                `popularity` REAL NOT NULL,
                `releaseDate` TEXT NOT NULL,
                `voteAverage` REAL NOT NULL,
                `voteCount` INTEGER NOT NULL,
                `posterPath` TEXT,
                `backdropPath` TEXT,
                `genreIds` TEXT,
                `adult` INTEGER NOT NULL,
                `originalLanguage` TEXT NOT NULL,
                `originalTitle` TEXT NOT NULL,
                `video` INTEGER NOT NULL,
                `cachedAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent(),
        )
        database.execSQL(
            """
            INSERT OR REPLACE INTO `movie_details` (
                `id`, `title`, `overview`, `popularity`, `releaseDate`,
                `voteAverage`, `voteCount`, `posterPath`, `backdropPath`,
                `genreIds`, `adult`, `originalLanguage`, `originalTitle`,
                `video`, `cachedAt`
            )
            SELECT
                `id`, `title`, `overview`, `popularity`, `releaseDate`,
                `voteAverage`, `voteCount`, `posterPath`, `backdropPath`,
                `genreIds`, `adult`, `originalLanguage`, `originalTitle`,
                `video`, `cachedAt`
            FROM `movies`
            WHERE `page` = 0
            ORDER BY `cachedAt`, `rowId`
            """.trimIndent(),
        )
        database.execSQL("DELETE FROM `movies` WHERE `page` = 0")
    }

    val ALL = arrayOf(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
}
