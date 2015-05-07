package eu.hallnet.ehack.knockknockehack;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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


public class MainActivity extends Activity {

    ImageView cameraView;
    TextView alertTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = (ImageView) findViewById(R.id.knockCameraImageView);
        alertTextView = (TextView) findViewById(R.id.knockText);

        new WaitForUpdates().execute();
    }

    private class WaitForUpdates extends AsyncTask<Void, Bitmap, Void> {

        private final String SENSE_URL = "ec2.hallnet.eu";
        private final int SENSE_PORT = 1337;
        private final String SUBSCRIPTION_QUERY_KNOCK = "name:knockSensor AND front door";
        private final String SUBSCRIPTION_TEMPERATURE = "name:temperatureSensor AND valueType:double";

        private final boolean START_POLLING = true;

        @Override
        protected Void doInBackground(Void... params) {
            SenseService sense = new SenseService(SENSE_URL, SENSE_PORT, SenseService.INTERVAL_FAST, START_POLLING);
            Log.d("WaitForUpdates", "SenseService started.");

            sense.subscribe(SUBSCRIPTION_QUERY_KNOCK, new UpdateListener() {
                @Override
                public void onUpdate(SensorPub sensorPub) {
                    Log.d("WaitForUpdates", "Somebody knocked. Value: " + sensorPub.getValue());

                    String imageURL = "http://213.159.191.231:8080/media/img.jpg"; //Lundhs Pi
                    try {
                        InputStream in = new URL(imageURL).openStream();
                        Bitmap imageMap = BitmapFactory.decodeStream(in);

                        publishProgress(imageMap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            sense.subscribe(SUBSCRIPTION_TEMPERATURE, new UpdateListener() {
                @Override
                public void onUpdate(SensorPub sensorPub) {
                    double tempValue = Double.parseDouble(sensorPub.getValue().toString());
                    Log.d("WaitForUpdates", "Temperature is " + tempValue);
                }
            });


            return null;
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            Toast.makeText(getApplicationContext(), "Somebody is knocking! :D", Toast.LENGTH_LONG).show();
            alertTextView.setText("Somebody be knockin' on my doh");
            cameraView.setImageBitmap(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("WaitForUpdates", "Done executing async task.... this should not be the case! Loop forever or service?");
        }
    }
}
