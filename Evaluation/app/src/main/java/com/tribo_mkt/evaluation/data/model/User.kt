package com.tribo_mkt.evaluation.data.model

import com.google.gson.annotations.SerializedName


data class User(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String
)
