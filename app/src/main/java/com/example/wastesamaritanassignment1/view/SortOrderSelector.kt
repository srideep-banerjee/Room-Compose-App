package com.example.wastesamaritanassignment1.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.wastesamaritanassignment1.model.ItemOrder

@Composable
fun SortOrderDialog(currentSortOrder: ItemOrder, onDismiss: (ItemOrder?)->Unit) {

    var sortOrder = remember {
        mutableStateOf(currentSortOrder)
    }

    Dialog(onDismissRequest = { onDismiss(null) }) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.surface).padding(8.dp),
            //verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = "Sort by", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            for (it in ItemOrder.entries) {
                val name = when(it) {
                    ItemOrder.NAME_ASC-> "Name -> Ascending order"
                    ItemOrder.NAME_DESC-> "Name -> Descending order"
                    ItemOrder.RATINGS_ASC-> "Ratings -> Ascending order"
                    ItemOrder.RATINGS_DESC-> "Ratings -> Descending order"
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { sortOrder.value = it }) {
                    RadioButton(
                        selected = (it == sortOrder.value),
                        onClick = { sortOrder.value = it }
                    )
                    Text(text = name, modifier = Modifier.padding(start = 8.dp))
                }
            }
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.padding(end = 8.dp).fillMaxWidth()) {
                Text(
                    text = "OK",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        onDismiss(if(currentSortOrder != sortOrder.value) sortOrder.value else null)
                    })
            }
        }
    }
}