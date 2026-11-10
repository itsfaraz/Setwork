package com.designlife.justdo.common.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.designlife.justdo.common.data.entities.Deck
import com.designlife.justdo.common.data.entities.FlashCard
import com.designlife.justdo.common.data.entities.Note
import com.designlife.justdo.common.data.entities.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Transaction
    @Insert
    suspend fun insertDeck(deck: Deck) : Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDeck(decks: List<Deck>)

    @Transaction
    @Query("SELECT * FROM Deck")
    fun getAllDecks() : Flow<List<Deck>>

    @Transaction
    @Query("SELECT * FROM DECK WHERE deckId=:deckId")
    suspend fun getDeckById(deckId : Long) : Deck

    @Transaction
    @Query("UPDATE DECK SET deckName=:deckName ,totalCards=:totalCards, modifiedDate=:modifiedDate, categoryId=:categoryId, cards=:cards  WHERE deckId=:deckId")
    suspend fun updateDeckById(deckId : Long,deckName : String,totalCards : Int,categoryId : Long,modifiedDate : Long,cards : List<FlashCard>)

    @Transaction
    @Query("DELETE FROM DECK WHERE deckId=:deckId")
    suspend fun deleteDeckById(deckId : Long)

    @Transaction
    @Query("UPDATE DECK SET isArchive=:isArchive  WHERE deckId=:deckId")
    suspend fun archiveDeckById(deckId : Long,isArchive : Boolean = true)

}