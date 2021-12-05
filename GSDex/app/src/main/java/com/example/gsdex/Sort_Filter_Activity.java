package com.example.gsdex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.ToggleButton;

public class Sort_Filter_Activity extends AppCompatActivity {

    private String query;
    private String ORDERBY;
    private boolean descending;
    private String WHERE;
    private String SORTBy;
    private int filterGen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_filter);
        String queryBase = "SELECT name, type FROM pokedex";
        // Use different options to generate a SQL query string and sends it back and updates the listview

        SORTBy = "dex_num";
        descending = false;


        WHERE = "WHERE";

        query = "SELECT name, type FROM pokedex ";

        Spinner sortbySpin = findViewById(R.id.sortbySpinner);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.sortoptions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        sortbySpin.setAdapter(adapter);

        Spinner filterByGen = findViewById(R.id.genSpinner);
        adapter=ArrayAdapter.createFromResource(this, R.array.Generation, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        filterByGen.setAdapter(adapter);


    }


    public void descending(View v){
        descending = !descending;
    }

    public void typeToggle(){

        String append = " ";
        ToggleButton normal = (ToggleButton) findViewById(R.id.NORMAL);
        ToggleButton fighting = (ToggleButton) findViewById(R.id.FIGHTING);
        ToggleButton flying = (ToggleButton) findViewById(R.id.FLYING);
        ToggleButton poison = (ToggleButton) findViewById(R.id.POISON);
        ToggleButton ground = (ToggleButton) findViewById(R.id.GROUND);
        ToggleButton rock = (ToggleButton) findViewById(R.id.ROCK);
        ToggleButton bug = (ToggleButton) findViewById(R.id.BUG);
        ToggleButton ghost = (ToggleButton) findViewById(R.id.GHOST);
        ToggleButton steel = (ToggleButton) findViewById(R.id.STEEL);
        ToggleButton fire = (ToggleButton) findViewById(R.id.FIRE);
        ToggleButton water = (ToggleButton) findViewById(R.id.WATER);
        ToggleButton grass = (ToggleButton) findViewById(R.id.GRASS);
        ToggleButton electric = (ToggleButton) findViewById(R.id.ELECTRIC);
        ToggleButton psychic = (ToggleButton) findViewById(R.id.PSYCHIC);
        ToggleButton ice = (ToggleButton) findViewById(R.id.ICE);
        ToggleButton dragon = (ToggleButton) findViewById(R.id.DRAGON);
        ToggleButton dark = (ToggleButton) findViewById(R.id.DARK);
        ToggleButton fairy = (ToggleButton) findViewById(R.id.FAIRY);

        String and = " AND ";

        if(normal.isChecked()) {
            if(WHERE.contains("LIKE")) append = append + and;
            append = append + "type LIKE '%normal%'";
        }
        if(fighting.isChecked()) {
            if(WHERE.contains("LIKE")) append = append + and;
            append = append + "type LIKE '%fighting%'";
        }
        if(flying.isChecked()) {
            if(WHERE.contains("LIKE")) append = append + and;
            append = append + "type LIKE '%flying%'";
        }
        if(poison.isChecked()) {
            if(WHERE.contains("LIKE")) append = append + and;
            append = append + "type LIKE '%poison%'";
        }
        if(ground.isChecked()) {
            if(WHERE.contains("LIKE")) append = append + and;
            append = append + "type LIKE '%ground%'";
        }
        if(rock.isChecked()) {
            if(WHERE.contains("LIKE")) append = append + and;
            append = append + "type LIKE '%rock%'";
        }
        if(bug.isChecked()) {
            if(WHERE.contains("LIKE")) append = append + and;
            append = append + "type LIKE '%bug%'";
        }
        if(ghost.isChecked()) {
            if(WHERE.contains("LIKE")) append = append + and;
            append = append + "type LIKE '%ghost%'";
        }
        if(steel.isChecked()) {
            if(WHERE.contains("LIKE")) append = append + and;
            append = append + "type LIKE '%steel%'";
        }
        if(fire.isChecked()) {
            if(WHERE.contains("LIKE")) append = append + and;
            append = append + "type LIKE '%fire%'";
        }
        if(water.isChecked()) {
            if(WHERE.contains("LIKE")) append = append + and;
            append = append + "type LIKE '%water%'";
        }
        if(grass.isChecked()) {
            if(WHERE.contains("LIKE")) append = append + and;
            append = append + "type LIKE '%grass%'";
        }
        if(electric.isChecked()) {
            if(WHERE.contains("LIKE")) append = append + and;
            append = append + "type LIKE '%electric%'";
        }
        if(psychic.isChecked()) {
            if(WHERE.contains("LIKE")) append = append + and;
            append = append + "type LIKE '%psychic%'";
        }
        if(ice.isChecked()) {
            if(WHERE.contains("LIKE")) append = append + and;
            append = append + "type LIKE '%ice%'";
        }
        if(dragon.isChecked()) {
            if(WHERE.contains("LIKE")) append = append + and;
            append = append + "type LIKE '%dragon%'";
        }
        if(dark.isChecked()) {
            if(WHERE.contains("LIKE")) append = append + and;
            append = append + "type LIKE '%dark%'";
        }
        if(fairy.isChecked()) {
            if(WHERE.contains("LIKE")) append = append + and;
            append = append + "type LIKE '%fairy%'";
        }


        WHERE = WHERE + append;
    }

    public void toggleClick(View v) {

        ToggleButton toggleButton = findViewById(v.getId());
        int current = toggleButton.getDrawingCacheBackgroundColor();

        if(!toggleButton.isChecked()){

            toggleButton.setBackgroundColor(current - 0x10);
        }
        else{
            toggleButton.setBackgroundColor(current + 0x10);
        }

    }


    public void back(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        System.out.println("TEST: " + query + WHERE);

        if(descending){ ORDERBY = " DESC";} else ORDERBY = " ASC";

        getGeneration();

        typeToggle();

        getSortBy();


        ORDERBY = " ORDER BY " + SORTBy + ORDERBY;



        String finalQuery = query;
        if(!WHERE.equals("WHERE ")) finalQuery = query + WHERE + ORDERBY;
        else finalQuery = query + ORDERBY;

        System.out.println("Q! " + finalQuery);
        intent.putExtra("query", finalQuery);
        startActivity(intent);
    }

    private void getGeneration(){
        String output;
        Spinner genSpin = findViewById(R.id.genSpinner);
        String str = genSpin.getSelectedItem().toString();
        if(str.contains("Gen 1")) str = "'1'";
        else if(str.contains("Gen 2")) str = "'2'";
        else if(str.contains("Gen 3")) str = "'3'";
        else if(str.contains("Gen 4")) str = "'4'";
        else if(str.contains("Gen 5")) str = "'5'";
        else if(str.contains("Gen 6")) str = "'6'";
        else if(str.contains("Gen 7")) str = "'7'";
        else if (str.contains("Gen 8")) str = "'8'";
        else str = "'%%'";

        WHERE = WHERE + " generation LIKE " + str + " ";
    }

    private void getSortBy(){
        Spinner spin = findViewById(R.id.sortbySpinner);
        SORTBy = spin.getSelectedItem().toString();
        System.out.println(SORTBy);
        if(SORTBy.contains("Pokedex") || SORTBy == null) SORTBy = "dex_num";
        else if(SORTBy.equals("Name")) SORTBy = "name";
        else if(SORTBy.equals("HP")) SORTBy = "hp";
        else if(SORTBy.equals("Attack")) SORTBy = "atk";
        else if(SORTBy.equals("Defense")) SORTBy = "def";
        else if(SORTBy.equals("Special Attack")) SORTBy = "spatk";
        else if(SORTBy.equals("Special Defense")) SORTBy = "spdef";
        else if(SORTBy.equals("Speed")) SORTBy = "speed";
        else if(SORTBy.equals("Base Stat Total")) SORTBy = "bst";
        else SORTBy = "dex_num";
    }

}