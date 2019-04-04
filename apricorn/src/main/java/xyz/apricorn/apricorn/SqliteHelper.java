package xyz.apricorn.apricorn;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class SqliteHelper extends SQLiteOpenHelper {


    public static Context myContext;
    public static final String DATABASE_NAME = "dialga-db.sqlite";
    public static final String DATABASE_PATH = "/data/data/xyz.apricorn.apricorn/databases/";
    public static final String TABLE_NAME_POKEMON = "pokemon";
    public static final String TABLE_NAME_FORMS = "forms";
    private static final String COLUMN_NAME_FORMS = "forms";
    public static final String COLUMN_NAME_FORM_NAME = "form_name";
    public static final String COLUMN_NAME_RETURN_STRING = "return_string";
    public static final String POKEMON_NAME_COLUMN = "pokemon_name";
    public static final String COLUMN_NAME_HEIGHT = "height";
    public static final String COLUMN_NAME_WEIGHT = "weight";
    public static final String COLUMN_NAME_GENERATION = "generation";
    public static final String COLUMN_NAME_DEX_NUMBER = "dex_number";
    public static final String COLUMN_NAME_TYPE = "type";
    public static String data;
    public SQLiteDatabase dialgaDataBase;


    public SqliteHelper(Context appContext){
        super(appContext, DATABASE_NAME,null,1);
        this.myContext = appContext;

    }

    public void createDataBase() throws IOException{

            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }

        }

    public boolean checkDataBase(){

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

        try {

            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

        } finally {

            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();

        }
    }
    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DATABASE_PATH + DATABASE_NAME;
        dialgaDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(dialgaDataBase != null) {
            dialgaDataBase.close();
        }
        super.close();

    }

    public void onCreate(SQLiteDatabase db) {

    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {}

    public String getPokemonName(int dexNumber){
        String name;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + POKEMON_NAME_COLUMN + " FROM " + TABLE_NAME_POKEMON
                + " WHERE "+ COLUMN_NAME_DEX_NUMBER
                + " = "+ dexNumber + ";";
        Cursor init = db.rawQuery(query, null);
        init.moveToFirst();

        name = init.getString(0);

        init.close();
        return name;

    }

    public int getPokemonHeight(int dexNumber){
        int height;

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_NAME_HEIGHT + " FROM "+ TABLE_NAME_POKEMON
                + " WHERE "+ COLUMN_NAME_DEX_NUMBER
                + " = "+ dexNumber + ";";
        Cursor init = db.rawQuery(query, null);
        init.moveToFirst();

        height = Integer.parseInt(init.getString(init.getColumnIndex(COLUMN_NAME_HEIGHT)));

        init.close();
        return height;

    }

    public int getPokemonWeight(int dexNumber){
        int weight;

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_NAME_WEIGHT + " FROM " + TABLE_NAME_POKEMON
                + " WHERE "+ COLUMN_NAME_DEX_NUMBER
                + " = "+ dexNumber + ";";
        Cursor init = db.rawQuery(query, null);
        init.moveToFirst();

        weight = Integer.parseInt(init.getString(init.getColumnIndex(COLUMN_NAME_WEIGHT)));

        init.close();
        return weight;

    }

    public Boolean checkPokemonForm(int dexNumber){
        Boolean form;
        int formCheck;

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_NAME_FORMS + " FROM " + TABLE_NAME_POKEMON
                + " WHERE "+ COLUMN_NAME_DEX_NUMBER
                + " = "+ dexNumber + ";";
        Cursor init = db.rawQuery(query, null);
        init.moveToFirst();

        formCheck = init.getInt(init.getColumnIndex(COLUMN_NAME_FORMS));

        init.close();

        if(formCheck >= 1){
            form = true;
        } else {
            form = false;
        }

        return form;

    }

    public ArrayList<String> getFormsList(int dexNumber){
        ArrayList<String> formsList = new ArrayList<>();
        String form;

        String[] dex = new String[]{Integer.toString(dexNumber)};

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_NAME_RETURN_STRING + " FROM " + TABLE_NAME_FORMS
                + " WHERE "+ COLUMN_NAME_DEX_NUMBER
                + " = "+ dexNumber + ";";
        Cursor init = db.rawQuery(query, null);


        try {
            init.moveToFirst();
            int check = init.getCount();
            while(!init.isAfterLast()) {

                form = init.getString(init.getColumnIndex(COLUMN_NAME_RETURN_STRING));
                formsList.add(form);
                init.moveToNext();


            }

        }finally{

                init.close();
            }
        return formsList;

    }

}
