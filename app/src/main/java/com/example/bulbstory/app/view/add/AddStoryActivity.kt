package com.example.bulbstory.app.view.add

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.example.bulbstory.app.R
import com.example.bulbstory.app.data.DataRepository
import com.example.bulbstory.app.data.remote.api.ApiClient
import com.example.bulbstory.app.databinding.ActivityStoryBinding
import com.example.bulbstory.app.util.Message
import com.example.bulbstory.app.util.NetworkResult
import com.example.bulbstory.app.util.PrefsManager
import com.example.bulbstory.app.util.ViewModelFactory
import com.example.bulbstory.app.util.createCustomTempFile
import com.example.bulbstory.app.util.reduceFileImage
import com.example.bulbstory.app.util.uriToFile
import com.example.bulbstory.app.view.home.HomeActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private val binding: ActivityStoryBinding by lazy {
        ActivityStoryBinding.inflate(layoutInflater)
    }
    private var getFile: File? = null
    private var uploadJob: Job = Job()
    private lateinit var viewModel: UploadStoryViewModel
    private lateinit var prefsManager: PrefsManager

    private lateinit var currentPhotoPath: String

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Message.setMessage(this, getString(R.string.deniedPermission))
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        prefsManager = PrefsManager(this)
        val dataRepository = DataRepository(ApiClient.getInstance())
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(dataRepository)
        )[UploadStoryViewModel::class.java]
        permissionGranted()
        binding.btnCamera.setOnClickListener {
            startCamera()
        }
        binding.btnGallery.setOnClickListener {
            startGallery()
        }
        binding.btnUpload.setOnClickListener {
            if(getFile != null || !TextUtils.isEmpty(binding.edtDescription.text.toString())) {
                uploadStory(prefsManager.token)
            } else {
                Message.setMessage(this, getString(R.string.emptyUpload))
            }
        }
    }


    private fun permissionGranted() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    @Suppress("DEPRECATION")
    private fun uploadStory(auth: String) {
        setLoadingState(true)
        val file = reduceFileImage(getFile as File)
        val description = binding.edtDescription.text.toString().trim()
        lifecycle.coroutineScope.launchWhenResumed {
            if (uploadJob.isActive) uploadJob.cancel()
            uploadJob = launch {
                viewModel.uploadStory(auth, description, file).collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            setLoadingState(false)
                            Message.setMessage(this@AddStoryActivity, getString(R.string.uploadStory))
                            startActivity(Intent(this@AddStoryActivity, HomeActivity::class.java))
                            finish()
                        }
                        is NetworkResult.Loading -> {
                            setLoadingState(true)
                        }
                        is NetworkResult.Error -> {
                            setLoadingState(false)
                            Message.setMessage(this@AddStoryActivity, getString(R.string.emptyStory))
                        }
                    }
                }
            }
        }
    }

    private fun setLoadingState(loading: Boolean) {
        when (loading) {
            true -> {
                binding.btnUpload.visibility = View.INVISIBLE
                binding.progressBar.visibility = View.VISIBLE
            }
            false -> {
                binding.btnUpload.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.example.bulbstory.app.view",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }


    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(myFile.path)
            binding.imgPreview.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            getFile = myFile

            binding.imgPreview.setImageURI(selectedImg)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}