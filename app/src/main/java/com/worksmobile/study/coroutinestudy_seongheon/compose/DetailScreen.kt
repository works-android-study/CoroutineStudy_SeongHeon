package com.worksmobile.study.coroutinestudy_seongheon.compose

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.skydoves.landscapist.glide.GlideImage
import com.worksmobile.study.coroutinestudy_seongheon.data.Item


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavController, jsonString: String?) {
    Log.d(":::jsonString", "$jsonString ${Gson().fromJson(jsonString, Item::class.java)}")
    val item = Gson().fromJson(jsonString ?: return, Item::class.java)

    Scaffold(
        topBar = {
            DetailTopAppBar(navController, item?.title)
        },
        content = {
            DetailContents(it, item)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopAppBar(navController: NavController, title: String?) {
    TopAppBar(
        title = { Text(text = title ?: "", overflow = TextOverflow.Ellipsis, fontSize = 15.sp) },
        navigationIcon = {
            if (navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            } else null
        }
    )
}

@Composable
fun DetailContents(padding: PaddingValues, item: Item?) {
    item ?: return

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(padding)
    ) {
        GlideImage(
            imageModel = item.thumbnail,
            contentScale = ContentScale.Fit,
            placeHolder = Icons.Filled.Place,
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)                       // clip to the circle shape
                .border(2.dp, Color.Gray, CircleShape)
        )
        Text(
            text = " width : ${item.sizeWidth} / height : ${item.sizeHeight}",
            modifier = Modifier.padding(16.dp),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        GlideImage(
            imageModel = item.link,
            contentScale = ContentScale.Fit,
            placeHolder = Icons.Filled.Place,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
