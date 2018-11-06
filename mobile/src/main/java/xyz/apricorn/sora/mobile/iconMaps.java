package xyz.apricorn.sora.mobile;

import java.util.HashMap;


public class iconMaps extends Main2Activity {

    protected static HashMap<String, Integer> genI = new HashMap<>();
    protected static HashMap<String, Integer> genII = new HashMap<>();
    protected static HashMap<String, Integer> genIII = new HashMap<>();
    protected static HashMap<String, Integer> genIV = new HashMap<>();
    protected static HashMap<String, Integer> genV = new HashMap<>();
    protected static HashMap<String, Integer> genVI = new HashMap<>();
    protected static HashMap<String, Integer> genVII = new HashMap<>();

        public static void putGenI(){

            genI.put("bulbasaur", R.drawable.bulbasaur);
            genI.put("charmander", R.drawable.charmander);

        }

        public static int getGenI(String pokemon){

            int x;

            x = genI.get(pokemon);

            return x;

        }


}
