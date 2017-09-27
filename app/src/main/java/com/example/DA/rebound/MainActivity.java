package com.example.DA.rebound;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.MainThread;
import android.support.v4.view.GestureDetectorCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.media.SoundPool;
import android.media.AudioManager;
import android.widget.Toast;


import com.example.DA.rebound.Ball;


import java.util.Random;


public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener  {

    public static  final String TAG= MainActivity.class.getSimpleName();
    public final static String EXTRA_MESSAGE = "com.example.darrelladjei.experiment.MESSAGE";

    //for dealing with the scaling
    private float ScaleX;
    private float ScaleY;

    GestureDetectorCompat mDetectorCompat;
    //for the patch
    private Ball ball;

    private Patch mPatch;
    private Paint mPaint;

    //for dealing with GameOver and pause
    private boolean GameOver=false;
    private MainThread thread;
    private boolean isPaused=true;
    private boolean PauseShown =false;
    private boolean PlayShown =false;
    Toast Pause_toast;
    Toast Play_toast;
    Toast Swipe_toast;

    //for dealing with retry, starting and highScore
    boolean start=false;
    Rect BeginRect;
    boolean Retry=false;
    private static SharedPreferences pref;
    private static int HighScore=0;
    private String saveScore="HighScore";
    private String resumeScore="ResumeScore";
    private static int ResumeScore=0;

    //for dealing with first swipe
    private boolean firstSwipe=false;
    private int Xvelocity=10;
    private int Yvelocity=10;

    //for dealing with color change
    private int[] ColorArray;

    //for making the sound effects
    SoundPool sounds;
    private int GainMuse;
    private int LoseMuse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mDetectorCompat=new GestureDetectorCompat(this,this);
        setContentView(new Game(this));
    }

    @Override
    protected void onPause(){
        super.onPause();
        thread.setRunning(false);
    }
    
    @Override
    protected void onStop(){
        super.onStop();
        Log.v(TAG, "stopped");
        finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.v(TAG,"destroy");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }
    
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        start=true;
        isPaused=false;
        if (GameOver){
            Log.v(TAG, "tapped");
            Retry=true;
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        firstSwipe=true;
        if(Math.abs(e1.getX()-mPatch.getXCentre()*ScaleX)<=mPatch.getRadius() ){
            {
                if(Math.abs(e1.getY()-mPatch.getYCentre()*ScaleY)<=mPatch.getRadius()) {
                    if (Math.abs(ball.getXposition()*ScaleX - mPatch.getXCentre()*ScaleX) <= mPatch.getRadius()*ScaleX ) {
                        if(Math.abs(ball.getYposition()*ScaleY - mPatch.getYCentre()*ScaleY) <= mPatch.getRadius()*ScaleX) {
                            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                                if (velocityX > 0) {
                                    ball.setXposition(540);
                                    ball.setYposition(888);
                                    ball.setDeltaY(0);
                                    ball.setDeltaX(Xvelocity);
                                } else {
                                    ball.setXposition(540);
                                    ball.setYposition(888);
                                    ball.setDeltaY(0);
                                    ball.setDeltaX(-Xvelocity);
                                }
                            } else if (Math.abs(velocityX) < Math.abs(velocityY)) {
                                if (velocityY > 0) {
                                    ball.setXposition(540);
                                    ball.setYposition(888);
                                    ball.setDeltaY(Yvelocity);
                                    ball.setDeltaX(0);
                                } else {
                                    ball.setXposition(540);
                                    ball.setYposition(888);
                                    ball.setDeltaX(0);
                                    ball.setDeltaY(-Yvelocity);
                                }
                            }
                        }

                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if(!GameOver) {
            if (!isPaused) {
                isPaused = true;
            } else {
                isPaused = false;
            }
        }
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    public class Game extends SurfaceView implements SurfaceHolder.Callback {
        
        private Canvas mCanvas;
        private int WIDTH = 1080;
        private int HEIGHT = 1776;
        
        //To monitor the score
        private int score = 0;
        private Paint painter;

        //The centre coordinates
        private int XCentre;
        private int YCentre;

        //making the colors
        Paint paint = new Paint();
        Paint paint2 = new Paint();
        Paint paint3 = new Paint();
        Paint paint4 = new Paint();

        Colors blue = new Colors(paint, Color.BLUE);
        Colors red = new Colors(paint, Color.RED);
        Colors green = new Colors(paint, Color.GREEN);
        Colors yellow = new Colors(paint, Color.YELLOW);
        Colors white = new Colors(paint, Color.WHITE);

        //Making the targets
        Rect testRect = new Rect();
        Rect testRect2 = new Rect();
        Rect testRect3 = new Rect();
        Rect testRect4 = new Rect();

        //for handling the GameOver
        private String FirstLine = "";
        private String SecondLine = "";
        private SlabsTarget slab1, slab2, slab3, slab4;
        
        public Game(Context context) {
            super(context);
            pref = context.getSharedPreferences("com.example.darrelladjei.experiment", context.MODE_PRIVATE);
            String spackage = "com.example.darrelladjei.experiment";
            HighScore = pref.getInt(saveScore, 0);
            ResumeScore=pref.getInt(resumeScore,0);
            getHolder().addCallback(this);
            setFocusable(true);
            thread = new MainThread(getHolder(), this);
        }


        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Pause_toast = Toast.makeText(getContext(), "Double tap to pause", Toast.LENGTH_LONG);
            Pause_toast.setGravity(Gravity.BOTTOM,0,30);
            Play_toast = Toast.makeText(getContext(), "Double tap to play", Toast.LENGTH_LONG);
            Play_toast.setGravity(Gravity.BOTTOM,0,30);
            Swipe_toast = Toast.makeText(getContext(), "Swipe the ball!", Toast.LENGTH_LONG);
            Swipe_toast.setGravity(Gravity.BOTTOM,0,30);
            mCanvas = holder.lockCanvas();
            sounds = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            GainMuse = sounds.load(getContext(), R.raw.gain_muse, 1);
            LoseMuse = sounds.load(getContext(), R.raw.lose_muse, 1);
            BeginRect = new Rect(0, 0, mCanvas.getWidth(), mCanvas.getHeight());
            initialScreen();
            mCanvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rebound), null, BeginRect, null);
            holder.unlockCanvasAndPost(mCanvas);
            thread.setRunning(true);
            thread.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            ResumeScore=score;
            if (score >= HighScore) {
                HighScore = score;
            }
            pref.edit().putInt(saveScore, HighScore).apply();
            pref.edit().putInt(resumeScore, ResumeScore).apply();
            boolean retry = true;
            while (retry) {
                try {
                    thread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    Log.e(TAG, "something bad obviously happened init");
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            mDetectorCompat.onTouchEvent(event);
            return super.onTouchEvent(event);
        }

        public void Draw(Canvas c) {
            if (!start) {
                mCanvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rebound), null, BeginRect, null);
            } else {
                if(!firstSwipe){
                    Swipe_toast.show();
                    firstSwipe=true;
                }
                if(!PauseShown){
                    Pause_toast.show();
                    PauseShown =true;
                }
                if(isPaused){
                    if(!PlayShown) {
                        Play_toast.show();
                        PlayShown=true;
                    }
                    thread.setPaused(true);

                }else{

                    thread.setPaused(false);
                }
                mCanvas.scale(ScaleX, ScaleY);
                mCanvas.drawColor(white.getPaintColor());
                mPatch = new Patch(mPaint, XCentre, YCentre, 150, mCanvas);
                ball.drawBall(mCanvas);
                slab1.draw(mCanvas);
                slab2.draw(mCanvas);
                slab3.draw(mCanvas);
                slab4.draw(mCanvas);
                painter.setFakeBoldText(true);
                painter.setColor(Color.BLACK);
                painter.setTextSize(100);
                Rect rec = new Rect();
                painter.getTextBounds("SCORE:" + score, 0, "SCORE".length(), rec);
                mCanvas.drawText("SCORE:" + score, 50, 200, painter);
                if(!GameOver){
                    FirstLine = "High Score:" + HighScore;
                    SecondLine = "";
                }else{
                    FirstLine = "Game Over!";
                    SecondLine = "Tap to restart";
                }
                mCanvas.drawText(FirstLine, 300, 1476, painter);
                mCanvas.drawText(SecondLine, 250, 1576, painter);
            }
        }


        public void update(){
            if(!GameOver){
                ball.setYposition(ball.getYposition() + ball.getDy());
                ball.setXposition(ball.getXposition() + ball.getDx());
            }
            if(Contact()){
                if (ColorMatch()) {
                    sounds.play(GainMuse,1.0f,1.0f,1,0,1.0f);
                    ball.setDeltaX(ball.getDx() * -1);
                    ball.setDeltaY(ball.getDy() * -1);
                    score+=1;
                    RandomizeArray(ColorArray);
                    slab1.SetPaintColor(ColorArray[0]);
                    slab2.SetPaintColor(ColorArray[1]);
                    slab3.SetPaintColor(ColorArray[2]);
                    slab4.SetPaintColor(ColorArray[3]);
                    if(score<3){
                        ball.setBallColor(ball.getBallColor());
                        ball.setPaintColor(ball.getBallColor());
                    }
                    else if(score>=3 && score<10){
                        Xvelocity=10;
                        Yvelocity=10;
                    }else if(score>=10 && score<15){
                        ball.setBallColor(red.getPaintColor());
                        ball.setPaintColor(red.getPaintColor());
                    }else if(score>=20 && score<25){
                        ball.setBallColor(green.getPaintColor());
                        ball.setPaintColor(green.getPaintColor());
                    }else if (score>=35 && score<40){
                        ball.setBallColor(yellow.getPaintColor());
                        ball.setPaintColor(yellow.getPaintColor());
                    }
                    else {
                        Random r = new Random();
                        int index=ColorArray[r.nextInt(4)];
                        ball.setBallColor(index);
                        ball.setPaintColor(index);
                        if(score>=60){
                            Xvelocity=20;
                            Yvelocity=20;
                        }
                    }
                }
                else {
                    sounds.play(LoseMuse, 1.0f, 1.0f, 1, 0, 1.0f);
                    if(score>=HighScore){
                        HighScore=score;
                    }
                    pref.edit().putInt(saveScore,HighScore).apply();
                    boolean beenThru=false;
                    while(GameOver){
                        thread.setPaused(true);
                        restart();
                        beenThru=true;
                    }
                    if(!beenThru){
                        GameOver=true;
                    }else{
                        GameOver=false;
                    }
                }
            }
        }

        private boolean Contact(){
            boolean contact=false;
            if((ball.getYposition()-ball.getRadius()==slab3.getBottomPosition()) ){
                //checking if at green
                contact=true;
            }
            else if( (ball.getYposition()+ball.getRadius() ==slab4.getTopPosition())){
                //checking if at yellow
                contact=true;
            } else if((ball.getXposition() +ball.getRadius()==slab2.getLeftPosition())) {
                //checking if at red
                contact=true;
            }
            else if((ball.getXposition()-ball.getRadius() == slab1.getRightPosition())) {
                //checking if at blue
                contact=true;
            }
            return contact;
        }

        private boolean ColorMatch(){
            boolean match=false;
            if((ball.getYposition()-ball.getRadius()==slab3.getBottomPosition())){
                //checking if at initial green
                if(ball.getBallColor()==slab3.getColor()){
                    match=true;
                }
            }else if((ball.getYposition()+ball.getRadius()==slab4.getTopPosition())){
                //checking if at initial yellow
                if(ball.getBallColor()==slab4.getColor()){
                    match=true;
                }
            }else if((ball.getXposition() +ball.getRadius()==slab2.getLeftPosition())){
                //checking if at initial red
                if(ball.getBallColor()==slab2.getColor()){
                    match=true;
                }
            }else if((ball.getXposition()-ball.getRadius() == slab1.getRightPosition())){
                //checking if at initial blue
                if(ball.getBallColor()==slab1.getColor()){
                    match=true;
                }
            }
            return match;
        }

        public void restart(){
            if (Retry) {
                Log.v(TAG, "yup, worked");
                GameOver=false;
                ball = new Ball(mCanvas,XCentre, YCentre, 50, blue.getPaintColor());
                score=0;
                Xvelocity=10;
                Yvelocity=10;
                thread.setPaused(false);
                Retry=false;
            }
        }

        public void initialScreen(){
            //for the scaling
            ScaleX=(float)getWidth()/WIDTH;
            ScaleY=(float)getHeight()/HEIGHT;
            
            //paint objs just for use
            mPaint=new Paint();
            painter=new Paint();

            //paint obj just for use
            XCentre=1080/2;
            YCentre= 1776/2;
            mPatch=new Patch(mPaint,XCentre,YCentre,150,mCanvas);

            //Coloring the background
            new Background(white.getPaintColor(),mCanvas);

            //making the ball
            XCentre=1080/2;
            YCentre= 1776/2;
            ball = new Ball(mCanvas,XCentre, YCentre, 50, blue.getPaintColor());

            //filling in the colorArray
            ColorArray=new int[4];
            ColorArray[0]=blue.getPaintColor();
            ColorArray[1]=red.getPaintColor();
            ColorArray[2]=green.getPaintColor();
            ColorArray[3]=yellow.getPaintColor();

            //Making the targets
            slab1= new SlabsTarget(mCanvas,paint,testRect,ColorArray[0],40, 500, 90, 1276);
            slab2= new SlabsTarget(mCanvas,paint2,testRect2,ColorArray[1],990, 500, 1040, 1276);
            slab3= new SlabsTarget(mCanvas,paint3,testRect3,ColorArray[2],190, 468, 890, 518);
            slab4= new SlabsTarget(mCanvas,paint4,testRect4,ColorArray[3],190, 1258, 890, 1308);

            //for the restart
            score=ResumeScore;
        }
        
        public int[] RandomizeArray(int[] array){
            Random rgen = new Random();  // Random number generator
            for (int i=0; i<array.length; i++) {
                int randomPosition = rgen.nextInt(array.length);
                int temp = array[i];
                array[i] = array[randomPosition];
                array[randomPosition] = temp;
            }
            return array;
        }
    }
}

