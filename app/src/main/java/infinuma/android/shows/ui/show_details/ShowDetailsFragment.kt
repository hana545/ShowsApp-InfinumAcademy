package infinuma.android.shows.ui.show_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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

    private val viewModel by viewModels<ShowDetailsViewModel>()

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

        viewModel.setShow(arguments?.getSerializable("show") as Show)

        bindShowData()

        setupSupportActionBar()
        viewModel.showLiveData.value?.let { setupReviewsAdapter(it.reviews) }
        setupAddReviewDialog()

        viewModel.showLiveData.observe(viewLifecycleOwner) { currentShow ->
            if (currentShow != null) {
                if (currentShow.reviews.size == 0) {
                    hideReviews()
                } else {
                    showReviews()
                    binding.apply {
                        reviewInfo.text = getString(R.string.reviews_info, currentShow.reviews.size, currentShow.avgReview)
                        reviewInfoRatingBar.rating = currentShow.avgReview
                    }
                }
            }
        }
    }

    private fun setupAddReviewDialog() {

        val dialog = BottomSheetDialog(requireContext())
        val dialogBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        var ratingValue = 0
        binding.btnAddReview.setOnClickListener {
            dialog.show()
            dialogBinding.apply {
                reviewRating.rating = 0F
                reviewText.text?.clear()
                btnSubmitReview.isEnabled = false
                reviewRating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                    if (rating > 0) {
                        dialogBinding.btnSubmitReview.isEnabled = true
                        ratingValue = rating.toInt()
                    }
                }
            }
        }
        dialogBinding.btnSubmitReview.setOnClickListener {
            viewModel.addReview(ratingValue, dialogBinding.reviewText.text.toString())
            viewModel.showLiveData.value?.reviews?.let { it1 -> adapter.notifyItemInserted(it1.lastIndex) }
            Toast.makeText(requireContext(), "Successfully added a review!", Toast.LENGTH_SHORT).show()
            dialog.cancel()
        }
    }

    private fun setupReviewsAdapter(reviews : MutableList<Review>) {
        adapter = ReviewsAdapter(reviews)
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
        viewModel.showLiveData.observe(viewLifecycleOwner, Observer { currentShow ->
            if (currentShow != null) {
                binding.apply {
                    toolbar.title = currentShow.name
                    showGenre.text = currentShow.genre
                    showDescription.text = currentShow.description
                    showImage.setImageResource(currentShow.imageResourceId)
                }
            }
        })
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