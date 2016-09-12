package com.obduratereptile.wackypong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.obduratereptile.wackypong.world.Ball;

/**
 * Created by Marc on 9/11/2016.
 */
public class TitleLetter extends Actor {
    static final public float HOVERSPEED = 5;

    public String letter;
    public Array<Sprite> img;
    public Animation ani = null;
    public boolean isAnimating;
    public boolean isWobbling;
    private float elapsedTimeHover = 0;
    private float elapsedTimeWobble = 0;
    private float elapsedTimeAni = 0;
    private float baselineX;
    private float baselineY;
    private float baselineRot;
    private float targetX;
    private float targetY;

    private Circle bounds;
    private Circle boundsAni;

    public TitleLetter(WackyPong game, String letter) {
        super();
        img = game.atlas.createSprites("letter"+letter);
        if (img.size > 1) {
            ani = new Animation(0.1f, img, Animation.PlayMode.NORMAL);
        }
        isAnimating = false;
        isWobbling = false;
        this.letter = letter;
        bounds = new Circle();
        boundsAni = new Circle();

        setSize(img.get(0).getWidth(), img.get(0).getHeight());
        setOrigin(getWidth()/2, getHeight()/2);
        switch (letter.charAt(0)) { // lucky that none of the letters repeated :)
            case 'W': setPosition(105, 252); setRotation( 30); setBounds(85); break;
            case 'A': setPosition(188, 287); setRotation( 18); setBounds(65); break;
            case 'C': setPosition(240, 303); setRotation( 10); setBounds(60); break;
            case 'K': setPosition(302, 304); setRotation(  6); setBounds(80); break;
            case 'Y': setPosition(370, 288); setRotation(  0); setBounds(75); break;

            case 'P': setPosition(465, 304); setRotation( -8); setBounds(75); break;
            case 'O': setPosition(521, 313); setRotation(-16); setBounds(46); break;
            case 'N': setPosition(580, 266); setRotation(-20); setBounds(75); break;
            case 'G': setPosition(636, 226); setRotation(-25); setBounds(70); break;
            default: setPosition(0,0); setRotation(0); setBounds(0); break;
        }

        baselineX = getX();
        baselineY = getY();
        baselineRot = getRotation();
        targetX = baselineX;
        targetY = baselineY;
    }

    public void setBounds(float radius) {
        bounds.set(getX()+getOriginX(), getY()+getOriginY(), radius);
    }

    public float getRadius() {
        return bounds.radius;
    }

    public void setBoundsAni(float x, float y, float radius) {
        boundsAni.set(x, y, radius);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        hover(delta);
    }

    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        TextureRegion tex;

        if (isAnimating) {
            elapsedTimeAni += Gdx.graphics.getDeltaTime();
            tex = ani.getKeyFrame(elapsedTimeAni);
        } else {
            tex = img.get(0);
        }
        batch.draw(tex,
                getX(), getY(),
                getOriginX(), getOriginY(),
                getWidth(), getHeight(),
                getScaleX(), getScaleY(),
                getRotation());
    }

    /*
     * Returns true if the ball has collided with the letter. The ball is updated so that it
     * is not collided and is moving away. The letter wobbles after the collision. If the
     * letter has an animation, a special area is checked, and if the ball is there, the animation
     * is played instead.
     */
    public boolean collision(Ball ball) {
        if (!bounds.overlaps(ball.bounds)) return false;

        bounce(ball);
        wobble();


        //TODO: special animation checking
        return true;
    }

    protected void bounce(Ball ball) {
        // compute unit normal and tangential vectors based on the collision
        Vector2 unitNormal = new Vector2(ball.bounds.x - bounds.x, ball.bounds.y - bounds.y);
        unitNormal.setLength(1);
        Vector2 normal = new Vector2(unitNormal); // need this below...
        Vector2 unitTangent = new Vector2(-unitNormal.y, unitNormal.x);

        // decompose the pre-collision velocity into the unit vectors
        float vN = unitNormal.dot(ball.velocity);
        float vT = unitTangent.dot(ball.velocity);

        // the tangential velocity doesn't change
        // the normal velocity is reversed
        ball.velocity.set(unitNormal.scl(-vN).add(unitTangent.scl(vT)));

        // move the ball off the hazard to prevent collision captures
        normal.scl(ball.bounds.radius + getRadius()).add(bounds.x, bounds.y);
        ball.bounds.x = normal.x;
        ball.bounds.y = normal.y;
    }

    /*
     * Causes the letter to float about a little bit
     */
    public void hover(float delta) {
        final float RANGE = 5;
        float x = getX();
        float y = getY();
        float step = delta * HOVERSPEED;

        // if we've reached the target, pick a new one
        if (x==targetX) targetX = baselineX - RANGE + (float)Math.random()*2*RANGE;
        if (y==targetY) targetY = baselineY - RANGE + (float)Math.random()*2*RANGE;

        // move towards the target
        if (x<targetX) {
            if ((targetX-x)<step) x = targetX;
            else x += step;
        } else {
            if ((x-targetX)<step) x = targetX;
            else x -= step;
        }
        if (y<targetY) {
            if ((targetY-y)<step) y = targetY;
            else y += step;
        } else {
            if ((y-targetY)<step) y = targetY;
            else y -= step;
        }
        setPosition(x, y);
    }

    /*
     * Makes the letter wobble, that is, a small damped rotational oscillation
     */
    public void wobble() {
        final float angle = 30;
        final float speed = 0.1f;

        addAction(Actions.sequence(
                Actions.rotateBy(angle, speed, Interpolation.circle),
                Actions.rotateBy(-2*angle, 2*speed, Interpolation.circle),
                Actions.rotateBy(1.75f*angle, 2*speed, Interpolation.circle),
                Actions.rotateBy(-1.5f*angle, 2*speed, Interpolation.circle),
                Actions.rotateBy(1.25f*angle, 2*speed, Interpolation.circle),
                Actions.rotateBy(-angle, 2*speed, Interpolation.circle),
                Actions.rotateBy(0.75f*angle, 2*speed, Interpolation.circle),
                Actions.rotateBy(-0.5f*angle, 2*speed, Interpolation.circle),
                Actions.rotateBy(0.25f*angle, 2*speed, Interpolation.circle),
                Actions.run(new Runnable() {
                    public void run() { isWobbling = false; }
                })
        ));
    }
}
