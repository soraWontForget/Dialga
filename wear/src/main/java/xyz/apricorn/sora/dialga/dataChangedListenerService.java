package xyz.apricorn.sora.dialga;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.view.SurfaceHolder;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;

public class dataChangedListenerService extends dialgaWatchFaceService implements DataClient.OnDataChangedListener {

        final String TEAM = "/team";
        String[] newTeam = new String[6];

        @Override
        public void onDataChanged(DataEventBuffer dataEvents) {
            for (DataEvent event : dataEvents){
                if(event.getType() == DataEvent.TYPE_CHANGED){
                    DataItem item = event.getDataItem();
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();

                    newTeam = dataMap.getStringArray("/team");



                    Bundle bundle = new Bundle();
                    Intent set_team = new Intent("ACTION_SET_WALLPAPER");
                    set_team.putExtra("newTeam", newTeam);


                    /*set_team.putExtras(bundle);*/

                    sendBroadcast(set_team);

                    }
                }

            }


}
