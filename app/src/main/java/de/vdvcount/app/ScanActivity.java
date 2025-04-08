package de.vdvcount.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Size;
import android.view.MenuItem;
import android.view.View;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import de.vdvcount.app.databinding.ActivityScanBinding;

public class ScanActivity extends AppCompatActivity {

    private ActivityScanBinding dataBinding;

    private ProcessCameraProvider cameraProvider;
    private Camera camera;
    private boolean cameraTorchEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_scan);

        this.initViewEvents();

        this.setSupportActionBar(this.dataBinding.toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Permissions.check(this, android.Manifest.permission.CAMERA, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                startCamera();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.getOnBackPressedDispatcher().onBackPressed();
        }

        return true;
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder()
                        .build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(960, 540))
                        .build();

                imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), new ImageAnalysis.Analyzer() {
                    @Override
                    @SuppressLint("UnsafeOptInUsageError")
                    public void analyze(@NonNull ImageProxy image) {
                        BarcodeScannerOptions barcodeScannerOptions = new BarcodeScannerOptions.Builder()
                                .setBarcodeFormats(Barcode.FORMAT_AZTEC, Barcode.FORMAT_QR_CODE, Barcode.FORMAT_PDF417)
                                .build();

                        BarcodeScanner barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions);
                        InputImage inputImage = InputImage.fromMediaImage(image.getImage(), image.getImageInfo().getRotationDegrees());

                        barcodeScanner.process(inputImage).addOnSuccessListener(barcodes -> {
                            Barcode barcode = barcodes.size() > 0 ? barcodes.get(0) : null;
                            if (barcode != null) {
                                setScanResult(barcode.getRawBytes());
                            }
                        }).addOnCompleteListener(task -> {
                            image.close();
                        });
                    }
                });

                preview.setSurfaceProvider(this.dataBinding.cameraPreview.getSurfaceProvider());

                cameraProvider.unbindAll();
                this.camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis);

                if (this.camera.getCameraInfo().hasFlashUnit()) {
                    this.dataBinding.fabToggleCameraLight.setVisibility(View.VISIBLE);
                } else {
                    this.dataBinding.fabToggleCameraLight.setVisibility(View.GONE);
                }
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void initViewEvents() {
        this.dataBinding.fabToggleCameraLight.setOnClickListener(view -> {
            try {
                this.cameraTorchEnabled = !this.cameraTorchEnabled;
                this.camera.getCameraControl().enableTorch(this.cameraTorchEnabled);
            } catch (Exception ex) {
            }
        });
    }

    private void setScanResult(byte[] dataBytes) {
        Intent intent = new Intent();
        intent.putExtra("dataBytes", dataBytes);

        this.setResult(Activity.RESULT_OK, intent);
        this.finish();
    }
}