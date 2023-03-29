package com.salazar.cheers.notes.data.repository

import com.salazar.cheers.data.Resource
import com.salazar.cheers.notes.domain.models.Note
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the Note data layer.
 */
interface NoteRepository {

    /**
     * Create a new note
     */
    suspend fun createNote(text: String): Result<Note>

    /**
     * Get the note of a specific user
     */
    suspend fun getNote(userID: String): Flow<Note>

    /**
     * Get the note of the current user
     */
    suspend fun getYourNote(): Flow<Note>

    /**
     * List friend notes.
     */
    fun listFriendNotes(): Flow<List<Note>>

    /**
     * Refresh friend notes from remote
     */
    suspend fun refreshFriendNotes(): Result<Unit>

    /**
     * Delete a note
     */
    suspend fun deleteNote(): Result<Unit>
}