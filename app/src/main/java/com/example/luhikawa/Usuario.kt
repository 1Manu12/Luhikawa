package com.example.luhikawa

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.io.ByteArrayOutputStream
import java.io.InputStream
import androidx.compose.ui.graphics.asImageBitmap


class MainActivityPerfil : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "greeting"
                    ) {
                        composable(route = "perfil") {
                            PerfilScreen(navController = navController)
                        }

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

                        composable(route = "perfil") {
                            PerfilScreen(navController = navController)
                        }

                        // CalendarScreen()
                        // AiScreen()
                    }
                }
            }
        }
    }
}
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavController) {
    var fotoPerfilBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showAccountDialog by remember { mutableStateOf(false) }
    var selectedAccount by remember { mutableStateOf("") }
    var cuentasGuardadas by remember { mutableStateOf<List<String>>(emptyList()) }
    var fotoPerfilUrl by remember { mutableStateOf("") }
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    var showDatosPersonales by remember { mutableStateOf(false) }
    var showHistorial by remember { mutableStateOf(false) }
    var showPreferencias by remember { mutableStateOf(false) }

    var tareasCompletadas by remember { mutableStateOf<List<String>>(emptyList()) }

    var notificacionesActivas by remember { mutableStateOf(true) }
    var temaOscuro by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var horaNotificacion by remember { mutableStateOf(8) }
    var minutoNotificacion by remember { mutableStateOf(30) }

    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        fotoPerfilUrl = document.getString("fotoPerfil") ?: ""
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("PerfilScreen", "Error al cargar foto de perfil: ${e.message}")
                }
        }
    }

    LaunchedEffect(Unit) {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val nombres = result.documents.mapNotNull { doc ->
                    doc.getString("nombreCompleto")
                }
                cuentasGuardadas = nombres
                if (selectedAccount.isEmpty() && nombres.isNotEmpty()) {
                    selectedAccount = nombres.first()
                }
            }
            .addOnFailureListener { e ->
                Log.e("PerfilScreen", "Error al cargar cuentas: ${e.message}")
            }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            guardarFotoPerfilBase64(uri, context, selectedAccount) { bitmap: Bitmap, base64: String ->
                fotoPerfilBitmap = bitmap
            }
        }
    }

    LaunchedEffect(selectedAccount) {
        if (selectedAccount.isNotEmpty()) {
            db.collection("users")
                .whereEqualTo("nombreCompleto", selectedAccount)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val base64 = result.documents[0].getString("fotoBase64")
                        if (!base64.isNullOrEmpty()) {
                            fotoPerfilBitmap = base64ToBitmap(base64)
                        }
                    }
                }
        }
    }

    val timePickerDialog = android.app.TimePickerDialog(
        context,
        R.style.CustomTimePickerTheme,
        { _, hourOfDay, minute ->
            horaNotificacion = hourOfDay
            minutoNotificacion = minute

            programarNotificacionLocal(
                context = context,
                hora = hourOfDay,
                minuto = minute,
                titulo = "¡Miau! Hora de revisar 🐾",
                mensaje = "Tienes tareas pendientes en tu agenda."
            )

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
            .background(BgDarka)
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
                        .padding(top = 50.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BgBeigea)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp, bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (selectedAccount.isEmpty()) "Sin cuenta" else selectedAccount,
                            style = TextStyle(
                                fontFamily = InriaSerif,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarka
                            ),
                            modifier = Modifier.clickable { showAccountDialog = true }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(BgDarka)
                        .border(BorderStroke(2.dp, BgBeigea), CircleShape)
                        .clickable { imagePicker.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (fotoPerfilBitmap != null) {
                        Image(
                            bitmap = fotoPerfilBitmap!!.asImageBitmap(),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop // Asegura que la imagen llene el círculo
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = BgBeigea,
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
                    color = TextBeigea
                ),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(20.dp))

            PerfilOptionButton(
                text = "Datos personales",
                icon = Icons.Outlined.Assignment,
                onClick = { showDatosPersonales = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PerfilOptionButton(
                text = "Historial",
                icon = Icons.Outlined.StarBorder,
                onClick = { showHistorial = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PerfilOptionButton(
                text = "Preferencias",
                icon = Icons.Outlined.FavoriteBorder,
                onClick = { showPreferencias = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PerfilOptionButton(
                text = "Notificaciones ".format(horaNotificacion, minutoNotificacion),
                icon = Icons.Outlined.Notifications,
                onClick = { timePickerDialog.show() }
            )
        }
    }

    // Diálogos AlertDialog...
    if (showAccountDialog) {
        AlertDialog(
            onDismissRequest = { showAccountDialog = false },
            containerColor = BgDarka,
            title = {
                Text(
                    text = "Cambiar de cuenta",
                    fontFamily = InriaSerif,
                    fontWeight = FontWeight.Bold,
                    color = TextBeigea
                )
            },
            text = {
                Column {
                    if (cuentasGuardadas.isEmpty()) {
                        Text(
                            text = "No hay cuentas guardadas",
                            fontFamily = InriaSerif,
                            color = TextBeigea.copy(alpha = 0.6f)
                        )
                    } else {
                        cuentasGuardadas.forEach { cuenta ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedAccount = cuenta
                                        showAccountDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (cuenta == selectedAccount) BgBeigea else TextBeigea.copy(alpha = 0.5f),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = cuenta,
                                    fontFamily = InriaSerif,
                                    fontSize = 18.sp,
                                    color = if (cuenta == selectedAccount) BgBeigea else TextBeigea
                                )
                                if (cuenta == selectedAccount) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Seleccionada",
                                        tint = BgBeigea,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAccountDialog = false }) {
                    Text("Cerrar", fontFamily = InriaSerif, color = TextBeigea)
                }
            }
        )
    }

    if (showDatosPersonales) {
        var nombreEditable by remember { mutableStateOf(selectedAccount) }
        var correoEditable by remember { mutableStateOf("usuario@correo.com") }

        AlertDialog(
            onDismissRequest = { showDatosPersonales = false },
            containerColor = BgDarka,
            title = {
                Text(
                    text = "Datos personales",
                    fontFamily = InriaSerif,
                    fontWeight = FontWeight.Bold,
                    color = TextBeigea
                )
            },
            text = {
                Column {
                    Text(
                        text = "Nombre",
                        fontFamily = InriaSerif,
                        fontSize = 14.sp,
                        color = TextBeigea.copy(alpha = 0.6f)
                    )
                    OutlinedTextField(
                        value = nombreEditable,
                        onValueChange = { nombreEditable = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontFamily = InriaSerif, color = TextBeigea),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BgBeigea,
                            unfocusedBorderColor = TextBeigea.copy(alpha = 0.4f),
                            cursorColor = BgBeigea
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Correo",
                        fontFamily = InriaSerif,
                        fontSize = 14.sp,
                        color = TextBeigea.copy(alpha = 0.6f)
                    )
                    OutlinedTextField(
                        value = correoEditable,
                        onValueChange = { correoEditable = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontFamily = InriaSerif, color = TextBeigea),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BgBeigea,
                            unfocusedBorderColor = TextBeigea.copy(alpha = 0.4f),
                            cursorColor = BgBeigea
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedAccount = nombreEditable
                        showDatosPersonales = false
                    }
                ) {
                    Text("Guardar", fontFamily = InriaSerif, color = BgBeigea)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatosPersonales = false }) {
                    Text("Cancelar", fontFamily = InriaSerif, color = TextBeigea)
                }
            }
        )
    }

    if (showHistorial) {
        AlertDialog(
            onDismissRequest = { showHistorial = false },
            containerColor = BgDarka,
            title = {
                Text(
                    text = "Historial",
                    fontFamily = InriaSerif,
                    fontWeight = FontWeight.Bold,
                    color = TextBeigea
                )
            },
            text = {
                Column {
                    if (tareasCompletadas.isEmpty()) {
                        Text(
                            text = "Aún no has completado tareas.",
                            fontFamily = InriaSerif,
                            fontSize = 16.sp,
                            color = TextBeigea.copy(alpha = 0.6f)
                        )
                    } else {
                        tareasCompletadas.forEach { tarea ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = BgBeigea,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = tarea,
                                    fontFamily = InriaSerif,
                                    fontSize = 16.sp,
                                    color = TextBeigea
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHistorial = false }) {
                    Text("Cerrar", fontFamily = InriaSerif, color = TextBeigea)
                }
            }
        )
    }

    if (showPreferencias) {
        AlertDialog(
            onDismissRequest = { showPreferencias = false },
            containerColor = BgDarka,
            title = {
                Text(
                    text = "Preferencias",
                    fontFamily = InriaSerif,
                    fontWeight = FontWeight.Bold,
                    color = TextBeigea
                )
            },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { notificacionesActivas = !notificacionesActivas }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Notificaciones",
                            fontFamily = InriaSerif,
                            fontSize = 18.sp,
                            color = TextBeigea,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = if (notificacionesActivas) "Activadas" else "Desactivadas",
                            fontFamily = InriaSerif,
                            fontSize = 16.sp,
                            color = if (notificacionesActivas) BgBeigea else TextBeigea.copy(alpha = 0.5f)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { temaOscuro = !temaOscuro }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tema oscuro",
                            fontFamily = InriaSerif,
                            fontSize = 18.sp,
                            color = TextBeigea,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = if (temaOscuro) "Activado" else "Desactivado",
                            fontFamily = InriaSerif,
                            fontSize = 16.sp,
                            color = if (temaOscuro) BgBeigea else TextBeigea.copy(alpha = 0.5f)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPreferencias = false }) {
                    Text("Cerrar", fontFamily = InriaSerif, color = TextBeigea)
                }
            }
        )
    }
    if (showAccountDialog) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        AlertDialog(
            onDismissRequest = { showAccountDialog = false },
            containerColor = BgDarka,
            title = {
                Text(
                    text = "Gestión de cuenta",
                    fontFamily = InriaSerif,
                    fontWeight = FontWeight.Bold,
                    color = TextBeigea
                )
            },
            text = {
                Column {
                    if (cuentasGuardadas.isNotEmpty()) {
                        Text(
                            text = "Cuentas en este dispositivo:",
                            fontFamily = InriaSerif,
                            fontSize = 14.sp,
                            color = TextBeigea.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        cuentasGuardadas.forEach { cuenta ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedAccount = cuenta
                                        showAccountDialog = false
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (cuenta == selectedAccount) BgBeigea else TextBeigea.copy(alpha = 0.5f),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = cuenta,
                                    fontFamily = InriaSerif,
                                    fontSize = 16.sp,
                                    color = if (cuenta == selectedAccount) BgBeigea else TextBeigea
                                )
                            }
                        }
                        Divider(color = TextBeigea.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))
                    }

                    if (currentUser != null) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    FirebaseAuth.getInstance().signOut()
                                    showAccountDialog = false
                                    navController.navigate("login") { // Reemplaza por tu ruta de Login
                                        popUpTo(0)
                                    }
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar sesión",
                                tint = Color(0xFFFF6B6B),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Cerrar sesión",
                                fontFamily = InriaSerif,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF6B6B)
                            )
                        }
                    } else {
                        // Si no hay sesión activa
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showAccountDialog = false
                                    navController.navigate("login") // Reemplaza por tu ruta de Login/Registro
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Login,
                                contentDescription = "Iniciar sesión",
                                tint = BgBeigea,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Iniciar sesión / Registrarse",
                                fontFamily = InriaSerif,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BgBeigea
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAccountDialog = false }) {
                    Text("Cerrar", fontFamily = InriaSerif, color = TextBeigea)
                }
            }
        )
    }
}
fun guardarFotoPerfilBase64(
    uri: Uri,
    context: Context,
    selectedAccount: String,
    onBitmapCargado: (Bitmap, String) -> Unit
) {
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bitmapOriginal = BitmapFactory.decodeStream(inputStream)

        if (bitmapOriginal != null) {
            // Redimensionar imagen para optimizar espacio en Firestore
            val maxDimension = 300
            val ratio = Math.min(
                maxDimension.toFloat() / bitmapOriginal.width,
                maxDimension.toFloat() / bitmapOriginal.height
            )
            val width = Math.round(ratio * bitmapOriginal.width)
            val height = Math.round(ratio * bitmapOriginal.height)
            val bitmapReducido = Bitmap.createScaledBitmap(bitmapOriginal, width, height, true)

            // Convertir Bitmap a Base64
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmapReducido.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

            // Guardar en Firestore
            val db = FirebaseFirestore.getInstance()
            if (selectedAccount.isNotEmpty()) {
                db.collection("users")
                    .whereEqualTo("nombreCompleto", selectedAccount)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val docId = querySnapshot.documents[0].id
                            db.collection("users").document(docId)
                                .set(mapOf("fotoBase64" to base64String), SetOptions.merge())
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Foto actualizada", Toast.LENGTH_SHORT).show()
                                    onBitmapCargado(bitmapReducido, base64String)
                                }
                        }
                    }
            }
        }
    } catch (e: Exception) {
        Log.e("PerfilScreen", "Error: ${e.message}")
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
        border = BorderStroke(1.dp, BgBeigea.copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextBeigea)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BgBeigea,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontFamily = InriaSerif,
                fontSize = 20.sp,
                color = TextBeigea,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}



@Composable
fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    tint: Color = TextBeigea,
    onClick: () -> Unit
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = tint,
        modifier = Modifier
            .size(26.dp)
            .clickable(onClick = onClick)
    )
}

@Composable
fun ParteAbajo(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgDarka)
            .padding(vertical = 16.dp, horizontal = 1.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            icon = Icons.Default.CalendarMonth,
            contentDescription = "Calendario",
            tint = Color.White,
            onClick = { navController.navigate("calendario") }
        )

        BottomNavItem(
            icon = Icons.Default.AutoAwesome,
            contentDescription = "IA",
            tint = Color.White,
            onClick = { /* IA*/ }
        )

        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
                .border(BorderStroke(1.5.dp, Color.White), CircleShape)
                .clickable {
                    navController.navigate("recordatorio")
                }
        )

        BottomNavItem(
            icon = Icons.Default.Home,
            contentDescription = "Agenda",
            tint = Color.White,
            onClick = { navController.navigate("greeting") }
        )

        BottomNavItem(
            icon = Icons.Default.PersonOutline,
            contentDescription = "Perfil",
            tint = Color.White,
            onClick = { navController.navigate("perfil") }
        )
    }
}

@Composable
fun RectanguloConImagen() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .background(AccentColor32),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logolk),
            contentDescription = "Logo LK",
            modifier = Modifier
                .size(85.dp)
                .align(Alignment.CenterEnd),
            contentScale = ContentScale.Fit
        )
    }
}