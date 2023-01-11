package com.salazar.cheers.data.repository.friendship

import cheers.friendship.v1.*
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.db.UserDao
import com.salazar.cheers.data.db.UserItemDao
import com.salazar.cheers.data.db.entities.UserItem
import com.salazar.cheers.data.mapper.toUserItem
import kotlinx.coroutines.flow.*
import javax.inject.Inject


class FriendshipRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val userItemDao: UserItemDao,
    private val service: FriendshipServiceGrpcKt.FriendshipServiceCoroutineStub,
): FriendshipRepository {
    override suspend fun createFriendRequest(userId: String): Result<Unit> {
        val request = CreateFriendRequestRequest.newBuilder()
            .setUserId(userId)
            .build()

        return try {
            service.createFriendRequest(request = request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun listFriend(userId: String): Flow<Resource<List<UserItem>>> = flow {
        emit(Resource.Loading(true))

        val request = ListFriendRequest.newBuilder()
            .setUserId(userId)
            .build()

        val remoteFriendRequests = try {
            val response = service.listFriend(request = request)
            val users = response.itemsList.map {
                it.toUserItem()
            }
            users
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Couldn't refresh friend requests"))
            null
        }

        remoteFriendRequests?.let {
            emit(Resource.Success(it))
        }

        emit(Resource.Loading(false))
    }

    override suspend fun listFriendRequest(): Flow<Resource<List<UserItem>>>
        = flow {
            emit(Resource.Loading(true))

            val request = ListFriendRequestsRequest.newBuilder()
                .setUserId(FirebaseAuth.getInstance().currentUser?.uid)
                .build()

            val remoteFriendRequests = try {
                val response = service.listFriendRequests(request = request)
                val users = response.itemsList.map {
                    it.toUserItem()
                }
                users
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Couldn't refresh friend requests"))
                null
            }

            remoteFriendRequests?.let {
                emit(Resource.Success(it))
            }

            emit(Resource.Loading(false))
        }

    override suspend fun acceptFriendRequest(userId: String): Result<Unit> {
        val request = AcceptFriendRequestRequest.newBuilder()
            .setUserId(userId)
            .build()

        return try {
            service.acceptFriendRequest(request = request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelFriendRequest(userId: String): Result<Unit> {
        val request = DeleteFriendRequestRequest.newBuilder()
            .setUserId(userId)
            .build()

        return try {
            service.deleteFriendRequest(request = request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFriend(userId: String): Result<Unit> {
        val request = DeleteFriendRequest2.newBuilder()
            .setUserId(userId)
            .build()

        return try {
            service.deleteFriend(request = request)
            userDao.deleteFriend(userId = userId)
            userItemDao.deleteFriend(userId = userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}