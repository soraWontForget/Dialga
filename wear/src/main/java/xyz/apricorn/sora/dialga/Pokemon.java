package xyz.apricorn.sora.dialga;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

public class Pokemon {

	private String name;
	private int dexNumber, height, weight;
	private Bitmap bitmap;
	private Boolean shiny;
	private Boolean form;

	Pokemon(Context context) {
		SqliteHelper sql = new SqliteHelper(context);
		RNGRolls rng = new RNGRolls();
		Breeding breeding = new Breeding();


		dexNumber = rng.rollDexNumber();
		name = sql.getPokemonName(dexNumber);
		height = sql.getPokemonHeight(dexNumber);
		weight = sql.getPokemonWeight(dexNumber);
        shiny = rng.shinyRoll(breeding.getForeignParent(), breeding.getShinyCharm());
        form = sql.checkPokemonForm(dexNumber);
        bitmap = setBitmap(name, context);

	}

	Pokemon(int dexNum, Context context) {
		SqliteHelper sql = new SqliteHelper(context);
		RNGRolls rng = new RNGRolls();
        Breeding breeding = new Breeding();

		dexNumber = dexNum;
		name = sql.getPokemonName(dexNumber);
		height = sql.getPokemonHeight(dexNumber);
		weight = sql.getPokemonWeight(dexNumber);
		form = sql.checkPokemonForm(dexNumber);
        shiny = rng.shinyRoll(breeding.getForeignParent(), breeding.getShinyCharm());
        bitmap = setBitmap(name, context);

	}

    private Bitmap setBitmap(String name, Context context)
    {
        int resId;
        Bitmap bitties;
        resId = queryResourceId(name, context);

        bitties = decodeBitmap(resId, context);

        return bitties;

    }

	private int queryResourceId(String name, Context context) {
		int resId;
        String shine, formie;

        shine = shiny ? "_shiny" : "";
        formie = form ? setForm(context) : "";

		resId = context.getResources().getIdentifier(name + formie + shine, "drawable", context.getPackageName());

        return resId;

	}

	private String setForm(Context context){
	    String returnForm = "";
	    Boolean hasForms;
	    SqliteHelper sql = new SqliteHelper(context);

	    ArrayList<String> formsList;

	    hasForms = sql.checkPokemonForm(dexNumber);

	    if(hasForms)
	    {
            formsList = sql.getFormsList(dexNumber);
            returnForm = formsList.get(0);
        }


	    return returnForm;

    }

	private Bitmap decodeBitmap(int resId, Context context)
    {

        Bitmap mBitmap;
		mBitmap = BitmapFactory.decodeResource(context.getResources(), resId);

		return mBitmap;

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