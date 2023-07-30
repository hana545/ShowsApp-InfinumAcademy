package infinuma.android.shows.ui.show_details

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetDialog
import infinuma.android.shows.R
import infinuma.android.shows.databinding.DialogAddReviewBinding
import infinuma.android.shows.databinding.DialogLoadingBinding
import infinuma.android.shows.databinding.FragmentShowDetailsBinding
import infinuma.android.shows.ui.MainActivity

class ShowDetailsFragment : Fragment() {

    private var _binding: FragmentShowDetailsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ReviewsAdapter

    private lateinit var loading: Dialog

    private val viewModel by viewModels<ShowDetailsViewModel>()

    private val args : ShowDetailsFragmentArgs by navArgs()

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
        loading = loadingDialog()
        loading.show()

        viewModel.getShow(args.showId)
        viewModel.getShowReviews(args.showId)

        bindShowData()

        setupReviewsAdapter()
        setupAddReviewDialog()

        viewModel.showLiveData.observe(viewLifecycleOwner) { currentShow ->
            if (currentShow != null) {
                if (currentShow.numReviews == 0) {
                    hideReviews()
                } else {
                    showReviews()
                    binding.apply {
                        reviewInfo.text = getString(R.string.reviews_info, currentShow.numReviews, currentShow.averageRating)
                        reviewInfoRatingBar.rating = if(currentShow.averageRating == null) 0F else currentShow.averageRating!!
                    }
                }
                loading.cancel()
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
            viewModel.addReview(dialogBinding.reviewText.text.toString(),ratingValue, viewModel.showLiveData.value!!.id)
            adapter.notifyItemInserted(viewModel.reviewsLiveData.value!!.size)
            dialog.cancel()
        }
        viewModel.postReviewResult.observe(viewLifecycleOwner) { result ->
            if (result) Toast.makeText(requireContext(), "Successfully added a review!", Toast.LENGTH_SHORT).show()
            if (!result) Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupReviewsAdapter() {
        viewModel.getReviewList().observe(viewLifecycleOwner) { reviews ->
            adapter = ReviewsAdapter(reviews)
            binding.apply {
                recyclerViewReviews.adapter = adapter
                recyclerViewReviews.addItemDecoration(
                    DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
                )
            }
        }
        viewModel.reviewsLiveData.observe(viewLifecycleOwner) { reviews ->
            adapter.notifyDataSetChanged()

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
        viewModel.showLiveData.observe(viewLifecycleOwner) { currentShow ->
            if (currentShow != null) {
                binding.apply {
                    toolbar.title = currentShow.title
                    setupSupportActionBar()
                    showDescription.text = currentShow.description
                    Glide.with(binding.root)
                        .load(currentShow.imageUrl)
                        .centerCrop()
                        .placeholder(R.drawable.ic_shows_empty_state)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(binding.showImage)
                }
            }
        }
    }

    private fun setupSupportActionBar() {
        (activity as MainActivity).apply {
            setSupportActionBar(binding.toolbar)
            binding.toolbar.title = viewModel.showLiveData.value?.title
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
    }
    private fun loadingDialog() : Dialog {
        val dialog= Dialog(requireContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        dialog.setContentView(DialogLoadingBinding.inflate(layoutInflater).root)
        return dialog
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