package xyz.apricorn.apricorn;

import java.util.Random;

public class RNGRolls {

    Random rand = new Random();

    public int rollDexNumber()
    {
        int dexNum;

        dexNum = rand.nextInt(807) + 1;

        return dexNum;

    }

    public Boolean shinyRoll(Boolean foreignParent, Boolean shinyCharm)
    {
        int roll;
        int randIndex = 8192;
        int personalityValue = 1;
        int counter = 0;
        boolean end = false;
        boolean bShiny = false;

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

    public int formRoll(int boundary) {

        int roll;


        roll = rand.nextInt(boundary);

        return roll;

    }

}
