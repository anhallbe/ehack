package eu.hallnet.ehack.knockknockehack;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.app.Notification.Builder;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import sense.jsense.SenseService;
import sense.jsense.util.SensorPub;
import sense.jsense.util.UpdateListener;

/**
 * Created by lundh on 2015-05-07.
 */

public class SensorIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    Builder builder;
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;


    public SensorIntentService() {
        super(SensorIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        final String SUBSCRIPTION_QUERY_KNOCK = "name:cameraSense AND the door";
        final String SUBSCRIPTION_TEMPERATURE = "name:temperatureSensor AND valueType:double";
        final String SENSE_URL = "ec2.hallnet.eu";
        final int SENSE_PORT = 1337;
        final boolean START_POLLING = true;
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        SenseService sense = new SenseService(SENSE_URL, SENSE_PORT, SenseService.INTERVAL_FAST, START_POLLING);

        Log.d("SIS", "Handle intent started");

        sense.subscribe(SUBSCRIPTION_QUERY_KNOCK, new UpdateListener() {
            @Override
            public void onUpdate(SensorPub sensorPub) {
                Log.d("WaitForUpdates", "Somebody knocked. Value: " + sensorPub.getValue());

                String imageURL = (String) sensorPub.getValue();//"http://213.159.191.231:8080/media/img.jpg"; //Lundhs Pi
                try {
                    InputStream in = new URL(imageURL).openStream();
                    final Bitmap imageMap = BitmapFactory.decodeStream(in);

                    //publishProgress(imageMap);
                    //makeNotification("KNOCK");
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("knock", imageMap);
                    receiver.send(STATUS_FINISHED, bundle);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        sense.subscribe(SUBSCRIPTION_TEMPERATURE, new UpdateListener() {
            @Override
            public void onUpdate(SensorPub sensorPub) {
                final double tempValue = Math.round(Double.parseDouble(sensorPub.getValue().toString()));
                //publishProgress(tempValue+"");
                Bundle bundle = new Bundle();
                bundle.putDouble("temp", tempValue);
                receiver.send(STATUS_FINISHED, bundle);
                Log.d("WaitForUpdates", "Temperature is " + tempValue);
            }
        });
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void makeNotification(String msg) {
        Log.d("NIS", "Making Notification");
        long[] vibraPattern = {0, 500, 250, 500 };
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        Builder mBuilder =
                new Builder(this)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setContentTitle("Knock!")
                        .setStyle(new Notification.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .setVibrate(vibraPattern);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
