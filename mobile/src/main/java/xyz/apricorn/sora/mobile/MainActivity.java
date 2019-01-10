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
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemAsset;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.StringBufferInputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;


public class Main2Activity extends Activity/*
    implements DataClient.OnDataChangedListener,
        MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener */{

    /*private static final String TAG = "MainActivity";

    private static final int CHANGE_SECOND_HAND_ICON = 1;

    private static final String CURRENT_ICON_PATH = "/second-hand-icon";

    */
    private static final String TEAM= "/team";
    private static final String TEAM_LEAD= "/team-lead";
    private static final String CUSTOMIZATION_CAPABILITY_NAME = "customization";
    private static final String CUSTOMIZATION_MESSAGE_PATH = "/customization";

    String TAG = "Main2Activity";
    String[] team = {"umbreon", "espeon", "flareon", "vaproeon", "jolteon", "sylveon"};
    String[] team2 = {"glaceon", "leafeon", "beedrill", "mimikyu", "garchomp", "raichu"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final Switch secondHandToggle = findViewById(R.id.switch2);
        iconMaps.putGenI();
        final PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(TEAM);
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
                 putDataMapRequest.getDataMap().putStringArray(TEAM, team);
                 final PutDataRequest request = putDataMapRequest.asPutDataRequest();
                 final Task<DataItem> dataItemTask = Wearable.getDataClient(Main2Activity.this).putDataItem(request);
                 request.setUrgent();
                 dataItemTask.addOnSuccessListener(
                         new OnSuccessListener<DataItem>() {
                             @Override
                             public void onSuccess(DataItem dataItem) {
                                 String statusSwitch;
                                 statusSwitch = secondHandToggle.getTextOn().toString();
                                 Toast.makeText(getApplicationContext(), "Team changed to: " + statusSwitch, Toast.LENGTH_LONG).show();
                             }
                         }
                 );

             }

             public void espeon(){
                 putDataMapRequest.getDataMap().putStringArray(TEAM, team2);
                 final PutDataRequest request = putDataMapRequest.asPutDataRequest();
                 final Task<DataItem> dataItemTask = Wearable.getDataClient(Main2Activity.this).putDataItem(request);
                 request.setUrgent();
                 dataItemTask.addOnSuccessListener(
                         new OnSuccessListener<DataItem>() {
                             @Override
                             public void onSuccess(DataItem dataItem) {
                                 String statusSwitch;
                                 statusSwitch = secondHandToggle.getTextOff().toString();
                                 Toast.makeText(getApplicationContext(), "Team changed to: " + statusSwitch, Toast.LENGTH_LONG).show();
                             }
                         }
                 );


             }

             public void setupCustomization() throws ExecutionException, InterruptedException {

                     CapabilityInfo capabilityInfo = Tasks.await(
                             Wearable.getCapabilityClient(getApplicationContext()).getCapability(
                                     CUSTOMIZATION_CAPABILITY_NAME, CapabilityClient.FILTER_REACHABLE));


                 updateTranscriptionCapability(capabilityInfo);
             }

             public String customizationNodeId = null;

             public void updateTranscriptionCapability(CapabilityInfo capabilityInfo){
                 Set<Node> connectedNodes = capabilityInfo.getNodes();
                 customizationNodeId = pickBestNodeId(connectedNodes);

             }

             public String pickBestNodeId(Set<Node> nodes){
                 String bestNodeId = null;

                 for (Node node : nodes) {
                     if(node.isNearby()) {
                         return node.getId();

                     }
                     bestNodeId = node.getId();
                 }
                 return bestNodeId;
             }

             private void sendCustomizationMethod(byte[] team) {
                 if(customizationNodeId != null){
                     Task<Integer> sendTask =
                             Wearable.getMessageClient(getApplicationContext()).sendMessage(
                                     customizationNodeId, CUSTOMIZATION_MESSAGE_PATH, team);

                 } else {

                 }

             }





        });
    }

}
