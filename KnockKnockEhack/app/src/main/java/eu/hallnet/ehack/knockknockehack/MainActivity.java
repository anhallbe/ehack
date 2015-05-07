package eu.hallnet.ehack.knockknockehack;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mashape.unirest.http.Unirest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import sense.jsense.SenseService;
import sense.jsense.util.SensorPub;
import sense.jsense.util.UpdateListener;


public class MainActivity extends Activity implements SensorResultReceiver.Receiver {

    ImageView cameraView;
    TextView alertTextView;
    TextView knockTextView;
    private SensorResultReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mReceiver = new SensorResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SensorIntentService.class);


        cameraView = (ImageView) findViewById(R.id.knockCameraImageView);
        alertTextView = (TextView) findViewById(R.id.knockText);

        //new WaitForKnockUpdates().execute();
        intent.putExtra("receiver", mReceiver);
        startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case SensorIntentService.STATUS_RUNNING:

                setProgressBarIndeterminateVisibility(true);
                break;
            case SensorIntentService.STATUS_FINISHED:
                /* Hide progress & extract result from bundle */
                setProgressBarIndeterminateVisibility(false);

                Parcelable pResults = resultData.getParcelable("knock");
                if(pResults != null){
                    Toast.makeText(getApplicationContext(), "Somebody is knocking! :D", Toast.LENGTH_LONG).show();
                    cameraView.setImageBitmap((Bitmap) pResults);
                }
                Double dResults = resultData.getDouble("temp");
                if (dResults != null){
                    alertTextView.setText("Temperature: " + dResults);
                }
                break;
            case SensorIntentService.STATUS_ERROR:
                /* Handle the error */
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
