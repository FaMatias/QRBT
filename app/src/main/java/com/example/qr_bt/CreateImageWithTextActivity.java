package com.example.qr_bt;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.content.pm.PackageManager;

public class CreateImageWithTextActivity extends AppCompatActivity {

    ImageView imageView;
    EditText editText;
    Button btnSelectImage, btnAddText, btnSaveImage;
    TextView textView;
    Bitmap bitmap;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_image_with_text);

        imageView = findViewById(R.id.imageView);
        editText = findViewById(R.id.editText);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnAddText = findViewById(R.id.btnAddText);
        btnSaveImage = findViewById(R.id.btnSaveImage);
        textView = findViewById(R.id.textView);

        btnSelectImage.setOnClickListener(v -> openFileChooser());

        btnAddText.setOnClickListener(v -> {
            String text = editText.getText().toString();
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
            // Aquí puedes agregar la lógica para personalizar el formato del texto
        });

        btnSaveImage.setOnClickListener(v -> {
            if (bitmap != null) {
                if (checkAndRequestPermissions()) {
                    saveImage(bitmap);
                }
            } else {
                Toast.makeText(CreateImageWithTextActivity.this, "Primero selecciona una imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

            return false; // Esperando respuesta del usuario
        }
        return true; // Permiso ya otorgado
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (bitmap != null) {
                        saveImage(bitmap);
                    }
                } else {
                    Toast.makeText(this, "El permiso es necesario para guardar la imagen.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            imageView.setVisibility(View.VISIBLE);
            // Convertir la imagen a Bitmap para poder editarla
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveImage(Bitmap finalBitmap) {
        if (checkAndRequestPermissions()) {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/saved_images");
            myDir.mkdirs();
            String fileName = "Image-" + System.currentTimeMillis() + ".jpg";
            File file = new File(myDir, fileName);
            if (file.exists()) file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                Toast.makeText(this, "Imagen guardada en: " + file.getPath(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

