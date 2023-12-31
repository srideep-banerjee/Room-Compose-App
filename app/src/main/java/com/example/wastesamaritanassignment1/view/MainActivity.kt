package com.example.wastesamaritanassignment1.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.example.wastesamaritanassignment1.R
import com.example.wastesamaritanassignment1.model.Item
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

                Scaffold(
                    topBar = { TopBar(scrollBehavior) },
                    floatingActionButton = { AddButton() },
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    val loading by mutableStateOf(true)
                    lifecycleScope.launch {

                    }
                    ItemList(
                        list = listOf()/*mainActivityViewModel
                            .database
                            .itemDao()
                            .getItemsByNameAsc()*/,

                        modifier = Modifier.padding(it)
                    )
                }
            }
        }
    }
}

fun onAddButtonClick() {

}

@Composable
fun AddButton() {
    FloatingActionButton(
        onClick = {
            onAddButtonClick()
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
fun ItemList(list:List<Item>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    LazyColumn(modifier = modifier) {
        items(
//            items = list,
//            key = { it.id }
            10000
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
                    .clickable { context.startActivity(Intent(context, ItemDetails::class.java)) }
            ) {
                Row(modifier = Modifier.padding(
                    start = 8.dp,
                    end = 8.dp,
                    top = 8.dp
                )) {
                    DetailsText(key = "Item", value = "Refrigerator", modifier = Modifier.weight(1.0f))
                    DetailsText(key = "Qty", value = "5")
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
                    Text("Ratings:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    val ratings = 3
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
                val remarks = "Samsung product."
                DetailsText(key = "Remarks",
                    value = remarks,
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
fun TopBar(scrollBehavior: TopAppBarScrollBehavior) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = "Items List")
        },

        scrollBehavior = scrollBehavior
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WasteSamaritanAssignment1Theme {
    }
}