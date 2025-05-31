package com.university_assignment.lab2

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private var imageUri: Uri? = null
    private lateinit var photoBLock: ImageView

    private val runCameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
        if (result) {
            photoBLock.setImageURI(imageUri as Uri)
        } else {
            Toast
                .makeText(this, "failed to capture a photo", Toast.LENGTH_LONG)
                .show()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        if (result) {
            launchCamera()
        } else {
            Toast
                .makeText(this, "failed to get a camera permission", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val makeSelfiBtn = findViewById<Button>(R.id.make_selfi_btn)
        val sendSelfiBtn = findViewById<Button>(R.id.send_selfi_btn)
        photoBLock = findViewById(R.id.photo_block)

        makeSelfiBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                launchCamera()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        sendSelfiBtn.setOnClickListener {
            imageUri?.let {
                val repositoryLink = "https://github.com/rabo452/android-lab-2"
                val dozent = "hodovychenko@op.edu.ua"

                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "message/rfc822"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(dozent))
                    putExtra(Intent.EXTRA_SUBJECT, "Laba 2")
                    putExtra(Intent.EXTRA_TEXT, "here is the link to the lab repository on github: $repositoryLink")
                    putExtra(Intent.EXTRA_STREAM, imageUri as Uri)
                }

                startActivity(emailIntent)
            }
        }
    }

    private fun launchCamera() {
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/.thumbnails")
            }
        })

        imageUri?.let {
            runCameraLauncher.launch(it)
        }
    }
}

