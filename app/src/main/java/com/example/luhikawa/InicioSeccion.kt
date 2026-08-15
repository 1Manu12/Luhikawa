package com.example.luhikawa

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


val BgDarka = Color(0xFF12100C)
val BgBeigea = Color(0xFFE0D4BE)
val TextBeigea = Color(0xFFE0D4BE)
val TextDarka = Color(0xFF12100C)
val AccentBordera = Color(0xFF9C8A6E)

val CustomFontFamilya = FontFamily.Serif

class MainActivityLogin : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgDarka
                ) {
                    LoginScreen()
                }
            }
        }
    }
}

@Composable
fun LoginScreen() {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDarka)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logolk),
            contentDescription = "Logo LK",
            modifier = Modifier.size(60.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = " L",
                style = TextStyle(
                    fontFamily = CustomFontFamilya,
                    fontSize = 28.sp,
                    color = TextBeigea
                )
            )
            Text(
                text = "UHIKAWA",
                style = TextStyle(
                    fontFamily = CustomFontFamilya,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBeigea
                )
            )
            Text(
                text = " ☽",
                style = TextStyle(
                    fontFamily = CustomFontFamilya,
                    fontSize = 28.sp,
                    color = TextBeigea
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Usuario",
                    style = TextStyle(fontFamily = CustomFontFamilya, color = TextBeigea.copy(alpha = 0.6f), fontSize = 18.sp)
                )
            },
            textStyle = TextStyle(fontFamily = CustomFontFamilya, color = TextBeigea, fontSize = 18.sp),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBordera,
                unfocusedBorderColor = AccentBordera,
                cursorColor = BgBeigea
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Icono Usuario",
                    tint = TextBeigea.copy(alpha = 0.7f)
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Contraseña",
                    style = TextStyle(fontFamily = CustomFontFamilya, color = TextBeigea.copy(alpha = 0.6f), fontSize = 18.sp)
                )
            },
            textStyle = TextStyle(fontFamily = CustomFontFamilya, color = TextBeigea, fontSize = 18.sp),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBordera,
                unfocusedBorderColor = AccentBordera,
                cursorColor = BgBeigea
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Icono Contraseña",
                    tint = TextBeigea.copy(alpha = 0.7f)
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BgBeigea,
                contentColor = TextDarka
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Inicio de sección",
                style = TextStyle(
                    fontFamily = CustomFontFamilya,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            style = TextStyle(
                fontFamily = CustomFontFamilya,
                fontSize = 18.sp,
                color = TextBeigea.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Registrate",
            style = TextStyle(
                fontFamily = CustomFontFamilya,
                fontSize = 18.sp,
                color = TextBeigea,
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.clickable { }
        )

        Spacer(modifier = Modifier.height(0.dp))

        Image(
            painter = painterResource(id = R.drawable.gato2),
            contentDescription = "Gatito durmiendo",
            modifier = Modifier
                .height(230.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )
    }
}