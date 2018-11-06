package xyz.apricorn.sora.dialga;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class backgroundTimeKeeping {

    private static int mHour;
    public static String timeOfDay;

    private static GregorianCalendar now = new GregorianCalendar();

    public static void updateTimeOfDay() {
        mHour = now.get(Calendar.HOUR);

        if (now.get(Calendar.AM) == 0) {
            if (mHour >= 5 & mHour < 9) {
                timeOfDay = "dawn";
            } else if ( mHour >= 9 ){
                timeOfDay = "day";
            }
        } else if ( now.get(Calendar.AM) != 0 ){
            if ( mHour == 12){
                timeOfDay = "day";
            } else if ( mHour >= 1 & mHour < 6 ){
                timeOfDay = "day";
            } else if ( mHour >= 6 & mHour < 8 ) {
                timeOfDay = "twilight";
            }
        } else {
            timeOfDay = "evening";
        }
    }

    public static String getTimeOfDay(){

        String returner;

        returner = timeOfDay;

        return returner;
    }



}
