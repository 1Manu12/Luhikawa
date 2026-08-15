package com.example.luhikawa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Edit

val BgDarker = Color(0xFF1A1717)
val BgBeigeer = Color(0xFFC7AF93)
val TextBeigeer = Color(0xFFC7AF93)
val TextDarkeer = Color(0xFF1A1717)
val AccentBorderer = Color(0xFFC7AF93)

val CustomFontFamilyer = FontFamily.Serif

class MainActivityIA : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgDarka
                ) {
                    AiScreen()
                }
            }
        }
    }
}

@Composable
fun AiScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDarker)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Menú", tint = TextBeigea)
                Image(
                    painter = painterResource(id = R.drawable.logolk),
                    contentDescription = "Logo",
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI",
                    style = TextStyle(
                        fontFamily = InriaSerif,
                        fontSize = 32.sp,
                        color = TextBeigea,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BgBeigea)
                ) {
                    Text(
                        text = "¿Para qué sirve organizar...?",
                        modifier = Modifier.padding(14.dp),
                        style = TextStyle(
                            fontFamily = InriaSerif,
                            fontSize = 15.sp,
                            color = TextDarka
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Spacer(modifier = Modifier.width(40.dp))
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BgBeigea)
                ) {
                    Text(
                        text = "Porque ayuda a...",
                        modifier = Modifier.padding(14.dp),
                        style = TextStyle(
                            fontFamily = InriaSerif,
                            fontSize = 15.sp,
                            color = TextDarka
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Aportaciones de la IA",
                style = TextStyle(
                    fontFamily = InriaSerif,
                    fontSize = 20.sp,
                    color = TextBeigea,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BgBeigea),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Sugerencia\nde tarea",
                            style = TextStyle(
                                fontFamily = InriaSerif,
                                fontSize = 13.sp,
                                color = TextDarka,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TextDarka, modifier = Modifier.size(20.dp))
                    }
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BgBeigea),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Optimización\nde contenido",
                            style = TextStyle(
                                fontFamily = InriaSerif,
                                fontSize = 13.sp,
                                color = TextDarka,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.Edit, contentDescription = null, tint = TextDarka, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BgBeigea),
                    modifier = Modifier.width(160.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Frases de\nmotivación",
                            style = TextStyle(
                                fontFamily = InriaSerif,
                                fontSize = 13.sp,
                                color = TextDarka,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.Face, contentDescription = null, tint = TextDarka, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Image(
                    painter = painterResource(id = R.drawable.gato3),
                    contentDescription = "Gato decorativo",
                    modifier = Modifier.size(130.dp),
                    contentScale = ContentScale.Fit
                )
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