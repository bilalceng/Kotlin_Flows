package com.bilalberekgm.kotlinflows.model

data class ProfileState(
    val profilePicUrl: String? = null,
    val userName: String? = null,
    val description: String? = null,
    val post: List<Post> = emptyList()
)