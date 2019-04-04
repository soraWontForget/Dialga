package xyz.apricorn.apricorn;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Random;

public class Pokemon {

	private String name;
	private int dexNumber, height, weight;
	private Bitmap bitmap;
	private Boolean shiny;
	private Boolean form;

	RNGRolls rng;

	Pokemon(Context context) {
		SqliteHelper sql = new SqliteHelper(context);
		rng = new RNGRolls();
		Breeding breeding = new Breeding();


		dexNumber = rng.rollDexNumber();
		name = sql.getPokemonName(dexNumber);
		height = sql.getPokemonHeight(dexNumber);
		weight = sql.getPokemonWeight(dexNumber);
        shiny = rng.shinyRoll(breeding.getForeignParent(), breeding.getShinyCharm());
        form = sql.checkPokemonForm(dexNumber);
        bitmap = setBitmap(context);

        sql.close();

	}

	Pokemon(Context context, int dexNum) {
		SqliteHelper sql = new SqliteHelper(context);
		rng = new RNGRolls();
        Breeding breeding = new Breeding();

		dexNumber = dexNum;

		name = sql.getPokemonName(dexNumber);
		height = sql.getPokemonHeight(dexNumber);
		weight = sql.getPokemonWeight(dexNumber);
		form = sql.checkPokemonForm(dexNumber);
        shiny = rng.shinyRoll(breeding.getForeignParent(), breeding.getShinyCharm());
        bitmap = setBitmap(context);

        sql.close();

	}

	public void setNewPokemon(String name){
		dexNumber 
	}

    private Bitmap setBitmap(Context context)
    {
        int resId;
        Bitmap bitties;
        resId = queryResourceId(context);

        bitties = decodeBitmap(resId, context);

        return bitties;

    }


	public int queryResourceId(Context context) {
		int resId;
        String shine, formie,resourceName;

        shine = shiny ? "_shiny" : "";
        formie = form ? setForm(context) : "";

        resourceName = name + formie;

		resId = context.getResources().getIdentifier(resourceName/*+ formie */+ shine, "drawable", context.getPackageName());

        return resId;

	}

	// Returns the form of a pokemon from a list of that pokemon's form(s)
	private String setForm(Context context){
	    String returnForm;
	    /*Boolean hasForms;*/
		int index;
	    SqliteHelper sql = new SqliteHelper(context);
	    ArrayList<String> formsList;

	    /*hasForms = sql.checkPokemonForm(dexNumber);*/

	    /*if(hasForms)
	    {*/
            formsList = sql.getFormsList(dexNumber);

			int boundary = formsList.size() - 1;

            if (boundary > 0) {
				index = rng.formRoll(boundary);
				returnForm = formsList.get(index);
			} else {
            	returnForm = formsList.get(0);
			}

        sql.close();

	    return returnForm;

    }

    //
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

    public void rerollPokemon(Context context)
	{
		SqliteHelper sql = new SqliteHelper(context);
		RNGRolls rng = new RNGRolls();
		Breeding breeding = new Breeding();

		dexNumber = rng.rollDexNumber();
		name = sql.getPokemonName(dexNumber);
		height = sql.getPokemonHeight(dexNumber);
		weight = sql.getPokemonWeight(dexNumber);
		shiny = rng.shinyRoll(breeding.getForeignParent(), breeding.getShinyCharm());
		form = sql.checkPokemonForm(dexNumber);

		bitmap.recycle();
		bitmap = setBitmap(context);

		sql.close();

	}

	public void rerollPokemonInc(Context context, int dexNum)
	{
		SqliteHelper sql = new SqliteHelper(context);
		RNGRolls rng = new RNGRolls();
		Breeding breeding = new Breeding();

		dexNumber = dexNum;
		name = sql.getPokemonName(dexNumber);
		height = sql.getPokemonHeight(dexNumber);
		weight = sql.getPokemonWeight(dexNumber);
		shiny = rng.shinyRoll(breeding.getForeignParent(), breeding.getShinyCharm());
		form = sql.checkPokemonForm(dexNumber);

		bitmap.recycle();
		bitmap = setBitmap(context);

		sql.close();

	}

}