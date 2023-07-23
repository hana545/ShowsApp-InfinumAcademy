package infinuma.android.shows.ui.shows

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import infinuma.android.shows.Constants
import infinuma.android.shows.R
import infinuma.android.shows.databinding.FragmentShowsBinding
import infinuma.android.shows.model.Show

class ShowsFragment : Fragment() {

    private var _binding: FragmentShowsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ShowsAdapter

    private lateinit var listShows : MutableList<Show>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.slide_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listShows = setShowList()

        adapter = ShowsAdapter(listShows) { show ->
            val direction = ShowsFragmentDirections.actionShowsFragmentToShowDetailsFragment(show)
            findNavController().navigate(direction)
        }

        binding.apply {
            recyclerViewShows.adapter = adapter

            showsSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    removeShowsList()
                } else {
                    addShowsList()
                }
            }

            btnLogOut.setOnClickListener {
                val preferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                preferences.edit {
                    putString(Constants().keyEmail, "")
                    putString(Constants().keyPassword, "")
                    putBoolean(Constants().keyLogedIn, false)
                }
                findNavController().navigate(ShowsFragmentDirections.actionShowsFragmentToLoginFragment())
            }
        }
    }

    fun removeShowsList(){
        listShows.removeAll(listShows)
        adapter.notifyDataSetChanged()
        binding.apply {
            showsEmpty.visibility = View.VISIBLE
            recyclerViewShows.visibility = View.GONE
        }
    }

    fun addShowsList(){
        listShows.addAll(setShowList())
        adapter.notifyDataSetChanged()
        binding.apply {
            showsEmpty.visibility = View.GONE
            recyclerViewShows.visibility = View.VISIBLE
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