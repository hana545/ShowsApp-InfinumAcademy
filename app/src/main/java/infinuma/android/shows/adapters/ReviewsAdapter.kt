package infinuma.android.shows.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import infinuma.android.shows.databinding.ViewReviewItemBinding
import infinuma.android.shows.model.Review

class ReviewsAdapter (
    private var items: MutableList<Review>
) : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(private val binding: ViewReviewItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) {
            binding.reviewAuthor.text = review.author
            binding.reviewNum.text = review.review.toString()
            binding.reviewDescription.text = review.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ViewReviewItemBinding.inflate(LayoutInflater.from(parent.context))
        return ReviewViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(items[position])
    }

}