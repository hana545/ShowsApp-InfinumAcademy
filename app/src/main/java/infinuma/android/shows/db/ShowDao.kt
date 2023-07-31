package infinuma.android.shows.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ShowDao {

    @Query("SELECT * FROM show")
    fun getAllShows(): LiveData<MutableList<ShowEntity>>

    @Query("SELECT * FROM show WHERE id IS :showId")
    suspend fun getShow(showId: String): ShowEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllShows(shows: List<ShowEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(show: ShowEntity)

    @Update
    fun updateShow(show: ShowEntity)

}