package com.example.image_saver;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
ImageView image;
float x, y, z, w;
    private List<File> imageFiles = new ArrayList<>();
    private List<File> textFiles = new ArrayList<>();
    private static final int REQUEST_CODE_PERMISSION = 1001;
Button save_btn;
    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image= findViewById(R.id.image);
        save_btn= findViewById(R.id.saving_btn);
        checkAndRequestPermission();
//        image.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        // Capture the touch coordinates when the user touches the image
//                        x = event.getX();
//                        y = event.getY();
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        // Capture the touch coordinates when the user releases the touch
//                        z = event.getX();
//                        w = event.getY();
//                        break;
//                }
//                return true;
//            }
//        });
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if valid touch coordinates have been captured
                if (x != -1 && y != -1 && z != -1 && w != -1) {
                    saveData(image, x, y, z, w);
                } else {
                    Toast.makeText(MainActivity.this, "Please interact with the image to capture coordinates.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }




    private void saveData(ImageView image, float x, float y, float z, float w) {
        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        String fileName = "Image" + imageFiles.size() + ".jpg";

        File mainDirectory = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOCUMENTS);

        if (!mainDirectory.exists()) {
            mainDirectory.mkdir();
            Log.d("image saver", "saveImage: main " + mainDirectory);
        }

        File imageFile = new File(mainDirectory, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

//            String textFileName = fileName.replace(".jpg", ".txt");
//            File textFile = new File(mainDirectory, textFileName);

            // Calculate normalized coordinates and dimensions
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            float normalizedX = x / imageWidth;
            float normalizedY = y / imageHeight;
            float normalizedWidth = z / imageWidth;
            float normalizedHeight = w / imageHeight;

            // Prepare the data line for the text file
            String coordinatesData = normalizedX + "\t" + normalizedY + "\t" + normalizedWidth + "\t" + normalizedHeight;
            FileOutputStream textFos = new FileOutputStream(imageFile.getAbsolutePath().replace(".jpg", ".txt"));
            textFos.write(coordinatesData.getBytes());
            textFos.close();

            imageFiles.add(imageFile);
           // textFiles.add(textFile);

            Toast.makeText(this, "Image and Text saved to " + mainDirectory.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image or data." + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION);
        } else {
            saveData(image,x,y,z,w);
           // saveImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                saveData(image,x,y,z,w);
                //saveImage();
            } else {
                Toast.makeText(this, "Permission denied. Cannot save image ....", Toast.LENGTH_SHORT).show();
            }
        }
    }


}