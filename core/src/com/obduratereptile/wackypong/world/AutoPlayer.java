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
 *     ball scores; ball reverses direction. Another ball collision could make it arrive earlier. <--- didn't make a difference
 *   * Predict the y location of an incoming ball, and don't update until the ball gets very close.
 *     Thus, interactions with the field won't be tracked.
 *   * Build in a lag before the AI responds? Only update the paddle target every 0.5s or so?
 *   * ...
 *
 *   Finally, I should slow the paddles down further, to make some targets out of reach.
*/
public class AutoPlayer extends Actor {
    public int difficultyLevel;
    public int player;
    private boolean firing = false;
    public World world;

    private Ball trackedBall;

    private long lagTime = 100L;
    private Vector3 delayedTarget = new Vector3();
    private Runnable runnable = new Runnable() {
        public void run() {
            try {
                Thread.sleep(lagTime);
            } catch (InterruptedException e) {
            }
            world.paddle[player].moveTo(delayedTarget);
        }
    };
    private Thread thread = null;

    public AutoPlayer(int p, int d, World w) {
        super();
        setVisible(false);

        player = p;
        difficultyLevel = d;
        world = w;

        trackedBall = null;

        if (difficultyLevel == 3) {
            lagTime = 200L;
            world.paddle[player].setSpeed(80);
        }
        if (difficultyLevel == 2) {
            lagTime = 100L;
            world.paddle[player].setSpeed(100);
        }
        if (difficultyLevel == 1) {
            lagTime = 50L;
            world.paddle[player].setSpeed(100);
        }
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

    public void restart() {
        trackedBall = null;
    }

    private void movePaddle() {
        Ball b = null;

        switch (difficultyLevel) {
            case 0: //  perfect mode - no lag at all
                b = findNearestBall();
                if (b != null) delayedTarget.y = b.getY();
                world.paddle[player].moveTo(delayedTarget);
                break;
            default:
                if (thread != null) {
                    if (thread.isAlive()) return;
                }
                thread = new Thread(runnable);

                findNearestBall();
                thread.start();
                break;
        }
    }

    private boolean isIncoming(Ball b) {
        if (player==0) {
            if (b.getX() > WackyPong.SCREENSIZEX/2) return false;
            return (b.velocity.x < 0);
        } else {
            if (b.getX() < WackyPong.SCREENSIZEX/2) return false;
            return (b.velocity.x > 0);
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
            // ignore balls that are on the other side of the field
            if (player==0) {
                if (bb.getX() > WackyPong.SCREENSIZEX/2) continue;
            } else {
                if (bb.getX() < WackyPong.SCREENSIZEX/2) continue;
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
        if (nearest==null)
            delayedTarget.y = WackyPong.SCREENSIZEY/2;
        else
            delayedTarget.y = predictY(nearest, time);
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

    private float predictY(Ball b, float time) {
        float py = b.getY();
        float vy = b.velocity.y;

        // I don't take the edges of the field into account. Oh well, should be good enough.
        return py + vy*time;
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
