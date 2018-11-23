package java.harbour.space.deeplearningdemohsu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnCam = (Button)findViewById(R.id.btnCam);
        Button btnGallery = (Button)findViewById(R.id.btnGallery);
        Button btnDetect = (Button)findViewById(R.id.btnDetect);
        ImageView imgDisplay=(ImageView)findViewById(R.id.picboxDisplay);
        TextView txtAcc = (TextView)findViewById(R.id.txtAcc);
        TextView txtClass = (TextView)findViewById(R.id.txtClass);
    }


}
