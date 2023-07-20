package infinuma.android.shows.ui.shows

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import infinuma.android.shows.databinding.ItemShowBinding
import infinuma.android.shows.model.Show

class ShowsAdapter (
    private var items: List<Show>,
    private var onItemClick : (Show) -> Unit
) : RecyclerView.Adapter<ShowsAdapter.ShowViewHolder>() {

    inner class ShowViewHolder(private val binding: ItemShowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(show: Show) {
            binding.cardContainer.setOnClickListener{
                onItemClick(show)
            }
            binding.showName.text = show.name
            binding.showGenre.text = show.genre
            binding.showDescription.text = show.description
            binding.showImage.setImageResource(show.imageResourceId)
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

}