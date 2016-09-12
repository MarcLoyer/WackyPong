package com.obduratereptile.wackypong.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Marc on 9/11/2016.
 */
public class CollisionAnimation extends Actor {
    public final float SCALERATE = 20;

    World world;
    public float scale = 1;
    public float elapsedTime = 0;
    Sprite img;

    public CollisionAnimation(World w, float x, float y, float radius) {
        world = w;

        img = world.game.atlas.createSprite("collision");
        setSize(radius*2, radius*2);
        setOrigin(radius, radius);
        setPosition(x-radius, y-radius);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        elapsedTime += Gdx.graphics.getDeltaTime();
        scale = 1.0f + (elapsedTime * SCALERATE);
        if (scale > 40.0f) {
            remove();
        }
        setScale(scale);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(img,
                getX(), getY(),
                getOriginX(), getOriginY(),
                getWidth(), getHeight(),
                getScaleX(), getScaleY(),
                0.0f);
    }
}
