package com.tribo_mkt.evaluation.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Album(
    @SerializedName("userId") val userId: String,
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String
): Parcelable
