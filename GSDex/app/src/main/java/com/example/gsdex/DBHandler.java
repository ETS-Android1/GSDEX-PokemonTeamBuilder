package com.example.gsdex;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public class DBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "POKEDEX";

    // below int is our database version
    private static final int DB_VERSION = 8;

    private static final String TABLE_NAME = "pokedex";

    private static final String ID_COL = "id";

    private static final String DEX_NUM_COL = "dex_num";

    private static final String NAME_COL = "name";

    private static final String TYPE_COL = "type";

    private static final String ABILITY_COL = "ability";

    private static final String HA_COL = "hidden_ability";

    private static final String HP_COL = "hp";
    private static final String DEF_COL = "def";
    private static final String ATK_COL = "atk";
    private static final String SPDEF_COL = "spdef";
    private static final String SPATK_COL = "spatk";
    private static final String SPEED_COL = "speed";
    private static final String BST_COL = "bst";

    private static final String GEN_COL = "generation";

    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER, "
                + DEX_NUM_COL + " INTEGER,"
                + NAME_COL + " TEXT,"
                + TYPE_COL + " TEXT,"
                + ABILITY_COL + " TEXT,"
                + HA_COL + " TEXT,"
                + HP_COL + " INTEGER,"
                + ATK_COL + " INTEGER,"
                + DEF_COL + " INTEGER,"
                + SPATK_COL + " INTEGER,"
                + SPDEF_COL + " INTEGER,"
                + SPEED_COL + " INTEGER,"
                + BST_COL + " INTEGER,"
                + GEN_COL + " INTEGER)"
                ;

        db.execSQL(query);
    }

    // this method is use to add new course to our sqlite database.
    public void addNewPokemon(Pokemon pokemon) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ID_COL, pokemon.getId_from_API());

        values.put(DEX_NUM_COL, pokemon.getDexNumber());

        String name = pokemon.getName().substring(0, 1).toUpperCase() + pokemon.getName().substring(1);
        values.put(NAME_COL, name);

        String type1 = pokemon.getType1().substring(0, 1).toUpperCase() + pokemon.getType1().substring(1);
        String type2 = pokemon.getType2().substring(0, 1).toUpperCase() + pokemon.getType2().substring(1);
        values.put(TYPE_COL, type1 + "/" + type2);


        values.put(ABILITY_COL, pokemon.getAbility1() + "/" + pokemon.getAbility2());


        values.put(HA_COL, pokemon.getHiddenAbility());

        values.put(HP_COL, pokemon.getHp());
        values.put(ATK_COL, pokemon.getAttack());
        values.put(DEF_COL, pokemon.getDef());
        values.put(SPATK_COL, pokemon.getSp_atk());
        values.put(SPDEF_COL, pokemon.getSp_def());
        values.put(SPEED_COL, pokemon.getSpeed());
        values.put(BST_COL, pokemon.getBst());

        System.out.println("DB:" + pokemon.toString());

        values.put(GEN_COL, pokemon.getGeneration());

        db.insert(TABLE_NAME, null, values);

        db.close();
    }

    // Get User Details as a set of pairs
    public ArrayList<Pair<String, String>> retrieveDex(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Pair<String, String>> dexList = new ArrayList<>();

        String query = "SELECT " + NAME_COL +", "+ TYPE_COL +" FROM "+ TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        int i = 0;
        while (cursor.moveToNext()){
            int colIndex = cursor.getColumnIndex(NAME_COL);
            String name = cursor.getString(colIndex);

            colIndex = cursor.getColumnIndex(TYPE_COL);
            String types = cursor.getString(colIndex);

            dexList.add(i, new Pair<>(name, types));
            i++;
        }
        return  dexList;
    }


    // Get User Details as a set of pairs
    public ArrayList<Pair<String, String>> filterDex(String query){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Pair<String, String>> dexList = new ArrayList<>();

        Cursor cursor = db.rawQuery(query,null);
        int i = 0;
        while (cursor.moveToNext()){
            int colIndex = cursor.getColumnIndex(NAME_COL);
            String name = cursor.getString(colIndex);

            colIndex = cursor.getColumnIndex(TYPE_COL);
            String types = cursor.getString(colIndex);

            dexList.add(i, new Pair<>(name, types));
            i++;
        }
        return  dexList;
    }


    // Generates Query from SearchView - Only Takes Name
    public ArrayList<Pair<String, String>> searchDex(String search){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Pair<String, String>> dexList = new ArrayList<>();
        String key = "'%" + search + "%'";

        String query = "SELECT " + NAME_COL +", "+ TYPE_COL +" FROM "+ TABLE_NAME + " WHERE " +
                TABLE_NAME + ".name LIKE " + key + " OR ability LIKE " + key
                + " OR hidden_ability LIKE " + key ;
        Cursor cursor = db.rawQuery(query,null);
        int i = 0;
        while (cursor.moveToNext()){
            int colIndex = cursor.getColumnIndex(NAME_COL);
            String name = cursor.getString(colIndex);
            colIndex = cursor.getColumnIndex(TYPE_COL);
            String types = cursor.getString(colIndex);
            dexList.add(i, new Pair<>(name, types));
            i++;
        }
        return  dexList;
    }





    // Retrieve Pokemon object from db when given a name
    public Pokemon getPokemon(String givenName){
        SQLiteDatabase db = this.getWritableDatabase();
        String name = givenName.toLowerCase(Locale.ROOT);

        String query = "SELECT * FROM "+ TABLE_NAME + " WHERE " +TABLE_NAME + ".name LIKE " + "'" + name + "'";
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToNext();

        int colIndex = cursor.getColumnIndex(NAME_COL);
        name = cursor.getString(colIndex);

        colIndex = cursor.getColumnIndex(DEX_NUM_COL);
        int dex_num = cursor.getInt(colIndex);

        colIndex = cursor.getColumnIndex(TYPE_COL);
        String type = cursor.getString(colIndex);
        String[] types = type.split("/");

        colIndex = cursor.getColumnIndex(ABILITY_COL);
        String ability = cursor.getString(colIndex);
        String[] abilities = ability.split("/");

        colIndex = cursor.getColumnIndex(HA_COL);
        String h_ability = cursor.getString(colIndex);

        colIndex = cursor.getColumnIndex(HP_COL);
        int HP = cursor.getInt(colIndex);
        colIndex = cursor.getColumnIndex(ATK_COL);
        int ATK = cursor.getInt(colIndex);
        colIndex = cursor.getColumnIndex(DEF_COL);
        int DEF = cursor.getInt(colIndex);
        colIndex = cursor.getColumnIndex(SPATK_COL);
        int SPATK = cursor.getInt(colIndex);
        colIndex = cursor.getColumnIndex(SPDEF_COL);
        int SPDEF = cursor.getInt(colIndex);
        colIndex = cursor.getColumnIndex(SPEED_COL);
        int SPEED = cursor.getInt(colIndex);
        colIndex = cursor.getColumnIndex(BST_COL);
        int BST = cursor.getInt(colIndex);

        return new Pokemon(name,dex_num,types[0], types[1],abilities[0],abilities[1],h_ability,HP,ATK,DEF,SPATK,SPDEF,SPEED,BST);
    }


    public ArrayList<HashMap<String, String>> InitListView(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> dexList = new ArrayList<>();
        String query = "SELECT " + NAME_COL +", "+ TYPE_COL +" FROM "+ TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> pokemon = new HashMap<>();
            int colIndex = cursor.getColumnIndex(NAME_COL);
            pokemon.put("name", cursor.getString(colIndex));
            colIndex = cursor.getColumnIndex(TYPE_COL);
            pokemon.put("type", cursor.getString(colIndex));
            dexList.add(pokemon);
        }
        return  dexList;
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void resetTabel(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public boolean isEmpty(int numPokemon){
        SQLiteDatabase db = getReadableDatabase();
        String count = "SELECT count(*) FROM " + TABLE_NAME;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);

        if(icount < numPokemon) return true;

        return false;
    }
}