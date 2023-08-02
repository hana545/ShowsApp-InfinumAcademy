package infinuma.android.shows.ui.shows

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import infinuma.android.shows.model.Show

class ShowsAdapter (
    private var items: MutableList<Show>,
    private var onItemClick : (Show) -> Unit
) : RecyclerView.Adapter<ShowsAdapter.ShowViewHolder>() {

    inner class ShowViewHolder(val showCardView: ShowCardView) : RecyclerView.ViewHolder(showCardView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val showCardView = ShowCardView(parent.context)
        return ShowViewHolder(showCardView)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        val show = items[position]
        holder.showCardView.tag = position
        holder.showCardView.setShow(show)
        holder.showCardView.onClick(onItemClick, show)
    }
    fun setItems(shows: MutableList<Show>) {
        items = shows
        notifyDataSetChanged()
    }

}