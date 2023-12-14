package infinuma.android.shows.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import infinuma.android.shows.model.Show

@Entity(tableName = "show")
data class ShowEntity(
    @ColumnInfo(name = "id") @PrimaryKey val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "avg_rating") val avgRating: Float?,
    @ColumnInfo(name = "num_reviews") val numReviews: Int?
) {
    fun toShow(): Show {
        return Show(this.id, this.title, this.description, this.imageUrl, this.avgRating, this.numReviews)
    }
}


