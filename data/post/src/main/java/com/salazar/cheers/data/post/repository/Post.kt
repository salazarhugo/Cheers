package com.salazar.cheers.data.post.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

object PostType {
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
    const val VIDEO = "VIDEO"
}

@Entity(
    tableName = "posts",
    indices = [
        Index(value = ["authorId"])
    ]
)
data class Post(
    @PrimaryKey
    @ColumnInfo(name = "postId")
    val id: String = "",
    val authorId: String = "",
    val isAuthor: Boolean = false,
    val caption: String = "",
    val username: String = "",
    val verified: Boolean = false,
    val profilePictureUrl: String = "",
    val createTime: Long = 0,
    val relativeTime: String = "",
    val likes: Int = 0,
    val liked: Boolean = false,
    val drinkId: Int = 0,
    val drinkName: String = "",
    val drinkPicture: String = "",
    val comments: Int = 0,
    val shares: Int = 0,
    val privacy: String = "",
    val photos: List<String> = emptyList(),
    val videoUrl: String = "",
    val videoThumbnailUrl: String = "",
    val drunkenness: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationName: String = "",
    val notify: Boolean = true,
    val tagUsersId: List<String> = emptyList(),
    val type: String = PostType.TEXT,
    val accountId: String = "",
    val lastCommentText: String = "",
    val lastCommentUsername: String = "",
    val lastCommentCreateTime: Long = 0,
)