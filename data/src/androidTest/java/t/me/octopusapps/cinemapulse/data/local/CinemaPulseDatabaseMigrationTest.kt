package t.me.octopusapps.cinemapulse.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import androidx.room.testing.MigrationTestHelper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.sqlite.db.SupportSQLiteDatabase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class CinemaPulseDatabaseMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        CinemaPulseDatabase::class.java
    )

    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @After
    fun tearDown() {
        context.deleteDatabase(TEST_DB)
    }

    @Test
    fun migrate2To6_preservesCachedMoviesAndValidatesSchema() {
        createVersion2DatabaseWithCachedMovie()

        helper.runMigrationsAndValidate(
            TEST_DB,
            6,
            true,
            *CinemaPulseMigrations.ALL
        ).use { database ->
            assertEquals("Inception", findCachedMovieTitle(database, movieId = 1))
            assertDuplicateInsertFails(database)
        }
    }

    @Test
    fun migrate4To6_removesDuplicateCachedMoviesAndCreatesUniqueIndex() {
        createVersion4DatabaseWithDuplicateCachedMovies()

        helper.runMigrationsAndValidate(
            TEST_DB,
            6,
            true,
            *CinemaPulseMigrations.ALL
        ).use { database ->
            assertEquals(1, countCachedMovieRows(database, movieId = 1))
            assertEquals("Inception Updated", findCachedMovieTitle(database, movieId = 1))
            assertDuplicateInsertFails(database)
        }
    }

    @Test
    fun migrate5To6_movesCachedDetailsToDedicatedTableAndKeepsListCache() {
        createVersion5DatabaseWithMixedDetailAndListCache()

        helper.runMigrationsAndValidate(
            TEST_DB,
            6,
            true,
            *CinemaPulseMigrations.ALL
        ).use { database ->
            assertEquals("Inception Details", findCachedMovieDetailsTitle(database, movieId = 1))
            assertEquals(0, countSyntheticListCacheRows(database))
            assertEquals("Inception List", findCachedMovieTitle(database, movieId = 1))
        }
    }

    private fun createVersion2DatabaseWithCachedMovie() {
        context.deleteDatabase(TEST_DB)
        helper.createDatabase(TEST_DB, 2).use { database ->
            database.insert(
                "movies",
                SQLiteDatabase.CONFLICT_NONE,
                cachedMovieValues(rowId = 1, title = "Inception")
            )
        }
    }

    private fun createVersion4DatabaseWithDuplicateCachedMovies() {
        context.deleteDatabase(TEST_DB)
        helper.createDatabase(TEST_DB, 4).use { database ->
            database.insert(
                "movies",
                SQLiteDatabase.CONFLICT_NONE,
                cachedMovieValues(rowId = 1, title = "Inception")
            )
            database.insert(
                "movies",
                SQLiteDatabase.CONFLICT_NONE,
                cachedMovieValues(rowId = 2, title = "Inception Updated")
            )
            database.insert(
                "movies",
                SQLiteDatabase.CONFLICT_NONE,
                cachedMovieValues(rowId = 3, movieId = 2, title = "Interstellar")
            )
        }
    }

    private fun createVersion5DatabaseWithMixedDetailAndListCache() {
        context.deleteDatabase(TEST_DB)
        helper.createDatabase(TEST_DB, 5).use { database ->
            database.insert(
                "movies",
                SQLiteDatabase.CONFLICT_NONE,
                cachedMovieValues(
                    rowId = 1,
                    title = "Inception Details",
                    page = 0,
                    totalPages = 0
                )
            )
            database.insert(
                "movies",
                SQLiteDatabase.CONFLICT_NONE,
                cachedMovieValues(
                    rowId = 2,
                    title = "Inception List",
                    page = 1,
                    totalPages = 5
                )
            )
        }
    }

    private fun cachedMovieValues(
        rowId: Int,
        movieId: Int = 1,
        title: String,
        page: Int = 1,
        totalPages: Int = 5
    ): ContentValues = ContentValues().apply {
        put("rowId", rowId)
        put("id", movieId)
        put("category", "POPULAR")
        put("title", title)
        put("overview", "Overview")
        put("popularity", 10.0)
        put("releaseDate", "2010-07-16")
        put("voteAverage", 8.8)
        put("voteCount", 1000)
        putNull("posterPath")
        putNull("backdropPath")
        put("genreIds", "28")
        put("adult", 0)
        put("originalLanguage", "en")
        put("originalTitle", title)
        put("video", 0)
        put("page", page)
        put("totalPages", totalPages)
        put("cachedAt", rowId.toLong())
    }

    private fun countCachedMovieRows(database: SupportSQLiteDatabase, movieId: Int): Int =
        database.query(
            "SELECT COUNT(*) FROM `movies` WHERE `category` = 'POPULAR' AND `page` = 1 AND `id` = $movieId"
        ).use { cursor ->
            cursor.moveToFirst()
            cursor.getInt(0)
        }

    private fun findCachedMovieTitle(database: SupportSQLiteDatabase, movieId: Int): String =
        database.query(
            "SELECT `title` FROM `movies` WHERE `category` = 'POPULAR' AND `page` = 1 AND `id` = $movieId"
        ).use { cursor ->
            cursor.moveToFirst()
            cursor.getString(0)
        }

    private fun findCachedMovieDetailsTitle(database: SupportSQLiteDatabase, movieId: Int): String =
        database.query(
            "SELECT `title` FROM `movie_details` WHERE `id` = $movieId"
        ).use { cursor ->
            cursor.moveToFirst()
            cursor.getString(0)
        }

    private fun countSyntheticListCacheRows(database: SupportSQLiteDatabase): Int =
        database.query("SELECT COUNT(*) FROM `movies` WHERE `page` = 0").use { cursor ->
            cursor.moveToFirst()
            cursor.getInt(0)
        }

    private fun assertDuplicateInsertFails(database: SupportSQLiteDatabase) {
        assertThrows(SQLiteConstraintException::class.java) {
            database.execSQL(
                """
                INSERT INTO `movies` (
                    `id`, `category`, `title`, `overview`, `popularity`, `releaseDate`,
                    `voteAverage`, `voteCount`, `posterPath`, `backdropPath`, `genreIds`,
                    `adult`, `originalLanguage`, `originalTitle`, `video`, `page`,
                    `totalPages`, `cachedAt`
                ) VALUES (
                    1, 'POPULAR', 'Duplicate', 'Overview', 10.0, '2010-07-16',
                    8.8, 1000, NULL, NULL, '28',
                    0, 'en', 'Duplicate', 0, 1,
                    5, 99
                )
                """.trimIndent()
            )
        }
    }

    private companion object {
        const val TEST_DB = "cinema-pulse-migration-test.db"
    }
}
