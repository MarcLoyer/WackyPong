package com.obduratereptile.wackypong.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

/**
 * Created by Marc on 9/11/2016.
 */
public class CollisionAnimation extends Actor {
    public final float SCALERATE = 20;
    public final float ALPHADROP = 1.5f;

    World world;
    public float scale = 1;
    public float elapsedTime = 0;
    Sprite img;

    public CollisionAnimation(World w, float x, float y, float radius) {
        world = w;

        img = world.game.atlas.createSprite("collision");
        img.setSize(radius*2, radius*2);
        img.setOrigin(radius, radius);
        img.setPosition(x-radius, y-radius);
        img.setScale(1.0f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        elapsedTime += delta;
        scale = 1.0f + (elapsedTime * SCALERATE);
        if (scale > 40.0f) {
            remove();
        }
        img.setScale(scale);

/*        if (elapsedTime>ALPHADROP) {
            float a = 1.0f - (elapsedTime - ALPHADROP);
            if (a < 0) a = 0;
            img.setAlpha(a);
        }*/
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        img.draw(batch, parentAlpha);
    }
}
