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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.luhikawa.R
import com.example.luhikawa.data.AccountManager
import com.example.luhikawa.data.StoredAccount
import com.example.luhikawa.domain.services.AuthService
import com.example.luhikawa.ui.theme.*
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.security.SecureRandom

class MainActivityLogin : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val auth = remember { FirebaseAuth.getInstance() }

            // 1. Manejo del ID del usuario activo o 'guest'
            var currentUserId by remember { mutableStateOf(auth.currentUser?.uid ?: "guest") }

            DisposableEffect(auth) {
                val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                    currentUserId = firebaseAuth.currentUser?.uid ?: "guest"
                }
                auth.addAuthStateListener(authStateListener)
                onDispose {
                    auth.removeAuthStateListener(authStateListener)
                }
            }

            // 2. Instancia reactiva del ViewModel usando el UID como key independiente
            val themeViewModel: ThemeViewModel = viewModel(key = currentUserId)

            LaunchedEffect(currentUserId) {
                themeViewModel.setUser(currentUserId)
            }

            // 3. Escuchar el color actual del tema de la cuenta
            val currentThemeColor by themeViewModel.appThemeColor.collectAsState()

            // 4. Aplicar el tema dinámico
            luhikawaTheme(appTheme = currentThemeColor) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
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
    var isLoading by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }
    val credentialManager = remember { CredentialManager.create(context) }
    val accountManager = remember { AccountManager(context) }

    val spacingXS = 24.dp
    val spacingS = 80.dp
    val spacingM = 16.dp
    val spacingL = 24.dp
    val spacingXL = 40.dp

    fun realizarLogin(emailFinal: String, pass: String) {
        auth.signInWithEmailAndPassword(emailFinal, pass)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Buscar datos en la colección "usuarios" (o "users" según tu base de datos)
                        db.collection("usuarios").document(user.uid).get()
                            .addOnSuccessListener { doc ->
                                val nombreGuardado = doc.getString("nombreCompleto")
                                    ?: doc.getString("usuario")
                                    ?: user.displayName
                                    ?: emailFinal

                                accountManager.saveAccount(
                                    StoredAccount(
                                        uid = user.uid,
                                        email = emailFinal,
                                        password = pass,
                                        displayName = nombreGuardado
                                    )
                                )
                                accountManager.setCurrentAccount(user.uid)

                                Toast.makeText(context, "¡Bienvenida de vuelta!", Toast.LENGTH_SHORT).show()
                                navController.navigate("greeting") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                            .addOnFailureListener {
                                accountManager.saveAccount(
                                    StoredAccount(
                                        uid = user.uid,
                                        email = emailFinal,
                                        password = pass,
                                        displayName = user.displayName ?: emailFinal
                                    )
                                )
                                accountManager.setCurrentAccount(user.uid)

                                navController.navigate("greeting") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                    }
                } else {
                    Toast.makeText(context, "Error de inicio de sesión: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )

        Spacer(modifier = Modifier.height(spacingXL))

        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Usuario o email", style = TextStyle(fontFamily = InriaSerif, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 18.sp))
            },
            textStyle = TextStyle(fontFamily = InriaSerif, color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = "Icono Usuario", tint = MaterialTheme.colorScheme.primary)
            }
        )

        Spacer(modifier = Modifier.height(spacingM))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Contraseña", style = TextStyle(fontFamily = InriaSerif, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 18.sp))
            },
            textStyle = TextStyle(fontFamily = InriaSerif, color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "Icono Contraseña", tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description, tint = MaterialTheme.colorScheme.primary)
                }
            }
        )

        Spacer(modifier = Modifier.height(spacingL))

        Button(
            onClick = {
                val inputUsuario = usuario.trim()
                val inputPass = contrasena.trim()

                if (inputUsuario.isBlank() || inputPass.isBlank()) {
                    Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true

                if (!inputUsuario.contains("@")) {
                    db.collection("usuarios")
                        .whereEqualTo("usuario", inputUsuario)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                val emailEncontrado = querySnapshot.documents[0].getString("email") ?: ""
                                realizarLogin(emailEncontrado, inputPass)
                            } else {
                                isLoading = false
                                Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener {
                            isLoading = false
                            Toast.makeText(context, "Error al verificar usuario", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    realizarLogin(inputUsuario, inputPass)
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                if (isLoading) "Cargando..." else "Inicio de sesión",
                style = TextStyle(fontFamily = InriaSerif, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            )
        }

        Spacer(modifier = Modifier.height(spacingM))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape)
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
                                        val user = auth.currentUser
                                        user?.let {
                                            accountManager.saveAccount(
                                                StoredAccount(
                                                    uid = it.uid,
                                                    email = it.email ?: "",
                                                    password = "",
                                                    displayName = it.displayName ?: it.email ?: ""
                                                )
                                            )
                                            accountManager.setCurrentAccount(it.uid)
                                        }

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
            style = TextStyle(fontFamily = InriaSerif, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f), textAlign = TextAlign.Center)
        )

        Spacer(modifier = Modifier.height(spacingXS))

        Text(
            text = "Registrate",
            style = TextStyle(
                fontFamily = InriaSerif, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline, textAlign = TextAlign.Center
            ),
            modifier = Modifier.clickable {
                navController.navigate("registro")
            }
        )

        Spacer(modifier = Modifier.height(spacingL))
    }
}