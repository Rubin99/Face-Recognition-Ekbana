package com.example.faceexpression

import android.R.attr
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import androidx.core.app.ActivityCompat.startActivityForResult

import android.provider.MediaStore

import android.content.Intent
import android.widget.Toast
import android.graphics.Bitmap
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import android.R.attr.data
import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnFailureListener

import com.google.firebase.ml.vision.face.FirebaseVisionFace

import com.google.android.gms.tasks.OnSuccessListener

import com.google.firebase.ml.vision.FirebaseVision

import android.R.attr.bitmap
import androidx.fragment.app.DialogFragment

import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private lateinit var btnCamera: Button

    private val REQUEST_IMAGE_CAPTURE = 124
    var image: FirebaseVisionImage? = null
    var detector: FirebaseVisionFaceDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        btnCamera = findViewById(R.id.btnCamera)

        btnCamera.setOnClickListener {

            val intent = Intent(
                MediaStore.ACTION_IMAGE_CAPTURE
            )
            if (intent.resolveActivity(
                    packageManager
                )
                != null
            ) {
                //startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    onActivityResult(REQUEST_IMAGE_CAPTURE, result)
                }.launch(intent)
            } else {
                // if the image is not captured, set
                // a toast to display an error image.
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private fun onActivityResult(requestImageCapture: Int, result: ActivityResult?) {
        if(result?.resultCode == Activity.RESULT_OK){

            val extra: Bundle = result.data?.extras!!
            val bitmap = extra["data"] as Bitmap?
            detectFace(bitmap)
        }
    }

    private fun detectFace(bitmap: Bitmap?) {
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setModeType(
                FirebaseVisionFaceDetectorOptions.ACCURATE_MODE
            )
            .setLandmarkType(
                FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS
            )
            .setClassificationType(
                FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS
            )
            .build()

        // we need to create a FirebaseVisionImage object
        // from the above mentioned image types(bitmap in
        // this case) and pass it to the model.

        // we need to create a FirebaseVisionImage object
        // from the above mentioned image types(bitmap in
        // this case) and pass it to the model.
        try {
            image = FirebaseVisionImage.fromBitmap(bitmap!!)
            detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // It’s time to prepare our Face Detection model.

        // It’s time to prepare our Face Detection model.
        detector!!.detectInImage(image!!)
            .addOnSuccessListener { firebaseVisionFaces ->

                // adding an onSuccess Listener, i.e, in case
// our image is successfully detected, it will
// append it's attribute to the result
// textview in result dialog box.
                var resultText: String? = ""
                var i = 1
                for (face in firebaseVisionFaces) {
                    resultText = resultText
                     """
            
            FACE NUMBER. $i: 
            """.trimIndent() +
                            ("\nSmile: "
                                    + (face.smilingProbability
                                    * 100) + "%") +
                            ("\nleft eye open: "
                                    + (face.leftEyeOpenProbability
                                    * 100) + "%") +
                            ("\nright eye open "
                                    + (face.rightEyeOpenProbability
                                    * 100) + "%")
                    i++
                }

                // if no face is detected, give a toast
                // message.
                if (firebaseVisionFaces.size == 0) {
                    Toast
                        .makeText(
                            this@MainActivity,
                            "NO FACE DETECT",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                } else {
                    val bundle = Bundle()
                    bundle.putString(
                        FaceDetection.RESULT_TEXT,
                        resultText
                    )
                    val resultDialog: DialogFragment = ResultDialog()
                    resultDialog.arguments = bundle
                    resultDialog.isCancelable = true
                    resultDialog.show(
                        supportFragmentManager,
                        FaceDetection.RESULT_DIALOG
                    )
                }
            } // adding an onfailure listener as well if
            // something goes wrong.
            .addOnFailureListener {
                Toast
                    .makeText(
                        this@MainActivity,
                        "Oops, Something went wrong",
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
    }

}