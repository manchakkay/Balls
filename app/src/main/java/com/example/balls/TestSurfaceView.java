package com.example.balls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

class Ball {
    int x, y, dx, dy, maxSpeed, radius;
    Paint p;
    Random r;
    ArrayList<Integer> colors;

    Ball(Random r_in, int radius, int maxSpeed, ArrayList<Integer> colors) {
        this.maxSpeed = maxSpeed;
        this.radius = radius;
        this.colors = colors;
        r = r_in;
        p = new Paint();

        nextColor();
        x = 100 + r.nextInt(400);
        y = 100 + r.nextInt(400);

        dx = (r.nextInt(maxSpeed) *r.nextInt());
        dy = (r.nextInt(maxSpeed) *r.nextInt());
    }

    public void nextColor() {
        p.setColor(colors.get(r.nextInt(colors.size())));
    }

    public void update(Rect rectCursor, Canvas c) {
        x += dx;
        y += dy;

        if (x >= c.getWidth() - radius) {
            dx *= -1;
        } else if (x < radius) {
            dx *= -1;
        } else if (y >= c.getHeight() - radius) {
            dy *= -1;
        } else if (y < radius) {
            dy *= -1;
        } else if ((x >= rectCursor.left && x <= rectCursor.right) && (y >= rectCursor.top && y <= rectCursor.bottom)) {
            dx *= -1;
            dy *= -1;

            nextColor();
        }
    }
}

public class TestSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    DrawThread thread;
    Random r;
    int ballsCount = 5;

    ArrayList<Ball> ballsList = new ArrayList<>();

    Rect rectCursor = new Rect();
    Paint rectColor = new Paint();

    int radius = 50;
    int maxSpeed = 50;

    int rect_l, rect_t, rect_r, rect_b;
    boolean RectIsActive = false;

    ArrayList<Integer> colors = new ArrayList<>(Arrays.asList(
            R.color.clr_0, R.color.clr_1, R.color.clr_2, R.color.clr_3,
            R.color.clr_4, R.color.clr_5, R.color.clr_6, R.color.clr_7
    ));

    class DrawThread extends Thread {



        boolean isRunning = true;



        public DrawThread(SurfaceHolder holder) {


            this.holder = holder;

        }

        SurfaceHolder holder;


        @Override
        public void run() {
            super.run();

            Log.d("run", "HERE");

            rect_l = r.nextInt(holder.getSurfaceFrame().right);
            rect_t = r.nextInt(holder.getSurfaceFrame().bottom);
            rect_r = r.nextInt(holder.getSurfaceFrame().right);
            rect_b = r.nextInt(holder.getSurfaceFrame().bottom);

            rectColor.setColor(getResources().getColor(R.color.darkgray));

            while (isRunning) {
                Canvas canvas = holder.lockCanvas();
                rectCursor.set(rect_l, rect_t, rect_r, rect_b);

                if (canvas != null) {
                    canvas.drawColor(getResources().getColor(R.color.white));

                    int same_color = ballsList.get(0).p.getColor();

                    for (Ball bA : ballsList) {
                        canvas.drawCircle(bA.x, bA.y, radius, bA.p );
                        Log.d("fuck", bA.x + " : " + bA.y + " - " + radius + ", " + bA.p.toString());

                        bA.update(rectCursor, canvas);
                        if (bA.p.getColor() != same_color) {
                            same_color = -1;
                        }
                        for (Ball bB : ballsList) {
                            if (bA != bB) {
                                if (Math.sqrt(Math.pow(bB.x - bA.x, 2) + Math.pow(bB.y - bA.y, 2)) <= 0) {
                                    bA.dx *= -1;
                                    bA.dy *= -1;
                                    bB.dx *= -1;
                                    bB.dy *= -1;
                                    bB.nextColor();
                                }
                            }
                        }
                    }

                    canvas.drawRect(rectCursor, rectColor);


                    if (same_color != -1) {
                        Paint pw = new Paint();
                        pw.setColor(getResources().getColor(R.color.white));
                        pw.setTextSize(70);
                        canvas.drawText("You Won!", 400, 200, pw);
                        isRunning = false;
                    }
                }

                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                RectIsActive = true;
                return true;

            case MotionEvent.ACTION_MOVE:
                if (RectIsActive) {
                    rect_l = (int) event.getX() - rectCursor.width() / 2;
                    rect_r = (int) event.getX() + rectCursor.width() / 2;
                    rect_t = (int) event.getY() - rectCursor.height() / 2;
                    rect_b = (int) event.getY() + rectCursor.height() / 2;
                }
                return true;

            case MotionEvent.ACTION_UP:
                RectIsActive = false;
                return true;
        }
        return true;
    }

    public TestSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);

        r = new Random(System.currentTimeMillis());
        for (int i = 0; i < ballsCount; i++) {
            Ball b = new Ball(r, radius, maxSpeed, colors);
            Log.d("ryik", String.valueOf(b.p.getColor()));
            ballsList.add(b);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        thread = new DrawThread(surfaceHolder);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        thread.isRunning = false;
        thread = new DrawThread(surfaceHolder);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        thread.isRunning = false;
    }


}