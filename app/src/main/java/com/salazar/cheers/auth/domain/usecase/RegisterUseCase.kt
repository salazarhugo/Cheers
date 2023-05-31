package com.salazar.cheers.auth.domain.usecase

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.auth.data.AuthRepository
import com.salazar.common.util.Resource
import com.salazar.cheers.core.data.datastore.StoreUserEmail
import com.salazar.common.di.IODispatcher
import com.salazar.cheers.data.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val storeUserEmail: StoreUserEmail,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        username: String,
    ): Resource<Unit> = withContext(ioDispatcher) {
        val auth = Firebase.auth

        if (auth.currentUser == null)
            return@withContext Resource.Error("Not signed in")

        val email = storeUserEmail.getEmail.firstOrNull()
            ?: return@withContext Resource.Error("No stored email")

        val result = userRepository.createUser(
            username = username,
            email = email,
        )

        if (result.isFailure)
            return@withContext Resource.Error("Couldn't create user")

        return@withContext Resource.Success(Unit)
    }
}
