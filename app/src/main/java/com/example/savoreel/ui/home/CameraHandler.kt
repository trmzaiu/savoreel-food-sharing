
import android.Manifest
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import java.util.concurrent.Executors

@Composable
fun CameraFrame(
    modifier: Modifier = Modifier,
    isFrontCamera: Boolean,
    onSwapCamera: () -> Unit,
    onCapturePhoto: (Uri) -> Unit,
    isCaptureLocked: Boolean
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val previewView = remember { PreviewView(context) }
    val outputDirectory = context.cacheDir

    val cameraSelector = if (isFrontCamera) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA

    DisposableEffect(Unit) {
        onDispose { cameraExecutor.shutdown() }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    ) { preview ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val previewUseCase = Preview.Builder()
                .setTargetRotation(preview.display.rotation)
                .build()
                .also {
                    it.setSurfaceProvider(preview.surfaceProvider)
                }

            val imageCaptureUseCase = ImageCapture.Builder()
                .setTargetRotation(preview.display.rotation)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    previewUseCase,
                    imageCaptureUseCase
                )

                previewView.tag = imageCaptureUseCase
            } catch (e: Exception) {
                Log.e("CameraFrame", "Failed to bind camera use cases", e)
            }
        }, ContextCompat.getMainExecutor(context))

        // Capture logic with lock enforcement
        previewView.setOnClickListener {
            if (!isCaptureLocked) {
                val imageCapture = previewView.tag as? ImageCapture
                if (imageCapture != null) {
                    val photoFile = File(outputDirectory, "${System.currentTimeMillis()}.jpg")
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    imageCapture.takePicture(
                        outputOptions,
                        cameraExecutor,
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                onCapturePhoto(Uri.fromFile(photoFile))
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("CameraFrame", "Photo capture failed: ${exception.message}", exception)
                            }
                        }
                    )
                }
            } else {
                Log.d("CameraFrame", "Capture is locked, please discard the current photo first.")
            }
        }
    }
}

@Composable
fun RequestCameraPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    var hasRequested by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasRequested) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            hasRequested = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Text(
            text = "Camera permission is required to use this feature.",
            textAlign = TextAlign.Center,
            color = Color.Red,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
            Text(text = "Grant Permission")

        }
    }
}
