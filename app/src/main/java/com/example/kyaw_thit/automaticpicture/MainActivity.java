package com.example.kyaw_thit.automaticpicture;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.btnCapture) Button btnCapture;
    @BindView(R.id.imgView) ImageView imageView;
    @BindView(R.id.btnSDcard) Button btnSDCard;
    @BindView(R.id.LinearLayout)
    android.widget.LinearLayout LinearLayout;

    public static final int CAPTURE_REQUEST = 1;
    public static final int REQUEST_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        EnableRunTImePermissions();

        /* Start Camera As Soon as the app opens */
        OpenCamera();

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCamera();
            }
        });
    }

    private void OpenCamera()
    {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(cameraIntent,CAPTURE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_REQUEST && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();

            assert extras != null;

            Bitmap bitmap = (Bitmap)extras.get("data");

            imageView.setImageBitmap(bitmap);
        }
    }

    /* Save Image In SD Card */
    @OnClick(R.id.btnSDcard)
    public void SaveInSDCard()
    {
        BitmapDrawable drawable = (BitmapDrawable)imageView.getDrawable();

        /* Check Image availability */
        if (drawable != null)
        {
            Bitmap bitmap = drawable.getBitmap();

            File SDCardDirectory = Environment.getExternalStorageDirectory();

            String filename = System.currentTimeMillis()+".png";

            System.out.println("Current Time " + filename);

            /* Define Image Name and Create New Image */
            File image = new File(SDCardDirectory,filename);

            FileOutputStream outputStream;

            try
            {
                /* Save Image to SD Card as PNG imgae */
                outputStream = new FileOutputStream(image);

                bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);

                outputStream.flush();

                outputStream.close();
            }
            catch (FileNotFoundException f)
            {
                f.printStackTrace();

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }else {
            /* If Imageview is null, show user error alrert */
            Snackbar snackbar = Snackbar.make(LinearLayout, "Imageview is Empty", Snackbar.LENGTH_LONG);

            snackbar.show();
        }
    }

    /* Enable Runtime Permissions */
    public void EnableRunTImePermissions()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            Toast.makeText(this,"Wirte external storage permissins allowed",Toast.LENGTH_LONG).show();
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this,"App permissins allowed",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(this,"App permissins denied",Toast.LENGTH_LONG).show();
                }
        }
    }
}
