package com.example.travelpoints.ui.views

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelpoints.R
import com.example.travelpoints.models.Site
import com.example.travelpoints.models.isCurrentUserAdmin
import com.example.travelpoints.ui.viewmodels.SiteDetailsViewModel

@Composable
fun SiteDetailsView(
    site: Site,
    viewModel: SiteDetailsViewModel = SiteDetailsViewModel(site),
    userIsLoggedIn: Boolean,
    onScreenClose: () -> Unit
) {
    val offerValue by viewModel.offerValue.collectAsState()

    BackHandler(onBack = onScreenClose)
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = site.name,
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
                .padding(it)
                .padding(24.dp)
        ) {
            item {
                val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black

                SiteAverageRating(
                    rating = viewModel.averageRating.collectAsState().value,
                    ratingsNumber = viewModel.ratingsNumber.collectAsState().value
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Description:", color = textColor)
                    Text(text = site.description, color = textColor)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Category:", color = textColor)
                    Text(text = site.category.toString(), color = textColor)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Latitude:", color = textColor)
                    Text(text = site.latitude.toString(), color = textColor)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Longitude:", color = textColor)
                    Text(text = site.longitude.toString(), color = textColor)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "EntryPrice:", color = textColor)
                    Row(
                        horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()
                    ) {
                        if (offerValue != 0.0) {
                            Text(
                                text = (site.entryPrice - site.entryPrice * offerValue).toFloat().toString(),
                                color = Color.Red,
                                modifier = Modifier.padding(horizontal = 5.dp),
                            )
                            Text(
                                text = site.entryPrice.toFloat().toString(),
                                color = textColor,
                                modifier = Modifier.padding(horizontal = 5.dp),
                                style = TextStyle(textDecoration = TextDecoration.LineThrough)
                            )
                        } else {
                            Text(
                                text = site.entryPrice.toFloat().toString(),
                                color = textColor,
                                modifier = Modifier.padding(horizontal = 5.dp),
                            )
                        }
                    }
                }
                val currentRating = viewModel.currentRating.collectAsState()
                val context = LocalContext.current
                RatingBar(currentRating = currentRating.value, saveRating = {
                    if (userIsLoggedIn) {
                        viewModel.updateCurrentRating(it)
                        viewModel.saveRatingToFirebase(it)
                    } else {
                        showLoginToast(context)
                    }
                })
                WishlistOption(viewModel, userIsLoggedIn)
                if (isCurrentUserAdmin()) {
                    ApplyOffer(site, updateOfferValue = { newValue ->
                        viewModel.updateOfferValue(newValue)
                    })
                }
                CommentsSectionView(viewModel = viewModel, userIsLoggedIn = userIsLoggedIn)
            }
        }
    }
}

@Composable
private fun RatingBar(
    currentRating: Int,
    saveRating: (Int) -> Unit,
) {
    Row {
        StarImage(
            index = 1, currentRating = currentRating
        ) {
            saveRating(it)
        }
        StarImage(
            index = 2, currentRating = currentRating
        ) {
            saveRating(it)
        }
        StarImage(
            index = 3, currentRating = currentRating
        ) {
            saveRating(it)
        }
        StarImage(
            index = 4, currentRating = currentRating
        ) {
            saveRating(it)
        }
        StarImage(
            index = 5, currentRating = currentRating
        ) {
            saveRating(it)
        }
    }
}

@Composable
private fun StarImage(
    index: Int, currentRating: Int, onClick: (Int) -> Unit
) {
    Image(painter = if (currentRating >= index) painterResource(id = R.drawable.ic_star_full_2) else painterResource(
        id = R.drawable.ic_star_empty_2
    ), contentDescription = null, modifier = Modifier
        .padding(end = 2.dp)
        .clickable {
            onClick(index)
        })
}

@Composable
private fun SiteAverageRating(
    rating: Float, ratingsNumber: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = rating.toString()
        )
        Image(
            painter = painterResource(id = R.drawable.ic_star_full_2),
            contentDescription = null,
            modifier = Modifier.padding(start = 2.dp)
        )
        Text(
            text = "($ratingsNumber reviews)"
        )
    }
}

@Composable
private fun WishlistOption(
    viewModel: SiteDetailsViewModel, userIsLoggedIn: Boolean
) {
    val isInWishlist = viewModel.isInWishlist.collectAsState()
    val context = LocalContext.current
    OutlinedButton(
        onClick = {
            if (userIsLoggedIn) {
                viewModel.updateIsInWishlist(!isInWishlist.value)
            } else {
                showLoginToast(context)
            }
        }, border = BorderStroke(1.dp, Color.Red)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = if (isInWishlist.value) painterResource(id = R.drawable.ic_heart_full) else painterResource(
                    id = R.drawable.ic_heart_empty
                ), contentDescription = null
            )
            Text(
                text = if (isInWishlist.value) "Added to Wishlist" else "Add to Wishlist",
                color = Color.Red,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun CommentsSectionView(
    viewModel: SiteDetailsViewModel, userIsLoggedIn: Boolean
) {
    val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    var input by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val comments = viewModel.comments.collectAsState()

    Text(text = "Comments", fontSize = 22.sp, color = textColor, modifier = Modifier)
    Column(
    ) {
        comments.value.forEach {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(8.dp),
                backgroundColor = Color.LightGray,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(text = "By ${it.second}")
                    Text(text = it.first, modifier = Modifier.padding(top = 10.dp))
                }
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .weight(0.95f),
            label = { Text(text = "Add a new comment") },
            value = input,
            shape = RoundedCornerShape(8.dp),
            onValueChange = {
                input = it
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        val context = LocalContext.current
        IconButton(onClick = {
            if (userIsLoggedIn) {
                viewModel.addNewComment(input)
                input = ""
            } else {
                showLoginToast(context)
            }
        }) {
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = null,
                tint = MaterialTheme.colors.primary
            )
        }
    }
}

@Composable
fun ApplyOffer(site: Site, updateOfferValue: (Double) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf((site.offerValue * 100).toInt().toString()) }
    val focusManager = LocalFocusManager.current

    OutlinedButton(onClick = { showDialog = true }) {
        Text(text = "Apply offer to this site")
    }
    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false }, text = {
            TextField(modifier = Modifier.padding(vertical = 8.dp),
                label = { Text(text = "Specify the discount percentage", fontSize = 16.sp) },
                value = input,
                shape = RoundedCornerShape(8.dp),
                onValueChange = {
                    if (it.toIntOrNull() != null && it.length < 4) {
                        if (it.toInt() <= 100) {
                            input = it
                        }
                    }
                    if (it.isEmpty()) {
                        input = it
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Decimal,
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                colors = TextFieldDefaults.textFieldColors(
                    unfocusedIndicatorColor = Color.Transparent
                ),
                trailingIcon = { Text(text = "%") })
        }, buttons = {
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(
                    onClick = { showDialog = false }, modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(text = "Cancel")
                }
                TextButton(onClick = {
                    val newOfferValue = (input.toInt().toDouble() / 100)
                    updateOfferValue(newOfferValue)
                    showDialog = false
                }) {
                    Text(text = "Done")
                }
            }
        })
    }
}

fun showLoginToast(context: Context) {
    Toast.makeText(context, "You must be logged in to interact", Toast.LENGTH_SHORT).show()
}