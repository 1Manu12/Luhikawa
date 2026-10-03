package com.example.luhikawa.ui

import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.luhikawa.R
import com.example.luhikawa.data.AccountManager
import com.example.luhikawa.data.StoredAccount
import com.example.luhikawa.data.UserRepository
import com.example.luhikawa.ui.HomeComponents.HeaderSection
import com.example.luhikawa.ui.HomeComponents.ParteAbajo
import com.example.luhikawa.ui.components.SeccionCambiarTema
import com.example.luhikawa.ui.theme.InriaSerif
import com.example.luhikawa.ui.theme.ThemeViewModel
import com.example.luhikawa.ui.theme.luhikawaTheme
import com.google.firebase.auth.FirebaseAuth
import java.io.File

class MainActivityPerfil : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val rutaInicial = if (auth.currentUser != null) "greeting" else "login"

        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val context = LocalContext.current
            val accountManager = remember { AccountManager(context) }

            var currentUsuarioId by remember {
                mutableStateOf(auth.currentUser?.uid ?: accountManager.getCurrentAccountUid() ?: "guest")
            }

            DisposableEffect(auth) {
                val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                    val newUid = firebaseAuth.currentUser?.uid ?: accountManager.getCurrentAccountUid() ?: "guest"
                    currentUsuarioId = newUid
                }
                auth.addAuthStateListener(authStateListener)
                onDispose {
                    auth.removeAuthStateListener(authStateListener)
                }
            }

            val themeViewModel: ThemeViewModel = viewModel(key = currentUsuarioId)

            LaunchedEffect(currentUsuarioId) {
                themeViewModel.setUser(currentUsuarioId)
            }

            val currentThemeColor by themeViewModel.appThemeColor.collectAsState()

            luhikawaTheme(appTheme = currentThemeColor) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        containerColor = MaterialTheme.colorScheme.background,
                        bottomBar = {
                            if (currentRoute != null && currentRoute != "login" && currentRoute != "registro") {
                                ParteAbajo(navController = navController)
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = rutaInicial,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("login") { LoginScreen(navController = navController) }
                            composable("registro") { RegistroScreen(navController = navController) }
                            composable("greeting") { Greeting(navController = navController) }

                            composable("perfil") {
                                PerfilScreen(
                                    navController = navController,
                                    themeViewModel = themeViewModel
                                )
                            }

                            composable("calendario") { CalendarScreen(navController = navController) }
                            composable("ia") { AiScreen() }
                            composable(
                                route = "recordatorio?taskId={taskId}",
                                arguments = listOf(
                                    navArgument("taskId") {
                                        type = NavType.StringType
                                        nullable = true
                                        defaultValue = null
                                    }
                                )
                            ) { backStackEntry ->
                                val taskId = backStackEntry.arguments?.getString("taskId")
                                RecordatorioScreen(navController = navController, taskId = taskId)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun solicitarHuellaDigital(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val biometricManager = BiometricManager.from(activity)
    val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL

    when (biometricManager.canAuthenticate(authenticators)) {
        BiometricManager.BIOMETRIC_SUCCESS -> {
            val executor = ContextCompat.getMainExecutor(activity)
            val biometricPrompt = BiometricPrompt(
                activity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        onSuccess()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        onError("Autenticación cancelada: $errString")
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        onError("Huella no reconocida. Intenta de nuevo.")
                    }
                }
            )

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Confirmar identidad")
                .setSubtitle("Coloca tu huella digital para acceder a la información del perfil")
                .setAllowedAuthenticators(authenticators)
                .build()

            biometricPrompt.authenticate(promptInfo)
        }
        else -> {
            Toast.makeText(activity, "Biometría no configurada. Accediendo...", Toast.LENGTH_SHORT).show()
            onSuccess()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel = viewModel(),
    userRepository: UserRepository = remember { UserRepository() }
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val accountManager = remember { AccountManager(context) }

    var cuentasGuardadas by remember { mutableStateOf<List<StoredAccount>>(emptyList()) }
    var selectedUid by remember { mutableStateOf(accountManager.getCurrentAccountUid()) }
    var currentUser by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }

    var fotoPerfilBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showAccountDialog by remember { mutableStateOf(false) }
    var showFotoDialog by remember { mutableStateOf(false) }

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Estado de Diálogos
    var showDatosPersonales by remember { mutableStateOf(false) }
    var showEditarCuentaDialog by remember { mutableStateOf(false) }
    var showCambiarPasswordDialog by remember { mutableStateOf(false) }

    var showHistorial by remember { mutableStateOf(false) }
    var showPreferencias by remember { mutableStateOf(false) }

    var horaNotificacion by remember { mutableStateOf(8) }
    var minutoNotificacion by remember { mutableStateOf(30) }

    // Campos de formulario para edición
    var nuevoNombre by remember { mutableStateOf("") }
    var nuevoEmail by remember { mutableStateOf("") }

    var passActual by remember { mutableStateOf("") }
    var nuevaPass by remember { mutableStateOf("") }

    val activeStoredAccount = remember(cuentasGuardadas, selectedUid, currentUser) {
        cuentasGuardadas.find { it.uid == selectedUid }
            ?: cuentasGuardadas.find { it.uid == currentUser?.uid }
    }

    val nombreMostrar = currentUser?.displayName?.ifBlank { null }
        ?: currentUser?.email?.ifBlank { null }
        ?: activeStoredAccount?.displayName?.ifBlank { null }
        ?: activeStoredAccount?.email?.ifBlank { null }
        ?: "Sin cuenta"

    val emailMostrar = currentUser?.email?.ifBlank { null }
        ?: activeStoredAccount?.email?.ifBlank { null }
        ?: "Sin email"

    fun procesarImagenFinal(uri: Uri?) {
        val activeUid = currentUser?.uid ?: selectedUid
        if (uri != null && !activeUid.isNullOrEmpty()) {
            userRepository.guardarFotoPerfilBase64(uri, context, activeUid) { bitmap, _ ->
                fotoPerfilBitmap = bitmap
                Toast.makeText(context, "Foto actualizada", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Inicia sesión para cambiar la foto", Toast.LENGTH_SHORT).show()
        }
    }

    val cropImageLauncher = rememberLauncherForActivityResult(
        contract = CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            procesarImagenFinal(result.uriContent)
        } else if (result.error != null) {
            Toast.makeText(context, "Error al recortar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    fun lanzarEditorDeImagen(inputUri: Uri) {
        try {
            val destinationFile = File.createTempFile("profile_cropped_", ".jpg", context.cacheDir)
            val destinationUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                destinationFile
            )

            cropImageLauncher.launch(
                CropImageContractOptions(
                    uri = inputUri,
                    cropImageOptions = CropImageOptions(
                        guidelines = CropImageView.Guidelines.ON,
                        aspectRatioX = 1,
                        aspectRatioY = 1,
                        fixAspectRatio = true,
                        cropShape = CropImageView.CropShape.OVAL,
                        allowRotation = true,
                        allowCounterRotation = true,
                        allowFlipping = true,
                        activityTitle = "Ajustar foto",
                        cropMenuCropButtonTitle = "GUARDAR",
                        outputCompressFormat = android.graphics.Bitmap.CompressFormat.JPEG,
                        outputCompressQuality = 90,
                        customOutputUri = destinationUri
                    )
                )
            )
        } catch (e: Exception) {
            Toast.makeText(context, "Error al preparar el editor: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { lanzarEditorDeImagen(it) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempCameraUri != null) {
            lanzarEditorDeImagen(tempCameraUri!!)
        }
    }

    LaunchedEffect(selectedUid, showAccountDialog, currentUser, showEditarCuentaDialog) {
        cuentasGuardadas = accountManager.getSavedAccounts()
        val targetUid = currentUser?.uid ?: selectedUid
        if (!targetUid.isNullOrEmpty()) {
            userRepository.getUserPhotoBase64(targetUid) { base64 ->
                fotoPerfilBitmap = if (!base64.isNullOrEmpty()) base64ToBitmap(base64) else null
            }
        } else {
            fotoPerfilBitmap = null
        }
    }

    val timePickerDialog = TimePickerDialog(
        context,
        R.style.CustomTimePickerTheme,
        { _, hourOfDay, minute ->
            horaNotificacion = hourOfDay
            minutoNotificacion = minute

            Toast.makeText(
                context,
                "Notificación programada a las %02d:%02d".format(hourOfDay, minute),
                Toast.LENGTH_SHORT
            ).show()
        },
        horaNotificacion,
        minutoNotificacion,
        true
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HeaderSection()

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp)
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp)
                        .clickable { showAccountDialog = true },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp, bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = nombreMostrar,
                            style = TextStyle(
                                fontFamily = InriaSerif,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.background)
                        .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary), CircleShape)
                        .clickable { showFotoDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (fotoPerfilBitmap != null) {
                        Image(
                            bitmap = fotoPerfilBitmap!!.asImageBitmap(),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Información del perfil",
                style = TextStyle(
                    fontFamily = InriaSerif,
                    fontSize = 26.sp,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(20.dp))

            PerfilOptionButton(
                text = "Datos personales",
                icon = Icons.AutoMirrored.Outlined.Assignment,
                onClick = {
                    if (activity != null) {
                        solicitarHuellaDigital(
                            activity = activity,
                            onSuccess = { showDatosPersonales = true },
                            onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
                        )
                    } else {
                        showDatosPersonales = true
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PerfilOptionButton(
                text = "Preferencias",
                icon = Icons.Outlined.FavoriteBorder,
                onClick = { showPreferencias = true }
            )

            if (showPreferencias) {
                AlertDialog(
                    onDismissRequest = { showPreferencias = false },
                    containerColor = MaterialTheme.colorScheme.surface,
                    title = {
                        Text(
                            text = "Preferencias",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {

                            SeccionCambiarTema(themeViewModel = themeViewModel)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showPreferencias = false }) {
                            Text(
                                text = "Cerrar",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PerfilOptionButton(
                text = "Notificaciones",
                icon = Icons.Outlined.Notifications,
                onClick = { timePickerDialog.show() }
            )
        }
    }

    //Datos personales
    if (showDatosPersonales) {
        AlertDialog(
            onDismissRequest = { showDatosPersonales = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Datos personales",
                    fontFamily = InriaSerif,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Nombre", fontFamily = InriaSerif, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Text(nombreMostrar, fontFamily = InriaSerif, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Email", fontFamily = InriaSerif, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Text(emailMostrar, fontFamily = InriaSerif, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))

                    OutlinedButton(
                        onClick = {
                            nuevoNombre = nombreMostrar
                            nuevoEmail = if (emailMostrar != "Sin email") emailMostrar else ""
                            showDatosPersonales = false
                            showEditarCuentaDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Editar cuenta", fontFamily = InriaSerif, color = MaterialTheme.colorScheme.onSurface)
                    }

                    OutlinedButton(
                        onClick = {
                            passActual = ""
                            nuevaPass = ""
                            showDatosPersonales = false
                            showCambiarPasswordDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cambiar contraseña", fontFamily = InriaSerif, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDatosPersonales = false }) {
                    Text("Cerrar", fontFamily = InriaSerif, color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }

    //Editar cuenta dialogo
    if (showEditarCuentaDialog) {
        var isSaving by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isSaving) showEditarCuentaDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Editar cuenta",
                    fontFamily = InriaSerif,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = nuevoNombre,
                        onValueChange = { nuevoNombre = it },
                        label = { Text("Nombre de usuario", color = MaterialTheme.colorScheme.onSurface) },
                        enabled = !isSaving,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nuevoEmail,
                        onValueChange = { nuevoEmail = it },
                        label = { Text("Email", color = MaterialTheme.colorScheme.onSurface) },
                        enabled = !isSaving,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isSaving) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = !isSaving,
                    onClick = {
                        val targetUid = currentUser?.uid ?: selectedUid ?: activeStoredAccount?.uid ?: ""

                        if (targetUid.isNotBlank()) {
                            isSaving = true

                            userRepository.editnameUser(
                                context = context,
                                targetUid = targetUid,
                                nuevoNombre = nuevoNombre,
                                accountManager = accountManager,
                                onSuccess = {
                                    if (nuevoEmail.isNotBlank() && nuevoEmail != emailMostrar) {
                                        userRepository.editEmailUser(
                                            context = context,
                                            targetUid = targetUid,
                                            nuevoEmail = nuevoEmail,
                                            accountManager = accountManager,
                                            onSuccess = {
                                                currentUser = FirebaseAuth.getInstance().currentUser
                                                isSaving = false
                                                showEditarCuentaDialog = false
                                                Toast.makeText(context, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                                            },
                                            onError = { error ->
                                                isSaving = false
                                                Toast.makeText(context, "Error al cambiar email: $error", Toast.LENGTH_LONG).show()
                                            }
                                        )
                                    } else {
                                        currentUser = FirebaseAuth.getInstance().currentUser
                                        isSaving = false
                                        showEditarCuentaDialog = false
                                        Toast.makeText(context, "Nombre actualizado correctamente", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onError = { error ->
                                    isSaving = false
                                    Toast.makeText(context, "Error al cambiar nombre: $error", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            Toast.makeText(context, "No se encontró un ID de usuario válido", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Guardar", fontFamily = InriaSerif, color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isSaving,
                    onClick = { showEditarCuentaDialog = false }
                ) {
                    Text("Cancelar", fontFamily = InriaSerif, color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }

    // dialogo cambio de contraseña
    if (showCambiarPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showCambiarPasswordDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Cambiar contraseña",
                    fontFamily = InriaSerif,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = passActual,
                        onValueChange = { passActual = it },
                        label = { Text("Contraseña actual", color = MaterialTheme.colorScheme.onSurface) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nuevaPass,
                        onValueChange = { nuevaPass = it },
                        label = { Text("Nueva contraseña", color = MaterialTheme.colorScheme.onSurface) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        userRepository.editPasswordUser(
                            context = context,
                            passActual = passActual,
                            nuevaPass = nuevaPass,
                            onSuccess = {
                                showCambiarPasswordDialog = false
                            },
                            onError = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Actualizar", fontFamily = InriaSerif, color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCambiarPasswordDialog = false }) {
                    Text("Cancelar", fontFamily = InriaSerif, color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }

    // dialogo foto de perfil
    if (showFotoDialog) {
        AlertDialog(
            onDismissRequest = { showFotoDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Foto de perfil",
                    fontFamily = InriaSerif,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showFotoDialog = false
                                galleryLauncher.launch("image/*")
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Galería",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Elegir de la Galería",
                            fontFamily = InriaSerif,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showFotoDialog = false
                                try {
                                    val photoFile = File.createTempFile("profile_cam_", ".jpg", context.cacheDir)
                                    val photoUri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        photoFile
                                    )
                                    tempCameraUri = photoUri
                                    cameraLauncher.launch(photoUri)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error al abrir la cámara", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Cámara",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Tomar una foto",
                            fontFamily = InriaSerif,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (fotoPerfilBitmap != null) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showFotoDialog = false
                                    val activeUid = currentUser?.uid ?: selectedUid ?: ""

                                    if (activeUid.isNotBlank()) {
                                        userRepository.deleteFotoDePerfil(
                                            userId = activeUid,
                                            onExito = {
                                                fotoPerfilBitmap = null
                                                Toast.makeText(context, "Foto eliminada", Toast.LENGTH_SHORT).show()
                                            },
                                            onError = { errorMsg ->
                                                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    } else {
                                        fotoPerfilBitmap = null
                                    }
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Quitar foto",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Quitar foto actual",
                                fontFamily = InriaSerif,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFotoDialog = false }) {
                    Text("Cancelar", fontFamily = InriaSerif, color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }

    // Gestor de cuentas
    if (showAccountDialog) {
        AlertDialog(
            onDismissRequest = { showAccountDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Gestión de cuenta",
                    fontFamily = InriaSerif,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column {
                    if (cuentasGuardadas.isNotEmpty()) {
                        Text(
                            text = "Cuentas en este dispositivo:",
                            fontFamily = InriaSerif,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        cuentasGuardadas.forEach { acc ->
                            val isCurrent = acc.uid == selectedUid || acc.uid == currentUser?.uid
                            val labelText = acc.displayName.ifBlank { acc.email }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (!isCurrent) {
                                            showAccountDialog = false
                                            accountManager.setCurrentAccount(acc.uid)
                                            selectedUid = acc.uid

                                            if (acc.email.contains("@") && acc.password.isNotEmpty()) {
                                                FirebaseAuth.getInstance().signOut()
                                                FirebaseAuth.getInstance()
                                                    .signInWithEmailAndPassword(acc.email, acc.password)
                                                    .addOnSuccessListener { authResult ->
                                                        currentUser = authResult.user
                                                        Toast.makeText(context, "Cuenta cambiada a $labelText", Toast.LENGTH_SHORT).show()
                                                    }
                                                    .addOnFailureListener {
                                                        currentUser = null
                                                        Toast.makeText(context, "Cuenta seleccionada: $labelText", Toast.LENGTH_SHORT).show()
                                                    }
                                            } else {
                                                currentUser = null
                                                Toast.makeText(context, "Cuenta seleccionada: $labelText", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = labelText,
                                    fontFamily = InriaSerif,
                                    fontSize = 16.sp,
                                    color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showAccountDialog = false
                                navController.navigate("login")
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar cuenta",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Agregar cuenta",
                            fontFamily = InriaSerif,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (currentUser != null || !selectedUid.isNullOrEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    FirebaseAuth.getInstance().signOut()
                                    currentUser = null
                                    selectedUid = null
                                    accountManager.setCurrentAccount("")
                                    showAccountDialog = false
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar sesión",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Cerrar sesión",
                                fontFamily = InriaSerif,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAccountDialog = false }) {
                    Text("Cerrar", fontFamily = InriaSerif, color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }
}

fun base64ToBitmap(base64Str: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        null
    }
}

@Composable
fun PerfilOptionButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontFamily = InriaSerif,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}