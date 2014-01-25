package com.sgtcodfish.colourBlind;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Holds the player.
 * @author Ashley Davis (SgtCoDFish)
 */
public class Player {
	enum PlayerState {
		STANDING,
		RUNNING,
		JUMPING
	}
	
	public static final float JUMP_VELOCITY = 40.0f;
	public static final float RUN_VELOCITY = 5.0f;
	public static final float GRAVITY = -5.0f;
	
	private Texture texture = null;
	private PlayerState state = PlayerState.STANDING;
	
	public Vector2 position = new Vector2();
	public Vector2 velocity = new Vector2();
	
	public Animation stand = null;
	
	public Player() {
		FileHandle playerImage = Gdx.files.internal("data/player.png");
		Gdx.app.debug("PLAYER_LOAD", "Player image exists = " + playerImage.exists());
		texture = new Texture(playerImage);

		TextureRegion[] regions = TextureRegion.split(texture, 64, 128)[0];
		stand = new Animation(0, regions[0]);
	}
	
	/**
	 * Called each frame to update the position and state of the player
	 * @param deltaTime The amount of time since the last frame
	 */
	public void update(float deltaTime) {
		if(deltaTime == 0.0f) {
			return;
		}
		
		if(Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) {
			velocity.x = -RUN_VELOCITY;
		} else if(Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
			velocity.x = RUN_VELOCITY;
		} else {
			velocity.x = 0;
		}
		
		if(Gdx.input.isKeyPressed(Keys.SPACE)) {
			if(position.y < 0.1f) {
				velocity.y = JUMP_VELOCITY;
			}
		}
		
		position.add(velocity);
		velocity.y += GRAVITY;
		if(position.y <= 0.0f) {
			velocity.y = 0.0f;
			position.y = 0.0f;
		}
		
		if(position.x > (Gdx.graphics.getWidth() - 64)) {
			position.x = Gdx.graphics.getWidth() - 64; // DIRTY HACK !!!!!!!!!!!!!!!!
		} else if(position.x < 0) {
			position.x = 0; // DIRTY HACK !!!!!!!!!!!!!!!!!!!!!!
		}
	}
	
	public void render(Level level) {
		SpriteBatch batch = level.renderer.getSpriteBatch();
		batch.begin();
		
		batch.draw(stand.getKeyFrame(0), position.x, position.y, 64, 128);
		
		batch.end();
	}
	
	public void dispose() {
		texture.dispose();
	}
	
	/**
	 * Called when the player should jump
	 */
	public void jump() {
		
	}
	
	public void setState(PlayerState ns) {
		state = ns;
	}
	
	public PlayerState getState() {
		return state;
	}
}
