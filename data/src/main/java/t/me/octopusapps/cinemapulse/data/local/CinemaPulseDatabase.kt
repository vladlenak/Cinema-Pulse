package t.me.octopusapps.cinemapulse.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import t.me.octopusapps.cinemapulse.data.local.dao.MovieDao
import t.me.octopusapps.cinemapulse.data.local.entities.MovieEntity

@Database(
    entities = [MovieEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
internal abstract class CinemaPulseDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}