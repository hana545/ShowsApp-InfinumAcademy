package infinuma.android.shows.ui.shows

import android.Manifest
import android.R.attr
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetDialog
import infinuma.android.shows.Constants
import infinuma.android.shows.FileUtil
import infinuma.android.shows.R
import infinuma.android.shows.databinding.DialogUserOptionsBinding
import infinuma.android.shows.databinding.FragmentShowsBinding
import infinuma.android.shows.model.Show
import java.io.File
import java.io.IOException

class ShowsFragment : Fragment() {

    private var _binding: FragmentShowsBinding? = null

    private var _dialogBinding : DialogUserOptionsBinding ? = null

    private val binding get() = _binding!!
    private val dialogBinding get() = _dialogBinding!!

    private lateinit var adapter: ShowsAdapter

    private lateinit var listShows : MutableList<Show>

    private lateinit var currentPhotoUri : Uri

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

        setUserOptions()

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
        }
    }
    private fun isReadWritePermissionGranted(): Boolean {
        val readPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val writePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val cameraPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        )
        return readPermission == PackageManager.PERMISSION_GRANTED &&
            writePermission == PackageManager.PERMISSION_GRANTED &&
        cameraPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestReadWritePermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ),
            Constants.PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED
            ) {
                openCamera()
            } else {
                Toast.makeText(context, "Permissions denied, can't take a picture!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val image = FileUtil.createImageFile(requireContext())
        currentPhotoUri = FileProvider.getUriForFile(requireContext(),
            "infinuma.android.shows.fileprovider",
            image!!)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
        startActivityForResult(intent, Constants.CAMERA_REQUEST_CODE);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            val preferences = requireActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
            preferences.edit {
                putString(Constants.keyImageUri, currentPhotoUri.toString())
            }
            setProfileImages()
        }
    }
    private fun setProfileImages(){
        val profileImageUri = requireActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.keyImageUri, "")
        if (profileImageUri!!.isNotEmpty()) {
            Glide.with(this)
                .load(profileImageUri.toUri())
                .circleCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.userOptions)
            Glide.with(this)
                .load(profileImageUri.toUri())
                .circleCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(dialogBinding.userProfilePhoto)
        }
    }

    private fun setUserOptions() {
        val dialog = BottomSheetDialog(requireContext())
        _dialogBinding = DialogUserOptionsBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        binding.userOptions.setOnClickListener {
            dialog.show()
        }

        dialogBinding.apply {
            userEmailDisplay.text = requireActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.keyEmail, "")
            btnChangeProfilePhoto.setOnClickListener {
                if (isReadWritePermissionGranted()) {
                    openCamera()
                } else {
                    requestReadWritePermission()
                }
            }
            setProfileImages()
            btnLogOut.setOnClickListener {
                val preferences = requireActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                preferences.edit {
                    putString(Constants.keyEmail, "")
                    putString(Constants.keyPassword, "")
                    putBoolean(Constants.keyLogedIn, false)
                    putString(Constants.keyImageUri, "")
                }
                dialog.cancel()
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