package com.example.gsdex;

import android.os.Parcel;
import android.os.Parcelable;

public class Pokemon implements Parcelable {

    private String name;
    private int imageID;
    private  int dexNumber;
    private String type1; private String type2;
    private String ability1; private String ability2; private String hiddenAbility;
    // Stats
    private int hp; private int def; private int attack;
    private int sp_def; private int sp_atk; private int speed;
    private int bst;

    private int generation;
    private boolean legendary;
    private boolean mythical;
    private boolean mega;
    private boolean dynamax;
    private int id_from_API;

    // Keeps track of ID of alternate forms
    //private List forms;


    public Pokemon(String name, int dexNumber,String type1, String type2,
                   String ability1, String ability2, String hiddenAbility,
                   int hp, int attack, int def, int sp_atk, int sp_def, int speed, int id_from_API) {

        this.name = name; this.dexNumber = dexNumber;
        this.type1 = type1; this.type2 = type2;
        this.ability1 = ability1; this.ability2 = ability2; this.hiddenAbility = hiddenAbility;
        this.hp = hp; this.def = def; this.attack = attack;
        this.sp_def = sp_def; this.sp_atk = sp_atk; this.speed = speed;
        this.bst = hp + def + attack + sp_def + sp_atk + speed;


        this.id_from_API = id_from_API;


        if(dexNumber < 152) this.generation = 1;
        else if(dexNumber > 151 && dexNumber < 252) this.generation = 2;
        else if(dexNumber > 251 && dexNumber < 387) this.generation = 3;
        else if(dexNumber > 386 && dexNumber < 494) this.generation = 4;
        else if(dexNumber > 493 && dexNumber < 650) this.generation = 5;
        else if(dexNumber > 649 && dexNumber < 722) this.generation = 6;
        else if(dexNumber > 721 && dexNumber < 810) this.generation = 7;
        else if(dexNumber > 809 && dexNumber < 899) this.generation = 8;
        else this.generation = 0;



        this.dynamax = false;
        this.legendary = false;
        this.mythical = false;

    }

    public String getName() {
        return name;
    }


    public int getDexNumber() {
        return dexNumber;
    }

    public String getType1() {
        return type1;
    }

    public String getType2() {
        return type2;
    }

    public String getAbility1() {
        return ability1;
    }

    public String getAbility2() {
        return ability2;
    }

    public String getHiddenAbility() {
        return hiddenAbility;
    }

    public int getHp() {
        return hp;
    }

    public int getDef() {
        return def;
    }

    public int getAttack() {
        return attack;
    }

    public int getSp_def() {
        return sp_def;
    }

    public int getSp_atk() {
        return sp_atk;
    }

    public int getSpeed() {
        return speed;
    }

    public int getBst() {
        return bst;
    }

    public int getGeneration() {
        return generation;
    }

    public int getId_from_API() {
        return id_from_API;
    }

    @Override // Change this later to Generate Showdown Copy/Paste String
    public String toString() {
        return "Pokemon{" +
                "name='" + name + '\'' +
                ", dexNumber=" + dexNumber +
                ", type1='" + type1 + '\'' +
                ", type2='" + type2 + '\'' +
                ", ability1='" + ability1 + '\'' +
                ", ability2='" + ability2 + '\'' +
                ", hiddenAbility='" + hiddenAbility + '\'' +
                ", hp=" + hp +
                ", def=" + def +
                ", attack=" + attack +
                ", sp_def=" + sp_def +
                ", sp_atk=" + sp_atk +
                ", speed=" + speed +
                ", bst=" + bst +
                ", generation=" + generation +
                '}';
    }


    public Pokemon(Parcel in){
        String[] data = new String[15];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.name = data[0];
        this.dexNumber = Integer.parseInt(data[1]);
        this.type1 = data[2];
        this.type2 = data[3];
        this.ability1 = data[4];
        this.ability2 = data[5];
        this.hiddenAbility = data[6];
        this.hp = Integer.parseInt(data[7]);
        this.def = Integer.parseInt(data[8]);
        this.attack = Integer.parseInt(data[9]);
        this.sp_def = Integer.parseInt(data[10]);
        this.sp_atk = Integer.parseInt(data[11]);
        this.speed = Integer.parseInt(data[12]);
        this.bst = Integer.parseInt(data[13]);
        this.generation = Integer.parseInt(data[14]);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.name,
                String.valueOf(this.dexNumber), this.type1, this.type2,
                this.ability1, this.ability2, this.hiddenAbility,
                String.valueOf(this.hp), String.valueOf(this.def), String.valueOf(this.attack),
                String.valueOf(this.sp_def), String.valueOf(this.sp_atk),
                String.valueOf(this.speed), String.valueOf(this.bst),
                String.valueOf(this.generation)
        });

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Pokemon createFromParcel(Parcel in) {
            return new Pokemon(in);
        }

        public Pokemon[] newArray(int size) {
            return new Pokemon[size];
        }
    };

}
