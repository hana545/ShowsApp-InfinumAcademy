package infinuma.android.shows.ui.shows

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import infinuma.android.shows.networking.ApiModule
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

        viewModel.getShows()
        viewModel.getShowList().observe(viewLifecycleOwner) { shows ->
            adapter = ShowsAdapter(viewModel.listShowsLiveData.value!!) { show ->
                val direction = ShowsFragmentDirections.actionShowsFragmentToShowDetailsFragment(show.id)
                findNavController().navigate(direction)
            }
            binding.recyclerViewShows.adapter = adapter
        }


        viewModel.listShowsLiveData.observe(viewLifecycleOwner) { shows ->
            binding.apply {
                showsEmpty.visibility = if (shows.isEmpty()) View.VISIBLE else View.GONE
                recyclerViewShows.visibility = if (shows.isEmpty()) View.GONE else View.VISIBLE
            }
            adapter.notifyDataSetChanged()

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
    private val requestPermissionLauncher = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(context, "Permissions denied!", Toast.LENGTH_LONG).show()
        }
    }

    private fun isPermissionGranted() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            showPermissionAlertDialog("storage")
        } else {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        }
    }

    private fun showPermissionAlertDialog(permission: String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext());
        alertDialogBuilder.setMessage("This app needs you to allow $permission permission in order to function. Will you allow it")
        alertDialogBuilder.setPositiveButton(android.R.string.yes) { dialog, which ->
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        alertDialogBuilder.setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.cancel()
        }
        alertDialogBuilder.show()

    }

    private fun openCamera() {
        val photoFile: File? = try {
            FileUtil.createImageFile(requireContext())
        } catch (ex: IOException) {
            null
        }
        // Continue only if the File was successfully created
        photoFile?.also {
            viewModel.setCurrentPhotoUri(FileProvider.getUriForFile(
                requireContext(),
                Constants.FileProviderAuthority,
                it
            ))
            takePicture.launch( viewModel.currentPhotoUriLiveData.value)
        }
    }
    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                viewModel.currentPhotoUriLiveData.value?.let { viewModel.setProfilePhotoUri(it) }
                setProfileImages()
            }
        }


    val pickGalleryMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            viewModel.setProfilePhotoUri(uri)
        }
    }

    private fun setProfileImages(){
        viewModel.profilePhotoUriLiveData.observe(viewLifecycleOwner) { uri ->
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
            val sharPreferences = requireActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
            dialogBinding.userEmailDisplay.text = sharPreferences.getString(Constants.keyEmail, "")
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
                isPermissionGranted()
                dialogPictureOptions.cancel()
            }
            btnPickGallery.setOnClickListener {
                pickGalleryMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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
        viewModel.clearSharedPreferences()
        _binding = null
    }
}