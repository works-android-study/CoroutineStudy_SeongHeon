package com.worksmobile.study.coroutinestudy_seongheon

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun MainScreen(viewModel: MainViewModel) {
    Column {
        SearchBox(viewModel)
        SearchResultBox(viewModel)
    }
}

@Composable
fun SearchBox(viewModel: MainViewModel) {
    var text by remember { viewModel.searchQuery }

    Row {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Search") },
            leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) },
            modifier = Modifier
                .weight(weight = 1.0F, fill = true)
                .testTag("TextField"),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                unfocusedIndicatorColor = Color.Gray
            )
        )

        Button(onClick = { viewModel.searchImages() }) {
            Text(text = "Search")
        }
    }
}

@Composable
fun SearchResultBox(viewModel: MainViewModel) {
    val searchResult = viewModel.searchResult.observeAsState()
    GlideImage(imageModel = searchResult.value?.link)
}
