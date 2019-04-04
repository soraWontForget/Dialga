package xyz.apricorn.apricorn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import xyz.apricorn.apricorn.R;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import android.support.wearable.companion.WatchFaceCompanion;
import android.widget.Spinner;

import java.io.IOException;

public class MainActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<DataApi.DataItemResult> {

    private static final String TAG = "DigitalWatchFaceConfig";
    private static final String TEAM_LEAD_KEY = "TEAM_LEAD";
    private static final String SLOT_2_KEY = "SLOT_2";
    private static final String SLOT_3_KEY = "SLOT_3";
    private static final String SLOT_4_KEY = "SLOT_4";
    private static final String SLOT_5_KEY = "SLOT_5";
    private static final String SLOT_6_KEY = "SLOT_6";
    private static final String PATH_WITH_FEATURE = "/watch_face_config/Dialga";

    private GoogleApiClient mGoogleApiClient;
    private String mPeerId;

    PokemonTeam pkmnTeam;
    int counter = 1;
    /*final ImageView imageView = findViewById(R.id.imageView);
    final ImageView imageView2 = findViewById(R.id.imageView2);
    final ImageView imageView3 = findViewById(R.id.imageView3);
    final ImageView imageView4 = findViewById(R.id.imageView4);
    final ImageView imageView5 = findViewById(R.id.imageView5);
    final ImageView imageView6 = findViewById(R.id.imageView6);*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPeerId = getIntent().getStringExtra(WatchFaceCompanion.EXTRA_PEER_ID);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        SqliteHelper sqliteHelper = new SqliteHelper(MainActivity.this);

            try {
                sqliteHelper.createDataBase();
                sqliteHelper.close();
            } catch (IOException ioe) {
                throw new Error("Unable to create database");
            }

        pkmnTeam = new PokemonTeam(MainActivity.this);

        final ImageButton button = findViewById(R.id.imageButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*ImageView imageView = findViewById(R.id.imageView);
                *//*Bitmap newBittie = BitmapFactory.decodeResource(getResources(), R.drawable.solgaleo_dawn);*//*
                imageView.setImageBitmap(*//*newBittie*//*pkmnTeam.getPokemonBitmap(1));*/

                /*roll(pkmnTeam);*/

                roll();

            }
        });


    }

    public void setBitmapSlotN(int slot){
        switch(slot){

            case 0:
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(pkmnTeam.getPokemonBitmap(slot));
                break;

            case 1:
                ImageView imageView2 = findViewById(R.id.imageView2);
                imageView2.setImageBitmap(pkmnTeam.getPokemonBitmap(slot));
                break;

            case 2:
                ImageView imageView3 = findViewById(R.id.imageView3);
                imageView3.setImageBitmap(pkmnTeam.getPokemonBitmap(slot));
                break;

            case 3:
                ImageView imageView4 = findViewById(R.id.imageView4);
                imageView4.setImageBitmap(pkmnTeam.getPokemonBitmap(slot));
                break;

            case 4:
                ImageView imageView5 = findViewById(R.id.imageView5);
                imageView5.setImageBitmap(pkmnTeam.getPokemonBitmap(slot));
                break;

            case 5:
                ImageView imageView6 = findViewById(R.id.imageView6);
                imageView6.setImageBitmap(pkmnTeam.getPokemonBitmap(slot));
                break;

        }

    }

    private void roll(){

        pkmnTeam.rerollTeamInc(MainActivity.this, counter);

        setBitmapTeam();
        counter++;
    }

    private void setBitmapTeam(){

        for(int i = 0; i < 6; i++)
        setBitmapSlotN(i);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override // GoogleApiClient.ConnectionCallbacks
    public void onConnected(Bundle connectionHint) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onConnected: " + connectionHint);
        }

        if (mPeerId != null) {
            Uri.Builder builder = new Uri.Builder();
            Uri uri = builder.scheme("wear").path(PATH_WITH_FEATURE).authority(mPeerId).build();
            Wearable.DataApi.getDataItem(mGoogleApiClient, uri).setResultCallback(this);
        } else {
            displayNoConnectedDeviceDialog();
        }
    }

    private void displayNoConnectedDeviceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String messageText = getResources().getString(R.string.title_no_device_connected);
        String okText = getResources().getString(R.string.ok_no_device_connected);
        builder.setMessage(messageText)
                .setCancelable(false)
                .setPositiveButton(okText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /*@Override
    public void onConnected(@Nullable Bundle bundle) {

    }*/

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
        if (dataItemResult.getStatus().isSuccess() && dataItemResult.getDataItem() != null) {
            DataItem configDataItem = dataItemResult.getDataItem();
            DataMapItem dataMapItem = DataMapItem.fromDataItem(configDataItem);
            DataMap config = dataMapItem.getDataMap();
            setUpAllPickers(config);
        } else {
            // If DataItem with the current config can't be retrieved, select the default items on
            // each picker.
            setUpAllPickers(null);
        }
    }


    /*private void setNewImages(PokemonTeam pkmnTeam){
        imageView.setImageBitmap(pkmnTeam.getPokemonBitmap(0));
        imageView.setImageBitmap(pkmnTeam.getPokemonBitmap(1));
        imageView.setImageBitmap(pkmnTeam.getPokemonBitmap(2));
        imageView.setImageBitmap(pkmnTeam.getPokemonBitmap(3));
        imageView.setImageBitmap(pkmnTeam.getPokemonBitmap(4));
        imageView.setImageBitmap(pkmnTeam.getPokemonBitmap(5));

    }*/

    private void setUpAllPickers(DataMap config) {
        setUpColorPickerSelection(R.id.imageButton, TEAM_LEAD_KEY, config,
                R.string.pokemon_cosmog);
        /*setUpColorPickerSelection(R.id.hours, KEY_HOURS_COLOR, config, R.string.color_white);
        setUpColorPickerSelection(R.id.minutes, KEY_MINUTES_COLOR, config, R.string.color_white);
        setUpColorPickerSelection(R.id.seconds, KEY_SECONDS_COLOR, config, R.string.color_gray);

        setUpColorPickerListener(R.id.background, KEY_BACKGROUND_COLOR);
        setUpColorPickerListener(R.id.hours, KEY_HOURS_COLOR);
        setUpColorPickerListener(R.id.minutes, KEY_MINUTES_COLOR);
        setUpColorPickerListener(R.id.seconds, KEY_SECONDS_COLOR);*/
    }

    private void setUpColorPickerSelection(int imageButtonId, final String configKey, DataMap config,
                                           int pokemonNameId) {
        String defaultPokemon = getString(pokemonNameId);
        /*int defaultColor = Color.parseColor(defaultPokemon);*/
        String currentConfig;
        if (config != null) {
            currentConfig = config.getString(configKey, defaultPokemon);
        } else {
            currentConfig = defaultPokemon;
        }
        Spinner spinner = findViewById(R.id.spinner);
        String[] generationSeven = getResources().getStringArray(R.array.Generation_Seven);
        for (int i = 0; i < generationSeven.length; i++) {
            if (generationSeven[i] == currentConfig) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void setUpColorPickerListener(int spinnerId, final String configKey) {
        Spinner spinner = (Spinner) findViewById(spinnerId);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                final String colorName = (String) adapterView.getItemAtPosition(pos);
                sendConfigUpdateMessage(configKey, Color.parseColor(colorName));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    private void sendConfigUpdateMessage(String configKey, int color) {
        if (mPeerId != null) {
            DataMap config = new DataMap();
            config.putInt(configKey, color);
            byte[] rawData = config.toByteArray();
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mPeerId, PATH_WITH_FEATURE, rawData);

            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Sent watch face config message: " + configKey + " -> "
                        + Integer.toHexString(color));
            }
        }
    }
}


