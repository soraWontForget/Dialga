package xyz.apricorn.sora.mobile;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

public class MainActivity extends Activity {

    final PokemonTeam pkmnTeam = new PokemonTeam(MainActivity.this);
    int currentImage = 0;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Bitmap[] pkmnTeamBitmap = new Bitmap[6];


        SqliteHelper sqliteHelper = new SqliteHelper(MainActivity.this);

        for (int i = 0; i < 6; i++) {
            pkmnTeamBitmap[i] = pkmnTeam.getPokemonBitmap(i);

        }

        initializeImageSwitcher();
        setInitialImage();
        setImageRotateListener();



        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pkmnTeam.rerollTeam(MainActivity.this);
                for (int i = 0; i < 6; i++) {
                    pkmnTeamBitmap[i] = pkmnTeam.getPokemonBitmap(i);

                }
            }
        });

    }

    private void initializeImageSwitcher() {
        final ImageSwitcher imageSwitcher = findViewById(R.id.imageSwitcher);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(MainActivity.this);
                return imageView;
            }
        });

    }

    private void setImageRotateListener() {
        final Button rotatebutton = findViewById(R.id.button2);
        rotatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                currentImage++;
                if (currentImage == 5) {
                    currentImage = 0;
                }
                setCurrentImage();
            }
        });
    }
    private void setInitialImage() {
        setCurrentImage();
    }

    private void setCurrentImage() {
        final ImageSwitcher imageSwitcher = findViewById(R.id.imageSwitcher);
        imageSwitcher.setImageResource(pkmnTeam.getResourceId(MainActivity.this, currentImage));
    }

    /*private void you()
    {
        imageSwitcher.setImageResource(pkmnTeam.getResourceId(MainActivity.this, currentImage));
    }*/
    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected  void onResume(){
        super.onResume();

    }

    protected void onStop(){
        super.onStop();
    }

    public void onDestroy(){
        super.onDestroy();
    }
}
