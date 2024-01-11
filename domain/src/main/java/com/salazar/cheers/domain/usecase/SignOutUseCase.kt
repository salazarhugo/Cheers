package com.salazar.cheers.domain.usecase

import com.salazar.cheers.data.account.AccountRepository
import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.cheers.data.post.repository.PostRepository
import com.salazar.cheers.data.user.datastore.DataStoreRepository
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val postRepository: PostRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke() = withContext(ioDispatcher) {
        postRepository.clearPosts()
        accountRepository.deleteAccount()
        authRepository.signOut()
        dataStoreRepository.updateIdToken("")
    }
}
