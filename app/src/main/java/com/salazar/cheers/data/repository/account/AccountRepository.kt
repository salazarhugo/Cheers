package com.salazar.cheers.data.repository.account

import cheers.account.v1.Account

/**
 * Interface for the Activity data layer.
 */
interface AccountRepository {

    /**
     * Get coins of current user.
     */
    suspend fun getAccount(): Result<Account>
}