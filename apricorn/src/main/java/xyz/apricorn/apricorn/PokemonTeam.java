package xyz.apricorn.apricorn;

import android.content.Context;
import android.graphics.Bitmap;


public class PokemonTeam {
    Pokemon teamLead;
    Pokemon slot2;
    Pokemon slot3;
    Pokemon slot4;
    Pokemon slot5;
    Pokemon slot6;

    PokemonTeam(Context context){
        teamLead = new Pokemon(context);
        slot2 = new Pokemon(context);
        slot3 = new Pokemon(context);
        slot4 = new Pokemon(context);
        slot5 = new Pokemon(context);
        slot6 = new Pokemon(context);

    }

    PokemonTeam(Context context, int dexNumber){
        teamLead = new Pokemon(context, dexNumber);
        slot2 = new Pokemon(context);
        slot3 = new Pokemon(context);
        slot4 = new Pokemon(context);
        slot5 = new Pokemon(context);
        slot6 = new Pokemon(context);

    }

    public Bitmap getPokemonBitmap(int slot)
    {
        Bitmap bitties;
        switch (slot) {
            case 0:
                bitties = teamLead.getBitmap();
                break;

            case 1:
                bitties = slot2.getBitmap();
                break;

            case 2:
                bitties = slot3.getBitmap();
                break;

            case 3:
                bitties = slot4.getBitmap();
                break;

            case 4:
                bitties = slot5.getBitmap();
                break;

            case 5:
                bitties = slot6.getBitmap();
                break;

            default:
                bitties = teamLead.getBitmap();
                break;
        }

        return bitties;

    }

    public int getResourceId(Context context, int slot)
    {
        int rezzie;
        switch (slot) {
            case 0:
                rezzie = teamLead.queryResourceId(context);
                break;

            case 1:
                rezzie = slot2.queryResourceId(context);
                break;

            case 2:
                rezzie = slot3.queryResourceId(context);
                break;

            case 3:
                rezzie = slot4.queryResourceId(context);
                break;

            case 4:
                rezzie = slot5.queryResourceId(context);
                break;

            case 5:
                rezzie = slot6.queryResourceId(context);
                break;

            default:
                rezzie = teamLead.queryResourceId(context);
                break;
        }

        return rezzie;

    }

    public void rerollTeam(Context context)
    {
        teamLead.rerollPokemon(context);
        slot2.rerollPokemon(context);
        slot3.rerollPokemon(context);
        slot4.rerollPokemon(context);
        slot5.rerollPokemon(context);
        slot6.rerollPokemon(context);
    }

    public void rerollTeamInc(Context context, int dexNum)
    {
        teamLead.rerollPokemonInc(context, dexNum);
        slot2.rerollPokemonInc(context, dexNum + 1);
        slot3.rerollPokemonInc(context, dexNum + 2);
        slot4.rerollPokemonInc(context, dexNum + 3);
        slot5.rerollPokemonInc(context, dexNum + 4);
        slot6.rerollPokemonInc(context, dexNum + 5);
    }

}
