
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.savoreel.ui.component.CustomButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

@Composable
fun CameraFrame(
    modifier: Modifier = Modifier,
    isFrontCamera: Boolean,
    onCapturePhoto: (Uri) -> Unit,
    onTakePhoto: (()-> Unit) -> Unit,
    flashEnabled: Boolean,

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

    fun adjustScreenBrightness(context: Context, brightness: Float) {
        (context as? Activity)?.runOnUiThread {
            val window = context.window
            window?.attributes = window?.attributes?.apply {
                screenBrightness = brightness
            }
        }
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
                .setFlashMode(
                    if (flashEnabled) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
                )
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

                // Provide the takePhoto action
                onTakePhoto {
                    if (isFrontCamera && flashEnabled) {
                        adjustScreenBrightness(context, 1.0f)
                    }

                    val photoFile = File(outputDirectory, "${System.currentTimeMillis()}.jpg")
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    imageCaptureUseCase.takePicture(
                        outputOptions,
                        cameraExecutor,
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    if (isFrontCamera) {
                                        mirrorImage(photoFile)
                                    }
                                    val photoUri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        photoFile
                                    )
                                    onCapturePhoto(photoUri)
                                    adjustScreenBrightness(context, WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE)
                                }
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("CameraFrame", "Photo capture failed: ${exception.message}", exception)
                                adjustScreenBrightness(context, WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE)

                            }
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e("CameraFrame", "Failed to bind camera use cases", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
}

fun mirrorImage(photoFile: File) {
    try {
        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
        val exif = ExifInterface(photoFile.absolutePath)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val matrix = Matrix()

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }

        matrix.postScale(-1f, 1f)

        val correctedBitmap = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )

        FileOutputStream(photoFile).use { outputStream ->
            correctedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        }

        Log.d("CameraFrame", "Front camera image corrected successfully")
    } catch (e: Exception) {
        Log.e("CameraFrame", "Failed to correct front camera image", e)
    }
}

@Composable
fun RequestCameraPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val context = LocalContext.current
    var hasRequested by remember { mutableStateOf(false) }
    var isCheckingPermission by remember { mutableStateOf(true) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
        isCheckingPermission = false
    }

    LaunchedEffect(Unit) {
        val permissionStatus = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        )
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted()
            isCheckingPermission = false
        } else if (!hasRequested) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            hasRequested = true
        } else {
            isCheckingPermission = false
        }
    }

    // Show UI only if permission hasn't been granted or is being checked
    if (!isCheckingPermission && hasRequested) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            Text(
                text = "Camera permission is required to use this feature.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            CustomButton(
                text = "Grant Permission",
                enabled = true,
                onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
            )
        }
    }
}
