package infinuma.android.shows.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import infinuma.android.shows.model.User
import kotlinx.serialization.SerialName

@Entity(tableName = "review")
data class ReviewEntity(
    @ColumnInfo(name = "id") @PrimaryKey val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "rating") val rating: Int,
    @ColumnInfo(name = "comment") val comment: String,
    @ColumnInfo(name = "show_id") val showId: String
)
