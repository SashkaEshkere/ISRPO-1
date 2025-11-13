package com.example.librarymobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.librarymobile.data.RetrofitClient
import com.example.librarymobile.repository.LibraryRepository
import com.example.librarymobile.ui.BooksScreen
import com.example.librarymobile.ui.VisitorsScreen
import com.example.librarymobile.ui.theme.LibraryMobileTheme
import com.example.librarymobile.viewmodel.BooksViewModel
import com.example.librarymobile.viewmodel.VisitorsViewModel

class MainActivity : ComponentActivity() {
    private val repository by lazy { LibraryRepository(RetrofitClient.api) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val booksVm = BooksViewModel(repository)
        val visitorsVm = VisitorsViewModel(repository)

        setContent {
            LibraryMobileTheme {
                var screen by remember { mutableStateOf("books") }

                @OptIn(ExperimentalMaterial3Api::class)
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Library App (minimal)") }
                            // убрал actions отсюда, чтобы не перекрывать место
                        )
                    }
                ) { innerPadding: PaddingValues ->
                    // Применяем padding и делаем колоночный layout с явными кнопками переключения
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(8.dp)
                    ) {
                        // Видимая строка с кнопками переключения — всегда на экране
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { screen = "books" },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Books")
                            }
                            Button(
                                onClick = { screen = "visitors" },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Visitors")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Основной контент — экраны
                        when (screen) {
                            "books" -> BooksScreen(booksVm)
                            "visitors" -> VisitorsScreen(visitorsVm)
                            else -> BooksScreen(booksVm)
                        }
                    }
                }
            }
        }
    }
}
