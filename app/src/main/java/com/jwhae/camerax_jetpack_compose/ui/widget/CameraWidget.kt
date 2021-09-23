package com.jwhae.camerax_jetpack_compose.ui.widget

import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.camera.core.Preview as CameraPreview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.jwhae.camerax_jetpack_compose.ui.theme.CameraxjetpackcomposeTheme
import java.util.*
import kotlin.collections.ArrayList

data class CameraOption(val lensFacing : Int = CameraSelector.LENS_FACING_BACK)

@Composable
fun CameraContentView(modifier : Modifier = Modifier, cameraOpt : CameraOption = CameraOption(), imageAnalysis : ImageAnalysis? = null, imageCapture : ImageCapture? = null){
    val localContext = LocalContext.current
    val localLifecycleOwner = LocalLifecycleOwner.current

    // use @remember to persists lifcycle
    val cameraProviderFuture = remember{ ProcessCameraProvider.getInstance(localContext) }

    AndroidView(factory = { ctx ->
        val previewView = PreviewView(ctx)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(cameraOpt.lensFacing)
                .build()

            val preview = androidx.camera.core.Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            bindToCamera(
                cameraProvider = cameraProvider,
                lifecycleOwner = localLifecycleOwner,
                preview = preview,
                cameraSelector = cameraSelector,
                imageAnalysis = imageAnalysis,
                imageCapture = imageCapture
            )
        }, ContextCompat.getMainExecutor(ctx))
        previewView
    }, modifier = modifier.fillMaxSize())
}

private fun bindToCamera(cameraProvider : ProcessCameraProvider,
                         lifecycleOwner: LifecycleOwner,
                         preview: CameraPreview,
                         cameraSelector : CameraSelector,
                         imageAnalysis : ImageAnalysis? = null,
                         imageCapture : ImageCapture? = null) : Camera{

    cameraProvider.unbindAll()
    val useCases = ArrayList<UseCase>()
    imageAnalysis?.let { analysis ->
        useCases.add(analysis)
    }
    imageCapture?.let{ capture ->
        useCases.add(capture)
    }
    return cameraProvider.bindToLifecycle(
        lifecycleOwner, cameraSelector, preview, *useCases.toTypedArray())
}

@Composable
fun Overlay(modifier : Modifier = Modifier){
    var isSelected by remember { mutableStateOf(false) }
}

@Composable
fun CameraContentWithOverlay(modifier : Modifier = Modifier){
    Box(modifier = modifier.fillMaxSize()){
        CameraContentView()
        Overlay()
    }
}

@Preview
@Composable
fun ContentView(){
    CameraxjetpackcomposeTheme {
        CameraContentWithOverlay()
    }
}