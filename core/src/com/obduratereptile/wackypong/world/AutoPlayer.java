package com.obduratereptile.wackypong.world;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.obduratereptile.wackypong.WackyPong;

import java.util.Iterator;

/**
 * Difficulty Levels are:
 *   0 = perfect, almost unbeatable player
 *   1 = occasionally makes subtle errors
 *   2 = often makes subtle errors
 *   3 = makes fairly obvious errors
 * Created by Marc on 9/4/2016.
 */
public class AutoPlayer extends Actor {
    public int difficultyLevel;
    public int player;
    private boolean firing = false;
    public World world;

    public AutoPlayer(int p, int d, World w) {
        super();
        setVisible(false);

        player = p;
        difficultyLevel = d;
        world = w;
    }

    public AutoPlayer(int p, World w) {
        this(p, 0, w);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        movePaddle();
        if (myTurnToLaunch()) fireCannon();
    }

    private void movePaddle() {
        switch (difficultyLevel) {
            default: // (case 0) perfect mode
                Ball b = findNearestBall();
                if (b == null) return;

                Vector3 target = new Vector3(0, b.getY(), 0);
                world.paddle[player].moveTo(target);
                break;
        }
    }

    private Ball findNearestBall() {
        Ball nearest = null;
        float time = 1000000;

        Iterator<Ball> b = world.ball.iterator();
        while (b.hasNext()) {
            Ball bb = b.next();
            // ignore balls that are moving away from us
            if (player==0)
                if (bb.velocity.x>=0) continue;
            else
                if (bb.velocity.x<=0) continue;

            // predict which ball will arrive first
            if (nearest==null) {
                nearest = bb;
                time = arrivalTime(bb);
            } else {
                float bbTime = arrivalTime(bb);
                if (bbTime < time) {
                    nearest = bb;
                    time = bbTime;
                }
            }
        }
        return nearest;
    }

    private float arrivalTime(Ball b) {
        float x = (player==1)? WackyPong.SCREENSIZEX - b.getX(): getX();
        float v = (player==1)? b.velocity.x: -b.velocity.x;

        return x/v;
    }

    private boolean myTurnToLaunch() {
        if (firing) return false;
        return (world.cannon.player == player);
    }

    private void fireCannon() {
        if (firing) return;
        firing = true;

        Thread thread = new Thread(new Runnable() {
            public void run() {
            long time = (long)(1000 * Math.random()); //random time between 0 and 1 second
            try {
                Thread.sleep(1000+time);
            } catch (InterruptedException e) {
            }
            world.cannon.fire();
            firing = false;
            }
        });
        thread.start();
    }

}
