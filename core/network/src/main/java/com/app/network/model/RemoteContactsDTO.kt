package com.app.network.model

import com.google.gson.annotations.SerializedName

data class RemoteContactsDTO(
    @SerializedName("users") val users: List<UserDTO>,
    @SerializedName("total") val total: Int,
    @SerializedName("skip") val skip: Int,
    @SerializedName("limit") val limit: Int
)

data class UserDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("image") val image: String
)
