package com.example.gsdex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;

public class PokemonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);
        Intent myIntent = getIntent();
        Bundle data = getIntent().getExtras();
        Pokemon pokemon = (Pokemon) data.getParcelable("pokemon");

        ImageView image = findViewById(R.id.imageV);
        TextView name = findViewById(R.id.nameV);
        TextView dexNum = findViewById(R.id.dexNum);
        TextView types = findViewById(R.id.typeV);
        TextView abilities = findViewById(R.id.abilities);

        image.setImageResource(getPokemonImageID(pokemon.getName().toLowerCase(Locale.ROOT)));
        name.setText(pokemon.getName());
        String temp = "POKEDEX NUMBER: " + pokemon.getDexNumber();
        dexNum.setText(temp);
        temp = "TYPES\n" + pokemon.getType1() + "\n" + pokemon.getType2();
        types.setText(temp);
        temp = "ABILITIES: " + pokemon.getAbility1() + ", " + pokemon.getAbility2() +
                "\nHIDDEN ABILITY: " + pokemon.getHiddenAbility();
        abilities.setText(temp);

        ProgressBar hp = findViewById(R.id.HP);
        hp.setMax(255);
        hp.setProgress(pokemon.getHp());
        TextView hpText = findViewById(R.id.hpStat);
        hpText.setText("HP: " + Integer.toString(pokemon.getHp()));


        ProgressBar atk = findViewById(R.id.ATTACK);
        atk.setMax(181);
        atk.setProgress(pokemon.getAttack());
        TextView atkText = findViewById(R.id.atkStat);
        atkText.setText("Attack: " + Integer.toString(pokemon.getAttack()));


        ProgressBar def = findViewById(R.id.DEF);
        def.setMax(230);
        def.setProgress(pokemon.getDef());
        TextView defText = findViewById(R.id.defStat);
        defText.setText("Defense: " + Integer.toString(pokemon.getDef()));


        TextView spaText = findViewById(R.id.spaStat);
        TextView spdText = findViewById(R.id.spdStat);

        ProgressBar spa = findViewById(R.id.SPATK);
        spa.setMax(173);
        spa.setProgress(pokemon.getSp_atk());
        spaText.setText("Special Attack: " + Integer.toString(pokemon.getSp_atk()));


        ProgressBar spd = findViewById(R.id.SPDEF);
        spd.setMax(230);
        spd.setProgress(pokemon.getSp_def());
        spdText.setText("Special Defense: " + Integer.toString(pokemon.getSp_def()));

        ProgressBar spe = findViewById(R.id.SPEED);
        spe.setMax(200);
        spe.setProgress(pokemon.getSpeed());
        TextView speedText = findViewById(R.id.speedStat);
        speedText.setText("Speed: " + Integer.toString(pokemon.getSpeed()));


        TextView bst = findViewById(R.id.bstNum);
        bst.setText(Integer.toString(pokemon.getBst()));


    }

    public void back(View v){
        finish();
    }

    public int getPokemonImageID(String name){
        name = name.replaceAll("-", "_");
        return getResources().getIdentifier(name, "drawable", getPackageName());
    }
}