package com.example.bulbstory.app.data.remote.respon.login

import com.google.gson.annotations.SerializedName

data class Result(
    @field:SerializedName("userId")
    val userId: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("token")
    val token: String,
)
