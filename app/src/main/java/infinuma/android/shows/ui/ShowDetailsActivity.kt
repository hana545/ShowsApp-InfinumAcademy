package infinuma.android.shows.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import infinuma.android.shows.adapters.ReviewsAdapter
import infinuma.android.shows.databinding.ActivityShowDetailsBinding
import infinuma.android.shows.model.Review
import infinuma.android.shows.model.Show
import infinuma.android.shows.R
import kotlin.random.Random

class ShowDetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityShowDetailsBinding

    private lateinit var adapter: ReviewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val show = intent.getSerializableExtra("SHOW") as? Show
        binding.toolbar.title = show!!.name
        binding.showGenre.text = show.genre
        binding.showDescription.text = show.description
        binding.showImage.setImageResource(show.imageResourceId)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        if(show.reviews.size == 0) {
            binding.reviewsEmpty.visibility = View.VISIBLE
            binding.recyclerViewReviews.visibility = View.GONE
            binding.reviewInfo.visibility = View.GONE
            binding.reviewInfoRatingBar.visibility = View.GONE
        } else {
            binding.reviewsEmpty.visibility = View.GONE
            binding.recyclerViewReviews.visibility = View.VISIBLE
            binding.reviewInfo.visibility = View.VISIBLE
            binding.reviewInfoRatingBar.visibility = View.VISIBLE
        }

        adapter = ReviewsAdapter(show.reviews)

        binding.recyclerViewReviews.adapter = adapter
        binding.recyclerViewReviews.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        var averageReview : Float = 0.0F
        binding.btnAddReview.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Add a Review")
                .setMessage("Are you sure you want to add a new review?")
                .setPositiveButton("Add") { dialog, which ->
                    show.reviews.add(Review("dummy_user", (0..5).random(), "This is a review of ${show.name}",R.drawable.ic_profile_placeholder))
                    averageReview = show.reviews.sumOf { it.review }.toFloat() / show.reviews.size.toFloat()
                    binding.reviewsEmpty.visibility = View.GONE
                    binding.reviewInfo.visibility = View.VISIBLE
                    binding.reviewInfoRatingBar.visibility = View.VISIBLE
                    binding.recyclerViewReviews.visibility = View.VISIBLE
                    binding.reviewInfo.text = getString(R.string.reviews_info, show.reviews.size, averageReview)
                    binding.reviewInfoRatingBar.rating = averageReview
                    adapter.notifyItemInserted(show.reviews.lastIndex)
                }
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}