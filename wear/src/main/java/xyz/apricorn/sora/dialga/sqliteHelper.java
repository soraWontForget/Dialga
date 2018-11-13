package xyz.apricorn.sora.dialga;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class sqliteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_PATH = "/data/data/xyz.apricorn.sora.dialga/databases/";
    public static final String DATABASE_NAME = "dialga-pokemon.db";
    public static final String POKEMON_NAME_COLUMN = "pokemon_name";
    public static final String GENERATION_COLUMN = "generation";
    public static final String NATIONAL_DEX_NUMBER_COLUMN = "dex_number";
    public static final String POKEMON_TYPE_COLOUMN = "type";
    public static final String RESOURCE_COLUMN = "resource";
    static String data;
    public final Context myContext;
    public SQLiteDatabase myDataBase;

    public sqliteHelper(Context context){
        super(context,DATABASE_NAME,null,1);
        this.myContext = context;
    }

    public void createDataBase() throws IOException{

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
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

        return checkDB != null ? true : false;
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
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    public void onCreate(SQLiteDatabase db) {}
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {}

    public String getIcon(int dexNumber){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + POKEMON_NAME_COLUMN + " FROM pokemon WHERE "+ NATIONAL_DEX_NUMBER_COLUMN +
                "='"+ dexNumber + "'";
        Cursor init = db.rawQuery(query, null);
        init.moveToFirst();

        data = init.getString(0);
        /*if (init.moveToNext()) {
            int x=0;
            data[x] = init.getString(0);
            ++x;
        }*/
        init.close();
        return data;

    }


}
