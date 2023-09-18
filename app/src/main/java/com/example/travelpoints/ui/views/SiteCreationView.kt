package com.example.travelpoints.ui.views

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelpoints.models.Category

@Composable
fun SiteCreationView(
    lat: Double,
    long: Double,
    onScreenClose: () -> Unit,
    onSaveSite: (String, String, Double, String) -> Unit
) {
    BackHandler(onBack = onScreenClose)
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = "New site",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                    fontSize = 22.sp
                )
            },
            backgroundColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
            navigationIcon = {
                IconButton(onClick = onScreenClose) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = null,
                        tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                    )
                }
            })
    }) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(24.dp)
        ) {
            item {
                val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black

                val focusManager = LocalFocusManager.current

                var name by remember { mutableStateOf("") }

                TextField(
                    modifier = Modifier.padding(vertical = 8.dp),
                    label = { Text(text = "Name") },
                    value = name,
                    shape = RoundedCornerShape(8.dp),
                    onValueChange = {
                        name = it
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                var description by remember { mutableStateOf("") }
                TextField(
                    modifier = Modifier.padding(vertical = 8.dp),
                    label = { Text(text = "Description") },
                    value = description,
                    shape = RoundedCornerShape(8.dp),
                    onValueChange = {
                        description = it
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                var entryPrice by remember { mutableStateOf("") }
                TextField(
                    modifier = Modifier.padding(vertical = 8.dp),
                    label = { Text(text = "Entry price") },
                    value = entryPrice,
                    shape = RoundedCornerShape(8.dp),
                    onValueChange = {
                        entryPrice = it
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done, keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                var showDropdown by remember { mutableStateOf(false) }
                var selectedIndex by remember { mutableStateOf(0) }
                val categories = listOf(
                    Category.Park, Category.Museum, Category.Monument
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable {
                            showDropdown = true
                        }) {
                    Text(text = "Category    ", color = textColor)
                    Box {

                        Text(text = categories[selectedIndex].toString(), color = MaterialTheme.colors.primary)
                        DropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false },
                            modifier = Modifier
                                .background(MaterialTheme.colors.background)
                                .padding(8.dp)
                        ) {
                            categories.forEachIndexed { index, s ->
                                Text(text = s.toString(),
                                    color = MaterialTheme.colors.primary,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .clickable {
                                            selectedIndex = index
                                            showDropdown = false
                                        })
                            }

                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = {
                            onSaveSite(name, description, entryPrice.toDouble(), categories[selectedIndex].toString())

                            onScreenClose()
                        }) {
                        Text(
                            text = "Save",
                            color = MaterialTheme.colors.primary,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}
