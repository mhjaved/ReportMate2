package com.hasanjaved.reportmate;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.hasanjaved.reportmate.utility.Utility;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class TestActivity extends AppCompatActivity {

    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> selectPictureLauncher;
    private Uri photoUri;
    private TextRecognizer recognizer;
    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

//        if(OpenCVLoader.initDebug()) Log.d(Utility.TAG, "open cv loaded");
//        else Log.d(Utility.TAG, "open cv not loaded");

        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        imageView = findViewById(R.id.img_camera);
        textView = findViewById(R.id.guidelines);
//      TextView  air_temp_label = findViewById(R.id.air_temp_label);
        imageView.setOnClickListener(view -> dispatchSelectPictureIntent());
//      air_temp_label.setOnClickListener(view -> dispatchTakePictureIntent());

//        setupActivityResultLaunchers();

    }


//    private void setupActivityResultLaunchers() {
//        takePictureLauncher = registerForActivityResult(
//                new ActivityResultContracts.TakePicture(),
//                success -> {
//                    if (success) {
//                        try (InputStream stream = getContentResolver().openInputStream(photoUri)) {
//                            Bitmap bitmap = BitmapFactory.decodeStream(stream);
//
////                            Mat mat = new Mat();
////                            Utils.bitmapToMat(bitmap, mat);
////
////// Convert to grayscale
////                            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
////
////// Invert
////                            Core.bitwise_not(mat, mat);
////
////// Optional: Threshold for cleaner output
////                            Imgproc.threshold(mat, mat, 128, 255, Imgproc.THRESH_BINARY);
////
////// Convert back to Bitmap
////                            Bitmap resultBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
////                            Utils.matToBitmap(mat, resultBitmap);
////
//
//                            Log.d(Utility.TAG,"bitmap");
//                            imageView.setImageBitmap(bitmap);
//                            processImage(bitmap);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//        selectPictureLauncher = registerForActivityResult(
//                new ActivityResultContracts.GetContent(),
//                uri -> {
//                    if (uri != null) {
//                        try (InputStream stream = getContentResolver().openInputStream(uri)) {
//                            Bitmap bitmap = preprocessImage(BitmapFactory.decodeStream(stream));;
//
//                            OCRProcessor ocr = new OCRProcessor(this);
//                            String result = ocr.extractText(bitmap);
//                            textView.setText(result);
//
//                            ocr.release();
//                            Log.d(Utility.TAG, result);
//                            imageView.setImageBitmap(bitmap);
////                            processImage(bitmap);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//    }

    private void dispatchTakePictureIntent() {
        try {
            File imageFile = File.createTempFile("IMG_", ".jpg", getCacheDir());
            photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", imageFile);
            takePictureLauncher.launch(photoUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatchSelectPictureIntent() {
        selectPictureLauncher.launch("image/*");
    }

    public Bitmap preprocessImage(Bitmap inputBitmap) {

        // Convert Bitmap to Mat
        Mat mat = new Mat();
        Utils.bitmapToMat(inputBitmap, mat);

        // Step 1: Convert to Grayscale
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);

        // Step 2: Apply Thresholding
        //Imgproc.threshold(mat, mat, 150, 255, Imgproc.THRESH_BINARY);

        // Step 3: Sharpen the image
        Mat kernel = new Mat(3, 3, CvType.CV_32F);
        float[] kernelData = {
                0, -1, 0,
                -1, 5, -1,
                0, -1, 0
        };
        kernel.put(0, 0, kernelData);
        Imgproc.filter2D(mat, mat, -1, kernel);
 
        // Convert Mat back to Bitmap
        Bitmap resultBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, resultBitmap);
        return resultBitmap;
    }

    private void processImage(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        recognizer.process(image)
                .addOnSuccessListener(visionText -> textView.setText(visionText.getText()))
                .addOnFailureListener(e -> {
                    if (e instanceof MlKitException &&
                            ((MlKitException) e).getErrorCode() == MlKitException.UNAVAILABLE) {
                        showMessage("Text recognition model is still downloading");
                    } else {
                        showMessage("Text recognition failed: " + e.getMessage());
                    }
                });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recognizer.close();
    }


}