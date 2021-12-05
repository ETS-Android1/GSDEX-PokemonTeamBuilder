package com.example.gsdex;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import android.widget.ImageButton;

import android.widget.ListView;

import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

//TODO: Fix Type Filter Toggels so they change darken on selection and lighen off selection. +x value on selection, -x value on deselect from HexColor?
//TODO: FIX Layouts so that they work on different sized devices
//TODO: Add Check for internet before trying to download - also only download if yes is clicked, and have wait menu thing


public class MainActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    private TeamDBHandler tdbHandler;
    private Deque<Pokemon> team;
    private int[] teamButtonResources;
    private int numPokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.team = new LinkedList<>();
        dbHandler = new DBHandler(MainActivity.this);
        tdbHandler = new TeamDBHandler(MainActivity.this);

        this.teamButtonResources = new int[]{R.id.member1,R.id.member2, R.id.member3, R.id.member4, R.id.member5, R.id.member6};

        numPokemon = 898;

        // For Comeback from SortFilter
        Intent myIntent = getIntent();
        String query = getIntent().getStringExtra("query");
        System.out.println(query);


        // Reinitialize Pokedex Database if there is not the proper number of elements
        if(dbHandler.isEmpty(numPokemon) == true) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("This App Requires Downloaded Data to Run. Do you Accept?").setPositiveButton("Yes!", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
            // Exits if no is selected, inits dex if yes is selected
        }else if (query == "" || query == null) {
            System.out.println("Table Had Values");
            updateListView(dbHandler.filterDex("SELECT name, type FROM pokedex ORDER BY dex_num"));
        } else{
            System.out.println("QUERY!");
            updateListView(dbHandler.filterDex(query));
        }


        // Initializes Team
        if(tdbHandler.isEmpty()){
            System.out.println("Empty Team");
        } else{
            initTeamFromDB();
        }

        // Search Box Usage
        SearchView searchBox = (SearchView) findViewById(R.id.searchbox);
        searchBox.setQueryHint("Search for a Specific Pokemon");


        searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateListView(dbHandler.searchDex(query));
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {

                updateListView(dbHandler.searchDex(newText));
                return false;
            }
        });

    }

    @Override
    protected void onDestroy() {
        dbHandler.close();
        super.onDestroy();
    }

    private void initApp(int numPokemon){
        // tests to see if connection
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Download Data
            new Thread(new DownloadThread(dbHandler, numPokemon)).start();

        } else {
            Toast.makeText(getApplicationContext(), "No network connection available", Toast.LENGTH_SHORT);
        }
    }

    private void initTeamFromDB(){
        ArrayList<Pair<String, String>> al = tdbHandler.retrieveTeam();
        int[] teamRes = new int[al.size()];

        for(int i = 0; i < al.size(); i++) {
            String name = al.get(i).first; // gets name
            System.out.println(name.toLowerCase(Locale.ROOT));
            Pokemon pokemon = dbHandler.getPokemon(name);
            team.addLast(pokemon);
            teamRes[i] = getPokemonImageID(pokemon.getName().toLowerCase(Locale.ROOT));
        }

        updateTeamUI(teamRes);
    }


    private class DownloadThread implements Runnable{

        private DBHandler dbHandler;
        private int num;
        DownloadThread(DBHandler db, int num){this.dbHandler = db; this.num = num;}

        @Override
        public void run() {

            try {
                initPokedexFromWeb(num);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private void initPokedexFromWeb(int num) throws IOException, JSONException {
            InputStream is = null;

            for(int i = 1 ; i <= num; i++) {
                String myUrl = "https://pokeapi.co/api/v2/pokemon/" + i;
                System.out.println(myUrl);
                URL url = new URL(myUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                //begins query
                conn.connect();

                int response = conn.getResponseCode();
                if(response == 404) throw new FileNotFoundException("404 Error: File not Found");

                is =  new BufferedInputStream(conn.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder result = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) {
                    result.append(line);
                }
                String received = result.toString();

                conn.disconnect();

                Pokemon pokemon = parseJSON(received);
                //Adds pokemon to pokedex SQLIteTable
                dbHandler.addNewPokemon(pokemon);

            }

            updateListView(dbHandler.retrieveDex());
        }

        private Pokemon parseJSON(String jsonStr) throws JSONException {

            JSONObject jsonObject = new JSONObject(jsonStr);

            String name = jsonObject.getString("name");
            //System.out.println(name);

            //** Gets Name and ID
            int dexNum = jsonObject.getInt("id");
            int id = jsonObject.getInt("order");

            //** Get Types
            JSONArray typesJ = jsonObject.getJSONArray("types");
            String []types = new String[2];
            Arrays.fill(types, " ");
            for(int i = 0; i < typesJ.length(); i++)
            {
                types[i] = typesJ.getJSONObject(i).getJSONObject("type").getString("name");
                //System.out.println(types[i]);
            }


            //** Get Abilities
            JSONArray abilityJ = jsonObject.getJSONArray("abilities");
            String []abilities = new String[3];
            Arrays.fill(abilities, " ");
            for(int i = 0; i < abilityJ.length(); i++)
            {
                String temp = abilityJ.getJSONObject(i).getJSONObject("ability").getString("name");

                if(abilityJ.getJSONObject(i).getBoolean("is_hidden")) {
                    abilities[2] = temp;
                }
                else {
                    abilities[i] = temp;
                }
            }

            //** Stats
            JSONArray statsJ = jsonObject.getJSONArray("stats");
            int []stats = new int[6];
            for(int i = 0; i < statsJ.length(); i++)
            {
                stats[i] =  Integer.parseInt(statsJ.getJSONObject(i).getString("base_stat"));
                //System.out.println(stats[i]);
            }

            // Gets image ID for the pokemon
            int resID = getResources().getIdentifier(name, "drawable", getPackageName());


            //** Creates pokemon from that data
            Pokemon pokemon = new Pokemon(name, dexNum,types[0], types[1],
                    abilities[0], abilities[1], abilities[2],
                    stats[0], stats[1], stats[2], stats[3], stats[4], stats[5], id);

            System.out.println(pokemon.toString());
            return pokemon;

        }
    }


    private void updateListView(ArrayList<Pair<String, String>> set){
        // Set Listview - adds all pokemon to the list

        // Gets names and types from database

        String[] names = new String[set.size()];
        String[] types = new String[set.size()];
        int[] imageIDs = new int[set.size()]; // Holds Drawable ids

        // Separates Data for ListViewAdapter
        int i = 0;
        for(Pair p : set){
            names[i] = (String)p.first;
            types[i] = (String)p.second;
            imageIDs[i] = getPokemonImageID(((String)p.first).toLowerCase(Locale.ROOT));
            i++;
        }

        ListViewAdapter adapter =
                new ListViewAdapter(MainActivity.this, names, types, imageIDs);

        ListView lv = (ListView) findViewById(R.id.dexView);

        lv.post(new Runnable() {
            @Override
            public void run() {
                lv.setAdapter(adapter);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("click! Fragment! With option to Add to Team! Also Pull more info from DB using query!");

                System.out.println(names[position]);

                Pokemon pkmn = dbHandler.getPokemon(names[position].toLowerCase(Locale.ROOT));

                viewPokemonDetails(pkmn);
            }
        });
    }


    public int getPokemonImageID(String name){
        name = name.replaceAll("-", "_");
        return getResources().getIdentifier(name, "drawable", getPackageName());
    }


    public void addToTeam(View v){
        Pokemon pkmn = dbHandler.getPokemon(v.getTag().toString());
        ImageButton imgB;
        if(team.size() < 6) {
            switch (team.size()){
                case 0:
                    imgB = findViewById(R.id.member1);
                    imgB.setImageResource(getPokemonImageID(pkmn.getName().toLowerCase(Locale.ROOT)));
                    break;
                case 1:
                    imgB = findViewById(R.id.member2);
                    imgB.setImageResource(getPokemonImageID(pkmn.getName().toLowerCase(Locale.ROOT)));
                    break;
                case 2:
                    imgB = findViewById(R.id.member3);
                    imgB.setImageResource(getPokemonImageID(pkmn.getName().toLowerCase(Locale.ROOT)));
                    break;
                case 3:
                    imgB = findViewById(R.id.member4);
                    imgB.setImageResource(getPokemonImageID(pkmn.getName().toLowerCase(Locale.ROOT)));
                    break;
                case 4:
                    imgB = findViewById(R.id.member5);
                    imgB.setImageResource(getPokemonImageID(pkmn.getName().toLowerCase(Locale.ROOT)));
                    break;
                case 5:
                    imgB = findViewById(R.id.member6);
                    imgB.setImageResource(getPokemonImageID(pkmn.getName().toLowerCase(Locale.ROOT)));
                    break;
                default:
            }

            team.addLast(pkmn);
            tdbHandler.addPokemonToTeam(pkmn);
        }
        else{
            Toast.makeText(getApplicationContext(), "Team already has 6 members!", Toast.LENGTH_LONG);
        }
    }



    public void removeMember(View v){
        if(team.size() == 0) return;
        int index = Integer.parseInt(v.getTag().toString());

        if(index > team.size()) return;

        int[] teamRes = new int[team.size() - 1];

        List<Pokemon> myList = new CopyOnWriteArrayList<Pokemon>();

        for(Pokemon p : team){
            myList.add(p);
        }

        Pokemon removeMe = myList.get(index - 1);

        myList.remove(index - 1);

        team = new LinkedList<>();

        for (int i = 0; i < myList.size(); i++) {
            Pokemon tempP = myList.get(i);
            team.addLast(tempP);
            teamRes[i] = getPokemonImageID(tempP.getName().toLowerCase(Locale.ROOT));
        }

        // update view
        tdbHandler.delete(removeMe.getName());
        updateTeamUI(teamRes);

    }


    public void removeMember(int index){
        if(index > team.size()) return;

        int[] teamRes = new int[team.size() - 1];

        List<Pokemon> myList = new CopyOnWriteArrayList<Pokemon>();

        for(Pokemon p : team){
            myList.add(p);
        }

        Pokemon removeMe = myList.get(index);

        myList.remove(index);

        team = new LinkedList<>();

        for (int i = 0; i < myList.size(); i++) {
            Pokemon tempP = myList.get(i);
            team.addLast(tempP);
            teamRes[i] = getPokemonImageID(tempP.getName().toLowerCase(Locale.ROOT));
        }

        // update view
        tdbHandler.delete(removeMe.getName());
        updateTeamUI(teamRes);;
    }


    private void updateTeamUI(int[] teamRes){
        ImageButton imgB;
        for(int i = 0; i < 6; i++) {
            imgB = findViewById(teamButtonResources[i]);
            if(i >= team.size()){
                // make Button hold pokeball
                imgB.setImageResource(R.drawable.pokeball);
            } else{
                imgB.setImageResource(teamRes[i]);
            }
        }
    }

    public void resetTeam(View v){
        int size = team.size();
        for(int i = 0; i < size; i++) {
            removeMember(0);
        }

        updateListView(dbHandler.filterDex("SELECT name, type FROM pokedex ORDER BY dex_num"));

    }


    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked

                    // Check for internet connection
                    boolean connected = false;
                    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        connected = true;
                    }
                    else
                        connected = false;

                    if(connected) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Download will take a moment, please wait").setPositiveButton("OK", null).show();

                        dbHandler.resetTabel();
                        initApp(numPokemon);

                    } else{
                        System.out.println("No connection");
                    }
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    System.exit(0);
                    break;
            }
        }
    };




    public void changeFilter(View v){
        Intent intent = new Intent(this, Sort_Filter_Activity.class);
        startActivity(intent);
        // call method in way that returns values back here - startActivityForResult but not depreciated
    }


    public void viewPokemonDetails(Pokemon pokemon){
        Intent intent = new Intent(this, PokemonActivity.class);
        intent.putExtra("pokemon", pokemon);
        startActivity(intent);
    }


}