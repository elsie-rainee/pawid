package com.example.ui

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AboutCard
import com.example.ui.components.ErrorResultCard
import com.example.ui.components.FunFactsCard
import com.example.ui.components.HeroBreedCard
import com.example.ui.components.ImageUploadCard
import com.example.ui.components.PawHeader
import com.example.ui.components.SampleDogsSection
import com.example.ui.components.ScannerLoadingView
import com.example.ui.components.StatsGridCard
import com.example.ui.components.TemperamentCard
import com.example.ui.theme.PawAmberDark
import com.example.ui.theme.PawAmberLight
import com.example.ui.theme.PawAmberPrimary
import com.example.ui.theme.PawBackground
import com.example.ui.theme.PawSurfaceBorder
import com.example.ui.theme.PawSurfaceCard
import com.example.ui.theme.PawSurfaceCardElevated
import com.example.ui.theme.PawTextPrimary
import com.example.ui.theme.PawTextSecondary

@Composable
fun PawIdScreen(
    viewModel: PawIdViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Photo picker launcher (Gallery)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.analyzeUri(context, uri)
        }
    }

    // Camera snapshot launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            viewModel.analyzeBitmap(bitmap)
        }
    }

    Scaffold(
        containerColor = PawBackground,
        modifier = modifier
            .fillMaxSize()
            .testTag("paw_id_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            PawHeader()

            // Main Content Box (Adaptive width on large screens / tablets)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 680.dp)
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                when (val state = uiState) {
                    is PawIdUiState.Idle -> {
                        IdleContent(
                            sampleDogs = viewModel.sampleDogs,
                            onPickGallery = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            onTakePhoto = {
                                cameraLauncher.launch(null)
                            },
                            onSelectSample = { sample ->
                                viewModel.analyzeSample(context, sample)
                            }
                        )
                    }

                    is PawIdUiState.Analyzing -> {
                        ScannerLoadingView(
                            stepMessage = state.stepMessage,
                            progress = state.progress,
                            imageUri = state.imageUri,
                            bitmap = state.bitmap,
                            drawableRes = state.drawableRes
                        )
                    }

                    is PawIdUiState.Success -> {
                        SuccessContent(
                            state = state,
                            onTryAnother = { viewModel.resetToIdle() }
                        )
                    }

                    is PawIdUiState.Error -> {
                        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                            ErrorResultCard(
                                title = state.message,
                                reason = state.notADogReason,
                                isNotDog = state.isNotDog,
                                imageUri = state.imageUri,
                                bitmap = state.bitmap,
                                drawableRes = state.drawableRes,
                                onTryAnother = { viewModel.resetToIdle() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IdleContent(
    sampleDogs: List<SampleDog>,
    onPickGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    onSelectSample: (SampleDog) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Upload Box
        ImageUploadCard(
            onPickGallery = onPickGallery,
            onTakePhoto = onTakePhoto
        )

        // Sample Dogs Quick Test Row
        SampleDogsSection(
            sampleDogs = sampleDogs,
            onSelectSample = onSelectSample
        )

        // Feature Highlights
        FeatureHighlightsCard()
    }
}

@Composable
private fun SuccessContent(
    state: PawIdUiState.Success,
    onTryAnother: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Hero Card (Breed name + Confidence Bar + Photo)
        HeroBreedCard(
            info = state.info,
            imageUri = state.imageUri,
            bitmap = state.bitmap,
            drawableRes = state.drawableRes
        )

        // 2. Stats Grid (Origin, Size, Weight, Lifespan)
        StatsGridCard(info = state.info)

        // 3. About Section (2-3 sentence breed description)
        AboutCard(info = state.info)

        // 4. Temperament Tags (flow tags)
        TemperamentCard(info = state.info)

        // 5. Fun Facts (3 numbered items)
        FunFactsCard(info = state.info)

        Spacer(modifier = Modifier.height(6.dp))

        // "Try Another Dog" Button
        Button(
            onClick = onTryAnother,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PawAmberPrimary,
                contentColor = Color(0xFF1F1202)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("try_another_dog_button")
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Try Another Dog",
                tint = Color(0xFF1F1202),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Try Another Dog",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun FeatureHighlightsCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PawSurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(PawSurfaceBorder, PawAmberPrimary.copy(alpha = 0.2f))
            ),
            width = 1.dp
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Why PawID?",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PawAmberPrimary
                )
            )

            FeatureItem(
                icon = Icons.Default.Speed,
                title = "Instant Gemini Vision 2.5",
                description = "Deep learning analyzes 300+ recognized dog breeds and mixed lineage."
            )

            FeatureItem(
                icon = Icons.Default.Shield,
                title = "Comprehensive Canine Profile",
                description = "Get instant facts, lifespan estimates, temperament traits, and weight ranges."
            )
        }
    }
}

@Composable
private fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(PawSurfaceCardElevated),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PawAmberPrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = PawTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = PawTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            )
        }
    }
}
