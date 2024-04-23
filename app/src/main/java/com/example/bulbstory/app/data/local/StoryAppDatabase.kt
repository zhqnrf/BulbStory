package com.example.bulbstory.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bulbstory.app.data.local.dao.DaoStory
import com.example.bulbstory.app.data.local.entity.StoryEntity

@Database(
    entities =[StoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StoryAppDatabase: RoomDatabase() {

    abstract fun getStoryDao(): DaoStory

}