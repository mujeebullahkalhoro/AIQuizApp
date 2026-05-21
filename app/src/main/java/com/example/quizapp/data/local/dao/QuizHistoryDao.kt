package com.example.quizapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quizapp.data.local.entities.QuizHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizHistory(quiz: QuizHistoryEntity): Long

    @Delete
    suspend fun deleteQuizHistory(quiz: QuizHistoryEntity)

    @Query("DELETE FROM quiz_history WHERE id = :quizId")
    suspend fun deleteQuizHistoryById(quizId: Int)

    @Query("SELECT * FROM quiz_history WHERE userId = :userId ORDER BY id DESC")
    fun getQuizHistoryByUser(userId: Int): Flow<List<QuizHistoryEntity>>

    @Query("SELECT * FROM quiz_history WHERE id = :quizId LIMIT 1")
    suspend fun getQuizHistoryById(quizId: Int): QuizHistoryEntity?

    @Query("SELECT COUNT(*) FROM quiz_history WHERE userId = :userId")
    suspend fun getQuizCountByUser(userId: Int): Int

    @Query("SELECT AVG(score) FROM quiz_history WHERE userId = :userId")
    suspend fun getAverageScoreByUser(userId: Int): Float?

    @Query("DELETE FROM quiz_history WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Int)
}
