package xyz.apricorn.sora.dialga;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
/*import android.support.v7.graphics.Palette;*/
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class dialgaWatchFaceService extends CanvasWatchFaceService {

    /*
     * Updates rate in milliseconds for interactive mode. We update once a second to advance the
     * second hand.
     */

    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<dialgaWatchFaceService.Engine> mWeakReference;

        public EngineHandler(dialgaWatchFaceService.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            dialgaWatchFaceService.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        private static final float HOUR_STROKE_WIDTH = 5f;
        private static final float MINUTE_STROKE_WIDTH = 3f;
        private static final float SECOND_TICK_STROKE_WIDTH = 2f;
        private static final float CENTER_GAP_AND_CIRCLE_RADIUS = 4f;
        private static final int SHADOW_RADIUS = 6;


        /* Handler to update the time once a second in interactive mode. */
        private final Handler mUpdateTimeHandler = new EngineHandler(this);

        private Calendar mCalendar;

        private final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();


            }
        };

        GregorianCalendar now = new GregorianCalendar();
        private boolean mRegisteredTimeZoneReceiver = false;
        private boolean mMuteMode;
        private float mCenterX;
        private float mCenterY;
        private float mSecondHandLength;
        private float sMinuteHandLength;
        private float sHourHandLength;
        private float resizePercentageWidth;
        private float resizePercentageHeight;
        private float iconBitmapWitdhScaler;
        private float iconBitmapHeightScaler;
        /*private float ldrResizePercentageWidth;
        private float ldrResizePercentageHeight;
        private float ldrIconBitmapWitdhScaler;
        private float ldrIconBitmapHeightScaler;*/

        // SQLite
        private String name;

        // Seconds hand
        private Bitmap[] teamBitmap = new Bitmap[6];
        int oldIndex = 0;
        int newIndex = 0;

        // Time of day for background array.
        int timeOfDay;

        /* Colors for all hands (hour, minute, seconds, ticks) based on photo loaded. */
        /*private int mWatchHandColor;*/
        private int mWatchHandShadowColor;
        private Paint mHourPaint;
        private Paint mMinutePaint;
        private Paint mTickAndCirclePaint;
        private Paint mBackgroundPaint;
        private Paint secondsHandIconPaint;
        /*private Paint pkmnTeamLeadPaint;
        private Paint secondHandOutlinePaint;*/
        private Bitmap mBackgroundBitmap;
        private Bitmap secondsHandIconBitmap;
        /*private Bitmap pkmnTeamLeadBitmap;
        private Bitmap secondHandOutlineBitmap;*/
        public Bitmap mBackgroundArray[] = new Bitmap[4];
        private boolean mAmbient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            // SQLite database for retrieving pokemon data
            sqliteHelper databaseHelper = new sqliteHelper(dialgaWatchFaceService.this);
            databaseHelper = new sqliteHelper(dialgaWatchFaceService.this);

            // Create the database on the wearable from database in apk's assets.
            try{
                databaseHelper.createDataBase();
            } catch (IOException ioe){
                throw new Error("Unable to create database");
            }

            // Open the database.
            try {
                databaseHelper.openDataBase();
            }catch (SQLException sqle){
                throw sqle;
            }

            mCalendar = Calendar.getInstance();

            // Initialize watchface.
            initializeBackground();
            initializeWatchFace();


            setWatchFaceStyle(new WatchFaceStyle.Builder(dialgaWatchFaceService.this)
                    .build());
        }

        private void initializeBackground(){
            Context contx = dialgaWatchFaceService.this;

            // Get resource IDs for the backgrounds.
            int dawn = contx.getResources().getIdentifier("dawn", "drawable",contx.getPackageName());
            int day = contx.getResources().getIdentifier("day", "drawable",contx.getPackageName());
            int twilight = contx.getResources().getIdentifier("twilight", "drawable",contx.getPackageName());
            int night = contx.getResources().getIdentifier("night", "drawable",contx.getPackageName());

            // Store background bitmaps in array.
            mBackgroundArray[0] = BitmapFactory.decodeResource(getResources(), dawn);
            mBackgroundArray[1] = BitmapFactory.decodeResource(getResources(), day);
            mBackgroundArray[2] = BitmapFactory.decodeResource(getResources(), twilight);
            mBackgroundArray[3] = BitmapFactory.decodeResource(getResources(), night);

            // Initialize background paint object.
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(Color.BLACK);

            // Initialize the timeOfDay variable so that the background can be drawn.
            setTimeOfDay();

            /* Extracts colors from background image to improve watchface style. */
            /*Palette.from(mBackgroundBitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    if (palette != null) {
                        mWatchHandColor = palette.getLightVibrantColor(Color.WHITE);
                        mWatchHandShadowColor = palette.getDarkMutedColor(Color.BLACK);
                        updateWatchHandStyle();
                    }
                }
            });*/
        }

        // Sets the timeOfDay variable based on the current time of day. Return an integer that
        // corresponds to an element's index stored in the backgrounds array.
        public void setTimeOfDay() {
            GregorianCalendar now = new GregorianCalendar();
            int mHour = now.get(Calendar.HOUR_OF_DAY);

            // If hours are between midnight and 4 in the morning, the time is "night."
            if (mHour >= 0 && mHour < 5 ){
                timeOfDay = 3;

            // If hours are between 5 and 9 in the morning, the time is "dawn."
            } else if (mHour >= 5 && mHour < 10 ) {
                timeOfDay = 0;

            // If hours are between 10 in the morning and 4 in the afternoon, the time is "day."
            }else if (mHour >= 10 && mHour < 17) {
                timeOfDay = 1;

            // If the hour is 5 in the evening, the time is "twilight."
            } else if (mHour == 17) {
                timeOfDay = 2;

            // If the hours are 6 in the evening until 11 at night, the time is, "night."
            } else {
                timeOfDay = 3;
            }

        }

        private void initializeWatchFace() {
            Context contx = dialgaWatchFaceService.this;

            // Get pokemon bitmap for seconds hand icon.

            //single icon.
            /*int resID = contx.getResources().getIdentifier(randomPokemon(), "drawable", contx.getPackageName());
            secondsHandIconBitmap = BitmapFactory.decodeResource(contx.getResources(), resID);*/

            // Multiple icons that are scaled and stored in an array.
            for (int i = 0; i < 6; i++) {
                    int resID = contx.getResources().getIdentifier(randomPokemon(), "drawable", contx.getPackageName());
                    secondsHandIconBitmap = BitmapFactory.decodeResource(contx.getResources(), resID);
                    /*secondsHandIconBitmap = scaleSecondsHandIcon(secondsHandIconBitmap);*/
                    teamBitmap[i] = secondsHandIconBitmap;/*BitmapFactory.decodeResource(contx.getResources(), resID);*/
            }

            /* Set defaults for colors */
            mHourPaint = new Paint();
            mMinutePaint = new Paint();
            mTickAndCirclePaint = new Paint();

            // Sets the hands to colors that are more visible depending on the background.
            if (timeOfDay < 2){
                mHourPaint.setColor(Color.BLACK);
                mMinutePaint.setColor(Color.BLACK);
                mTickAndCirclePaint.setColor(Color.BLACK);
            } else {
                mHourPaint.setColor(Color.WHITE);
                mMinutePaint.setColor(Color.WHITE);
                mTickAndCirclePaint.setColor(Color.WHITE);
            }

            mHourPaint = new Paint();
            mHourPaint.setStrokeWidth(HOUR_STROKE_WIDTH);
            mHourPaint.setAntiAlias(true);
            mHourPaint.setStrokeCap(Paint.Cap.ROUND);

            mMinutePaint = new Paint();
            mMinutePaint.setStrokeWidth(MINUTE_STROKE_WIDTH);
            mMinutePaint.setAntiAlias(true);
            mMinutePaint.setStrokeCap(Paint.Cap.ROUND);

            mTickAndCirclePaint = new Paint();
            mTickAndCirclePaint.setStrokeWidth(SECOND_TICK_STROKE_WIDTH);
            mTickAndCirclePaint.setAntiAlias(true);
            mTickAndCirclePaint.setStyle(Paint.Style.STROKE);

            secondsHandIconPaint = new Paint();
            secondsHandIconPaint.setAntiAlias(true);
            secondsHandIconPaint.setFilterBitmap(true);


            /*pkmnTeamLeadPaint = new Paint();
            pkmnTeamLeadPaint.setAntiAlias(true);*/

            /*secondHandOutlinePaint = new Paint();
            secondHandOutlinePaint.setAntiAlias(true);*/

        }

        // Get pokemon name from randomly generated dex number
        public String randomPokemon(){
            Random rand = new Random();

            // All non-shiny, non-mega pokemon excluding alternate forms.
            int n = rand.nextInt(807) + 1;

            // Open database
            sqliteHelper sql = new sqliteHelper(dialgaWatchFaceService.this);
            sql.openDataBase();

            // Query the pokemon's name based on national dex number.
            name = sql.getIcon(n);

            return name;
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();

        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);

        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            setTimeOfDay();
            setSecondsHand();
            invalidate();
        }


        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            mAmbient = inAmbientMode;
            updateWatchHandStyle();

            // Check and trigger whether or not timer should be running (only in active mode)
            updateTimer();

        }

        private void updateWatchHandStyle() {
            if (mAmbient) {
                mHourPaint.setColor(Color.WHITE);
                mMinutePaint.setColor(Color.WHITE);
                mTickAndCirclePaint.setColor(Color.WHITE);

                mHourPaint.setAntiAlias(false);
                mMinutePaint.setAntiAlias(false);
                mTickAndCirclePaint.setAntiAlias(false);

                mHourPaint.clearShadowLayer();
                mMinutePaint.clearShadowLayer();
                mTickAndCirclePaint.clearShadowLayer();

            } else {

                // While not in ambient mode, set hands colors based on the background for better
                // visibility.
                if (timeOfDay < 2){
                    mHourPaint.setColor(Color.BLACK);
                    mMinutePaint.setColor(Color.BLACK);
                    mTickAndCirclePaint.setColor(Color.BLACK);
                } else {
                    mWatchHandShadowColor = Color.WHITE;
                    mHourPaint.setColor(Color.WHITE);
                    mMinutePaint.setColor(Color.WHITE);
                    mTickAndCirclePaint.setColor(Color.WHITE);
                }

                mHourPaint.setAntiAlias(true);
                mMinutePaint.setAntiAlias(true);
                mTickAndCirclePaint.setAntiAlias(true);
                secondsHandIconPaint.setAntiAlias(true);
                secondsHandIconPaint.setFilterBitmap(true);


            }
        }


        @Override
        public void onInterruptionFilterChanged(int interruptionFilter) {
            super.onInterruptionFilterChanged(interruptionFilter);
            boolean inMuteMode = (interruptionFilter == WatchFaceService.INTERRUPTION_FILTER_NONE);

            /* Dim display in mute mode. */
            if (mMuteMode != inMuteMode) {
                mMuteMode = inMuteMode;
                mHourPaint.setAlpha(inMuteMode ? 100 : 255);
                mMinutePaint.setAlpha(inMuteMode ? 100 : 255);
                invalidate();
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            // Find center of screen
            mCenterX = width / 2f;
            mCenterY = height / 2f;

            // Scale everything
            scaleHandLengths();
            scaleBackgroundArray();
            scaleSecondsHandArray();

        }

        private void scaleHandLengths(){
            mSecondHandLength = (mCenterX - (mCenterX * 0.05f));
            sMinuteHandLength = (float) (mCenterX * 0.8);
            sHourHandLength = (float) (mCenterX * 0.55);
        }

        // Loop through background array and scale each element.
        public void scaleBackgroundArray(){
            for (int i = 0; i < 4; i++) {
                mBackgroundArray[i] = scaleBackground(mBackgroundArray[i]);
            }
        }

        // Loop through icon bitmap array and scale each element.
        public void scaleSecondsHandArray(){
            for (int i = 0; i <6; i++) {
                teamBitmap[i] = scaleSecondsHandIcon(teamBitmap[i]);
            }
        }

        // Scale the background to fit to wearable's screen.
        private Bitmap scaleBackground(Bitmap bitmap){
            float backgroundWallpaperScaler = (( mCenterX * 2f )
                    / (float) bitmap.getWidth());

            // For future square displays.
            /*float backgroundWallpaperHeightScaler = (( mCenterX * 2f )
                    / (float) mBackgroundBitmap.getHeight());*/

            mBackgroundBitmap = Bitmap.createScaledBitmap(
                    bitmap
                    , (int) (bitmap.getWidth()
                            * backgroundWallpaperScaler)
                    , (int) (bitmap.getHeight()
                            * backgroundWallpaperScaler)
                    , true);

            return mBackgroundBitmap;
        }

        // Scale icon bitmaps to fit wearable's screen.
        private Bitmap scaleSecondsHandIcon(Bitmap bitmap) {
            resizePercentageWidth = (((float) bitmap.getWidth() * 2f)
                    / 326f);
            resizePercentageHeight = (((float) bitmap.getHeight() * 2f)
                    / 326f);

            iconBitmapWitdhScaler = ((mCenterX * 2f) * resizePercentageWidth)
                    / bitmap.getWidth();

            iconBitmapHeightScaler = ((mCenterX * 2f) * resizePercentageHeight)
                    / bitmap.getHeight();

            secondsHandIconBitmap = Bitmap.createScaledBitmap(
                    bitmap
                    , (int) (bitmap.getWidth()
                            * iconBitmapWitdhScaler)
                    , (int) (bitmap.getHeight()
                            * iconBitmapHeightScaler)
                    , true);

            return secondsHandIconBitmap;

        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);
            drawBackground(canvas);
            drawWatchFace(canvas);

            // For future overworld pokemon on screen
            /*drawPokemon(canvas);*/
        }

        private void drawBackground(Canvas canvas) {

            if (mAmbient) {
                canvas.drawColor(Color.BLACK);
            } else {

                // Draws the element from array that is appropriate for the time of day
                canvas.drawBitmap(mBackgroundArray[timeOfDay], 0, 0, mBackgroundPaint);
            }

        }

        private void drawWatchFace(Canvas canvas) {

            // Rotation degrees per unit of time
            final float seconds =
                    (mCalendar.get(Calendar.SECOND)
                            + mCalendar.get(Calendar.MILLISECOND)
                            / 1000f);

            final float secondsRotation = seconds * 6f;

            final float minutesRotation = mCalendar.get(Calendar.MINUTE) * 6f;

            final float hourHandOffset = mCalendar.get(Calendar.MINUTE) / 2f;
            final float hoursRotation = (mCalendar.get(Calendar.HOUR) * 30) + hourHandOffset;


            //Save the canvas state before we can begin to rotate it.
            canvas.save();

            // Rotate and draw hours hand
            canvas.rotate(hoursRotation, mCenterX, mCenterY);
            canvas.drawLine(
                    mCenterX,
                    mCenterY - CENTER_GAP_AND_CIRCLE_RADIUS,
                    mCenterX,
                    mCenterY - sHourHandLength,
                    mHourPaint);

            // Rotate and draw minutes hand
            canvas.rotate(minutesRotation - hoursRotation, mCenterX, mCenterY);
            canvas.drawLine(
                    mCenterX,
                    mCenterY - CENTER_GAP_AND_CIRCLE_RADIUS,
                    mCenterX,
                    mCenterY - sMinuteHandLength,
                    mMinutePaint);

            // Rotate and draw seconds hand when not in ambient mode
            if (!mAmbient) {
                canvas.rotate(secondsRotation - minutesRotation, mCenterX, mCenterY);
                canvas.drawBitmap(
                        // Chooses element from icon bitmap Array to display for one minute.
                        teamBitmap[newIndex],
                        mCenterY - (teamBitmap[newIndex].getWidth()/2),
                        mCenterY - mSecondHandLength,
                        secondsHandIconPaint);
            }

            // Draw center circle
            canvas.drawCircle(
                    mCenterX,
                    mCenterY,
                    CENTER_GAP_AND_CIRCLE_RADIUS,
                    mTickAndCirclePaint);

            // Restore canvas to original position
            canvas.restore();
        }

        // Randomly selects the index for a team of pokemon that were selected during the
        // watchface initialization. Also ensures that a chosen icon will not be the same every
        // sequential minute.
        public void setSecondsHand() {
            boolean check;
            Random rand = new Random();

            // Generate a random number to be used for the index of the teamBitmap array.
            // If it's the same as the previously used icon, roll until a different one.
            do {
                newIndex = rand.nextInt(5);
                check = newIndex == oldIndex ? true : false;
            } while (check);

            // Store the new index for comparison in the next roll.
            oldIndex = newIndex;

        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();
                // Update timezone after becoming visible
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            } else {
                unregisterReceiver();
            }

            // Check and trigger timer in active mode
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            dialgaWatchFaceService.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            dialgaWatchFaceService.this.unregisterReceiver(mTimeZoneReceiver);
        }

        // Starts or stops the timer based on the state of the watch face

        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        // Returns whether the timer handler should be running. The timer
        // should only run in active mode
        private boolean shouldTimerBeRunning() {
            return isVisible() && !mAmbient;
        }

        // Handle updating the time periodically in interactive mode
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }

        }

    }
}
