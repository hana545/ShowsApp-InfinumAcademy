package infinuma.android.shows.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import infinuma.android.shows.R
import infinuma.android.shows.adapters.ShowsAdapter
import infinuma.android.shows.databinding.ActivityShowsBinding
import infinuma.android.shows.model.Review
import infinuma.android.shows.model.Show

class ShowsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowsBinding

    private lateinit var adapter: ShowsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val listShows = setShowList()


        adapter = ShowsAdapter(listShows) { show ->
            val intent = Intent(this, ShowDetailsActivity::class.java)
            intent.putExtra("SHOW", show)
            startActivity(intent)
        }

        binding.recyclerViewShows.adapter = adapter

        binding.showsSwitch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                listShows.removeAll(listShows)
                adapter.notifyDataSetChanged()
                binding.showsEmpty.visibility = View.VISIBLE
                binding.recyclerViewShows.visibility = View.GONE
            } else {
                listShows.addAll(setShowList())
                adapter.notifyDataSetChanged()
                binding.showsEmpty.visibility = View.GONE
                binding.recyclerViewShows.visibility = View.VISIBLE
            }
        }
    }

    fun setShowList() : MutableList<Show>{
        var autoincrement = 0
        val description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
        return mutableListOf(
            Show("SH-${autoincrement++}","The Office", description, "Sitcom",  R.drawable.the_office, mutableListOf()),
            Show("SH-${autoincrement++}","Stranger Things", description, "Science fiction", R.drawable.stranger_things, mutableListOf()),
            Show("SH-${autoincrement++}","Grey's anatomy", description, "Medical drama", R.drawable.greys_anatomy, mutableListOf()),
            Show("SH-${autoincrement++}","Supernatural", description, "Fantasy drama", R.drawable.supernatural, mutableListOf()),
            Show("SH-${autoincrement++}","Parks and Recreation", description, "Sitcom", R.drawable.parks_and_recreation, mutableListOf()),
            Show("SH-${autoincrement++}","Breaking Bad", description, "Crime drama", R.drawable.breaking_bad, mutableListOf()),
            Show("SH-${autoincrement++}","Friends", description, "Sitcom", R.drawable.friends, mutableListOf())
        )
    }
}