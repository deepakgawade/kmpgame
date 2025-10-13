package org.example.kmpgame

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen (
    onNavigateToSignUp:()->Unit){
    Column(){
        Text("Welcome to the App")
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick=onNavigateToSignUp){
            Text("Go to sign up")
        }

    }

}