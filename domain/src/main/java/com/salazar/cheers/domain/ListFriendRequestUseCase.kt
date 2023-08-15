package com.salazar.cheers.domain

import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.data.friendship.FriendshipRepository
import com.salazar.cheers.data.user.UserRepository
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListFriendRequestUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val friendshipRepository: FriendshipRepository,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(): Flow<List<UserItem>> = withContext(ioDispatcher) {
        friendshipRepository.listFriendRequest()
            .combine(userRepository.listUserItems()) { friendRequests, users ->
                friendRequests.mapNotNull { friendRequest ->
                    val user = users.find { it.id == friendRequest.id }
                    user
                }
            }
    }
}