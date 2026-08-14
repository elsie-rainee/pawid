package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DogBreedInfo
import com.example.ui.theme.PawAmberPrimary
import com.example.ui.theme.PawSurfaceBorder
import com.example.ui.theme.PawSurfaceCard
import com.example.ui.theme.PawTextPrimary
import com.example.ui.theme.PawTextSecondary

@Composable
fun AboutCard(
    info: DogBreedInfo,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PawSurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(PawSurfaceBorder, PawAmberPrimary.copy(alpha = 0.25f))
            ),
            width = 1.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("about_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = PawAmberPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "About the Breed",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = PawTextPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = info.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PawTextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            )
        }
    }
}
