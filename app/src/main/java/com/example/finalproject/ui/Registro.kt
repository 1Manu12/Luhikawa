package com.example.finalproject.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

class MainActivityRegistro : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgDarka
                ) {
                    RegistroScreen()
                }
            }
        }
    }
}

@Composable
fun RegistroScreen() {
    var usuario by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val spacingXS = 20.dp
    val spacingS = 50.dp
    val spacingM = 16.dp
    val spacingL = 24.dp
    val spacingXL = 30.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDarka)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(spacingS))

        Image(
            painter = painterResource(id = R.drawable.logolkb),
            contentDescription = "Logo LK",
            modifier = Modifier.size(60.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(spacingXS))

        Image(
            painter = painterResource(id = R.drawable.titlebeige),
            contentDescription = "LUHIKAWA",
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .wrapContentHeight(),
            contentScale = ContentScale.FillWidth,
            colorFilter = ColorFilter.tint(Color(0xFFC7AF93))
        )

        Spacer(modifier = Modifier.height(spacingXL))

        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Usuario", style = TextStyle(fontFamily = InriaSerif, color = TextBeigea.copy(alpha = 0.6f), fontSize = 18.sp))
            },
            textStyle = TextStyle(fontFamily = InriaSerif, color = TextBeigea, fontSize = 18.sp),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBordera,
                unfocusedBorderColor = AccentBordera,
                cursorColor = BgBeigea
            ),
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = "Icono Usuario", tint = TextBeigea.copy(alpha = 0.7f))
            }
        )

        Spacer(modifier = Modifier.height(spacingM))

        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Nombre Completo", style = TextStyle(fontFamily = InriaSerif, color = TextBeigea.copy(alpha = 0.6f), fontSize = 18.sp))
            },
            textStyle = TextStyle(fontFamily = InriaSerif, color = TextBeigea, fontSize = 18.sp),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBordera,
                unfocusedBorderColor = AccentBordera,
                cursorColor = BgBeigea
            ),
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = "Icono Usuario", tint = TextBeigea.copy(alpha = 0.7f))
            }
        )

        Spacer(modifier = Modifier.height(spacingM))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Correo electrónico", style = TextStyle(fontFamily = InriaSerif, color = TextBeigea.copy(alpha = 0.6f), fontSize = 18.sp))
            },
            textStyle = TextStyle(fontFamily = InriaSerif, color = TextBeigea, fontSize = 18.sp),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBordera,
                unfocusedBorderColor = AccentBordera,
                cursorColor = BgBeigea
            ),
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = "Icono Email", tint = TextBeigea.copy(alpha = 0.7f))
            }
        )

        Spacer(modifier = Modifier.height(spacingM))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Contraseña", style = TextStyle(fontFamily = InriaSerif, color = TextBeigea.copy(alpha = 0.6f), fontSize = 18.sp))
            },
            textStyle = TextStyle(fontFamily = InriaSerif, color = TextBeigea, fontSize = 18.sp),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBordera,
                unfocusedBorderColor = AccentBordera,
                cursorColor = BgBeigea
            ),
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "Icono Contraseña", tint = TextBeigea.copy(alpha = 0.7f))
            }
        )

        Spacer(modifier = Modifier.height(spacingM))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Comfirmar Contraseña", style = TextStyle(fontFamily = InriaSerif, color = TextBeigea.copy(alpha = 0.6f), fontSize = 18.sp))
            },
            textStyle = TextStyle(fontFamily = InriaSerif, color = TextBeigea, fontSize = 18.sp),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBordera,
                unfocusedBorderColor = AccentBordera,
                cursorColor = BgBeigea
            ),
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "Icono Contraseña", tint = TextBeigea.copy(alpha = 0.7f))
            }
        )


        Spacer(modifier = Modifier.height(spacingL))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BgBeigea, contentColor = TextDarka),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Registrarse", style = TextStyle(fontFamily = InriaSerif, fontSize = 18.sp, fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.height(spacingM))

        Text(
            text = "¿Ya tienes una cuenta?",
            style = TextStyle(fontFamily = InriaSerif, fontSize = 18.sp, color = TextBeigea.copy(alpha = 0.8f), textAlign = TextAlign.Center)
        )

        Spacer(modifier = Modifier.height(spacingXS))

        Text(
            text = "Inicia sesión",
            style = TextStyle(
                fontFamily = InriaSerif, fontSize = 18.sp, color = TextBeigea,
                textDecoration = TextDecoration.Underline, textAlign = TextAlign.Center
            ),
            modifier = Modifier.clickable { }
        )


    }
}