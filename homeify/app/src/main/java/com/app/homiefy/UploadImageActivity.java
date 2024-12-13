package com.app.homiefy;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UploadImageActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 2;

    private ImageView imageView;
    private ProgressBar progressBar;
    private Button btnChooseImage, btnUpload;

    private Uri imageUri;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        if (toastView != null) {
            toastView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        toast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        // Initialize Firebase
        storageRef = FirebaseStorage.getInstance().getReference("uploads");
        databaseRef = FirebaseDatabase.getInstance().getReference("properties");

        // Initialize views
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnUpload = findViewById(R.id.btnUpload);

        btnChooseImage.setOnClickListener(v -> checkPermissionAndOpenChooser());
        btnUpload.setOnClickListener(v -> uploadFile());
    }

    private void checkPermissionAndOpenChooser() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        } else {
            openFileChooser();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileChooser();
            } else {
                showToast("Cần cấp quyền truy cập để chọn ảnh");
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                // Hiển thị ảnh đã chọn
                Glide.with(this)
                        .load(imageUri)
                        .into(imageView);
            } catch (Exception e) {
                showToast("Lỗi load ảnh: " + e.getMessage());
            }
        }
    }

    private void uploadFile() {
        if (imageUri == null) {
            showToast("Chưa chọn ảnh");
            return;
        }

        // Compress image trước khi upload
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageData = baos.toByteArray();

            // Tạo tên file unique
            String fileName = "property_" + System.currentTimeMillis() + ".jpg";
            StorageReference fileRef = storageRef.child(fileName);

            progressBar.setVisibility(View.VISIBLE);

            // Upload ảnh
            UploadTask uploadTask = fileRef.putBytes(imageData);
            uploadTask.addOnProgressListener(taskSnapshot -> {
                // Tính progress %
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressBar.setProgress((int) progress);
            }).addOnSuccessListener(taskSnapshot -> {
                // Lấy download URL
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    // Tạo property object
                    Property property = new Property();
                    property.setImageUrl(imageUrl);
                    property.setUploadTime(System.currentTimeMillis());

                    // Lưu vào database
                    String propertyId = databaseRef.push().getKey();
                    databaseRef.child(propertyId).setValue(property)
                            .addOnSuccessListener(aVoid -> {
                                progressBar.setVisibility(View.GONE);
                                showToast("Upload thành công");
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                showToast("Lỗi: " + e.getMessage());
                            });
                });
            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                showToast("Upload thất bại: " + e.getMessage());
            });

        } catch (IOException e) {
            e.printStackTrace();
            showToast("Lỗi xử lý ảnh: " + e.getMessage());
        }
    }
}