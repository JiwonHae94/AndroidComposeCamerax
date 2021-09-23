package com.jwhae.camerax_jetpack_compose

import android.Manifest
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.jwhae.camerax_jetpack_compose.ui.theme.CameraxjetpackcomposeTheme
import com.jwhae.camerax_jetpack_compose.ui.widget.CameraContentView
import com.jwhae.camerax_jetpack_compose.ui.widget.CameraContentWithOverlay
import com.jwhae.camerax_jetpack_compose.util.Permission
import com.nota.fr_exhibition.support.dialog.DialogFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraxjetpackcomposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    CameraContentWithOverlay()
                }
            }
        }


        if (Permission.checkAllPermission(this, REQUIRED_PERMISSIONS)) {

        } else {
            handleRequiredPermissions()
        }

    }

    protected fun handleRequiredPermissions(){
        if(Permission.checkPermissionsRequireRationale(this, REQUIRED_PERMISSIONS)){
            DialogFactory.getInstance(this@MainActivity)
                .setTitle("어플을 사용하기 위해 필수적으로 필요한 권한들입니다")
                .setPositiveButtonListener(object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        ActivityCompat.requestPermissions( this@MainActivity, REQUIRED_PERMISSIONS, REQUIRED_PERMISSION_CODE)
                    }
                })
                .setNegativeButtonListener(object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        Toast.makeText(this@MainActivity, "필수 권한이 제공되지 않아 서비스를 종료합니다", Toast.LENGTH_SHORT).show()
                        finish()
                        System.exit(0)
                    }
                }).build()
                .show()
        }else{
            ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS, REQUIRED_PERMISSION_CODE);
        }
    }


    companion object{
        const val REQUIRED_PERMISSION_CODE = 0
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CameraxjetpackcomposeTheme {
        CameraContentWithOverlay()
    }
}