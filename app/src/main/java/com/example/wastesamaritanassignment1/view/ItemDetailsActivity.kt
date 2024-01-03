package com.example.wastesamaritanassignment1.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.wastesamaritanassignment1.BuildConfig
import com.example.wastesamaritanassignment1.R
import com.example.wastesamaritanassignment1.model.Item
import com.example.wastesamaritanassignment1.view.ui.theme.WasteSamaritanAssignment1Theme
import com.example.wastesamaritanassignment1.viewmodel.ItemDetailsViewModel
import java.io.File
import java.util.Objects

class ItemDetails : ComponentActivity() {
    private val itemDetailsViewModel by viewModels<ItemDetailsViewModel>(factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ItemDetailsViewModel(application, id) as T
            }
        }
    })
    val id by lazy {
        intent.extras?.getInt("item_id")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this) {
            itemDetailsViewModel.deleteAddedFiles(itemDetailsViewModel.newImagesState.keys.toList())
            finish()
        }
        setContent {
            WasteSamaritanAssignment1Theme {
                // A surface container using the 'background' color from the theme

                Scaffold(
                    topBar = {
                        TopBar()
                    }
                ) {
                    val isLoading = itemDetailsViewModel.isLoading.collectAsState()

                    if (isLoading.value) {
                        Box(
                            modifier = Modifier
                                .padding(it)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Loading...",
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                modifier = Modifier
                            )
                        }
                    } else {

                        val item = remember {
                            mutableStateOf(itemDetailsViewModel.item)
                        }

                        val context = LocalContext.current
                        var file = context.createImageFile()
                        var uri = FileProvider.getUriForFile(
                            context,
                            BuildConfig.APPLICATION_ID + ".provider", file
                        )

                        val imgState = itemDetailsViewModel.imagesState
                        val newImgSet = itemDetailsViewModel.newImagesState

                        val cameraLauncher =
                            rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {successful ->
                                if (successful) {
                                    imgState.add(0, file.absolutePath)
                                    newImgSet[file.absolutePath] = true
                                } else {
                                    Toast.makeText(context, "Image not taken", Toast.LENGTH_SHORT).show()
                                }
                            }

                        val permissionLauncher = rememberLauncherForActivityResult(
                            ActivityResultContracts.RequestPermission()
                        ) {successful ->
                            if (successful) {
                                cameraLauncher.launch(uri)
                            } else {
                                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                            }
                        }

                        Details(
                            item = item,
                            images = imgState,
                            newImages = newImgSet,
                            onSave = { name,qty,rat,rem->
                                val trimmedName = name.trim()
                                if (imgState.size <= 1) {
                                    toast(context, "At least two photos must be provided")
                                } else if (trimmedName.isEmpty()) {
                                    toast(context, "Name cannot be empty or whitespace")
                                } else if (qty.isEmpty()) {
                                    toast(context, "Quantity must be provided")
                                } else if (rat == 0) {
                                    toast(context, "Rating must be provided")
                                } else {
                                    itemDetailsViewModel.save(
                                        imgState,
                                        trimmedName,
                                        qty.toIntOrNull() ?: 0,
                                        rat,
                                        rem
                                    ) {
                                        toast(context, "Details updated")
                                    }
                                }

                            },
                            onAddPhoto = {
                                file = context.createImageFile()
                                uri = FileProvider.getUriForFile(
                                    Objects.requireNonNull(context),
                                    BuildConfig.APPLICATION_ID + ".provider", file
                                )

                                val permissionCheckResult =
                                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                    cameraLauncher.launch(uri)
                                } else {
                                    // Request a permission
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            onDeletePhoto = {index ->
                                val path = imgState[index]
                                imgState.removeAt(index = index)
                                newImgSet.remove(path)
                                itemDetailsViewModel.deleteFile(path)
                            },
                            modifier = Modifier.padding(it)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(title = {
        Text(text = "Item Details")
    })
}


@Composable
fun Details(
    item: MutableState<Item?>,
    images: SnapshotStateList<String>,
    newImages: SnapshotStateMap<String, Boolean>,
    onSave: (String,String,Int,String?)->Unit,
    onAddPhoto: ()->Unit,
    onDeletePhoto: (Int)->Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier
        .padding(horizontal = 8.dp)
    ) {

        val nameState = rememberSaveable {
            mutableStateOf(item.value?.name?:"")
        }
        val qtyState = rememberSaveable {
            mutableStateOf(item.value?.quantity?.toString()?:"")
        }
        val remState = rememberSaveable {
            mutableStateOf(item.value?.remarks?:"")
        }
        val ratState = rememberSaveable {
            mutableIntStateOf(item.value?.rating?:0)
        }

        Text(text = "PHOTOS", fontSize = 14.sp)
        ImageViewer(
            photos = images,
            newPhotos = newImages,
            onAddPhoto = { onAddPhoto() }, onDeletePhoto = {
                onDeletePhoto(it)
            })

        Spacer(modifier = Modifier.height(8.dp))
        Divider()
        Spacer(modifier = Modifier.height(8.dp))

        NameViewer(nameState = nameState)

        QuantityViewer(qtyState = qtyState)

        RatingsViewer(ratState = ratState)

        Text(text = "Remarks:", modifier = Modifier.padding(bottom = 8.dp))
        TextField(
            value = remState.value,
            onValueChange = {if (it.length <= 200) remState.value = it},
            placeholder = {
                Text("Enter remarks")
                          },
            modifier = Modifier
                .weight(1f, true)
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                onSave(nameState.value, qtyState.value, ratState.intValue, remState.value)
            }) {
                Text("Save")
            }
        }

    }
}

@Composable
fun NameViewer(nameState: MutableState<String>) {

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
        Text(text = "Item Name:", modifier = Modifier.width(104.dp))
        TextField(
            value = nameState.value,
            onValueChange = { if (it.length <= 15) nameState.value = it },
            placeholder = { Text("Enter name") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
    }
}

@Composable
fun QuantityViewer(qtyState: MutableState<String>) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
        Text(text = "Quantity:", modifier = Modifier.width(104.dp))
        TextField(
            value = qtyState.value,
            onValueChange = {qtyState.value = it},
            placeholder = { Text("Enter quantity") },
            modifier = Modifier
                .weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
    }
}

@Composable
fun RatingsViewer(ratState: MutableState<Int>) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
        Text(text = "Ratings:", modifier = Modifier.width(104.dp))
        val filledStar = rememberAsyncImagePainter(R.drawable.baseline_star_24)
        val outlinedStar = rememberAsyncImagePainter(R.drawable.baseline_star_outline_24)

        for (i in 1..5) {

            Image(
                painter = if(i <= ratState.value) filledStar else outlinedStar ,
                contentDescription = if(i <= ratState.value) "star filled" else "star outlined",
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(40.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { ratState.value = i }
            )
        }
    }
}

@Composable
fun ImageViewer(photos: SnapshotStateList<String>, newPhotos: SnapshotStateMap<String, Boolean>, onAddPhoto: () -> Unit, onDeletePhoto: (Int) -> Unit) {

    Row(modifier = Modifier
        .height(92.dp)
        .fillMaxWidth()
    ) {
        LazyRow(modifier = Modifier
            .weight(weight = 1f, fill = false)
            .clip(shape = RoundedCornerShape(8.dp))
            .fillMaxHeight()
        ) {
            items(photos.size) {
                val imgRequest = ImageRequest
                    .Builder(LocalContext.current)
                    .data(File(photos[it]))
                    .build()
                Box(
                    contentAlignment = Alignment.TopEnd
                ) {
                    AsyncImage(
                        model = imgRequest,
                        contentDescription = "Image ${it + 1}",
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.outline_timer_24),
                        error = painterResource(id = R.drawable.baseline_error_24),
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                    if (photos[it] in newPhotos.keys) {
                        Image(
                            painter = rememberAsyncImagePainter(R.drawable.baseline_delete_24),
                            contentDescription = "Delete image ${it + 1}",
                            colorFilter = ColorFilter.tint(color = Color.White),
                            modifier = Modifier
                                .width(28.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .background(Color.Red)
                                .padding(4.dp)
                                .clickable { onDeletePhoto(it) }
                        )
                    }
                }
                if (it !=  photos.size - 1) Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Spacer(modifier = Modifier.width(8.dp))
        
        AddPhotoButton { onAddPhoto() }
    }
}

fun toast(context: Context, str: String) {
    Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
}
