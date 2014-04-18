package com.sgtcodfish.colourBlind;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.sgtcodfish.colourBlind.components.Coloured;
import com.sgtcodfish.colourBlind.components.Facing;
import com.sgtcodfish.colourBlind.components.Flashlight;
import com.sgtcodfish.colourBlind.components.FocusTaker;
import com.sgtcodfish.colourBlind.components.HumanoidAnimatedSprite;
import com.sgtcodfish.colourBlind.components.PlayerInputListener;
import com.sgtcodfish.colourBlind.components.Position;
import com.sgtcodfish.colourBlind.components.Solid;
import com.sgtcodfish.colourBlind.components.Velocity;
import com.sgtcodfish.colourBlind.components.Weight;

/**
 * Holds defaults and helper methods for creating a player Entity.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class PlayerEntityFactory implements Disposable {
	public static int			DEFAULT_PLAYER_TEXTURE_WIDTH	= 64;
	public static int			DEFAULT_PLAYER_TEXTURE_HEIGHT	= 128;

	public static final Vector2	INITIAL_POSITION				= new Vector2(3.0f, 2.0f);
	public static final float	JUMP_VELOCITY					= 1.0f;
	public static final float	RUN_VELOCITY					= 1.0f;

	public Texture				playerTexture					= null;

	public Animation			stand							= null;
	public Animation			run								= null;
	public Animation			jump							= null;

	/**
	 * Creates an entity with typical components one might expect a player
	 * character to have.
	 * 
	 * @param world
	 *        The world from which to create the entity.
	 * @param takesFocus
	 *        Whether or not this entity should take the focus of the camera. If
	 *        it should, be mindful that only 1 Entity in total does.
	 * @return A player entity with sensible default values for its components.
	 */
	public Entity createPlayerEntity(World world, boolean takesFocus) {
		if (stand == null || run == null || jump == null) {
			throw new IllegalArgumentException("Call to createPlayerEntity(World) with uninitiated animations.");
		}

		Entity e = world.createEntity();

		e.addComponent(new Position(INITIAL_POSITION));
		e.addComponent(new Velocity());
		if (takesFocus) {
			e.addComponent(new FocusTaker());
		}

		e.addComponent(new PlayerInputListener());

		e.addComponent(new Facing());
		e.addComponent(new Coloured());
		e.addComponent(new HumanoidAnimatedSprite(stand, run, jump));

		e.addComponent(new Weight());
		e.addComponent(new Solid());

		e.addComponent(new Flashlight("Player's Flashlight"));

		return e;
	}

	/**
	 * Creates an entity with typical components one might expect a player
	 * character to have, with the default that this entity does not take focus
	 * of the camera.
	 * 
	 * @param world
	 *        The world from which to create the entity.
	 * @return A player entity with sensible default values for its components.
	 */
	public Entity createPlayerEntity(World world) {
		return createPlayerEntity(world, false);
	}

	/**
	 * Loads a texture located at the internal location fname, and loads the top
	 * row of images to create animations. The first block is the standing
	 * sprite, the next two are the running animations and the 4th is the jump
	 * sprite.
	 * 
	 * Custom animations need to be loaded through a customised load method;
	 * this could be done by overriding this class and changing the
	 * implementation of loadAnimations(String, int, int).
	 * 
	 * @param fname
	 *        The internal path to the texture to use as sheet.
	 * @param tileWidth
	 *        The width of an individual tile.
	 * @param tileHeight
	 *        The height of an individual tile.
	 */
	protected void loadAnimations(String fname, int tileWidth, int tileHeight) {
		if (tileWidth <= 0 || tileHeight <= 0) {
			throw new IllegalArgumentException(
					"Call to loadAnimations for a player with invalid tileWidth or tileHeight");
		} else if (!Gdx.files.internal(fname).exists()) {
			throw new IllegalArgumentException(
					"Call to loadAnimations for a player with a texture path that does not exist.");
		}

		FileHandle playerImage = Gdx.files.internal(fname);
		playerTexture = new Texture(playerImage);
		playerTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		TextureRegion[] regions = TextureRegion.split(playerTexture, tileWidth, tileHeight)[0];
		stand = new Animation(0.0f, regions[0]);
		run = new Animation(0.2f, regions[1], regions[2]);
		jump = new Animation(0.0f, regions[3]);

		run.setPlayMode(Animation.LOOP_PINGPONG);
	}

	/**
	 * One should create a PlayerEntityFactory to hold textures, and generate
	 * the required entity. Use the PlayerEntityFactory(String) constructor to
	 * ensure that animations are loaded.
	 */
	protected PlayerEntityFactory() {
	}

	/**
	 * Sets up the animations and texture for a PlayerEntityFactory entity to be
	 * created. After the PlayerEntityFactory
	 * 
	 * @param textureFileName
	 *        The internal path to the texture to use as sheet.
	 * @param tileWidth
	 *        The width of an individual tile.
	 * @param tileHeight
	 *        The height of an individual tile.
	 */
	public PlayerEntityFactory(String textureFileName, int tileWidth, int tileHeight) {
		loadAnimations(textureFileName, tileWidth, tileHeight);
	}

	@Override
	public void dispose() {
		stand = run = jump = null;
		if (playerTexture != null) {
			playerTexture.dispose();
		}
	}

	// /**
	// * Called each frame to update the position and state of the player
	// *
	// * @param deltaTime
	// * The amount of time since the last frame
	// */
	// public boolean update(LevelEntityFactory level, float deltaTime) {
	// if (deltaTime == 0.0f) {
	// return false;
	// }
	//
	// stateTime += deltaTime;
	//
	// if (Gdx.app.getLogLevel() == Application.LOG_DEBUG) {
	// if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
	// position.set(PlayerEntityFactory.INITIAL_POSITION);
	// velocity.set(0.0f, 0.0f);
	// }
	//
	// if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
	// Gdx.app.debug("PLAYER_COORDS", "(X,Y)=(" + position.x + ", " + position.y
	// + ")");
	// }
	// }
	//
	// if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP)) {
	// Rectangle playerRectangle = new Rectangle();
	// playerRectangle.set(position.x, position.y, PLAYER_WIDTH, PLAYER_HEIGHT);
	//
	// if (level.doorRect.contains(playerRectangle)) {
	// Gdx.app.debug("DOOR_OPENED", "PlayerEntityFactory opened a door!");
	// return true;
	// }
	// }
	//
	// // handle colour changes - we default to RED because why not
	// if (Gdx.input.isKeyPressed(Keys.I) || Gdx.input.isKeyPressed(Keys.NUM_1))
	// {
	// setPlayerColour(CBColour.GameColour.RED);
	// } else if (Gdx.input.isKeyPressed(Keys.J) ||
	// Gdx.input.isKeyPressed(Keys.NUM_2)) {
	// setPlayerColour(CBColour.GameColour.GREEN);
	// } else if (Gdx.input.isKeyPressed(Keys.K) ||
	// Gdx.input.isKeyPressed(Keys.NUM_3)) {
	// setPlayerColour(CBColour.GameColour.BLUE);
	// } else if (Gdx.input.isKeyPressed(Keys.L) ||
	// Gdx.input.isKeyPressed(Keys.NUM_4)) {
	// setPlayerColour(CBColour.GameColour.YELLOW);
	// }
	//
	// // handle jumping
	// if (Gdx.input.isKeyPressed(Keys.SPACE)) {
	// if (isGrounded) {
	// velocity.y += JUMP_VELOCITY;
	// setState(PlayerState.JUMPING);
	// isGrounded = false;
	// }
	// }
	//
	// // handle moving left
	// if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A))
	// {
	// velocity.x = -RUN_VELOCITY;
	//
	// if (isGrounded) {
	// setState(PlayerState.RUNNING);
	// }
	//
	// facingLeft = true;
	// }
	//
	// // handle moving right
	// if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D))
	// {
	// velocity.x = RUN_VELOCITY;
	//
	// if (isGrounded) {
	// setState(PlayerState.RUNNING);
	// }
	//
	// facingLeft = false;
	// }
	//
	// velocity.y += GRAVITY;
	//
	// // stop if we've clearly stopped running.
	// if (Math.abs(velocity.x) < 0.1f) {
	// velocity.x = 0.0f;
	//
	// if (isGrounded) {
	// setState(PlayerState.STANDING);
	// }
	// }
	//
	// // don't want to fall too ridiculously fast
	// if (Math.abs(velocity.y) > TERMINAL_VELOCITY) {
	// velocity.y = Math.signum(velocity.y) * TERMINAL_VELOCITY;
	// }
	//
	// Vector2 ptCoords = new Vector2(position);
	// ptCoords.add(velocity);
	//
	// if (ptCoords.y < 0) {
	// ptCoords.y = 0;
	// }
	//
	// if (ptCoords.x < 0) {
	// ptCoords.x = 0;
	// }
	//
	// TiledMapTileLayer levelLayer = (TiledMapTileLayer)
	// level.renderer.getMap().getLayers().get("level");
	//
	// TiledMapTileLayer platformLayer = (TiledMapTileLayer)
	// level.renderer.getMap().getLayers().get("platforms");
	//
	// if (velocity.y > 0.0f) {
	// ptCoords.y += PLAYER_HEIGHT;
	// }
	//
	// if (velocity.x > 0.0f) {
	// ptCoords.x += PLAYER_WIDTH;
	// }
	//
	// // check for y collisions
	// Cell levelCell = levelLayer.getCell((int) (position.x + (PLAYER_WIDTH /
	// 2)), (int) ptCoords.y);
	// Cell platformCell = platformLayer.getCell((int) position.x, (int)
	// ptCoords.y);
	//
	// if (levelCell != null) {
	// // there's something there in the level, so it must be collidable
	// handleYCollision();
	// } else if (platformCell != null) {
	// // check if we collide with this platform
	// if
	// (this.getPlayerColour().equals(level.getPlatformCellColour(platformCell)))
	// {
	// // if we get here, we collide since colours match
	// // Gdx.app.debug("PLATFORM_COLLISION",
	// // "Platform collision detected.");
	// handleYCollision();
	// }
	// }
	//
	// levelCell = levelLayer.getCell((int) ptCoords.x, (int) (position.y +
	// (PLAYER_HEIGHT / 2)));
	// platformCell = platformLayer.getCell((int) ptCoords.x, (int) position.y);
	//
	// if (levelCell != null) {
	// handleXCollision();
	// } else if (platformCell != null) {
	// if
	// (this.getPlayerColour().equals(level.getPlatformCellColour(platformCell)))
	// {
	// handleXCollision();
	// }
	// }
	//
	// position.add(velocity);
	// velocity.x *= (RUN_VELOCITY / 10.0f);
	//
	// // TODO: Saner bounds checking.
	// if (position.y < -2.0f) {
	// position.set(1, 3);
	// }
	//
	// return false;
	// }

	// private void handleYCollision() {
	// if (velocity.y < 0.0f) {
	// isGrounded = true;
	// }
	//
	// velocity.y = 0.0f;
	// }
	//
	// private void handleXCollision() {
	// velocity.x = 0.0f;
	// }
}
