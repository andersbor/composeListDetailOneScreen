package com.example.listdetailonescreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.listdetailonescreen.ui.theme.ListDetailOneScreenTheme

// https://betterprogramming.pub/managing-jetpack-compose-ui-state-with-sealed-classes-d864c1609279

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ListDetailOneScreenTheme {
                BooksScaffold()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksScaffold(modifier: Modifier = Modifier) {
    //val scope = rememberCoroutineScope()
    var adding by remember { mutableStateOf(false) }
    // TODO stopAdding is better, or setAdding?
    val toggleAdding = { adding = !adding }

    Scaffold(
        modifier = modifier,
        // https://developer.android.com/develop/ui/compose/components/app-bars
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Books") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    //scope.launch {
                    adding = true
                    //}
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
    paddingValues: PaddingValues = PaddingValues(0.dp),
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
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(8.dp)
    ) {
        if (selectedBook != null && !adding) BookDetails(selectedBook!!)
        if (adding) AddPanel(add = {
            books.add(it)
            toggleAdding()
        }, cancelAdd = {
            toggleAdding()
        })
        BookListPanel(books,
            onClick = { selectedBook = it },
            onClickDelete = {
                books.remove(it)
                if (it == selectedBook) {
                    selectedBook = null
                }
            })
    }
}

@Composable
fun BookDetails(book: Book, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(8.dp)) {
        Text(text = book.title)
        Text(text = book.author)
        Text(text = book.price.toString())
    }
}

@Composable
fun AddPanel(add: (Book) -> Unit = {}, cancelAdd: () -> Unit = {}) {
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
            OutlinedTextField(modifier = Modifier.weight(2f),
                value = priceStr,
                onValueChange = { priceStr = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = { Text("Price") }
            )
            Button(modifier = Modifier
                .weight(1f)
                .padding(2.dp), onClick = { cancelAdd() }) {
                Text("Cancel")
            }
            Button(modifier = Modifier
                .weight(1f)
                .padding(2.dp), onClick = {
                localAdd()
            }) {
                Text("Add")
            }
        }
    }
}

@Composable
fun BookListPanel(
    books: List<Book>,
    modifier: Modifier = Modifier,
    onClick: (Book) -> Unit = {},
    onClickDelete: (Book) -> Unit = {}
) {
    LazyColumn(modifier = modifier) {
        items(books) { book ->
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
    Card(modifier = modifier
        .fillMaxWidth()
        .padding(5.dp),
        onClick = { onClick(book) }) {
        Row(
            modifier = Modifier.padding(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
fun BooksPreview() {
    ListDetailOneScreenTheme {
        //BooksScreen()
        BooksScaffold()
    }
}