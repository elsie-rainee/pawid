package com.example.ui.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.DogBreedInfo
import com.example.ui.theme.PawAmberDark
import com.example.ui.theme.PawAmberLight
import com.example.ui.theme.PawAmberPrimary
import com.example.ui.theme.PawSuccessGreen
import com.example.ui.theme.PawSurfaceBorder
import com.example.ui.theme.PawSurfaceCard
import com.example.ui.theme.PawSurfaceCardElevated
import com.example.ui.theme.PawTextPrimary
import com.example.ui.theme.PawTextSecondary

@Composable
fun HeroBreedCard(
    info: DogBreedInfo,
    imageUri: Uri? = null,
    bitmap: Bitmap? = null,
    @DrawableRes drawableRes: Int? = null,
    modifier: Modifier = Modifier
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val progressAnimation by animateFloatAsState(
        targetValue = if (animationPlayed) info.confidencePercentage / 100f else 0f,
        animationSpec = tween(durationMillis = 1000, delayMillis = 150, easing = FastOutSlowInEasing),
        label = "confidenceProgress"
    )

    LaunchedEffect(key1 = info.breedName) {
        animationPlayed = true
    }

    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = PawSurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(PawAmberPrimary.copy(alpha = 0.5f), PawSurfaceBorder)
            ),
            width = 1.5.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("hero_breed_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Dog Image Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
                    .background(PawSurfaceCardElevated)
            ) {
                when {
                    drawableRes != null -> {
                        Image(
                            painter = painterResource(id = drawableRes),
                            contentDescription = info.breedName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    bitmap != null -> {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = info.breedName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    imageUri != null -> {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = info.breedName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pets,
                                contentDescription = null,
                                tint = PawAmberPrimary,
                                modifier = Modifier.size(56.dp)
                            )
                        }
                    }
                }

                // Gradient scrim on bottom for readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.2f),
                                    Color.Transparent,
                                    PawSurfaceCard.copy(alpha = 0.95f)
                                ),
                                startY = 0f,
                                endY = 600f
                            )
                        )
                )

                // Top Match Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F1117).copy(alpha = 0.8f))
                        .border(1.dp, PawAmberPrimary.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = PawAmberPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "AI Verified",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PawAmberLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Breed Info & Confidence Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Identified Breed",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PawAmberPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = info.breedName,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                                color = PawTextPrimary
                            )
                        )
                    }

                    // Confidence Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(PawAmberPrimary.copy(alpha = 0.15f))
                            .border(1.dp, PawAmberPrimary.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${info.confidencePercentage}% Match",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = PawAmberLight,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Confidence Bar
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Confidence Level",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = PawTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = "${(progressAnimation * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = PawAmberPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(PawSurfaceCardElevated)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = progressAnimation.coerceIn(0f, 1f))
                                .height(10.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(PawAmberDark, PawAmberPrimary, PawAmberLight)
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}
