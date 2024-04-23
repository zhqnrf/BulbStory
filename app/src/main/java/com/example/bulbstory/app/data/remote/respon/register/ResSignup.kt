package com.example.bulbstory.app.data.remote.respon.register

import com.google.gson.annotations.SerializedName

data class ResSignup(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,
)
