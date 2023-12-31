package com.example.wastesamaritanassignment1.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.example.wastesamaritanassignment1.R
import com.example.wastesamaritanassignment1.model.Item
import com.example.wastesamaritanassignment1.model.ItemDetailsShortened
import com.example.wastesamaritanassignment1.model.ItemOrder
import com.example.wastesamaritanassignment1.ui.theme.WasteSamaritanAssignment1Theme
import com.example.wastesamaritanassignment1.viewmodel.MainActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val mainActivityViewModel by viewModels<MainActivityViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WasteSamaritanAssignment1Theme {
                // A surface container using the 'background' color from the theme
                val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
                val showSortOrderDialog = rememberSaveable {
                    mutableStateOf(false)
                }

                Scaffold(
                    topBar = { TopBar(scrollBehavior) {
                        showSortOrderDialog.value = true
                    } },
                    floatingActionButton = { AddButton() },
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    val isLoading = mainActivityViewModel.isLoading.collectAsState()
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

                        ItemList(
                            list =  mainActivityViewModel.itemList.toMutableStateList(),

                            modifier = Modifier.padding(it)
                        )

                        if (showSortOrderDialog.value) {
                            SortOrderDialog(currentSortOrder = mainActivityViewModel.sortOrder, ) {sortOrder->
                                showSortOrderDialog.value = false
                                if(sortOrder != null)
                                    mainActivityViewModel.updateSortOrder(sortOrder)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivityViewModel.updateSortOrder(ItemOrder.NAME_ASC)
    }
}

fun onAddButtonClick(context: Context) {
    context.startActivity(Intent(context, ItemDetails::class.java))
}

fun onItemClick(context: Context, id: Int) {
    val intent = Intent(context, ItemDetails::class.java)
    intent.putExtra("item_id", id)
    context.startActivity(intent)
}

@Composable
fun AddButton() {
    val context = LocalContext.current
    FloatingActionButton(
        onClick = {
            onAddButtonClick(context)
        },
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Image(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add",
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimary),
        )
    }
}

@Composable
fun ItemList(list: SnapshotStateList<ItemDetailsShortened>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    if (list.size == 0) {
        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No items added yet",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                modifier = Modifier
            )
        }
    } else {
        LazyColumn(modifier = modifier) {
            items(
                items = list,
                key = { it.id }
            ) {
                Column(
                    modifier = Modifier
                        .padding(
                            start = 8.dp,
                            end = 8.dp,
                            bottom = 16.dp
                        )
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onItemClick(context, it.id) }
                ) {
                    Row(modifier = Modifier.padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = 8.dp
                    )) {
                        DetailsText(key = "Item", value = it.name, modifier = Modifier.weight(1.0f))
                        DetailsText(key = "Qty", value = it.quantity.toString())
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
                        Text("Ratings:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        val ratings = it.rating
                        for (i in 1..5) {
                            val painterId = if(i <= ratings) R.drawable.baseline_star_24 else R.drawable.baseline_star_outline_24

                            Image(
                                painter = painterResource(id = painterId),
                                contentDescription = if(i <= ratings) "star filled" else "star outlined",
                                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    DetailsText(key = "Remarks",
                        value = it.remarks?:"",
                        modifier = Modifier
                            .padding(
                                start = 8.dp,
                                end = 8.dp,
                                bottom = 8.dp
                            )
                            .fillMaxWidth(),
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun DetailsText(key: String, value: String, modifier: Modifier = Modifier, overflow: TextOverflow = TextOverflow.Clip) {
    Text(text = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            append("$key: ")
        }
        append(value)
    }, modifier = modifier, overflow = overflow, maxLines = 1)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(scrollBehavior: TopAppBarScrollBehavior, onClick: ()->Unit) {
    TopAppBar(
        title = {
            Text(text = "Items List")
        },
        scrollBehavior = scrollBehavior,
        actions = {
            IconButton(onClick = { onClick() }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_sort_24),
                    contentDescription = "sort order",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WasteSamaritanAssignment1Theme {
    }
}