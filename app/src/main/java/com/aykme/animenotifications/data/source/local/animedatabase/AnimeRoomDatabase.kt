package com.aykme.animenotifications.data.source.local.animedatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aykme.animenotifications.domain.model.Anime

@Database(entities = [Anime::class], version = 1, exportSchema = false)
abstract class AnimeRoomDatabase : RoomDatabase() {

    abstract fun animeDao(): AnimeDao

    companion object {

        @Volatile
        private var INSTANCE: AnimeRoomDatabase? = null

        fun getDatabase(context: Context): AnimeRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AnimeRoomDatabase::class.java,
                    "anime_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}