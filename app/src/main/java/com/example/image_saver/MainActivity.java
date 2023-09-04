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
    private List<File> imageFiles = new ArrayList<>();
    private List<File> textFiles = new ArrayList<>();
    private static final int REQUEST_CODE_PERMISSION = 1001;
Button save_btn;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image= findViewById(R.id.image);
        save_btn= findViewById(R.id.saving_btn);
        checkAndRequestPermission();
        save_btn. setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    saveImage();
            }
        });

    }
    private void saveImage() {
        // Get the drawable from the ImageView
        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        // Define a filename for the saved image
        String fileName = "Image" + imageFiles.size() + ".jpg";
        // Get the internal storage directory specific to this app
        File mainDirectory = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOCUMENTS);
        // Create main 'data' directory if it doesn't exist
        if (!mainDirectory.exists()) {
            mainDirectory.mkdir();
            Log.d("image saver", "saveImage: main " + mainDirectory);
        }
        // Create 'images' subdirectory inside 'data'
        File imagesDirectory = new File(mainDirectory, "images");
        if (!imagesDirectory.exists()) {
            imagesDirectory.mkdir();
            Log.d("image saver", "saveImage: image" + imagesDirectory);
        }
        // Create 'texts' subdirectory inside 'data' (if you need it later)
        File textsDirectory = new File(mainDirectory, "texts");
        if (!textsDirectory.exists()) {
            textsDirectory.mkdir();
            Log.d("image saver", "saveImage: text " + textsDirectory);
        }
        // Create a new file in the 'images' directory
        File imageFile = new File(imagesDirectory, fileName);
        try {
            // Open an output stream to save the image to the file
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            // Flush and close the output stream
            fos.flush();
            fos.close();

            // Create a text file with the same name as the image file
            String textFileName = fileName.replace(".jpg", ".txt");
            File textFile = new File(textsDirectory, textFileName);
            textFile.createNewFile();

            // Add the image and text files to the lists
            imageFiles.add(imageFile);
            textFiles.add(textFile);

            // Display a toast message to indicate the image and text save locations
            Toast.makeText(this, "Image saved to " + imageFile.getAbsolutePath() + "\nText saved to " + textFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // Log the exception and display a toast message
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image.", Toast.LENGTH_SHORT).show();
        }
    }
 

    private void checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION);
        } else {
            saveImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImage();
            } else {
                Toast.makeText(this, "Permission denied. Cannot save image ....", Toast.LENGTH_SHORT).show();
            }
        }
    }


}