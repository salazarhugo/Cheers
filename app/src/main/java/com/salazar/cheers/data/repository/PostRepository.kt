package com.salazar.cheers.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.salazar.cheers.backend.CoreService
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.paging.PostRemoteMediator
import com.salazar.cheers.internal.Comment
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.Privacy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val coreService: CoreService,
    private val service: Neo4jService,
    private val database: CheersDatabase
) {
    val postDao = database.postDao()

    fun getPosts(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true,
                initialLoadSize = NETWORK_PAGE_SIZE * 1, // default: pageSize * 3
            ),
            remoteMediator = PostRemoteMediator(database = database, service = coreService),
        ) {
            postDao.pagingSourceFeed()
        }.flow
    }

    suspend fun profilePost(userIdOrUsername: String): Flow<List<Post>> {
        return flow {
            val posts = postDao.getUserPosts(userIdOrUsername)

            emit(posts)

            val remoteUserPosts = try {
                val response = coreService.getUserPosts(userIdOrUsername, 0, 20)
                response
            } catch (e: Exception) {
                null
            }

            remoteUserPosts?.let {
                postDao.insertAll(it)
                emit(postDao.getUserPosts(userIdOrUsername))
            }
        }
    }

    suspend fun likePost(postId: String) {
        try {
            coreService.likePost(postId = postId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun unlikePost(postId: String) {
        try {
            coreService.unlikePost(postId = postId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun commentPost(comment: Comment) {
        try {
            coreService.postComment(comment = comment)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun addPost(post: Post) {
        try {
            coreService.createPost(post = post)
            postDao.insert(post)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getUserPosts(): List<Post> {
        try {
            val posts = coreService.getPosts()
            postDao.insertAll(posts)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return postDao.getPosts()
    }

    suspend fun getPostsWithAuthorId(authorId: String): List<Post> {
        return postDao.getPostsWithAuthorId(authorId = authorId)
    }

    suspend fun deletePost(postId: String) {
        try {
            coreService.deletePost(postId = postId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getPostMembers(postId: String) = withContext(Dispatchers.IO) {
        return@withContext try {
            coreService.postMembers(postId = postId, pageSize = 20, page = 0)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getMapPosts(privacy: Privacy): List<Post> {
        return postDao.getMapPosts(privacy = privacy)
//            .map {
//            it.copy(tagUsers = postDao.getPostUsers(it.post.tagUsersId))
//        }
    }

    suspend fun postFlow(postId: String) = postDao.postFlow(postId = postId)

    suspend fun getPost(postId: String): Post {
        val post = postDao.getPost(postId = postId)
        return post.copy()
    }

    suspend fun toggleLike(post: Post) = withContext(Dispatchers.IO) {
        val likes = if (post.liked) post.likes - 1 else post.likes + 1

        postDao.update(post.copy(liked = !post.liked, likes = likes))

        if (post.liked)
            unlikePost(postId = post.id)
        else
            likePost(postId = post.id)
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }
}