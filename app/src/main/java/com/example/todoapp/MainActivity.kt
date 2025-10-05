package com.example.todoapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todoapp.ui.theme.ToDoAppTheme


data class ToDoItem(
    val id: Long = System.currentTimeMillis(),
    val label: String,
    var isCompleted: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ToDoApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ToDoApp(modifier: Modifier = Modifier) {
    val items = rememberSaveable(saver = listSaver) {
        mutableStateListOf<ToDoItem>()
    }

    val onAddItem: (String) -> Unit = { newItemLabel ->
        items.add(ToDoItem(label = newItemLabel))
    }
    val onRemoveItem: (ToDoItem) -> Unit = { item ->
        items.remove(item)
    }

    val onToggleItem: (ToDoItem, Boolean) -> Unit = { item, isChecked ->
        // Find the index of the item that needs to be updated.
        val itemIndex = items.indexOfFirst { it.id == item.id }
        if (itemIndex != -1) {
            // Create a new, updated copy of the item.
            val updatedItem = items[itemIndex].copy(isCompleted = isChecked)

            // Remove the old item and add the new one at the same position.
            items[itemIndex] = updatedItem
        }
    }

    // Derived state: these lists automatically update when `items` changes.
    val activeItems = items.filter { !it.isCompleted }
    val completedItems = items.filter { it.isCompleted }

    Column(modifier = modifier.padding(16.dp)) {
        ToDoInput(onAddItem = onAddItem)

        Spacer(modifier = Modifier.height(24.dp))

        ToDoList(
            title = "Active Items",
            items = activeItems,
            emptyMessage = "No active items. Add one above.",
            onToggleItem = onToggleItem,
            onRemoveItem = onRemoveItem
        )

        Spacer(modifier = Modifier.height(24.dp))

        ToDoList(
            title = "Completed Items",
            items = completedItems,
            emptyMessage = "No completed items yet.",
            onToggleItem = onToggleItem,
            onRemoveItem = onRemoveItem
        )
    }
}

@Composable
fun ToDoInput(onAddItem: (String) -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("New To-Do item") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = {
                val trimmedText = text.trim()
                if (trimmedText.isNotBlank()) {
                    onAddItem(trimmedText)
                    text = "" // Clear the input field after adding
                } else {
                    // Show a brief message for blank input
                    Toast.makeText(context, "Item cannot be empty", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = text.isNotBlank() // Disable button if input is blank
        ) {
            Text("Add")
        }
    }
}

@Composable
fun ToDoList(
    title: String,
    items: List<ToDoItem>,
    emptyMessage: String,
    onToggleItem: (ToDoItem, Boolean) -> Unit,
    onRemoveItem: (ToDoItem) -> Unit
) {
    // Show section header only when the list is not empty.
    if (items.isNotEmpty()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items, key = { it.id }) { item ->
                ToDoRow(
                    item = item,
                    onToggle = { isChecked -> onToggleItem(item, isChecked) },
                    onRemove = { onRemoveItem(item) }
                )
            }
        }
    } else {
        Text(
            text = emptyMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
    }
}

@Composable
fun ToDoRow(
    item: ToDoItem,
    onToggle: (Boolean) -> Unit,
    onRemove: () -> Unit
) {
    //Compose layout with Row, Checkbox, IconButton
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(
            checked = item.isCompleted,
            onCheckedChange = onToggle
        )
        Text(
            text = item.label,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        )
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove ${item.label}" // Accessibility best practice
            )
        }
    }
}

//Custom saver for ToDoItem list
private val listSaver = androidx.compose.runtime.saveable.listSaver<MutableList<ToDoItem>, Any>(
    save = { list ->
        // Save as a list of pairs: [id1, label1, isCompleted1, id2, label2, isCompleted2, ...]
        list.flatMap { listOf(it.id, it.label, it.isCompleted) }
    },
    restore = { saved ->
        // Restore from the flat list back into a list of ToDoItem objects
        saved.chunked(3).map {
            ToDoItem(id = it[0] as Long, label = it[1] as String, isCompleted = it[2] as Boolean)
        }.toMutableStateList()
    }
)


//Previews

@Preview(showBackground = true)
@Composable
fun ToDoAppPreview() {
    ToDoAppTheme {
        ToDoApp(modifier = Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun ToDoRowPreview() {
    ToDoAppTheme {
        ToDoRow(
            item = ToDoItem(label = "Buy milk", isCompleted = false),
            onToggle = {},
            onRemove = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ToDoInputPreview() {
    ToDoAppTheme {
        ToDoInput(onAddItem = {})
    }
}
