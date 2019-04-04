package xyz.apricorn.sora.dialga;

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
	private RNGRolls rng;
	private Breeding breeding;

	Pokemon(Context context) {
		SqliteHelper sql = new SqliteHelper(context);
		rng = new RNGRolls();
		breeding = new Breeding();


		dexNumber = rng.rollDexNumber();
		name = sql.getPokemonName(dexNumber);
		height = sql.getPokemonHeight(dexNumber);
		weight = sql.getPokemonWeight(dexNumber);
        shiny = rng.shinyRoll(breeding.getForeignParent(), breeding.getShinyCharm());
        form = sql.checkPokemonForm(dexNumber);
        bitmap = setBitmap(name, context);

        sql.close();

	}

	Pokemon(Context context, int dexNum) {
		SqliteHelper sql = new SqliteHelper(context);
		rng = new RNGRolls();
        breeding = new Breeding();

		dexNumber = dexNum;

		name = sql.getPokemonName(dexNumber);
		height = sql.getPokemonHeight(dexNumber);
		weight = sql.getPokemonWeight(dexNumber);
		form = sql.checkPokemonForm(dexNumber);
        shiny = rng.shinyRoll(breeding.getForeignParent(), breeding.getShinyCharm());
        bitmap = setBitmap(name, context);

        sql.close();

	}

	public void setNewPokemon(Context context, int dex)
	{
		SqliteHelper sql = new SqliteHelper(context);
		dexNumber = dex;

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
        resId = getResourceId(name, context);

        bitties = decodeBitmap(resId, context);

        return bitties;

    }


	private int getResourceId(String name, Context context) {
		int resId;
        String shine, formie;

        shine = shiny ? "_shiny" : "";
        formie = form ? setForm(context) : "";

		resId = context.getResources().getIdentifier(name + formie + shine, "drawable", context.getPackageName());

        return resId;

	}

	// Returns the form of a pokemon from a list of that pokemon's form(s)
	private String setForm(Context context){
	    String returnForm;
	    /*Boolean hasForms;*/
		int index;
	    SqliteHelper sql = new SqliteHelper(context);
	    Random random = new Random();

	    ArrayList<String> formsList;

	    /*hasForms = sql.checkPokemonForm(dexNumber);*/

	    /*if(hasForms)
	    {*/
            formsList = sql.getFormsList(dexNumber);

			index = formsList.size() - 1;

            if (index > 0) {
				index = random.nextInt(index);
			}

			returnForm = formsList.get(index);
        /*}*/

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
		bitmap = setBitmap(name, context);

	}

}