package infinuma.android.shows.ui.show_details

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import infinuma.android.shows.R
import infinuma.android.shows.databinding.ItemReviewBinding
import infinuma.android.shows.model.Review
import infinuma.android.shows.model.Show

class ReviewsAdapter (
    private var items: MutableList<Review>
) : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) {
            binding.reviewAuthor.text = review.user.email
            binding.reviewNum.text = review.rating.toString()
            binding.reviewDescription.text = review.comment
            Glide.with(binding.root)
                .load(review.user.imageUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(binding.reviewImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context))
        return ReviewViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setItems(reviews: MutableList<Review>) {
        items = reviews
        notifyDataSetChanged()
    }

}