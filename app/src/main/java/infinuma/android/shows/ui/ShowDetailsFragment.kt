package infinuma.android.shows.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.transition.TransitionInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import infinuma.android.shows.R
import infinuma.android.shows.adapters.ReviewsAdapter
import infinuma.android.shows.adapters.ShowsAdapter
import infinuma.android.shows.databinding.DialogAddReviewBinding
import infinuma.android.shows.databinding.FragmentShowDetailsBinding
import infinuma.android.shows.databinding.FragmentShowsBinding
import infinuma.android.shows.model.Review
import infinuma.android.shows.model.Show

class ShowDetailsFragment : Fragment() {

    private var _binding: FragmentShowDetailsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ReviewsAdapter

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

        val show = arguments?.getSerializable("show") as Show
        binding.toolbar.title = show.name
        binding.showGenre.text = show.genre
        binding.showDescription.text = show.description
        binding.showImage.setImageResource(show.imageResourceId)

        (activity as MainActivity).apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }


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
            DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        )
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
                                show.reviews.add(
                                    Review(
                                        "dummy_user",
                                        rating.toInt(),
                                        dialogBinding.reviewText.text.toString(),
                                        R.drawable.ic_profile_placeholder
                                    )
                                )
                                averageReview = show.reviews.sumOf { it.review }.toFloat() / show.reviews.size.toFloat()
                                binding.apply {
                                    reviewsEmpty.visibility = View.GONE
                                    reviewInfo.visibility = View.VISIBLE
                                    reviewInfoRatingBar.visibility = View.VISIBLE
                                    recyclerViewReviews.visibility = View.VISIBLE
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