package xyz.apricorn.sora.dialga;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.support.wearable.watchface.WatchFaceService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class SqliteHelper extends SQLiteOpenHelper {


    public static final String DATABASE_PATH = "/data/data/xyz.apricorn.sora.dialga/databases/";
    public static final String DATABASE_NAME = "dialga-db.sqlite";
    public static final String TABLE_NAME_POKEMON = "pokemon";
    public static final String TABLE_NAME_FORMS = "forms";
    private static final String COLUMN_NAME_FORMS = "forms";
    public static final String COLUMN_NAME_FORM_NAME = "form_name";
    public static final String COLUMN_NAME_RETURN_STRING = "return_string";
    public static final String POKEMON_NAME_COLUMN = "pokemon_name";
    public static final String RETURN_STRING_COLUMN = "return_string";
    public static final String POKEMON_HEIGHT_COLUMN = "height";
    public static final String POKEMON_WEIGHT_COLUMN = "weight";
    public static final String GENERATION_COLUMN = "generation";
    public static final String NATIONAL_DEX_NUMBER_COLUMN = "dex_number";
    public static final String POKEMON_TYPE_COLOUMN = "type";
    public static String data;
    public SQLiteDatabase dialgaDataBase;
    public final Context myContext;

    public SqliteHelper(Context watchFaceContext){
        super(watchFaceContext, DATABASE_NAME,null,1);
        this.myContext = watchFaceContext;

    }

    public void createDataBase() throws IOException{

            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }

        }

    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DATABASE_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return (checkDB != null);
    }

    private void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = DATABASE_PATH + DATABASE_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DATABASE_PATH + DATABASE_NAME;
        dialgaDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(dialgaDataBase != null)
            dialgaDataBase.close();

        super.close();

    }

    public void onCreate(SQLiteDatabase db) {

    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {}

    public String getPokemonName(int dexNumber){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + POKEMON_NAME_COLUMN + " FROM " + TABLE_NAME_POKEMON
                + " WHERE "+ NATIONAL_DEX_NUMBER_COLUMN
                + " = '"+ dexNumber + "'";
        Cursor init = db.rawQuery(query, null);
        init.moveToFirst();

        data = init.getString(0);

        init.close();
        return data;

    }

    public int getPokemonHeight(int dexNumber){
        int height;

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + POKEMON_HEIGHT_COLUMN + " FROM "+ TABLE_NAME_POKEMON
                + " WHERE "+ NATIONAL_DEX_NUMBER_COLUMN
                + " = '"+ dexNumber + "'";
        Cursor init = db.rawQuery(query, null);
        init.moveToFirst();

        height = Integer.parseInt(init.getString(0));

        init.close();
        return height;

    }

    public int getPokemonWeight(int dexNumber){
        int weight;

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + POKEMON_WEIGHT_COLUMN + " FROM " + TABLE_NAME_POKEMON
                + " WHERE "+ NATIONAL_DEX_NUMBER_COLUMN
                + " = '"+ dexNumber + "'";
        Cursor init = db.rawQuery(query, null);
        init.moveToFirst();

        weight = Integer.parseInt(init.getString(0));

        init.close();
        return weight;

    }

    public Boolean checkPokemonForm(int dexNumber){
        Boolean form;
        int formCheck;

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_NAME_FORMS + " FROM " + TABLE_NAME_POKEMON
                + " WHERE "+ NATIONAL_DEX_NUMBER_COLUMN
                + " = '"+ dexNumber + "'";
        Cursor init = db.rawQuery(query, null);
        init.moveToFirst();

        formCheck = init.getInt(0);

        init.close();

        if(formCheck == 1){
            form = true;
        } else {
            form = false;
        }

        return form;

    }

    public ArrayList<String> getFormsList(int dexNumber){
        ArrayList<String> formsList = new ArrayList<>();
        String form;

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_NAME_RETURN_STRING + " FROM " + TABLE_NAME_FORMS
                + " WHERE "+ NATIONAL_DEX_NUMBER_COLUMN
                + " = '"+ dexNumber + "'";
        Cursor init = db.rawQuery(query, null);
        init.moveToFirst();

        do
        {

            form = init.getString(0);
            formsList.add(form);
            init.moveToNext();

        } while (init.moveToNext());



        init.close();

        return formsList;

    }

}
