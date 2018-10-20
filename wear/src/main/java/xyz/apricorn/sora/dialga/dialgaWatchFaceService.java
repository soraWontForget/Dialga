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
import android.support.v7.graphics.Palette;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/*import xyz.apricorn.sora.dialga.iconAndBackgroundBitmaps;*/

/*import static xyz.apricorn.sora.dialga.iconAndBackgroundBitmaps.fuckinga;*/


public class dialgaWatchFace extends CanvasWatchFaceService {
    /*
     * Updates rate in milliseconds for interactive mode. We update once a second to advance the
     * second hand.
     */

    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
    /*private static int backgroundSetter;*/
    public int placeholder;

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<dialgaWatchFace.Engine> mWeakReference;

        public EngineHandler(dialgaWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            dialgaWatchFace.Engine engine = mWeakReference.get();
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
        private int hour;
        private int minute;
        private int timeOfDay = 0;
        private int hourTimer;
        private int dexNumber = 0;

        private final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        mCalendar.setTimeZone(TimeZone.getDefault());
        invalidate();


            }
        };
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
        private float ldrResizePercentageWidth;
        private float ldrResizePercentageHeight;
        private float ldrIconBitmapWitdhScaler;
        private float ldrIconBitmapHeightScaler;



        /* Colors for all hands (hour, minute, seconds, ticks) based on photo loaded. */
        private int mWatchHandColor;
        private int mWatchHandShadowColor;
        private Paint mHourPaint;
        private Paint mMinutePaint;
        private Paint mTickAndCirclePaint;
        private Paint mBackgroundPaint;
        private Paint secondsHandIconPaint;
        private Paint pkmnTeamLeadPaint;
        private Bitmap mBackgroundBitmap;
        private Bitmap secondsHandIconBitmap;
        private Bitmap pkmnTeamLeadBitmap;
        private boolean mAmbient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            GregorianCalendar now = new GregorianCalendar();
            mCalendar = Calendar.getInstance();
            hour = now.get(Calendar.HOUR);
            minute = now.get(Calendar.MINUTE);


            initializeBackground();
            initializeWatchFace();

            setWatchFaceStyle(new WatchFaceStyle.Builder(dialgaWatchFace.this)
                    .build());



        }

        private void initializeBackground() {
            /*updateTimeOfDay();*/
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(Color.BLACK);
            mBackgroundBitmap = BitmapFactory.decodeResource(getResources(), iconAndBackgroundBitmaps.currentBackground(timeOfDay));/*R.drawable.pokemon_park_day_wallpaper);

            /* Extracts colors from background image to improve watchface style. */
            Palette.from(mBackgroundBitmap).generate(new Palette.PaletteAsyncListener() {
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

        private void initializeWatchFace() {

            secondsHandIconBitmap = /*fuckinga(dexNumber);*/BitmapFactory.decodeResource(getResources(), iconAndBackgroundBitmaps.fuckinga(dexNumber) /*R.drawable.umbreon*/);
            pkmnTeamLeadBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.espeon);/*iconAndBackgroundBitmaps.fuckinga(dexNumber));*/

            /* Set defaults for colors */
            mHourPaint = new Paint();
            mHourPaint.setColor(mWatchHandColor);
            mHourPaint.setStrokeWidth(HOUR_STROKE_WIDTH);
            mHourPaint.setAntiAlias(true);
            mHourPaint.setStrokeCap(Paint.Cap.ROUND);
            mHourPaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);

            mMinutePaint = new Paint();
            mMinutePaint.setColor(mWatchHandColor);
            mMinutePaint.setStrokeWidth(MINUTE_STROKE_WIDTH);
            mMinutePaint.setAntiAlias(true);
            mMinutePaint.setStrokeCap(Paint.Cap.ROUND);
            mMinutePaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);

            mTickAndCirclePaint = new Paint();
            mTickAndCirclePaint.setColor(mWatchHandColor);
            mTickAndCirclePaint.setStrokeWidth(SECOND_TICK_STROKE_WIDTH);
            mTickAndCirclePaint.setAntiAlias(true);
            mTickAndCirclePaint.setStyle(Paint.Style.STROKE);
            mTickAndCirclePaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);

            secondsHandIconPaint = new Paint();
            secondsHandIconPaint.setAntiAlias(true);

            pkmnTeamLeadPaint = new Paint();
            pkmnTeamLeadPaint.setAntiAlias(true);

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
            /*hourChecker();*/
            invalidate();
        }


        /*public void updateTimeOfDay() {

            GregorianCalendar now = new GregorianCalendar();

            if (now.get(Calendar.AM) == 0) {

                if (hour >= 5 & hour < 9) {

                        timeOfDay = 1;


                } else if ( hour >= 9 ){

                    timeOfDay = 2;

                }
            } else if ( now.get(Calendar.AM) != 0 ){

                if ( hour == 12){

                    timeOfDay = 2;

                } else if ( hour >= 1 & hour < 6 ){

                    timeOfDay = 2;

                } else if ( hour >= 6 & hour < 8 ) {

                    timeOfDay = 3;
                }

            } else {

                timeOfDay = 4;
            }
        }*/
         /*       timeOfDay = 1;
            } else if ( hour >= 1000 ) {
                backgroundSetter = 2;
            } else if ( timeOfDay >= 64800000 ) {
                backgroundSetter = 3;
            } else if ( currentTime >= 72000000 ) {
                backgroundSetter = 4;
            } else {
                backgroundSetter = 4;
            }

        }*/

         /*private void hourChecker(){

             GregorianCalendar now = new GregorianCalendar();

             if (now.get(Calendar.MINUTE) < 1 && timeOfDay != timeOfDayComparison){

                updateTimeOfDay();

             }
         }*/

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            mAmbient = inAmbientMode;
            updateWatchHandStyle();
            /*updateWeather();*/
            /*checkTimeOfDay();*/

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
                mHourPaint.setColor(mWatchHandColor);
                mMinutePaint.setColor(mWatchHandColor);
                mTickAndCirclePaint.setColor(mWatchHandColor);

                mHourPaint.setAntiAlias(true);
                mMinutePaint.setAntiAlias(true);
                mTickAndCirclePaint.setAntiAlias(true);

                mHourPaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);
                mMinutePaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);
                mTickAndCirclePaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);
            }
        }

        /*public void checkTimeOfDay(){

            if (timeOfDay != timeOfDayComparison ){

                updateBackground();
                timeOfDayComparison = timeOfDay;
            }
        }*/

        public void updateBackground(){


        }

        public void onSurfaceRedrawNeeded(SurfaceHolder holder){

        };

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
            scaleAndAssignBackgroundWallpaper();
            scaleTeamLeaderIcon(pkmnTeamLeadBitmap);
            scaleSecondsHandIcon(secondsHandIconBitmap);

        }

        private void scaleHandLengths(){
            mSecondHandLength = (mCenterX - (mCenterX * 0.05f));
            sMinuteHandLength = (float) (mCenterX * 0.8);
            sHourHandLength = (float) (mCenterX * 0.55);
        }

        private void scaleAndAssignBackgroundWallpaper(){
            float backgroundWallpaperScaler = (( mCenterX * 2f )
                    / (float) mBackgroundBitmap.getWidth());

            // For future square displays
            /*float backgroundWallpaperHeightScaler = (( mCenterX * 2f )
                    / (float) mBackgroundBitmap.getHeight());*/

            mBackgroundBitmap = Bitmap.createScaledBitmap(
                    mBackgroundBitmap
                    , (int) (mBackgroundBitmap.getWidth()
                            * backgroundWallpaperScaler)
                    , (int) (mBackgroundBitmap.getHeight()
                            * backgroundWallpaperScaler)
                    , true);

        }

        private void scaleTeamLeaderIcon(Bitmap bitmap) {
            ldrResizePercentageWidth = (((float) bitmap.getWidth() * 2f)
                    / 326f);
            ldrResizePercentageHeight = (((float) bitmap.getHeight() * 2f)
                    / 326f);

            ldrIconBitmapWitdhScaler = ((mCenterX * 2f) * ldrResizePercentageWidth)
                    / bitmap.getWidth();

            ldrIconBitmapHeightScaler = ((mCenterX * 2f) * ldrResizePercentageHeight)
                    / bitmap.getHeight();

            pkmnTeamLeadBitmap = Bitmap.createScaledBitmap(bitmap
                    , (int) (bitmap.getWidth()
                            * ldrIconBitmapWitdhScaler)
                    , (int) (bitmap.getHeight()
                            * ldrIconBitmapHeightScaler)
                    , true);

        }

        private void scaleSecondsHandIcon(Bitmap bitmap) {
            resizePercentageWidth = (((float) bitmap.getWidth() * 2f)
                    / 326f);
            resizePercentageHeight = (((float) bitmap.getHeight() * 2f)
                    / 326f);

            iconBitmapWitdhScaler = ((mCenterX * 2f) * resizePercentageWidth)
                    / bitmap.getWidth();

            iconBitmapHeightScaler = ((mCenterX * 2f) * resizePercentageHeight)
                    / bitmap.getHeight();

            secondsHandIconBitmap = Bitmap.createScaledBitmap(bitmap
                    , (int) (bitmap.getWidth()
                            * iconBitmapWitdhScaler)
                    , (int) (bitmap.getHeight()
                            * iconBitmapHeightScaler)
                    , true);


        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);
            drawBackground(canvas);
            drawWatchFace(canvas);
            drawPokemon(canvas);
        }

        private void drawBackground(Canvas canvas) {

            if (mAmbient) {
                canvas.drawColor(Color.BLACK);
            } else {
                canvas.drawBitmap(mBackgroundBitmap, 0, 0, mBackgroundPaint);
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

            /*
             * Save the canvas state before we can begin to rotate it.
             */
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
                canvas.drawBitmap(secondsHandIconBitmap,
                        mCenterY - (secondsHandIconBitmap.getWidth()/2),
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

        private void drawPokemon(Canvas canvas){

            //TODO
            // variable containing random xy position for draw
            // within a confined space that is initial position
            // of where the pokemon spawns

            //TODO
            // animation procedures for overworld
            // sprites once drawn

            //spawn
            if(!mAmbient) {
                canvas.drawBitmap(pkmnTeamLeadBitmap,
                        mCenterY - (secondsHandIconBitmap.getWidth() / 2),
                        mCenterY - mSecondHandLength,
                        pkmnTeamLeadPaint);
            }
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
            dialgaWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            dialgaWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
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
