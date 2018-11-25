package java.harbour.space.deeplearningdemohsu;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.graphics.*;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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
    private static final String MODEL_PATH = "mobilenet_quant_v1_224.tflite";
    private static final String LABEL_PATH = "labels.txt";
    private static final int INPUT_SIZE = 224;
    private Classifier classifier;
    private Button btnCam;
    private Button btnGallery;
    private Button btnDetect;
    private TextView txtAcc;
    private TextView txtClass;
    private ImageView imgDisplay;
    int resultCodeGallery;
    int resultCodeCam;
    private Executor executor = Executors.newSingleThreadExecutor();

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
                takePhotoFromGallery();
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

        btnDetect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(null!=imgDisplay.getDrawable())
                {
                    Bitmap img = ((BitmapDrawable)imgDisplay.getDrawable()).getBitmap();
                    activateDetection();
                }
                else
                    {
                        Toast.makeText(getApplicationContext(),"Please upload an image",Toast.LENGTH_LONG).show();

                }

            }
        });
    }

    public void takePhotoFromGallery()
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,resultCodeGallery=1);
    }
    public void takePhotofromCam()
    {
        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camIntent,resultCodeCam=2);
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data)
    {
        if (resultCode==RESULT_OK)
        {
            if(reqCode==resultCodeGallery)
            {
               displayFromGallery(data);
            }
            else if (reqCode==resultCodeCam)
            {
               displayFromCamera(data);
            }
        }
    }

    public void displayFromGallery(Intent data)
    {
        Uri imageAdd = data.getData();
        InputStream inputStream;
        if (imageAdd!=null)
        {
            try {
                inputStream = getContentResolver().openInputStream(imageAdd);
                Bitmap img = BitmapFactory.decodeStream(inputStream);
                imgDisplay.setImageBitmap(img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "We were not able to open the file", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void displayFromCamera(Intent data)
    {
        Bitmap img = (Bitmap)data.getExtras().get("data");
        imgDisplay.setImageBitmap(img);
//        ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
//        File destination = new File(Environment.getExternalStorageDirectory(),System.currentTimeMillis() +".jpg");
//        FileOutputStream fileOutStream;
//        try
//        {
//            destination.createNewFile();
//            fileOutStream = new FileOutputStream(destination);
//            fileOutStream.write(imgBytes.toByteArray());
//
//            fileOutStream.close();
//        }
//        catch (FileNotFoundException e)
//        {
//            Toast.makeText(this,"The File was not found",Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        }
//        catch (IOException e)
//        {
//            Toast.makeText(this,"There was an IO error",Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        }

    }
    public void load_model()
    {
        executor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                      classifier = TensorflowiInfrence.create(
                            getAssets(),
                            MODEL_PATH,
                            LABEL_PATH,
                            INPUT_SIZE);

                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });

    }

    public void activateDetection()
    {
        load_model();
    }

}
