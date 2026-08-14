package com.example.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.model.DogBreedInfo
import com.example.data.remote.GeminiDogIdentifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.InputStream

data class SampleDog(
    val id: String,
    val name: String,
    val breedHint: String,
    val subtitle: String,
    @DrawableRes val drawableRes: Int
)

sealed interface PawIdUiState {
    data object Idle : PawIdUiState

    data class Analyzing(
        val imageUri: Uri? = null,
        val bitmap: Bitmap? = null,
        @DrawableRes val drawableRes: Int? = null,
        val stepMessage: String = "Scanning canine facial landmarks...",
        val progress: Float = 0.3f
    ) : PawIdUiState

    data class Success(
        val info: DogBreedInfo,
        val imageUri: Uri? = null,
        val bitmap: Bitmap? = null,
        @DrawableRes val drawableRes: Int? = null
    ) : PawIdUiState

    data class Error(
        val message: String,
        val isNotDog: Boolean = false,
        val notADogReason: String? = null,
        val imageUri: Uri? = null,
        val bitmap: Bitmap? = null,
        @DrawableRes val drawableRes: Int? = null
    ) : PawIdUiState
}

class PawIdViewModel : ViewModel() {

    private val identifier = GeminiDogIdentifier()

    private val _uiState = MutableStateFlow<PawIdUiState>(PawIdUiState.Idle)
    val uiState: StateFlow<PawIdUiState> = _uiState.asStateFlow()

    val sampleDogs = listOf(
        SampleDog(
            id = "golden",
            name = "Golden Retriever",
            breedHint = "Golden Retriever",
            subtitle = "Family favorite • Scotland",
            drawableRes = R.drawable.golden_retriever_sample_1786688090144
        ),
        SampleDog(
            id = "husky",
            name = "Siberian Husky",
            breedHint = "Siberian Husky",
            subtitle = "Arctic sledder • Siberia",
            drawableRes = R.drawable.siberian_husky_sample_1786688102797
        ),
        SampleDog(
            id = "frenchie",
            name = "French Bulldog",
            breedHint = "French Bulldog",
            subtitle = "Playful companion • France",
            drawableRes = R.drawable.french_bulldog_sample_1786688118294
        ),
        SampleDog(
            id = "shiba",
            name = "Shiba Inu",
            breedHint = "Shiba Inu",
            subtitle = "Spirited hunter • Japan",
            drawableRes = R.drawable.shiba_inu_sample_1786688134287
        )
    )

    fun analyzeUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.value = PawIdUiState.Analyzing(
                imageUri = uri,
                stepMessage = "Reading photo data...",
                progress = 0.2f
            )

            val bitmap = loadBitmapFromUri(context, uri)
            if (bitmap == null) {
                _uiState.value = PawIdUiState.Error(
                    message = "Could not load image file. Please try another photo.",
                    imageUri = uri
                )
                return@launch
            }

            processImageAnalysis(bitmap = bitmap, imageUri = uri, presetHint = null)
        }
    }

    fun analyzeBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.value = PawIdUiState.Analyzing(
                bitmap = bitmap,
                stepMessage = "Processing camera snapshot...",
                progress = 0.2f
            )
            processImageAnalysis(bitmap = bitmap, imageUri = null, presetHint = null)
        }
    }

    fun analyzeSample(context: Context, sample: SampleDog) {
        viewModelScope.launch {
            _uiState.value = PawIdUiState.Analyzing(
                drawableRes = sample.drawableRes,
                stepMessage = "Inspecting ${sample.name} features...",
                progress = 0.25f
            )

            val bitmap = BitmapFactory.decodeResource(context.resources, sample.drawableRes)
            if (bitmap == null) {
                _uiState.value = PawIdUiState.Error(
                    message = "Could not load sample dog image.",
                    drawableRes = sample.drawableRes
                )
                return@launch
            }

            processImageAnalysis(
                bitmap = bitmap,
                imageUri = null,
                drawableRes = sample.drawableRes,
                presetHint = sample.breedHint
            )
        }
    }

    private suspend fun processImageAnalysis(
        bitmap: Bitmap,
        imageUri: Uri? = null,
        @DrawableRes drawableRes: Int? = null,
        presetHint: String? = null
    ) {
        // Multi-stage animation feedback
        delay(350)
        _uiState.value = PawIdUiState.Analyzing(
            imageUri = imageUri,
            bitmap = bitmap,
            drawableRes = drawableRes,
            stepMessage = "Analyzing canine muzzle, coat & ears...",
            progress = 0.5f
        )

        delay(400)
        _uiState.value = PawIdUiState.Analyzing(
            imageUri = imageUri,
            bitmap = bitmap,
            drawableRes = drawableRes,
            stepMessage = "Matching with Gemini breed registry...",
            progress = 0.8f
        )

        val result = identifier.identifyBreed(bitmap, presetHint)

        result.fold(
            onSuccess = { info ->
                if (!info.isDog) {
                    _uiState.value = PawIdUiState.Error(
                        message = "No Dog Detected",
                        isNotDog = true,
                        notADogReason = info.notADogReason
                            ?: "Gemini AI analyzed this image but could not identify a dog. Please upload a clear photo of a canine.",
                        imageUri = imageUri,
                        bitmap = bitmap,
                        drawableRes = drawableRes
                    )
                } else {
                    _uiState.value = PawIdUiState.Success(
                        info = info,
                        imageUri = imageUri,
                        bitmap = bitmap,
                        drawableRes = drawableRes
                    )
                }
            },
            onFailure = { error ->
                _uiState.value = PawIdUiState.Error(
                    message = error.localizedMessage ?: "Failed to identify breed. Please check your internet connection and try again.",
                    imageUri = imageUri,
                    bitmap = bitmap,
                    drawableRes = drawableRes
                )
            }
        )
    }

    fun resetToIdle() {
        _uiState.value = PawIdUiState.Idle
    }

    private fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            val input: InputStream? = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            null
        }
    }
}
