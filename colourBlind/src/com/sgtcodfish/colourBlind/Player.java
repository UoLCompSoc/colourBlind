package com.sgtcodfish.colourBlind;

import com.badlogic.gdx.Application;
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

	public static final float	JUMP_VELOCITY			= 1.0f;
	public static final float	RUN_VELOCITY			= 1.0f;
	public static final float	GRAVITY					= -2.0f;

	private Texture				texture					= null;
	private int					PLAYER_TEXTURE_WIDTH	= 64;
	private int					PLAYER_TEXTURE_HEIGHT	= 128;

	public static final Vector2	INITIAL_POSITION		= new Vector2(3, 1);

	private float				PLAYER_WIDTH			= 0;
	private float				PLAYER_HEIGHT			= 0;

	private PlayerState			state					= PlayerState.STANDING;

	public Vector2				position				= new Vector2();
	public Vector2				velocity				= new Vector2();

	public Animation			stand					= null;
	public Animation			run						= null;
	public Animation			jump					= null;

	private boolean				facingLeft				= false;

	public Player() {
		FileHandle playerImage = Gdx.files
				.internal("data/RaySprites/RaySprites.png");
		Gdx.app.debug("PLAYER_LOAD",
				"Player image exists = " + playerImage.exists());
		texture = new Texture(playerImage);

		TextureRegion[] regions = TextureRegion.split(texture,
				PLAYER_TEXTURE_WIDTH, PLAYER_TEXTURE_HEIGHT)[0];
		jump = new Animation(0, regions[3]);
		stand = new Animation(0, regions[0]);
		run = new Animation(0.2f, regions[0], regions[1], regions[2]);
		facingLeft = false;

		PLAYER_WIDTH = (PLAYER_TEXTURE_WIDTH * (1.0f / 32.0f));
		PLAYER_HEIGHT = (PLAYER_TEXTURE_HEIGHT * (1.0f / 32.0f));

		position.set(Player.INITIAL_POSITION);
	}

	/**
	 * Called each frame to update the position and state of the player
	 * 
	 * @param deltaTime
	 *            The amount of time since the last frame
	 */
	public void update(Level level, float deltaTime) {
		if (Gdx.app.getLogLevel() == Application.LOG_DEBUG) {
			if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
				position.set(Player.INITIAL_POSITION);
				return;
			}
		}

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
		}
		// else {
		// // we must be jumping so set to stand if we collide with floor
		// if (isCollidingY(level)) {
		// if (velocity.x > 0.0f || velocity.y < 0.0f) {
		// setState(PlayerState.RUNNING);
		// } else {
		// setState(PlayerState.STANDING);
		// }
		// }
		// }

		if (getState() != PlayerState.JUMPING) {
			if (Gdx.input.isKeyPressed(Keys.SPACE)) {
				velocity.y = JUMP_VELOCITY;
				setState(PlayerState.JUMPING);
			}
		}

		position.add(velocity);
		velocity.y += GRAVITY;

		if (isCollidingY(level)) {
			// if we're colliding and we're travelling down, change state
			if (velocity.y < 0.0f) {
				if (velocity.x > 0.0f || velocity.x < 0.0f) {
					setState(PlayerState.RUNNING);
				} else {
					setState(PlayerState.STANDING);
				}
			}

			// whatever direction we're travelling, we want to set velocity to 0
			velocity.y = 0.0f;
		}

		if (isCollidingX(level)) {
			if (velocity.x < 0) {
				// travelling left
				velocity.x = 0;
			} else {
				// travelling right
				velocity.x = 0;
			}
		}

		if (position.y < 1.0f) {
			position.y = 1.0f;
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
			batch.draw(jump.getKeyFrame(0), position.x, position.y,
					(float) PLAYER_WIDTH, (float) PLAYER_HEIGHT);
		} else if (currState == PlayerState.RUNNING) {
			// TODO: FIXME for running animation
			batch.draw(stand.getKeyFrame(0), position.x, position.y,
					(float) PLAYER_WIDTH, (float) PLAYER_HEIGHT);
		} else if (currState == PlayerState.STANDING) {
			if (!facingLeft) {
				batch.draw(stand.getKeyFrame(0), position.x, position.y,
						(float) -PLAYER_WIDTH, (float) PLAYER_HEIGHT);
			} else {
				batch.draw(stand.getKeyFrame(0), position.x, position.y,
						(float) PLAYER_WIDTH, (float) PLAYER_HEIGHT);
			}
		}

		batch.setColor(startColor);
		batch.end();
	}

	public void dispose() {
		texture.dispose();
	}

	public boolean isCollidingX(Level level) {
		Rectangle playerRect = new Rectangle();
		playerRect.set(position.x, position.y, PLAYER_WIDTH, PLAYER_HEIGHT);

		int startXTile = 0, startYTile = 0, endXTile = 0, endYTile = 0;

		startYTile = (int) position.y;
		endYTile = (int) (position.y + velocity.y);

		if (velocity.x > 0.0f) {
			// are we colliding right?
			startXTile = endXTile = (int) (position.x + PLAYER_WIDTH + velocity.x);
		} else {
			startXTile = endXTile = (int) (position.x + velocity.x);
		}

		Array<Rectangle> tiles = level.getTiles(startXTile, startYTile,
				endXTile, endYTile);

		playerRect.x += velocity.x;

		if (tiles != null) {
			Gdx.app.debug("COLLISON_Y", "Y tiles found, size = " + tiles.size);
			for (Rectangle tile : tiles) {
				if (playerRect.overlaps(tile)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isCollidingY(Level level) {
		Rectangle playerRect = new Rectangle();
		playerRect.set(position.x, position.y, PLAYER_WIDTH, PLAYER_HEIGHT);

		int startXTile = 0, startYTile = 0, endXTile = 0, endYTile = 0;

		startXTile = (int) position.x;
		endXTile = (int) (position.x + velocity.x);

		if (velocity.y > 0.0f) {
			// are we colliding above
			startYTile = endYTile = (int) (position.y + PLAYER_TEXTURE_HEIGHT + velocity.y);
		} else {
			startYTile = endYTile = (int) (position.y + velocity.y);
		}

		Array<Rectangle> tiles = level.getTiles(startXTile, startYTile,
				endXTile, endYTile);

		playerRect.y += velocity.y;
		if (tiles != null) {
			Gdx.app.debug("COLLISON_Y", "Y tiles found, size = " + tiles.size);

			for (Rectangle tile : tiles) {
				if (playerRect.overlaps(tile)) {
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
