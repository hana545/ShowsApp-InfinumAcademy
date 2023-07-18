package infinuma.android.shows.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import infinuma.android.shows.R
import infinuma.android.shows.adapters.ShowsAdapter
import infinuma.android.shows.databinding.ActivityShowsBinding
import infinuma.android.shows.databinding.FragmentLoginBinding
import infinuma.android.shows.databinding.FragmentShowsBinding
import infinuma.android.shows.model.Show

class ShowsFragment : Fragment() {

    private var _binding: FragmentShowsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ShowsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listShows = setShowList()

        adapter = ShowsAdapter(listShows) { show ->
            val direction = ShowsFragmentDirections.actionShowsFragmentToShowDetailsFragment(show)
            findNavController().navigate(direction)
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

        binding.btnLogOut.setOnClickListener {
            findNavController().navigate(ShowsFragmentDirections.actionShowsFragmentToLoginFragment())
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}