package com.example.luhikawa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val BgDarke = Color(0xFF1A1717)
val BgBeigee = Color(0xFFC7AF93)
val TextBeigee = Color(0xFFC7AF93)
val TextDarkee = Color(0xFF1A1717)
val AccentBordere = Color(0xFFC7AF93)

val CustomFontFamilye = FontFamily.Serif

class MainActivityCalendar : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgDarka
                ) {
                    CalendarScreen()
                }
            }
        }
    }
}

@Composable
fun CalendarScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDarka)
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextBeigea)
                Image(painter = painterResource(id = R.drawable.logolk), contentDescription = "Logo", modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = TextBeigea)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Mayo 2026", style = TextStyle(fontFamily = InriaSerif, fontSize = 22.sp, color = TextBeigea))
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = TextBeigea)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .border(1.dp, AccentBordera, RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Tareas asignadas", style = TextStyle(fontFamily = InriaSerif, fontSize = 20.sp, color = TextBeigea, fontWeight = FontWeight.Bold))

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = BgBeigea),
                border = BorderStroke(1.dp, AccentBordera)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(24.dp).border(1.dp, TextDarka, CircleShape))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Presentación", style = TextStyle(fontFamily = InriaSerif, fontSize = 18.sp, color = TextDarka))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, AccentBordera)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(24.dp).border(1.dp, TextBeigea, CircleShape))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Presentación", style = TextStyle(fontFamily = InriaSerif, fontSize = 18.sp, color = TextBeigea))
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.DateRange, contentDescription = "Calendario", tint = TextBeigea, modifier = Modifier.size(26.dp))
            Icon(Icons.Default.Cloud, contentDescription = "Clima", tint = TextBeigea, modifier = Modifier.size(26.dp))
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .border(1.dp, AccentBordera, CircleShape)
            )
            Icon(Icons.Default.CheckCircle, contentDescription = "Reloj", tint = TextBeigea, modifier = Modifier.size(26.dp))
            Icon(Icons.Default.Person, contentDescription = "Perfil", tint = TextBeigea, modifier = Modifier.size(26.dp))
        }
    }
}