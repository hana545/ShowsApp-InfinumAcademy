package infinuma.android.shows.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import infinuma.android.shows.databinding.ViewShowItemBinding
import infinuma.android.shows.model.Show

class ShowsAdapter (
    private var items: List<Show>,
    private var onItemClick : (Show) -> Unit
) : RecyclerView.Adapter<ShowsAdapter.ShowViewHolder>() {

    inner class ShowViewHolder(private val binding: ViewShowItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(show: Show) {
            binding.cardContainer.setOnClickListener{
                onItemClick.invoke(show)
            }
            binding.showName.text = show.name
            binding.showGenre.text = show.genre
            binding.showDescription.text = show.description
            binding.showImage.setImageResource(show.imageResourceId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding = ViewShowItemBinding.inflate(LayoutInflater.from(parent.context))
        return ShowViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(items[position])
    }

}