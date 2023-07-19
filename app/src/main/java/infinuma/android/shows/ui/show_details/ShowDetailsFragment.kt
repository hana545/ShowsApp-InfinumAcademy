package infinuma.android.shows.ui.show_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomsheet.BottomSheetDialog
import infinuma.android.shows.R
import infinuma.android.shows.databinding.DialogAddReviewBinding
import infinuma.android.shows.databinding.FragmentShowDetailsBinding
import infinuma.android.shows.model.Review
import infinuma.android.shows.model.Show
import infinuma.android.shows.ui.MainActivity

class ShowDetailsFragment : Fragment() {

    private var _binding: FragmentShowDetailsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ReviewsAdapter

    private lateinit var show: Show

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        show = arguments?.getSerializable("show") as Show

        bindShowData()

        setupSupportActionBar()
        setupReviewsAdapter()
        setupAddReviewDialog()


        if(show.reviews.size == 0) {
            hideReviews()
        } else {
            showReviews()
        }


    }

    private fun setupAddReviewDialog() {

        val dialog = BottomSheetDialog(requireContext())
        val dialogBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        var averageReview : Float = 0.0F
        binding.btnAddReview.setOnClickListener {
            dialog.show()
            dialogBinding.apply {
                reviewRating.rating = 0F
                reviewText.text?.clear()
                reviewRating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                    if (rating > 0) {
                        dialogBinding.apply {
                            btnAddReview.isEnabled = true
                            btnAddReview.setOnClickListener {
                                addReview(rating.toInt(), dialogBinding.reviewText.text.toString())
                                averageReview = show.reviews.sumOf { it.review }.toFloat() / show.reviews.size.toFloat()
                                showReviews()
                                binding.apply {
                                    reviewInfo.text = getString(R.string.reviews_info, show.reviews.size, averageReview)
                                    reviewInfoRatingBar.rating = averageReview
                                }
                                adapter.notifyItemInserted(show.reviews.lastIndex)
                                Toast.makeText(requireContext(), "Successfully added a review!", Toast.LENGTH_SHORT).show()
                                dialog.cancel()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addReview(rating : Int, text: String) {
        show.reviews.add(
            Review(
                "dummy_user",
                rating,
                text,
                R.drawable.ic_profile_placeholder
            )
        )
    }

    private fun setupReviewsAdapter() {
        adapter = ReviewsAdapter(show.reviews)
        binding.apply {
            recyclerViewReviews.adapter = adapter
            recyclerViewReviews.addItemDecoration(
                DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
            )
        }
    }

    private fun showReviews() {
        binding.apply {
            reviewsEmpty.visibility = View.GONE
            recyclerViewReviews.visibility = View.VISIBLE
            reviewInfo.visibility = View.VISIBLE
            reviewInfoRatingBar.visibility = View.VISIBLE
        }
    }

    private fun hideReviews() {
        binding.apply {
            reviewsEmpty.visibility = View.VISIBLE
            recyclerViewReviews.visibility = View.GONE
            reviewInfo.visibility = View.GONE
            reviewInfoRatingBar.visibility = View.GONE
        }
    }

    private fun bindShowData() {
        binding.apply {
            toolbar.title = show.name
            showGenre.text = show.genre
            showDescription.text = show.description
            showImage.setImageResource(show.imageResourceId)
        }
    }

    private fun setupSupportActionBar() {
        (activity as MainActivity).apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}