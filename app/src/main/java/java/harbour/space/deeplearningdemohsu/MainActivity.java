package java.harbour.space.deeplearningdemohsu;

import android.graphics.*;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
public class MainActivity extends AppCompatActivity
{
    private Button btnCam;
    private Button btnGallery;
    private Button btnDetect;
    private TextView txtAcc;
    private TextView txtClass;
    private ImageView imgDisplay;
    int resultCodeGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCam = findViewById(R.id.btnCam);
        btnGallery = findViewById(R.id.btnGallery);
        btnDetect = findViewById(R.id.btnDetect);
        imgDisplay=findViewById(R.id.picboxDisplay);
        txtAcc = findViewById(R.id.txtAcc);
        txtClass =findViewById(R.id.txtClass);

        btnGallery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                choosePhotoFromGallery();
            }

        });

        btnCam.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               takePhotofromCam();
            }
        });
    }

    public void choosePhotoFromGallery()
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,resultCodeGallery);
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data)
    {
        if (resultCode==RESULT_OK)
        {
            if(reqCode==resultCodeGallery)
            {
                Uri imageAdd = data.getData();
                InputStream inputStream;
                try
                {
                 inputStream = getContentResolver().openInputStream(imageAdd);
                 Bitmap img = BitmapFactory.decodeStream(inputStream);
                 imgDisplay.setImageBitmap(img);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                    Toast.makeText(this,"We were not able to open the file",Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    public void takePhotofromCam()
    {
    }
}
