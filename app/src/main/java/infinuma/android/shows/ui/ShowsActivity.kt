package infinuma.android.shows.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import infinuma.android.shows.R
import infinuma.android.shows.adapters.ShowsAdapter
import infinuma.android.shows.databinding.ActivityShowsBinding
import infinuma.android.shows.model.Show

class ShowsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowsBinding

    private lateinit var adapter: ShowsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        var listShows = setShowList()


        adapter = ShowsAdapter(listShows)

        binding.recyclerViewShows.adapter = adapter

        binding.showsSwitch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                listShows.removeAll(listShows)
                adapter.notifyDataSetChanged()
                binding.showsEmpty.visibility = View.VISIBLE
            } else {
                listShows.addAll(setShowList())
                adapter.notifyDataSetChanged()
                binding.recyclerViewShows.visibility = View.VISIBLE
                binding.showsEmpty.visibility = View.GONE
            }
        }
    }

    fun setShowList() : MutableList<Show>{
        Log.i("SETLIST", "setting lsit")
        var autoincrement = 0
        val description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
        return mutableListOf(
            Show("SH-${autoincrement++}","The Office", description, "Sitcom",  R.drawable.the_office),
            Show("SH-${autoincrement++}","Stranger Things", description, "Science fiction", R.drawable.stranger_things),
            Show("SH-${autoincrement++}","Grey's anatomy", description, "Medical drama", R.drawable.greys_anatomy),
            Show("SH-${autoincrement++}","Supernatural", description, "Fantasy drama", R.drawable.supernatural),
            Show("SH-${autoincrement++}","Parks and Recreation", description, "Sitcom", R.drawable.parks_and_recreation),
            Show("SH-${autoincrement++}","Breaking Bad", description, "Crime drama", R.drawable.breaking_bad),
            Show("SH-${autoincrement++}","Friends", description, "Sitcom", R.drawable.friends),
        )
    }
}