package xyz.apricorn.sora.dialga;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

public class Pokemon extends DialgaWatchFaceService{

	private String name;
	private int dexNumber, height, weight;
	private Bitmap bitmap;
	private Boolean shiny;
	private Boolean form;

	Pokemon() {
		SqliteHelper sql = new SqliteHelper(this);
		RNGRolls rng = new RNGRolls();
		Breeding breeding = new Breeding();


		dexNumber = rng.rollDexNumber();
		name = sql.getPokemonName(dexNumber);
		height = sql.getPokemonHeight(dexNumber);
		weight = sql.getPokemonWeight(dexNumber);
        shiny = rng.shinyRoll(breeding.getForeignParent(), breeding.getShinyCharm());
        form = sql.checkPokemonForm(dexNumber);
        bitmap = setBitmap(name);

	}

	Pokemon(int dexNum) {
		SqliteHelper sql = new SqliteHelper(this);
		RNGRolls rng = new RNGRolls();
        Breeding breeding = new Breeding();

		dexNumber = dexNum;
		name = sql.getPokemonName(dexNumber);
		height = sql.getPokemonHeight(dexNumber);
		weight = sql.getPokemonWeight(dexNumber);
		form = sql.checkPokemonForm(dexNumber);
        shiny = rng.shinyRoll(breeding.getForeignParent(), breeding.getShinyCharm());
        bitmap = setBitmap(name);

	}

    private Bitmap setBitmap(String name)
    {
        int resId;
        Bitmap bitties;
        resId = queryResourceId(name);

        bitties = decodeBitmap(resId);

        return bitties;

    }

	private int queryResourceId(String name) {
		int resId;
        String shine, formie;

        shine = shiny ? "_shiny" : "";
        formie = form ? setForm() : "";

		resId = getResources().getIdentifier(name + formie + shine, "drawable", getPackageName());

        return resId;

	}

	private String setForm(){
	    String returnForm;
	    SqliteHelper sql = new SqliteHelper(this);

	    ArrayList<String> formsList;

	    formsList = sql.getFormsList(dexNumber);

	    returnForm = formsList.get(0);

	    return returnForm;

    }

	private Bitmap decodeBitmap(int resId)
    {

		bitmap = BitmapFactory.decodeResource(getResources(), resId);

		return bitmap;

	}

	public String getName()
    {
        return name;
    }

    public int getHeight()
    {
        return height;
    }

    public int getWeight()
    {
        return weight;
    }

    public Bitmap getBitmap()
    {
        return bitmap;
    }

    public void setBitmap(Bitmap bitties)
    {
        bitmap = bitties;
    }

}