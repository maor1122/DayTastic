package com.example.daytastic.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CalendarEvent::class], version = 2, exportSchema = false)
abstract class EventDatabase : RoomDatabase(){
    abstract fun eventDao(): EventDao

    companion object{
        @Volatile
        private var INSTANCE: EventDatabase? = null

        fun getDatabase(context: Context): EventDatabase{
            val tempInstance = INSTANCE
            if(tempInstance !=null)
                return tempInstance
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventDatabase::class.java,
                    "app_database"
                )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}