package com.sgtcodfish.colourBlind;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Holds the player.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class Player {
	enum PlayerState {
		STANDING, RUNNING, JUMPING
	}

	public static final float	JUMP_VELOCITY			= 40.0f;
	public static final float	RUN_VELOCITY			= 5.0f;
	public static final float	GRAVITY					= -4.0f;

	private Texture				texture					= null;
	private int					PLAYER_TEXTURE_WIDTH	= 64;
	private int					PLAYER_TEXTURE_HEIGHT	= 128;

	private PlayerState			state					= PlayerState.STANDING;

	public Vector2				position				= new Vector2();
	public Vector2				velocity				= new Vector2();

	public Animation			stand					= null;
	public Animation			run						= null;
	public Animation			jump					= null;

	private boolean				facingLeft				= false;

	public Player() {
		FileHandle playerImage = Gdx.files.internal("data/player.png");
		Gdx.app.debug("PLAYER_LOAD",
				"Player image exists = " + playerImage.exists());
		texture = new Texture(playerImage);

		TextureRegion[] regions = TextureRegion.split(texture,
				PLAYER_TEXTURE_WIDTH, PLAYER_TEXTURE_HEIGHT)[0];
		jump = new Animation(0, regions[0]);
		stand = new Animation(0, regions[1]);
		facingLeft = false;
	}

	/**
	 * Called each frame to update the position and state of the player
	 * 
	 * @param deltaTime
	 *            The amount of time since the last frame
	 */
	public void update(Level level, float deltaTime) {
		if (deltaTime == 0.0f) {
			return;
		}

		if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) {
			velocity.x = -RUN_VELOCITY;

			if (getState() != PlayerState.JUMPING) {
				setState(PlayerState.RUNNING);
			}

			facingLeft = true;
		} else if (Gdx.input.isKeyPressed(Keys.D)
				|| Gdx.input.isKeyPressed(Keys.RIGHT)) {
			velocity.x = RUN_VELOCITY;

			if (getState() != PlayerState.JUMPING) {
				setState(PlayerState.RUNNING);
			}

			facingLeft = false;
		} else if (getState() != PlayerState.JUMPING) {
			setState(PlayerState.STANDING);
			velocity.x = 0.0f;
		} else {
			// we must be jumping so set to stand if we collide with floor
			if (isCollidingY(level)) {
				if (velocity.x > 0.0f || velocity.y < 0.0f) {
					setState(PlayerState.RUNNING);
				} else {
					setState(PlayerState.STANDING);
				}
			}
		}

		if (getState() != PlayerState.JUMPING) {
			if (Gdx.input.isKeyPressed(Keys.SPACE)) {
				velocity.y = JUMP_VELOCITY;
				setState(PlayerState.JUMPING);
			}
		}

		position.add(velocity);
		velocity.y += GRAVITY;

		if (isCollidingY(level)) {
			if (velocity.x > 0.0f || velocity.x < 0.0f) {
				setState(PlayerState.RUNNING);
			} else {
				setState(PlayerState.STANDING);
			}

			velocity.y = 0.0f;
		}

		if (position.x > (Gdx.graphics.getWidth() - 64)) {
			position.x = Gdx.graphics.getWidth() - 64; // DIRTY HACK
														// !!!!!!!!!!!!!!!!
		} else if (position.x < 0) {
			position.x = 0; // DIRTY HACK !!!!!!!!!!!!!!!!!!!!!!
		}
	}

	public void render(Level level) {
		SpriteBatch batch = level.renderer.getSpriteBatch();
		batch.begin();
		Color startColor = batch.getColor();
		// batch.setBlendFunction(GL11.GL_SRC_COLOR, GL11.GL_ONE);
		// batch.setColor(Color.RED);
		PlayerState currState = getState();

		if (currState == PlayerState.JUMPING) {
			// TODO: FIXME for jumping animation
			batch.draw(jump.getKeyFrame(0), position.x, position.y,
					(float) PLAYER_TEXTURE_WIDTH, (float) PLAYER_TEXTURE_HEIGHT);
		} else if (currState == PlayerState.RUNNING) {
			// TODO: FIXME for running animation
			batch.draw(stand.getKeyFrame(0), position.x, position.y,
					(float) PLAYER_TEXTURE_WIDTH, (float) PLAYER_TEXTURE_HEIGHT);
		} else if (currState == PlayerState.STANDING) {
			if (!facingLeft) {
				batch.draw(stand.getKeyFrame(0), position.x, position.y,
						(float) PLAYER_TEXTURE_WIDTH,
						(float) PLAYER_TEXTURE_HEIGHT);
			} else {
				batch.draw(stand.getKeyFrame(0), position.x, position.y,
						(float) PLAYER_TEXTURE_WIDTH,
						(float) PLAYER_TEXTURE_HEIGHT);
			}
		}

		batch.setColor(startColor);
		batch.end();
	}

	public void dispose() {
		texture.dispose();
	}

	public boolean isCollidingY(Level level) {
		Rectangle playerRect = new Rectangle();
		playerRect.set(position.x, position.y, (float) PLAYER_TEXTURE_WIDTH,
				(float) PLAYER_TEXTURE_HEIGHT);

		int startXTile = 0, startYTile = 0, endXTile = 0, endYTile = 0;

		if (velocity.y > 0.0f) {
			// are we colliding above
			startYTile = endYTile = (int) (position.y + PLAYER_TEXTURE_HEIGHT + velocity.y);
		} else {
			startYTile = endYTile = (int) (position.y + velocity.y);
		}

		Array<Rectangle> tiles = level.getTiles(startXTile, startYTile,
				endXTile, endYTile);

		for (Rectangle tile : tiles) {
			if (playerRect.overlaps(tile)) {
				if (velocity.y > 0) {
					return true;
				} else {
					return true;
				}
			}
		}

		return false;
	}

	public void setState(PlayerState ns) {
		state = ns;
	}

	public PlayerState getState() {
		return state;
	}
}
