package com.netmontools.filesguide

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

class ImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val imageView: SubsamplingScaleImageView = findViewById(R.id.imageView)

        val path = getIntent().getStringExtra("path")
        imageView.setImage(ImageSource.uri(path!!))

        // Получаем URI изображения из Intent'a
//        val uri = intent?.data ?: return
//        imageView.setImageURI(uri)

    }
}