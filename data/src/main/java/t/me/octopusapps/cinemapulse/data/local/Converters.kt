package t.me.octopusapps.cinemapulse.data.local

import androidx.room.TypeConverter

internal class Converters {

    @TypeConverter
    fun fromGenreIds(genreIds: List<Int>?): String? =
        genreIds?.joinToString(",")

    @TypeConverter
    fun toGenreIds(value: String?): List<Int>? =
        value?.split(",")?.mapNotNull { it.toIntOrNull() }
}