package com.sgtcodfish.colourBlind;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
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

	public static final float	JUMP_VELOCITY					= 0.70f;
	public static final float	RUN_VELOCITY					= 0.25f;
	public static final float	GRAVITY							= -0.05f;
	public static final float	TERMINAL_VELOCITY				= JUMP_VELOCITY;

	private Texture				texture							= null;
	private int					PLAYER_TEXTURE_WIDTH			= 64;
	private int					PLAYER_TEXTURE_HEIGHT			= 128;

	public static final Vector2	INITIAL_POSITION				= new Vector2(
																		3, 2);

	public static final float	PLAYER_SCALE_FACTOR				= 55.0f;

	private float				PLAYER_WIDTH					= 0f;
	private float				PLAYER_HEIGHT					= 0f;

	private PlayerState			state							= PlayerState.STANDING;
	private float				stateTime						= 0.0f;

	public Vector2				position						= new Vector2();
	public Vector2				velocity						= new Vector2();

	public Animation			stand							= null;
	public Animation			run								= null;
	public Animation			jump							= null;

	private boolean				facingLeft						= false;
	private boolean				isGrounded						= true;

	private CBColour			playerColour					= null;

	// time in seconds the fl has been on
	private float				flashLightOnTime				= -1.0f;
	// time in seconds the fl will stay on
	public static final float	FLASHLIGHT_ON_DURATION			= 2.0f;
	// time in seconds the fl has left to cool down.
	private float				flashLightCooldown				= 0.0f;
	// time in seconds the fl needs to cool down each time
	public static final float	FLASHLIGHT_COOLDOWN_DURATION	= 2.0f;

	public Player() {
		FileHandle playerImage = Gdx.files.internal("data/RaySpritesSq.png");
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

		PLAYER_WIDTH = (PLAYER_TEXTURE_WIDTH * (1.0f / PLAYER_SCALE_FACTOR));
		PLAYER_HEIGHT = (PLAYER_TEXTURE_HEIGHT * (1.0f / PLAYER_SCALE_FACTOR));

		position.set(Player.INITIAL_POSITION);

		playerColour = new CBColour(CBColour.GameColour.RED);
	}

	/**
	 * Called each frame to update the position and state of the player
	 * 
	 * @param deltaTime
	 *            The amount of time since the last frame
	 */
	public boolean update(Level level, float deltaTime) {
		if (deltaTime == 0.0f) {
			return false;
		}

		stateTime += deltaTime;

		if (Gdx.app.getLogLevel() == Application.LOG_DEBUG) {
			if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
				position.set(Player.INITIAL_POSITION);
				velocity.set(0.0f, 0.0f);
			}

			if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
				Gdx.app.debug("PLAYER_COORDS", "(X,Y)=(" + position.x + ", "
						+ position.y + ")");
			}
		}

		if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP)) {
			Rectangle playerRectangle = new Rectangle();
			playerRectangle.set(position.x, position.y, PLAYER_WIDTH,
					PLAYER_HEIGHT);

			if (level.doorRect.contains(playerRectangle)) {
				Gdx.app.debug("DOOR_OPENED", "Player opened a door!");
				return true;
			}
		}

		// handle colour changes - we default to RED because why not
		// i - RED
		// j - GREEN
		// k - BLUE
		// l - YELLOW
		if (Gdx.input.isKeyPressed(Keys.I)
				|| Gdx.input.isKeyPressed(Keys.NUM_1)) {
			setPlayerColour(CBColour.GameColour.RED);
		} else if (Gdx.input.isKeyPressed(Keys.J)
				|| Gdx.input.isKeyPressed(Keys.NUM_2)) {
			setPlayerColour(CBColour.GameColour.GREEN);
		} else if (Gdx.input.isKeyPressed(Keys.K)
				|| Gdx.input.isKeyPressed(Keys.NUM_3)) {
			setPlayerColour(CBColour.GameColour.BLUE);
		} else if (Gdx.input.isKeyPressed(Keys.L)
				|| Gdx.input.isKeyPressed(Keys.NUM_4)) {
			setPlayerColour(CBColour.GameColour.YELLOW);
		}

		if (isLightOn()) {
			// if light is on, check if it's been on for too long, and turn it
			// off and start cooldown if it's been on
			flashLightOnTime += deltaTime;

			if (flashLightOnTime >= FLASHLIGHT_ON_DURATION) {
				Gdx.app.debug("FLASHLIGHT",
						"Flashlight time up. Cooldown started.");
				flashLightOnTime = -1.0f;
				flashLightCooldown = FLASHLIGHT_COOLDOWN_DURATION;
			}
		} else if (isLightOnCooldown()) {
			// if light is on cooldown, handle in a similar way until it's
			// cooled off.
			flashLightCooldown -= deltaTime;

			if (flashLightCooldown < 0.0f) {
				Gdx.app.debug("FLASHLIGHT", "Flashlight finished cooling down.");
				flashLightCooldown = 0.0f;
			}
		} else if (Gdx.input.isButtonPressed(0)
				|| Gdx.input.isKeyPressed(Keys.E)) {
			// LMB -> Turn on light if we can
			// only get here if not on and not on cooldown
			Gdx.app.debug("FLASHLIGHT", "Flashlight turned on.");
			flashLightOnTime = 0.01f;
		}

		// handle jumping
		if (Gdx.input.isKeyPressed(Keys.SPACE)) {
			if (isGrounded) {
				velocity.y += JUMP_VELOCITY;
				setState(PlayerState.JUMPING);
				isGrounded = false;
			}
		}

		// handle moving left
		if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) {
			velocity.x = -RUN_VELOCITY;

			if (isGrounded) {
				setState(PlayerState.RUNNING);
			}

			facingLeft = true;
		}

		// handle moving right
		if (Gdx.input.isKeyPressed(Keys.RIGHT)
				|| Gdx.input.isKeyPressed(Keys.D)) {
			velocity.x = RUN_VELOCITY;

			if (isGrounded) {
				setState(PlayerState.RUNNING);
			}

			facingLeft = false;
		}

		velocity.y += GRAVITY;

		// stop if we've clearly stopped running.
		if (Math.abs(velocity.x) < 0.1f) {
			velocity.x = 0.0f;

			if (isGrounded) {
				setState(PlayerState.STANDING);
			}
		}

		// don't want to fall too ridiculously fast
		if (Math.abs(velocity.y) > TERMINAL_VELOCITY) {
			velocity.y = Math.signum(velocity.y) * TERMINAL_VELOCITY;
		}

		Vector2 ptCoords = new Vector2(position);
		ptCoords.add(velocity);

		if (ptCoords.y < 0) {
			ptCoords.y = 0;
		}

		if (ptCoords.x < 0) {
			ptCoords.x = 0;
		}

		TiledMapTileLayer levelLayer = (TiledMapTileLayer) level.renderer
				.getMap().getLayers().get("level");

		TiledMapTileLayer platformLayer = (TiledMapTileLayer) level.renderer
				.getMap().getLayers().get("platforms");

		if (velocity.y > 0.0f) {
			ptCoords.y += PLAYER_HEIGHT;
		}

		if (velocity.x > 0.0f) {
			ptCoords.x += PLAYER_WIDTH;
		}

		// check for y collisions
		Cell levelCell = levelLayer.getCell(
				(int) (position.x + (PLAYER_WIDTH / 2)), (int) ptCoords.y);
		Cell platformCell = platformLayer.getCell((int) position.x,
				(int) ptCoords.y);

		if (levelCell != null) {
			// there's something there in the level, so it must be collidable
			handleYCollision();
		} else if (platformCell != null) {
			// check if we collide with this platform
			if (this.getPlayerColour().equals(
					level.getPlatformCellColour(platformCell))) {
				// if we get here, we collide since colours match
				// Gdx.app.debug("PLATFORM_COLLISION",
				// "Platform collision detected.");
				handleYCollision();
			}
		}

		levelCell = levelLayer.getCell((int) ptCoords.x,
				(int) (position.y + (PLAYER_HEIGHT / 2)));
		platformCell = platformLayer
				.getCell((int) ptCoords.x, (int) position.y);

		if (levelCell != null) {
			handleXCollision();
		} else if (platformCell != null) {
			if (this.getPlayerColour().equals(
					level.getPlatformCellColour(platformCell))) {
				handleXCollision();
			}
		}

		position.add(velocity);
		velocity.x *= (RUN_VELOCITY / 10.0f);

		if (position.y < -2.0f) {
			position.set(1, 3);
		}

		return false;
	}

	public void render(SpriteBatch batch) {
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
	}

	private void handleYCollision() {
		if (velocity.y < 0.0f) {
			isGrounded = true;
		}

		velocity.y = 0.0f;
	}

	private void handleXCollision() {
		velocity.x = 0.0f;
	}

	public boolean isLightOn() {
		return (flashLightOnTime >= 0.0f);
	}

	public boolean isLightOnCooldown() {
		return (flashLightCooldown > 0.0f);
	}

	public void resetFlashLight() {
		flashLightCooldown = -1.0f;
		flashLightOnTime = -1.0f;
	}

	public void dispose() {
		if (texture != null)
			texture.dispose();
	}

	public void setPlayerColour(CBColour.GameColour colour) {
		playerColour = new CBColour(colour);
	}

	public void setPlayerColour(CBColour colour) {
		playerColour = colour;
	}

	public CBColour getPlayerColour() {
		return playerColour;
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

	public float getPlayerWidth() {
		return PLAYER_WIDTH;
	}

	public float getPlayerHeight() {
		return PLAYER_HEIGHT;
	}
}
