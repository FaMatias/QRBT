package com.example.qr_bt;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;

import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;
import androidx.annotation.NonNull;

public class CreateQRActivity extends AppCompatActivity {
    private EditText linkEditText;
    private ImageView qrImageView;
    private Button generarQRButton;
    private Button saveQrButton;
    private static final int TU_CODIGO_DE_SOLICITUD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_qr);
        linkEditText = findViewById(R.id.linkEditText);
        qrImageView = findViewById(R.id.qrImageView);
        generarQRButton = findViewById(R.id.generarQRButton);
        saveQrButton = findViewById(R.id.save_qr_button);

        generarQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enlace = linkEditText.getText().toString();
                Bitmap bitmap = generarQR(enlace);
                if (bitmap != null) {
                    qrImageView.setImageBitmap(bitmap);
                }
            }
        });

        saveQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap qrBitmap = ((BitmapDrawable) qrImageView.getDrawable()).getBitmap();
                String nombreArchivoQR = obtenerNombreArchivoUnico();
                guardarQR(qrBitmap, nombreArchivoQR);
                subirImagenAFirebase(qrBitmap, nombreArchivoQR);
                Toast.makeText(CreateQRActivity.this, "QR guardado y subido con éxito", Toast.LENGTH_LONG).show();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, TU_CODIGO_DE_SOLICITUD);
        }
    }

    public Bitmap generarQR(String texto) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(texto, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(CreateQRActivity.this, "Error al generar el QR: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private void guardarQR(Bitmap bitmap, String nombreArchivo) {
        File ruta = getExternalFilesDir(null);
        OutputStream fOut;
        File file = new File(ruta, nombreArchivo + ".png");
        try {
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void subirImagenAFirebase(Bitmap bitmap, String nombreArchivo) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference qrImagesRef = storageRef.child("qr_images/" + nombreArchivo + ".png");
        UploadTask uploadTask = qrImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Manejar errores
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Imagen subida con éxito
            }
        });
    }

    private String obtenerNombreArchivoUnico() {
        // Genera un nombre de archivo único para el QR
        return "qr_" + System.currentTimeMillis();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // ... código para manejar los permisos ...
    }
}
