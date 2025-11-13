package com.example.librarymobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.librarymobile.viewmodel.VisitorsViewModel
import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun VisitorsScreen(viewModel: VisitorsViewModel) {
    val visitors = viewModel.visitors
    var name by remember { mutableStateOf("") }
    val error = viewModel.errorMessage

    // Для быстрой отладки — увидим в логах, что пришло
    LaunchedEffect(visitors) {
        Log.d("VisitorsScreen", "visitors size = ${visitors.size}; raw = $visitors")
    }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Text("Visitors", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        // Add visitor row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        viewModel.addVisitor(name.trim())
                        name = ""
                    }
                },
                modifier = Modifier.wrapContentWidth()
            ) {
                Text("Add")
            }
        }

        Spacer(Modifier.height(12.dp))

        // Заголовок таблицы (фиксируем поведение)
        VisitorHeaderRow(listOf("ID", "Name", "Actions"))

        Spacer(Modifier.height(4.dp))

        // Список — безопасный keys и безопасный вывод полей
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(
                items = visitors,
                key = { v -> v.id ?: v.hashCode() } // защита от null id
            ) { v ->
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Показываем дефолтные значения, если null
                    Text(text = "${v.id ?: "-"}", modifier = Modifier.weight(1f))
                    Text(text = v.name ?: "-", modifier = Modifier.weight(3f))

                    // Кнопки действий не растягиваем weight'ом — так меньше шансов на MeasureException
                    Row(modifier = Modifier.weight(2f), horizontalArrangement = Arrangement.End) {
                        Button(
                            onClick = { v.id?.let { viewModel.deleteVisitor(it) } },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.wrapContentWidth()
                        ) {
                            Text("Delete")
                        }
                    }
                }
                Divider()
            }
        }

        // Ошибки
        error?.let {
            Spacer(Modifier.height(8.dp))
            Text("Error: $it", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun VisitorHeaderRow(columns: List<String>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        columns.forEach { col ->
            Text(
                col,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
