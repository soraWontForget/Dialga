package xyz.apricorn.apricorn;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

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


        SqliteHelper sqliteHelper = new SqliteHelper(MainActivity.this);

            try {
                sqliteHelper.createDataBase();
                sqliteHelper.close();
            } catch (IOException ioe) {
                throw new Error("Unable to create database");
            }

        pkmnTeam = new PokemonTeam(MainActivity.this);

        final Button button = findViewById(R.id.button);
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


    /*private void setNewImages(PokemonTeam pkmnTeam){
        imageView.setImageBitmap(pkmnTeam.getPokemonBitmap(0));
        imageView.setImageBitmap(pkmnTeam.getPokemonBitmap(1));
        imageView.setImageBitmap(pkmnTeam.getPokemonBitmap(2));
        imageView.setImageBitmap(pkmnTeam.getPokemonBitmap(3));
        imageView.setImageBitmap(pkmnTeam.getPokemonBitmap(4));
        imageView.setImageBitmap(pkmnTeam.getPokemonBitmap(5));

    }*/

}
