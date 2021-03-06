package xyz.apricorn.sora.dialga;

import android.content.Context;
import android.graphics.Bitmap;
import xyz.apricorn.sora.dialga.Pokemon;


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

    public void rerollTeam(Context context)
    {
        teamLead.rerollPokemon(context);
        slot2.rerollPokemon(context);
        slot3.rerollPokemon(context);
        slot4.rerollPokemon(context);
        slot5.rerollPokemon(context);
        slot6.rerollPokemon(context);
    }

}
