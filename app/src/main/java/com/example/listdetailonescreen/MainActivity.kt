package com.example.listdetailonescreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.listdetailonescreen.ui.theme.ListDetailOneScreenTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ListDetailOneScreenTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //BookScreen()
                    BooksScaffold()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksScaffold(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    var adding by remember { mutableStateOf(false) }
    val toggleAdding = { adding = !adding }
    //val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
        /*snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },*/
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Books")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        adding = true
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) {
        BooksScreen(it, adding, toggleAdding)
    }
}

// Very general example https://blog.stylingandroid.com/compose-list-detail-basics/
@Composable
fun BooksScreen(
    values: PaddingValues = PaddingValues(0.dp),
    adding: Boolean,
    toggleAdding: () -> Unit = {}
) {
    val books = remember {
        mutableStateListOf(
            Book(1, "Kotlin", "Anders B", 9.95),
            Book(2, "More Kotlin", "Bobby C", 19.95),
            Book(3, "Even more Kotlin", "Bobby C", 4.95),
            Book(4, "Kotlin for the win", "Anders B", 29.95),
            Book(5, "Kotlin for the win", "Anders B", 29.95),
            Book(6, "Kotlin for the win", "Anders B", 29.95),
        )
    }
    var book by remember { mutableStateOf<Book?>(null) }
    Column(modifier = Modifier.padding(values).padding(5.dp)) {
        if (book != null && !adding) BookDetails(book)
        if (adding) AddPanel(add = {
            books.add(it)
            toggleAdding()
            //book = it
        })
        BookList(books,
            onClick = { book = it },
            onClickDelete = {
                books.remove(it)
                if (it == book) {
                    book = null
                }
            })
    }
}

@Composable
fun BookDetails(book: Book?, modifier: Modifier = Modifier) {
    if (book != null) {
        Column(modifier = modifier) {
            Text(text = book.title)
            Text(text = book.author)
            Text(text = book.price.toString())
        }
    } else {
        Text(text = "No book selected", modifier = modifier)
    }
}

@Composable
fun AddPanel(add: (Book) -> Unit = {}) {
    var author by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    val localAdd = {
        val price = priceStr.toDoubleOrNull() ?: 0.0
        val book = Book(0, title, author, price)
        add(book)

    }
    Column {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = author,
            //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            //modifier = Modifier.fillMaxWidth(),
            onValueChange = { author = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            label = { Text("Author") }
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = title,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),

            ) {
            OutlinedTextField(
                value = priceStr,
                onValueChange = { priceStr = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = { Text("Price") }
            )
            Button(onClick = {
                // TODO close keyboard
                //LocalSoftwareKeyboardController.current?.hide()
                localAdd()
            }) {
                Text("Add")
            }
        }
    }
}

@Composable
fun BookList(
    books: List<Book>,
    modifier: Modifier = Modifier,
    onClick: (Book) -> Unit = {},
    onClickDelete: (Book) -> Unit = {}
) {
    // TODO LazyColumn
    LazyColumn(modifier = modifier) {
        items(books) {book ->
            BookItem(book, onClick = onClick, onClickDelete = onClickDelete)
        }
    }

    /*Column(modifier = modifier) {
        for (book in books) {
            BookItem(book = book, onClick = onClick, onClickDelete = onClickDelete)
        }
    }*/
}

@Composable
fun BookItem(
    book: Book, modifier: Modifier = Modifier,
    onClick: (Book) -> Unit = {},
    onClickDelete: (Book) -> Unit = {}
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp),
        onClick = { onClick(book) }) {
        Row(modifier = Modifier.padding(2.dp)) {
            Text(
                text = book.author,
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Bold
            )
            Text(text = book.title, modifier = Modifier.padding(8.dp), fontStyle = FontStyle.Italic)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onClickDelete(book) }) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ListDetailOneScreenTheme {
        //BooksScreen()
        BooksScaffold()
    }
}