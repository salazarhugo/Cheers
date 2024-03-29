package com.salazar.cheers.domain.list_suggestions

import com.salazar.cheers.data.user.UserRepository
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListSuggestionsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke() = withContext(ioDispatcher) {
        return@withContext userRepository.listSuggestions()
    }
}
