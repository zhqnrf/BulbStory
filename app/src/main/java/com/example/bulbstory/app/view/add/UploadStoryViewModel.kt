package com.example.bulbstory.app.view.add

import androidx.lifecycle.ViewModel
import com.example.bulbstory.app.data.DataRepository
import java.io.File

class UploadStoryViewModel constructor(private val dataRepository: DataRepository) : ViewModel() {

    suspend fun uploadStory(auth: String, description: String, file: File) =
        dataRepository.uploadStory(auth, description, file)
}