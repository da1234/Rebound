package com.example.DA.rebound;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by darrelladjei on 14/10/2015.
 */
public class Ball {

    //for scaling
    private com.example.DA.rebound.MainActivity object;
    private float ScaleX;
    private float ScaleY;
    
    private float mRadius;
    private float mXposition;
    private float mYposition;
    private int mBallColor;
    private Canvas mCanvas;
    private float dx;
    private float dy;
    private Paint mColor;
    
    public Ball(Canvas c, int x, int y, int radius,int BallColor){
        mCanvas=c;
        ScaleX=(float)c.getWidth()/1080;
        ScaleY=(float)c.getHeight()/1776;
        
        //object=new MainActivity();
        mRadius=radius*Scaling();//*object.Scaling();
        mXposition=x*ScalingX();//*object.ScalingX();
        mYposition=y*ScalingY();//*object.ScalingY();
        mBallColor=BallColor;
        mColor = new Paint();
        mColor.setColor(mBallColor);
        mCanvas.drawCircle(mXposition, mYposition, mRadius, mColor);
    }

    public int getRadius() {
        return (int)mRadius;
    }

    public void setRadius(int radius) {
        mRadius = radius;
    }

    public int getXposition() {
        return (int)mXposition;
    }

    public void setXposition(int xposition) {
        mXposition = xposition;
    }

    public int getYposition() {
        return (int)mYposition;
    }

    public void setYposition(int yposition) {
        mYposition = yposition;
    }

    public int getBallColor() {
        return mBallColor;
    }

    public void setBallColor(int ballColor) {
        mBallColor = ballColor;
    }

    public Canvas getCanvas() {
        return mCanvas;
    }

    public void setCanvas(Canvas canvas) {
        mCanvas = canvas;
    }

    public void setDeltaX(float dx){
        this.dx=dx*ScalingX();//*object.ScalingX();
        mXposition+=dx;
    }

    public int getDx(){
        return (int)dx;
    }

    public void setDeltaY(int dy){
        this.dy=dy*ScalingY();//*object.ScalingY();
        mYposition+=dy;
    }

    public int getDy(){
        return (int)dy;
    }

    public Paint getColor() {
        return mColor;
    }

    public void setColor(Paint color) {
        mColor = color;
    }

    public void setPaintColor(int color){
        mColor.setColor(color);
    }

    public void drawBall(Canvas canvas){
        canvas.drawCircle(getXposition(),getYposition(),getRadius(),getColor());
    }


    public float ScalingX(){
        float scaleX=(float)ScaleX;
        return 1;//scaleX;
    }

    public float ScalingY(){
        float scaleY=(float)ScaleX;
        return 1;//scaleY;
    }

    public float Scaling(){
        double Scaling=Math.sqrt(Math.pow(ScaleX*1.4,2.0+Math.pow(ScaleY*1.4,2.0)));
        float scaling=(float)Scaling;
        return 1;//ScaleX;
    }
}
