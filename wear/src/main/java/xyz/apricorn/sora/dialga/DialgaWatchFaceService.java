package xyz.apricorn.sora.dialga;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.support.v7.graphics.Palette;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class DialgaWatchFaceService extends CanvasWatchFaceService {

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
        private final WeakReference<DialgaWatchFaceService.Engine> mWeakReference;

        public EngineHandler(DialgaWatchFaceService.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            DialgaWatchFaceService.Engine engine = mWeakReference.get();
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
        /*private static final int SHADOW_RADIUS = 6;*/

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

        @Override
        public void onTapCommand(
                @TapType int tapType, int x, int y, long eventTime) {
            switch (tapType) {
                case WatchFaceService.TAP_TYPE_TAP:
                    rerollPkmnTeam.run();
                    Toast.makeText(DialgaWatchFaceService.this, R.string.new_team_confirmation, Toast.LENGTH_SHORT).show();
                    break;

                case WatchFaceService.TAP_TYPE_TOUCH:
                    /*if (withinTapRegion(x, y)) {
                        // Provide visual feedback of touch event
                        startTapHighlight(x, y, eventTime);
                    }*/
                    break;

                case WatchFaceService.TAP_TYPE_TOUCH_CANCEL:
                    /*hideTapHighlight();*/
                    break;

                default:
                    /*super.onTapCommand(tapType, x, y, eventTime);*/
                    break;
            }
        }

        private boolean mRegisteredTimeZoneReceiver = false;
        private boolean mMuteMode;
        private float mCenterX;
        private float mCenterY;
        private float mSecondHandLength;
        private float sMinuteHandLength;
        private float sHourHandLength;
        private float resizePercentageWidth;
        private float resizePercentageHeight;
        private float iconBitmapWidthScaler;
        private float iconBitmapHeightScaler;

        // Date Paint

        // Seconds hand
        private Bitmap[] teamBitmap = new Bitmap[6];
        /*private String[] pkmnWalk = new String[2];*/
        /*private Bitmap[] pkmnWalkBitmap = new Bitmap[2];*/
        private int oldIndex = 0;
        private int newIndex = 0;
        private Paint secondsHandIconPaint;
        private Bitmap secondsHandIconBitmap;
        /*private boolean animation = true;*/
        /*private int walkIndex;*/

        // Background
        private int timeOfDay;
        private Bitmap mBackgroundArray[] = new Bitmap[4];
        private Paint mBackgroundPaint;
        private Bitmap mBackgroundBitmap;

        /* Colors for all hands (hour, minute, seconds, ticks) based on photo loaded. */
        private Paint mHourPaint;
        private Paint mMinutePaint;
        private Paint mTickAndCirclePaint;
        private int mWatchHandColor;
        private int mWatchHandShadowColor;
        private boolean mAmbient;

        Random rand = new Random();
        PokemonTeam pkmnTeam = new PokemonTeam(DialgaWatchFaceService.this);

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            // SQLite database for retrieving pokemon data
            SqliteHelper databaseHelper;
            databaseHelper = new SqliteHelper(DialgaWatchFaceService.this);



            /*DialgaWatchFaceService.this*/

            // Create the database on the wearable from database in apk's assets.
            try {
                databaseHelper.createDataBase();
                databaseHelper.close();
            } catch (IOException ioe) {
                throw new Error("Unable to create database");
            }

            mCalendar = Calendar.getInstance();


            // Initialize everything.

            initializeWatchFace();
            initializeBackground();


            setWatchFaceStyle(new WatchFaceStyle.Builder(DialgaWatchFaceService.this)
                    .setAcceptsTapEvents(true)
                    .setShowUnreadCountIndicator(true)
                    .build());
        }

        private void initializeBackground() {
            Context contx = DialgaWatchFaceService.this;

            // Get resource IDs for the backgrounds.
            int dawn = contx.getResources().getIdentifier("dawn", "drawable", contx.getPackageName());
            int day = contx.getResources().getIdentifier("day", "drawable", contx.getPackageName());
            int twilight = contx.getResources().getIdentifier("twilight", "drawable", contx.getPackageName());
            int night = contx.getResources().getIdentifier("night", "drawable", contx.getPackageName());

            // Store background bitmaps in array.
            mBackgroundArray[0] = BitmapFactory.decodeResource(getResources(), dawn);
            mBackgroundArray[1] = BitmapFactory.decodeResource(getResources(), day);
            mBackgroundArray[2] = BitmapFactory.decodeResource(getResources(), twilight);
            mBackgroundArray[3] = BitmapFactory.decodeResource(getResources(), night);

            generatePalette(mBackgroundArray[newIndex]);
            // Initialize background paint object.
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(Color.BLACK);

            // Initialize the timeOfDay variable so that the background can be drawn.
            setTimeOfDay();

            /* Extracts colors from background image to improve watchface style. */

        }

        private void generatePalette(Bitmap sourceBitmap){
            Palette.from(sourceBitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    if (palette != null) {
                        mWatchHandColor = palette.getLightVibrantColor(Color.WHITE);
                        mWatchHandShadowColor = palette.getDarkMutedColor(Color.BLACK);
                        updateWatchHandStyle();
                    }
                }
            });
        }
        // Sets the timeOfDay variable based on the current time of day. Return an integer that
        // corresponds to an element's index stored in the backgrounds array.
        private void setTimeOfDay() {
            GregorianCalendar now = new GregorianCalendar();
            int mHour = now.get(Calendar.HOUR_OF_DAY);

            // If hours are between midnight and 4 in the morning, the time is "night."
            if (mHour >= 0 && mHour < 5) {
                timeOfDay = 3;

                // If hours are between 5 and 9 in the morning, the time is "dawn."
            } else if (mHour >= 5 && mHour < 10) {
                timeOfDay = 0;

                // If hours are between 10 in the morning and 4 in the afternoon, the time is "day."
            } else if (mHour >= 10 && mHour < 17) {
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
            setTeamBitmap();
            /*setPkmnWalk();*/

            /* Set defaults for colors */
            mHourPaint = new Paint();
            mMinutePaint = new Paint();
            mTickAndCirclePaint = new Paint();
            secondsHandIconPaint = new Paint();
            /*hello = new Paint();*/

            // Sets the hands to colors that are more visible depending on the background.
            /*if (timeOfDay < 2) {*/
                /*mHourPaint.setColor(Color.BLACK);
                mMinutePaint.setColor(Color.BLACK);
                mTickAndCirclePaint.setColor(Color.BLACK);*/
                mHourPaint.setColor(mWatchHandColor);
                mMinutePaint.setColor(mWatchHandColor);
                mTickAndCirclePaint.setColor(mWatchHandColor);
                /*hello.setColor(Color.BLACK);*/
            /*} else {*/
              /*  mHourPaint.setColor(Color.WHITE);
                mMinutePaint.setColor(Color.WHITE);
                mTickAndCirclePaint.setColor(Color.WHITE);*/
                /*hello.setColor(Color.WHITE);*/
            /*}*/

            // For displaying date on watchface
            /*hello.setStrokeWidth(MINUTE_STROKE_WIDTH);
            hello.setAntiAlias(true);
            hello.setStrokeCap(Paint.Cap.ROUND);
            hello.setFilterBitmap(true);
            hello.setAntiAlias(true);
            hello.setTextSize(16);*/

            /*mHourPaint = new Paint();*/
            mHourPaint.setStrokeWidth(HOUR_STROKE_WIDTH);
            mHourPaint.setAntiAlias(true);
            mHourPaint.setStrokeCap(Paint.Cap.ROUND);
            mHourPaint.setFilterBitmap(true);

            /*mMinutePaint = new Paint();*/
            mMinutePaint.setStrokeWidth(MINUTE_STROKE_WIDTH);
            mMinutePaint.setAntiAlias(true);
            mMinutePaint.setStrokeCap(Paint.Cap.ROUND);
            mMinutePaint.setFilterBitmap(true);

            /*mTickAndCirclePaint = new Paint();*/
            mTickAndCirclePaint.setStrokeWidth(SECOND_TICK_STROKE_WIDTH);
            mTickAndCirclePaint.setAntiAlias(true);
            mTickAndCirclePaint.setStyle(Paint.Style.STROKE);
            mTickAndCirclePaint.setFilterBitmap(true);


            secondsHandIconPaint.setFilterBitmap(true);

            // For displaying the outline of the second hand sprite bitmap while
            // the watchface is in ambient mode
            /*secondHandOutlinePaint = new Paint();
            secondHandOutlinePaint.setAntiAlias(true);*/

        }

        // Asynchronously reroll the new teeam
        private Runnable rerollPkmnTeam = new Runnable(){
            public void run(){
                pkmnTeam.rerollTeam(DialgaWatchFaceService.this);
                setTeamBitmap();
                scaleSecondsHandArray();

            }

        };

        private void setTeamBitmap() {
            Context context = DialgaWatchFaceService.this;
            /*PokemonTeam pkmnTeam = new PokemonTeam(context);*/

            // Sets a team of six pokemon to display for the seconds hand
            for (int i = 0; i < 6; i++) {
                teamBitmap[i] = pkmnTeam.getPokemonBitmap(i);

            }

        }

        // For future animated overworld sprites for an animated ticking seconds hand
        /*private void setPkmnWalk()
        {
            int resID;

                *//*pkmnWalk[0] = "ralts1";
                pkmnWalk[1] = "ralts2";*//*




                resID = getResources().getIdentifier("ralts1" *//*+ megaRoll()*//**//* + shinyRoll()*//*, "drawable", getPackageName());
                pkmnWalkBitmap[0] = BitmapFactory.decodeResource(getResources(), resID);

                resID = getResources().getIdentifier("ralts2" *//*+ megaRoll()*//**//* + shinyRoll()*//*, "drawable", getPackageName());
                pkmnWalkBitmap[1] = BitmapFactory.decodeResource(getResources(), resID);


        }*/

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
            changeSecondsHand();
            invalidate();
        }

        // Randomly selects the index for a team of pokemon that were selected during the
        // watch face initialization. Also ensures that a chosen icon will not be the same every
        // sequential minute.
        private void changeSecondsHand() {
            int boundary = 5;

            // Generate a random number to be used for the index of the teamBitmap array.
            // If it's the same as the previously used icon, add 1 to the index. If the index is
            // Already at the capacity of of the array, set the index to 0.
            newIndex = rand.nextInt(boundary);
            if (newIndex == oldIndex) {
                if (newIndex == 5) {
                    newIndex = 0;

                } else {
                    newIndex++;

                }

            }

            /*generatePalette(mBackgroundArray[newIndex]);
            updateWatchHandStyle();*/
            invalidate();
            // Store the new index for comparison in the next roll.
            oldIndex = newIndex;

        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            mAmbient = inAmbientMode;
            updateWatchHandStyle();
            changeSecondsHand();

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

            } else {

                // While not in ambient mode, set hands colors based on the background for better
                // visibility.
                /*if (timeOfDay < 2) {*/
                    mHourPaint.setColor(mWatchHandColor);
                    mMinutePaint.setColor(mWatchHandColor);
                    mTickAndCirclePaint.setColor(mWatchHandColor);
                /*} else {
                    mHourPaint.setColor(Color.WHITE);
                    mMinutePaint.setColor(Color.WHITE);
                    mTickAndCirclePaint.setColor(Color.WHITE);
                }*/

                mHourPaint.setAntiAlias(true);
                mMinutePaint.setAntiAlias(true);
                mTickAndCirclePaint.setAntiAlias(true);

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
            /*scaleWalkArray();*/

        }

        private void scaleHandLengths() {
            mSecondHandLength = (mCenterX - (mCenterX * 0.05f));
            sMinuteHandLength = (float) (mCenterX * 0.8);
            sHourHandLength = (float) (mCenterX * 0.55);
        }

        // Loop through background array and scale each element.
        private void scaleBackgroundArray() {
            for (int i = 0; i < 4; i++) {
                mBackgroundArray[i] = scaleBackground(mBackgroundArray[i]);
            }
        }

        // Loop through icon bitmap array and scale each element.
        private void scaleSecondsHandArray() {
            /*if(!toggleTeam) {*/
            for (int i = 0; i < 6; i++) {
                teamBitmap[i] = scaleSecondsHandIcon(i);
            }

        }

        // For future overworld animated sprites
        /*private void scaleWalkArray(){
            for (int i = 0; i < 2; i++) {
                pkmnWalkBitmap[i] = scaleSecondsHandIcon(pkmnWalkBitmap[i]);
            }
        }*/

        // Scale the background to fit to wearable's screen.
        private Bitmap scaleBackground(Bitmap bitmap) {
            float backgroundWallpaperScaler = ((mCenterX * 2f)
                    / (float) bitmap.getWidth());

            // For future rectangular displays
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
        private Bitmap scaleSecondsHandIcon(int index/*Bitmap bitmap*/) {
            resizePercentageWidth = (((float) teamBitmap[index].getWidth() / 2f)
                    / 326f);
            resizePercentageHeight = (((float) teamBitmap[index].getHeight() / 2f)
                    / 326f);

            iconBitmapWidthScaler = ((mCenterX * 2f) * resizePercentageWidth)
                    / teamBitmap[index].getWidth();

            iconBitmapHeightScaler = ((mCenterX * 2f) * resizePercentageHeight)
                    / teamBitmap[index].getHeight();

            secondsHandIconBitmap = Bitmap.createScaledBitmap(
                    teamBitmap[index]
                    , (int) (teamBitmap[index].getWidth()
                            * iconBitmapWidthScaler)
                    , (int) (teamBitmap[index].getHeight()
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

            // For future overworld pokemon on watch face
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

            // Testing drawing text on the watchface
            /*canvas.drawText("hello", mCenterY, mCenterX, hello);*/

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

                // Testing seconds hand bounce animation at each second tick. Force of bounce in tick
                // to account for the pokemon's average weight.
                /*float boingDistance = secondsRotation - minutesRotation + 8f;
                float boing = boingDistance;
                while (boing != secondsRotation - minutesRotation) {*/

                // Seconds hand animation for overworld sprites
                /*walk(animation);*/
                canvas.rotate(secondsRotation - minutesRotation /*+ boingDistance*/, mCenterX, mCenterY);
                canvas.drawBitmap(

                        // Chooses element from icon bitmap Array to display for one minute.
                        teamBitmap[newIndex],
                        mCenterY - (teamBitmap[newIndex].getWidth() / 2),
                        mCenterY - mSecondHandLength,
                        secondsHandIconPaint);

                // For seconds hand walking animation
                        /*pkmnWalkBitmap[walkIndex],
                        mCenterY - (pkmnWalkBitmap[walkIndex].getWidth()/2),
                        mCenterY - mSecondHandLength,
                        secondsHandIconPaint);*/
                 /*       boingDistance /=-2;
                        boing = secondsRotation - minutesRotation + boingDistance - 1;
                        invalidate();
                }*/
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

        // Method for animating the seconds hand.
        /*private void walk(boolean anima){
            if(anima){
                walkIndex = 0;
                animation = false;
            } else{
                walkIndex = 1;
                animation = true;
            }
        }*/


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

            IntentFilter timeZone = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);

            DialgaWatchFaceService.this.registerReceiver(mTimeZoneReceiver, timeZone);

        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }

            mRegisteredTimeZoneReceiver = false;
            /*mRegisteredTeamReceiver = false;*/

            DialgaWatchFaceService.this.unregisterReceiver(mTimeZoneReceiver);

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
