package com.obduratereptile.wackypong.world;

import com.badlogic.gdx.Gdx;
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
/* some ideas for errors:
 *   * Pick a ball to track, and don't change until one of these things happen: ball hits paddle;
 *     ball scores; ball reverses direction. A glancing blow from a hazard or another ball could
 *     slow the ball, making another ball arrive earlier.
 *   * Predict the y location of an incoming ball, and don't update until the ball gets very close.
 *     Thus, interactions with the field won't be tracked.
 *   * ...
 *
 *   Finally, I should slow the paddles down further, to make some targets out of reach.
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
            if (player==0) {
                if (bb.velocity.x >= 0) continue;
            } else {
                if (bb.velocity.x <= 0) continue;
            }

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
        float x;
        float v;

        if (player == 1) {
            x = (WackyPong.SCREENSIZEX - b.getX());
            v = b.velocity.x;
        } else {
            x = b.getX();
            v = -b.velocity.x;
        }

        return x/v;
    }

    private boolean myTurnToLaunch() {
        if (world.cannon.isHidden) return false;
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
