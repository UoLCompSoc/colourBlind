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

	public static final Vector2	INITIAL_POSITION				= new Vector2(32.0f, 32.0f);
	public static final float	JUMP_VELOCITY					= 32.0f;
	public static final float	RUN_VELOCITY					= 32.0f;

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
		e.addComponent(new Solid(0.0f, 0.0f, DEFAULT_PLAYER_TEXTURE_WIDTH, 5.0f));

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
}
