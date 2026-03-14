package com.dogfight.magic.navigationwithcompose.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dogfight.magic.navigationwithcompose.ui.theme.NavigationWithComposeTheme

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Profile Image
        Image(
            //  painter = painterResource(id = R.drawable.profile_pic), // Замените на свой ресурс
            imageVector = Icons.Default.Person,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(120.dp)
                .border(4.dp, Color.Gray, CircleShape)
                .background(Color.White, CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // User Name
        Text(
            text = "John Doe",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // User Bio
        Text(
            text = "Android Developer | Passionate about Kotlin and Jetpack Compose. Always learning.",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(60.dp))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProfileButton(text = "Follow")
            ProfileButton(text = "Message")
        }
    }
}

@Composable
fun ProfileButton(text: String) {
    Button(
        onClick = {},
        modifier = Modifier
            .width(120.dp)
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(text = text, color = Color.White)
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    NavigationWithComposeTheme  {
        ProfileScreen()
    }
}
