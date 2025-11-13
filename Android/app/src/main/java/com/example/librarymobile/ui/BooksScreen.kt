package com.example.librarymobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.librarymobile.viewmodel.BooksViewModel

@Composable
fun BooksScreen(viewModel: BooksViewModel) {
    val books = viewModel.books
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var linkBookId by remember { mutableStateOf("") }
    var linkVisitorId by remember { mutableStateOf("") }
    val error = viewModel.errorMessage

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Text("Books", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        // Add book row
        Row {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = author,
                onValueChange = { author = it },
                label = { Text("Author") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                if (title.isNotBlank()) {
                    viewModel.addBook(title.trim(), author.takeIf { it.isNotBlank() })
                    title = ""
                    author = ""
                }
            }) {
                Text("Add")
            }
        }

        Spacer(Modifier.height(12.dp))

        HeaderRow(listOf("ID", "Title", "Author", "Visitors", "Actions"))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(items = books, key = { it.id }) { b ->
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Text("${b.id}", modifier = Modifier.weight(0.5f))
                    Text(b.title, modifier = Modifier.weight(2f))
                    Text(b.author ?: "-", modifier = Modifier.weight(1f))
                    Text(b.visitors.joinToString { it.name }, modifier = Modifier.weight(2f))
                    Row(modifier = Modifier.weight(2f)) {
                        Button(onClick = { viewModel.deleteBook(b.id) }) { Text("Delete") }
                    }
                }
                Divider()
            }
        }

        Spacer(Modifier.height(8.dp))

        Text("Link / Unlink (by IDs)", style = MaterialTheme.typography.titleMedium)
        Row {
            OutlinedTextField(
                value = linkBookId,
                onValueChange = { linkBookId = it },
                label = { Text("BookId") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = linkVisitorId,
                onValueChange = { linkVisitorId = it },
                label = { Text("VisitorId") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                val bid = linkBookId.toIntOrNull()
                val vid = linkVisitorId.toIntOrNull()
                if (bid != null && vid != null) {
                    viewModel.addLink(bid, vid) { _, _ -> }
                    linkBookId = ""
                    linkVisitorId = ""
                }
            }) { Text("Add link") }
            Spacer(Modifier.width(4.dp))
            Button(onClick = {
                val bid = linkBookId.toIntOrNull()
                val vid = linkVisitorId.toIntOrNull()
                if (bid != null && vid != null) {
                    viewModel.deleteLink(bid, vid) { _, _ -> }
                    linkBookId = ""
                    linkVisitorId = ""
                }
            }) { Text("Del link") }
        }

        error?.let {
            Spacer(Modifier.height(8.dp))
            Text("Error: $it", color = MaterialTheme.colorScheme.error)
        }
    }
}