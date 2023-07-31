package infinuma.android.shows.ui.shows

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import infinuma.android.shows.R
import infinuma.android.shows.databinding.ItemShowBinding
import infinuma.android.shows.model.Show

class ShowsAdapter (
    private var items: MutableList<Show>,
    private var onItemClick : (Show) -> Unit
) : RecyclerView.Adapter<ShowsAdapter.ShowViewHolder>() {

    inner class ShowViewHolder(private val binding: ItemShowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(show: Show) {
            binding.cardContainer.setOnClickListener{
                onItemClick(show)
            }

            binding.showName.text = show.title
            binding.showDescription.text = show.description
            Glide.with(binding.root)
                .load(show.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_shows_empty_state)
                .into(binding.showImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding = ItemShowBinding.inflate(LayoutInflater.from(parent.context))
        return ShowViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(items[position])
    }
    fun setItems(shows: MutableList<Show>) {
        items = shows
        notifyDataSetChanged()
    }

}