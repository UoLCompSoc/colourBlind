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
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;

/**
 * Holds the player.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class Player {
	enum PlayerState {
		STANDING, RUNNING, JUMPING
	}

	public static final float	JUMP_VELOCITY			= 0.70f;
	public static final float	RUN_VELOCITY			= 0.25f;
	public static final float	GRAVITY					= -0.05f;

	private Texture				texture					= null;
	private int					PLAYER_TEXTURE_WIDTH	= 64;
	private int					PLAYER_TEXTURE_HEIGHT	= 128;

	public static final Vector2	INITIAL_POSITION		= new Vector2(3, 2);

	private float				PLAYER_WIDTH			= 0;
	private float				PLAYER_HEIGHT			= 0;

	private PlayerState			state					= PlayerState.STANDING;
	private float				stateTime				= 0.0f;

	public Vector2				position				= new Vector2();
	public Vector2				velocity				= new Vector2();

	public Animation			stand					= null;
	public Animation			run						= null;
	public Animation			jump					= null;

	private boolean				facingLeft				= false;
	private boolean				isGrounded				= true;

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
		run = new Animation(0.2f, regions[1], regions[2]);
		run.setPlayMode(Animation.LOOP_PINGPONG);
		facingLeft = false;

		PLAYER_WIDTH = (PLAYER_TEXTURE_WIDTH * (1.0f / 64.0f));
		PLAYER_HEIGHT = (PLAYER_TEXTURE_HEIGHT * (1.0f / 64.0f));

		position.set(Player.INITIAL_POSITION);
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

		stateTime += deltaTime;

		if (Gdx.app.getLogLevel() == Application.LOG_DEBUG) {
			if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
				position.set(Player.INITIAL_POSITION);
				velocity.set(0.0f, 0.0f);
				return;
			}

			if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
				Gdx.app.debug("PLAYER_COORDS", "(X,Y)=(" + position.x + ", "
						+ position.y + ")");
			}
		}

		if (Gdx.input.isKeyPressed(Keys.SPACE)) {
			if (isGrounded) {
				velocity.y += JUMP_VELOCITY;
				setState(PlayerState.JUMPING);
				isGrounded = false;
			}
		}

		if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) {
			velocity.x = -RUN_VELOCITY;

			if (isGrounded) {
				setState(PlayerState.RUNNING);
			}

			facingLeft = true;
		}

		if (Gdx.input.isKeyPressed(Keys.RIGHT)
				|| Gdx.input.isKeyPressed(Keys.D)) {
			velocity.x = RUN_VELOCITY;

			if (isGrounded) {
				setState(PlayerState.RUNNING);
			}

			facingLeft = false;
		}

		velocity.y += GRAVITY;

		if (Math.abs(velocity.x) < 0.1f) {
			velocity.x = 0.0f;

			if (isGrounded) {
				setState(PlayerState.STANDING);
			}
		}

		Vector2 ptCoords = new Vector2(position);
		ptCoords.add(velocity);

		if (ptCoords.y < 0) {
			ptCoords.y = 0;
		}

		if (ptCoords.x < 0) {
			ptCoords.x = 0;
		}

		TiledMapTileLayer layer = (TiledMapTileLayer) level.renderer.getMap()
				.getLayers().get("level");

		if (velocity.y > 0.0f) {
			ptCoords.y += PLAYER_HEIGHT;
		}

		if (velocity.x > 0.0f) {
			ptCoords.x += PLAYER_WIDTH;
		}

		// check for y collisions
		Cell cell = layer.getCell((int) position.x, (int) ptCoords.y);

		if (cell != null) {
			// there's something there
			if (velocity.y < 0.0f) {
				isGrounded = true;
			}

			velocity.y = 0;
		}

		cell = layer.getCell((int) ptCoords.x, (int) position.y);

		if (cell != null) {
			velocity.x = 0;
		}

		position.add(velocity);
		velocity.x *= (RUN_VELOCITY / 10);
	}

	public void render(Level level) {
		SpriteBatch batch = level.renderer.getSpriteBatch();
		batch.begin();
		Color startColor = batch.getColor();
		// batch.setBlendFunction(GL11.GL_SRC_COLOR, GL11.GL_ONE);
		// batch.setColor(Color.RED);
		PlayerState currState = getState();
		TextureRegion frame = null;

		if (currState == PlayerState.JUMPING) {
			frame = jump.getKeyFrame(stateTime);
		} else if (currState == PlayerState.RUNNING) {
			frame = run.getKeyFrame(stateTime);
		} else if (currState == PlayerState.STANDING) {
			frame = stand.getKeyFrame(stateTime);
		}

		if (!facingLeft) {
			batch.draw(frame, position.x, position.y, (float) PLAYER_WIDTH,
					(float) PLAYER_HEIGHT);

		} else {
			batch.draw(frame, position.x + PLAYER_WIDTH, position.y,
					(float) -PLAYER_WIDTH, (float) PLAYER_HEIGHT);

		}

		batch.setColor(startColor);
		batch.end();
	}

	public void dispose() {
		texture.dispose();
	}

	public void setState(PlayerState ns) {
		state = ns;
	}

	public PlayerState getState() {
		return state;
	}

	public float getX() {
		return position.x;
	}

	public float getY() {
		return position.y;
	}
}
