package xyz.apricorn.sora.mobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SqliteHelper databaseHelper = new SqliteHelper(this);
        try {
            databaseHelper.createDataBase();
            databaseHelper.close();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           /*     pkmnTeam.rerollTeam(MainActivity.this);
                for (int i = 0; i < 6; i++) {
                    pkmnTeamBitmap[i] = pkmnTeam.getPokemonBitmap(i);*/

            }

        });

        final Button button2 = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });

        final ImageView imageView = findViewById(R.id.imageView);
        final ImageView imageView2 = findViewById(R.id.imageView2);
        final ImageView imageView3 = findViewById(R.id.imageView3);
        final ImageView imageView4 = findViewById(R.id.imageView4);
        final ImageView imageView5 = findViewById(R.id.imageView5);
    }
}
