package com.example.luhikawa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.InriaSerif
import com.example.finalproject.UI.*
import com.example.finalproject.R

class MainActivityPerfil : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgDark3
                ) {
                    PerfilScreen()
                }
            }
        }
    }
}

@Composable
fun PerfilScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark3)
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
                    colors = CardDefaults.cardColors(containerColor = BgBeige3)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp, bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Manuela Tejada",
                            style = TextStyle(
                                fontFamily = InriaSerif,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark3
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(BgDark3)
                        .border(BorderStroke(2.dp, BgBeige3), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.person_24),
                        contentDescription = "Avatar",
                        tint = BgBeige3,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Información del perfil",
                style = TextStyle(
                    fontFamily = InriaSerif,
                    fontSize = 26.sp,
                    color = TextBeige3
                ),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(20.dp))

            PerfilOptionButton(
                text = "Datos personales",
                icon = ImageVector.vectorResource(R.drawable.assignment_24)
            )

            Spacer(modifier = Modifier.height(16.dp))

            PerfilOptionButton(
                text = "Historial",
                icon = ImageVector.vectorResource(R.drawable.star_24)
            )

            Spacer(modifier = Modifier.height(16.dp))

            PerfilOptionButton(
                text = "Preferencias",
                icon = ImageVector.vectorResource(R.drawable.favorite_24)
            )
        }

        BottomNavBarPerfil()
    }
}

@Composable
fun PerfilOptionButton(text: String, icon: ImageVector) {
    OutlinedButton(
        onClick = { },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, AccentBorder3),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = TextBeige3
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = InriaSerif,
                    fontSize = 20.sp,
                    color = TextBeige3
                )
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextBeige3,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun BottomNavBarPerfil() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgDark3)
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(icon = ImageVector.vectorResource(R.drawable.calendar_month_24), contentDescription = "Calendario")
        BottomNavItem(icon = ImageVector.vectorResource(R.drawable.cloud_24), contentDescription = "Clima")
        Box(
            modifier = Modifier
                .size(60.dp)
                .border(BorderStroke(1.dp, Color.White), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
            )
        }
        BottomNavItem(icon = ImageVector.vectorResource(R.drawable.schedule_24), contentDescription = "Agenda")
        BottomNavItem(icon = ImageVector.vectorResource(R.drawable.person_24), contentDescription = "Perfil")
    }
}