package infinuma.android.shows.ui.shows

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
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
import infinuma.android.shows.databinding.DialogLoadingBinding
import infinuma.android.shows.databinding.DialogProfilePictureOptionsBinding
import infinuma.android.shows.databinding.DialogUserOptionsBinding
import infinuma.android.shows.databinding.FragmentShowsBinding
import infinuma.android.shows.db.ShowsDatabase
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

    private lateinit var loading: Dialog

    private val viewModel by viewModels<ShowsViewModel>{
        ShowsViewModelFactory(requireActivity().application,ShowsDatabase.getDatabase(requireContext()))
    }

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
        loading = loadingDialog()
        loading.show()

        viewModel.getShows()
        initShowsRecycler()

        viewModel.showsLiveData.observe(viewLifecycleOwner) { shows ->
            updateItems(shows)
        }

        setUserOptions()
    }
    private fun initShowsRecycler() {
        adapter = ShowsAdapter(mutableListOf()) { item ->
            Toast.makeText(requireContext(), item.title, Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewShows.adapter = adapter
    }

    private fun updateItems(shows: MutableList<Show>) {
        adapter.setItems(shows)
        binding.apply {
            showsEmpty.visibility = if (shows.isEmpty()) View.VISIBLE else View.GONE
            recyclerViewShows.visibility = if (shows.isEmpty()) View.GONE else View.VISIBLE
        }
        if(shows.isNotEmpty()) loading.cancel()
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
        photoFile?.also {
            viewModel.setCurrentPhotoUri(FileProvider.getUriForFile(
                requireContext(),
                Constants.FileProviderAuthority,
                it
            ))
            takePicture.launch(viewModel.currentPhotoUriLiveData.value)
        }
    }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                viewModel.currentPhotoUriLiveData.value?.let { viewModel.setProfilePhotoUri(it) }
                setProfileImages()
                FileUtil.getImageFile(requireContext())?.let { viewModel.uploadPhoto(it) }
            }
        }

    val pickGalleryMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val photoFile: File? = try {
                FileUtil.createImageFile(requireContext())
            } catch (ex: IOException) {
                null
            }
            photoFile?.also {
                viewModel.setCurrentPhotoUri(uri)
            }
            if (photoFile != null) {
                copyFile(uri, photoFile.toUri())
                viewModel.setProfilePhotoUri(photoFile.toUri())
                FileUtil.getImageFile(requireContext())?.let { viewModel.uploadPhoto(it) }
            }
        }
    }
    @Throws(IOException::class)
    private fun copyFile(pathFrom: Uri, pathTo: Uri) {
        requireActivity().contentResolver.openInputStream(pathFrom).use { `in` ->
            if (`in` == null) return
            requireActivity().contentResolver.openOutputStream(pathTo).use { out ->
                if (out == null) return
                // Transfer bytes from in to out
                val buf = ByteArray(1024)
                var len: Int
                while (`in`.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
            }
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
    private fun loadingDialog() : Dialog {
        val dialog= Dialog(requireContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        dialog.setContentView(DialogLoadingBinding.inflate(layoutInflater).root)
        return dialog
    }

    private fun logOut(){
        viewModel.clearSharedPreferences()
        findNavController().navigate(ShowsFragmentDirections.actionShowsFragmentToLoginFragment())
    }

    override fun onDestroy() {
        super.onDestroy()
        //viewModel.clearSharedPreferences()
        _binding = null
    }
}