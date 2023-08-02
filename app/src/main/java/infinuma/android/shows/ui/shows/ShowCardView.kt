package infinuma.android.shows.ui.shows

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import infinuma.android.shows.R
import infinuma.android.shows.databinding.ItemShowBinding
import infinuma.android.shows.model.Show

class ShowCardView(context: Context) : FrameLayout(context) {

    private val binding: ItemShowBinding = ItemShowBinding.inflate(LayoutInflater.from(context), this)


    fun onClick(action: (Show) -> Unit, show: Show) {
        binding.cardContainer.setOnClickListener { action(show) }
    }


    fun setShow(show: Show) {
        binding.showName.text = show.title
        binding.showDescription.text = show.description
        Glide.with(this)
            .load(show.imageUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_shows_empty_state)
            .into(binding.showImage)
    }

}