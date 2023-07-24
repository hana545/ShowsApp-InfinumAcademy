package infinuma.android.shows.ui.shows

import android.Manifest
import android.R.attr
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetDialog
import infinuma.android.shows.Constants
import infinuma.android.shows.FileUtil
import infinuma.android.shows.R
import infinuma.android.shows.databinding.DialogProfilePictureOptionsBinding
import infinuma.android.shows.databinding.DialogUserOptionsBinding
import infinuma.android.shows.databinding.FragmentShowsBinding
import infinuma.android.shows.model.Show
import java.io.File
import java.io.IOException

class ShowsFragment : Fragment() {

    private var _binding: FragmentShowsBinding? = null
    private var _dialogBinding : DialogUserOptionsBinding ? = null
    private var _dialogPictureOptionsBinding : DialogProfilePictureOptionsBinding ? = null

    private val binding get() = _binding!!
    private val dialogBinding get() = _dialogBinding!!
    private val dialogPictureOptionsBinding get() = _dialogPictureOptionsBinding!!

    private lateinit var adapter: ShowsAdapter

    private val viewModel by viewModels<ShowsViewModel>()

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

        viewModel.listShows.observe(viewLifecycleOwner) { shows ->
            adapter = ShowsAdapter(shows) { show ->
                val direction = ShowsFragmentDirections.actionShowsFragmentToShowDetailsFragment(show)
                findNavController().navigate(direction)
            }
            binding.apply {
                recyclerViewShows.adapter = adapter
                binding.showsEmpty.visibility = if (shows.isEmpty()) View.VISIBLE else View.GONE
                binding.recyclerViewShows.visibility = if (shows.isEmpty()) View.GONE else View.VISIBLE
            }
        }

        binding.showsSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.removeShowsList()
            } else {
                viewModel.addShowsList()
            }
        }

        setUserOptions()
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
        return readPermission == PackageManager.PERMISSION_GRANTED &&
            writePermission == PackageManager.PERMISSION_GRANTED
    }
    private fun isCameraPermissionGranted(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        )
        return cameraPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestReadWritePermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            ),
            Constants.PERMISSION_REQUEST_CODE
        )
    }
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.CAMERA,
            ),
            Constants.CAMERA_REQUEST_CODE
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
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                //openCamera()
            } else {
                Toast.makeText(context, "Permissions denied, can't take a picture!", Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == Constants.CAMERA_REQUEST_CODE){
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                //openCamera()
            } else {
                Toast.makeText(context, "Permissions denied, can't take a picture!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val image = FileUtil.createImageFile(requireContext())
        viewModel.setCurrentPhotoUri(FileProvider.getUriForFile(requireContext(),
            Constants.FileProviderAuthority,
            image!!)
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, viewModel.currentPhotoUri.value)
        startActivityForResult(intent, Constants.CAMERA_REQUEST_CODE);
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, Constants.IMAGE_PICKER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            viewModel.currentPhotoUri.value?.let { viewModel.setProfilePhotoUri(it) }
            setProfileImages()
        }
        if (requestCode == Constants.IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            data.data?.let { viewModel.setProfilePhotoUri(it) }
        }
    }
    private fun setProfileImages(){
        viewModel.profilePhotoUri.observe(viewLifecycleOwner) { uri ->
            if (uri.toString().isNotEmpty()) {
                Glide.with(this)
                    .load(uri)
                    .circleCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.userOptions)
                Glide.with(this)
                    .load(uri)
                    .circleCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(dialogBinding.userProfilePhoto)
            }
        }
    }

    private fun setUserOptions() {
        val dialogUserOptions = BottomSheetDialog(requireContext())
        _dialogBinding = DialogUserOptionsBinding.inflate(layoutInflater)
        dialogUserOptions.setContentView(dialogBinding.root)

        setProfileImages()
        binding.userOptions.setOnClickListener {
            dialogUserOptions.show()
        }
        val pictureOptions = setPictureOptionsDialog()

        dialogBinding.apply {
            viewModel.userEmail.observe(viewLifecycleOwner, Observer { email ->
                dialogBinding.userEmailDisplay.text = email
            })
            btnChangeProfilePhoto.setOnClickListener {
                pictureOptions.show()
            }
            btnLogOut.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Logging out")
                builder.setMessage("Are you sure you want to log out?")
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    dialogUserOptions.cancel()
                    logOut()
                }
                builder.setNegativeButton(android.R.string.no) { dialog, which ->
                    dialog.cancel()
                }
                builder.show()
            }
        }
    }

    private fun setPictureOptionsDialog() : Dialog {
        val dialogPictureOptions = Dialog(requireContext())
        _dialogPictureOptionsBinding = DialogProfilePictureOptionsBinding.inflate(layoutInflater)
        dialogPictureOptions.setContentView(dialogPictureOptionsBinding.root)

        dialogPictureOptionsBinding.apply {
            btnOpenCamera.setOnClickListener {
                if (isReadWritePermissionGranted() && isCameraPermissionGranted()) {
                    openCamera()
                } else {
                    if (isReadWritePermissionGranted()) requestReadWritePermission()
                    if (isCameraPermissionGranted()) requestCameraPermission()
                }
                dialogPictureOptions.cancel()
            }
            btnPickGallery.setOnClickListener {
                if (isReadWritePermissionGranted()) {
                    openGallery()
                } else {
                    requestReadWritePermission()
                }
                dialogPictureOptions.cancel()
            }
        }
        return dialogPictureOptions
    }

    private fun logOut(){
        viewModel.clearSharedPreferences()
        findNavController().navigate(ShowsFragmentDirections.actionShowsFragmentToLoginFragment())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}