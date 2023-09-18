package com.example.travelpoints.ui.views

import android.content.Context
import android.graphics.fonts.FontStyle
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelpoints.R
import com.example.travelpoints.models.getActiveUserId
import com.example.travelpoints.ui.theme.TravelPointsTheme
import com.example.travelpoints.ui.viewmodels.SupportViewModel

@Composable
fun SupportView(
    onSend: (String, Context) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(top = 60.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var input by remember { mutableStateOf("") }
        val focusManager = LocalFocusManager.current
        val context = LocalContext.current

        Text(
            text = "Need any help? Don't hesitate to ask!",
            modifier = Modifier.padding(horizontal = 32.dp),
            fontSize = 32.sp,
        )

        Image(
            painter = painterResource(
                id = R.drawable.flat_customer_support_illustration_23_2148892450
            ),
            contentDescription = null,
        )
        TextField(
            modifier = Modifier
                .padding(vertical = 8.dp),
            label = { Text(text = "Contact administrators") },
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
            ),
            trailingIcon = {
                IconButton(onClick = {
                    if (getActiveUserId() != null) {
                        onSend(input, context)
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
        )
    }
}

@Composable
@Preview
private fun SupportPreview() {
    TravelPointsTheme {
        SupportView(onSend = { message, context -> })
    }
}