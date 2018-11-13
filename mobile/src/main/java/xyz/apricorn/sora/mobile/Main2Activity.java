package xyz.apricorn.sora.mobile;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;
import xyz.apricorn.sora.mobile.iconMaps;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemAsset;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.StringBufferInputStream;
import java.util.Map;


public class Main2Activity extends Activity/*
    implements DataClient.OnDataChangedListener,
        MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener */{

    /*private static final String TAG = "MainActivity";

    private static final int CHANGE_SECOND_HAND_ICON = 1;

    private static final String CURRENT_ICON_PATH = "/second-hand-icon";

    */
    private static final String SECOND_HAND_CONFIG = "/second-hand";
    private static final String BACKGROUND_CONFIG = "/background";
    String secondHandPokemon;
    String TAG = "Main2Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final Switch secondHandToggle = findViewById(R.id.switch2);
        iconMaps.putGenI();
        final PutDataMapRequest secondHandConfig = PutDataMapRequest.create(SECOND_HAND_CONFIG);
        final PutDataRequest request = secondHandConfig.asPutDataRequest();
        final Task<DataItem> dataItemTask = Wearable.getDataClient(this).putDataItem(request);
        secondHandToggle.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick (View view){

                String statusSwitch;
                if (secondHandToggle.isChecked()) {

                    umbreon();

                } else {

                    espeon();

                }


             }

             public void umbreon(){
                 secondHandConfig.getDataMap().putInt("secondHand", R.drawable.umbreon);
                 request.setUrgent();
                 dataItemTask.addOnSuccessListener(
                         new OnSuccessListener<DataItem>() {
                             @Override
                             public void onSuccess(DataItem dataItem) {
                                 String statusSwitch;
                                 statusSwitch = secondHandToggle.getTextOn().toString();
                                 Toast.makeText(getApplicationContext(), "Second hand changed to: " + statusSwitch, Toast.LENGTH_LONG).show();
                             }
                         }
                 );

             }

             public void espeon(){
                 secondHandConfig.getDataMap().putInt("secondHand", R.drawable.espeon);
                 request.setUrgent();
                 dataItemTask.addOnSuccessListener(
                         new OnSuccessListener<DataItem>() {
                             @Override
                             public void onSuccess(DataItem dataItem) {
                                 String statusSwitch;
                                 statusSwitch = secondHandToggle.getTextOff().toString();
                                 Toast.makeText(getApplicationContext(), "Second hand changed to: " + statusSwitch, Toast.LENGTH_LONG).show();
                             }
                         }
                 );


             }


        });
    }

}
