package com.example.wastesamaritanassignment1.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wastesamaritanassignment1.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun AddImageModeSelector(onSelection: (ImageMethodSelectionType)->Unit) {
    Column {
        Text(
            text = "Get image from",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(Modifier.padding(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelection(ImageMethodSelectionType.CAMERA) }
                .padding(vertical = 8.dp)
        ) {
            Spacer(Modifier.width(16.dp))
            Image(
                painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                contentDescription = "Add image",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .width(36.dp)
                    .aspectRatio(1f)
            )
            Spacer(Modifier.width(16.dp))
            Text(text = "Camera", fontSize = 16.sp)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelection(ImageMethodSelectionType.GALLERY) }
                .padding(vertical = 8.dp)
        ) {
            Spacer(Modifier.width(16.dp))
            Image(
                painter = painterResource(id = R.drawable.baseline_image_24),
                contentDescription = "Add image",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .width(36.dp)
                    .aspectRatio(1f)
            )
            Spacer(Modifier.width(16.dp))
            Text(text = "Gallery", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

enum class ImageMethodSelectionType {
    CAMERA,
    GALLERY
}

fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File("${filesDir.absolutePath}${File.separator}$imageFileName.jpg");
    return image
}