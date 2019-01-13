package xyz.apricorn.sora.dialga;

import java.util.Random;

public class RNGRolls {

    public int rollDexNumber()
    {
        int dexNum;

        Random rand = new Random();

        dexNum = rand.nextInt(807) + 1;

        return dexNum;

    }

    public Boolean shinyRoll(Boolean foreignParent, Boolean shinyCharm)
    {
        Random rand = new Random();
        int roll;
        int randIndex = 8192;
        int personalityValue = 1;
        int counter = 1;
        boolean end = false;
        boolean bShiny = false;
        String rShiny;

        if(shinyCharm){
            randIndex = 4096;

        }

        if(foreignParent){
            personalityValue = 6;

        }

        do
        {

            roll = rand.nextInt(randIndex) + 1;
            counter++;

            if (roll < personalityValue) {
                bShiny = true;
                end = true;

            }

            if (counter == 6) {
                end = true;

            }

        } while(!end);

        return bShiny;

    }

    public String megaRoll() {
        Random rand = new Random();

        int roll;
        String mega;

        roll = rand.nextInt(3);

        mega = roll < 2 ? "_mega" : "";

        return mega;

    }

}
