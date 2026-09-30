package com.example.luhikawa.ui

import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.luhikawa.ui.theme.*
import com.example.luhikawa.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.security.SecureRandom
import com.example.luhikawa.domain.services.AuthService
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

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
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") {
                            LoginScreen(navController = navController)
                        }
                        composable("greeting") {
                            Greeting(navController = navController)
                        }

                        composable("registro") {
                            RegistroScreen(navController = navController)
                        }
                    }

                        }
                    }
                }
            }
        }

@Composable
fun LoginScreen(navController: NavController) {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }
    val credentialManager = remember { CredentialManager.create(context) }

    val spacingXS = 24.dp
    val spacingS = 80.dp
    val spacingM = 16.dp
    val spacingL = 24.dp
    val spacingXL = 40.dp

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
            modifier = Modifier.size(70.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(spacingXS))

        Image(
            painter = painterResource(id = R.drawable.titlebeige),
            contentDescription = "LUHIKAWA",
            modifier = Modifier
                .fillMaxWidth(0.85f)
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
                Text("Usuario o email", style = TextStyle(fontFamily = InriaSerif, color = TextBeigea.copy(alpha = 0.6f), fontSize = 18.sp))
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
            value = contrasena,
            onValueChange = { contrasena = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Contraseña", style = TextStyle(fontFamily = InriaSerif, color = TextBeigea.copy(alpha = 0.6f), fontSize = 18.sp))
            },
            textStyle = TextStyle(fontFamily = InriaSerif, color = TextBeigea, fontSize = 18.sp),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBordera,
                unfocusedBorderColor = AccentBordera,
                cursorColor = BgBeigea
            ),
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "Icono Contraseña", tint = TextBeigea.copy(alpha = 0.7f))
            },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description, tint = TextBeigea.copy(alpha = 0.7f))
                }
            }
        )

        Spacer(modifier = Modifier.height(spacingL))

        Button(
            onClick = {
                if (usuario.isBlank() || contrasena.isBlank()) {
                    Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                auth.signInWithEmailAndPassword(usuario, contrasena)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "¡Bienvenida de vuelta!", Toast.LENGTH_SHORT).show()
                            navController.navigate("greeting") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "Error: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BgBeigea, contentColor = TextDarka),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Inicio de sesión", style = TextStyle(fontFamily = InriaSerif, fontSize = 18.sp, fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.height(spacingM))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(BgDarka, shape = CircleShape)
                    .clickable {
                        coroutineScope.launch {
                            try {
                                val secureRandom = SecureRandom()
                                val bytes = ByteArray(64)
                                secureRandom.nextBytes(bytes)
                                val nonce = Base64.encodeToString(bytes, Base64.NO_WRAP)

                                val googleIdOption = GetGoogleIdOption.Builder()
                                    .setServerClientId("550323438631-7ju4fallk6fvg6vce4s1vbdtfguvb4tp.apps.googleusercontent.com")
                                    .setFilterByAuthorizedAccounts(false)
                                    .setNonce(nonce)
                                    .build()

                                val request = GetCredentialRequest.Builder()
                                    .addCredentialOption(googleIdOption)
                                    .build()

                                val result = credentialManager.getCredential(
                                    context = context,
                                    request = request
                                )

                                AuthService.handleGoogleCredentialResponse(
                                    result = result,
                                    auth = auth,
                                    db = db,
                                    context = context,
                                    onSuccess = {
                                        navController.navigate("greeting") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                )

                            } catch (e: Exception) {
                                Toast.makeText(context, "Error de Google: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Registrarse con Google",
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(spacingM))

        Text(
            text = "¿Olvidaste tu contraseña?",
            style = TextStyle(fontFamily = InriaSerif, fontSize = 18.sp, color = TextBeigea.copy(alpha = 0.8f), textAlign = TextAlign.Center)
        )

        Spacer(modifier = Modifier.height(spacingXS))

        Text(
            text = "Registrate",
            style = TextStyle(
                fontFamily = InriaSerif, fontSize = 18.sp, color = TextBeigea,
                textDecoration = TextDecoration.Underline, textAlign = TextAlign.Center
            ),
            modifier = Modifier.clickable {
                navController.navigate("registro")
            }
        )

        Spacer(modifier = Modifier.height(spacingL))

    }
}
