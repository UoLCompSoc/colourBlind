package com.sgtcodfish.colourBlind;

import java.util.ArrayList;
import java.util.HashMap;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;
import com.sgtcodfish.colourBlind.components.Position;
import com.sgtcodfish.colourBlind.components.TiledRenderable;
import com.sgtcodfish.colourBlind.tiled.CBOrthogonalTiledMapRenderer;

/**
 * Handles loading levels from data, storing them ready for use in entities, and
 * the creation of those entities.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class LevelEntityFactory implements Disposable {
	public ArrayList<LevelDetails>	levels			= null;

	private int						currentLevel	= 0;
	private Batch					batch			= null;

	/**
	 * Creates a LevelEntityFactory, loading all the levels in the given folder,
	 * and a renderer with the given batch.
	 * 
	 * @param batch
	 *        The {@link Batch} (probably {@link SpriteBatch}) to use to render
	 *        the level.
	 * @param levelFolder
	 *        The folder where the levels for this LevelEntityFactory are
	 *        located.
	 */
	public LevelEntityFactory(Batch batch, String levelFolder) {
		this.batch = batch;
		this.currentLevel = 0;

		loadLevelsFromFolder(levelFolder);
	}

	protected LevelEntityFactory() {

	}

	/**
	 * Called to generate the entity for the next level at the origin. Will
	 * invalidate the current level and dispose it, and create the next level
	 * for use.
	 * 
	 * @param world
	 *        The world from which to create the entity. It will <em>not</em> be
	 *        added to the world.
	 * 
	 * @return The next level to be played. If there is no level loaded, the
	 *         first level will be returned. If there are no further levels,
	 *         null will be returned.
	 */
	public Entity generateNextLevelEntity(World world) {
		return generateNextLevelEntity(world, 0.0f, 0.0f);
	}

	/**
	 * Called to generate the entity for the next level. Will invalidate the
	 * current level and dispose it, and create the next level for use.
	 * 
	 * @param world
	 *        The world from which to create the entity. It will <em>not</em> be
	 *        added to the world.
	 * @param x
	 *        The x position of the level.
	 * @param y
	 *        The y position of the level.
	 * 
	 * @return The next level to be played. If there is no level loaded, the
	 *         first level will be returned. If there are no further levels,
	 *         null will be returned.
	 */
	public Entity generateNextLevelEntity(World world, float x, float y) {
		Entity levelEntity = world.createEntity();

		LevelDetails level = levels.get(currentLevel);

		if (level == null) {
			return null;
		} else {
			levelEntity.addComponent(new Position(x, y));
			levelEntity.addComponent(new TiledRenderable(level.platformColours, level.map, level.renderer));

			currentLevel++;

			return levelEntity;
		}
	}

	/**
	 * Loads all the levels in a specified folder. For internal use; construct a
	 * new LevelEntityFactory to load the levels in a new folder.
	 * 
	 * @param levelFolder
	 *        The folder containing the level files.
	 */
	protected void loadLevelsFromFolder(String levelFolder) {
		FileHandle handle = Gdx.files.internal(levelFolder);
		FileHandle[] levelHandles = null;

		if (!handle.isDirectory()) {
			Gdx.app.debug("LOAD_LEVELS",
					"Non-directory detected by level loader, attempting to load files from list in: " + levelFolder);
			String[] levelNames = handle.readString().split("\n");
			levelHandles = new FileHandle[levelNames.length];

			for (int i = 0; i < levelNames.length; i++) {
				levelNames[i] = levelFolder + levelNames[i];
				FileHandle temp = Gdx.files.internal(levelNames[i]);

				if (temp.exists()) {
					Gdx.app.debug("LOAD_LEVELS", "Found level: " + levelNames[i]);
					levelHandles[i] = temp;
				}
			}
		} else {
			Gdx.app.debug("LOAD_LEVELS", "Directory detected, loading from handle.list.");
			levelHandles = handle.list(".tmx");
		}

		if (levelHandles.length == 0) {
			String message = "No levels found in folder: " + levelFolder;
			Gdx.app.debug("LOAD_LEVELS", message);
			throw new IllegalArgumentException(message);
		}

		Gdx.app.debug("LOAD_LEVELS", String.valueOf(levelHandles.length) + " levels found in " + levelFolder + ".");

		levels = new ArrayList<LevelDetails>(levelHandles.length);
		TmxMapLoader loader = new TmxMapLoader();

		for (FileHandle fh : levelHandles) {
			TiledMap map = loader.load(fh.path());

			if (!isValidLevel(map)) {
				Gdx.app.debug("LOAD_LEVELS", fh.path() + " is an invalid level format. Skipping.");
				continue;
			}

			LevelDetails level = new LevelDetails();
			level.map = map;
			level.platformColours = LevelEntityFactory.generatePlatformColours(map);
			level.renderer = new CBOrthogonalTiledMapRenderer(level.platformColours, level.map, this.batch);
			levels.add(level);
		}

		if (Gdx.app.getLogLevel() == Application.LOG_DEBUG) {
			String levelNameDebug = "Following levels were loaded: ";
			String delim = ", ";

			for (FileHandle fh : levelHandles) {
				levelNameDebug += fh.name() + delim;
			}

			levelNameDebug = levelNameDebug.substring(0, levelNameDebug.length() - delim.length());
			Gdx.app.debug("LOAD_LEVELS", levelNameDebug);
		}
	}

	/**
	 * A map is a valid Colour Blind level if it contains at the very least, the
	 * following layers:
	 * <ul>
	 * <li>"level" - Containing solid, unpassable blocks.</li>
	 * <li>"platforms" - Containing platforms, who have colours assigned and are
	 * solid if the colliding object's colour matches.</li>
	 * <li>"door" - Containing the door to the next level.</li>
	 * </ul>
	 * 
	 * This function checks for existance of these layers, with no verbose
	 * output.
	 * 
	 * @param map
	 *        The map whose layers are to be checked.
	 * @return True if the map is a valid CB level, false otherwise.
	 */
	public boolean isValidLevel(TiledMap map) {
		return isValidLevel(map, false);
	}

	/**
	 * A map is a valid Colour Blind level if it contains at the very least, the
	 * following layers:
	 * <ul>
	 * <li>"level" - Containing solid, unpassable blocks.</li>
	 * <li>"platforms" - Containing platforms, who have colours assigned and are
	 * solid if the colliding object's colour matches.</li>
	 * <li>"door" - Containing the door to the next level.</li>
	 * </ul>
	 * 
	 * This function checks for existance of these layers, with optional verbose
	 * output.
	 * 
	 * @param map
	 *        The map whose layers are to be checked.
	 * @param verbose
	 *        Set to true to enable verbose logging output for the check to
	 *        Gdx.app.debug.
	 * @return True if the map is a valid CB level, false otherwise.
	 */
	public boolean isValidLevel(TiledMap map, boolean verbose) {
		MapLayers layers = map.getLayers();
		if (verbose) {
			Gdx.app.debug("VALID_LEVEL", "Level layer count: " + layers.getCount());
		}

		boolean levelLayerFound = false, platformsLayerFound = false, doorLayerFound = false;

		for (int i = 0; i < layers.getCount(); i++) {
			String layerName = layers.get(i).getName();
			if (verbose) {
				Gdx.app.debug("VALID_LEVEL", "Layer " + i + " name: " + layerName);
			}

			if (layerName.equals("level")) {
				levelLayerFound = true;
			} else if (layerName.equals("platforms")) {
				platformsLayerFound = true;
			} else if (layerName.equals("door")) {
				doorLayerFound = true;
			}
		}

		if (!(levelLayerFound && platformsLayerFound && doorLayerFound)) {
			if (verbose) {
				Gdx.app.debug("VALID_LEVEL", "Level layer missing: " + (!levelLayerFound));
				Gdx.app.debug("VALID_LEVEL", "Platforms layer missing: " + (!platformsLayerFound));
				Gdx.app.debug("VALID_LEVEL", "Door layer missing: " + (!doorLayerFound));
			}

			return false;
		} else {
			if (verbose) {
				Gdx.app.debug("VALID_LEVEL", "Map is valid.");
			}

			return true;
		}
	}

	/**
	 * @return true if this factory has exhausted its list of levels (i.e. a
	 *         call to generateNextLevelEntity() will return null).
	 */
	public boolean isFactoryFinished() {
		return currentLevel > levels.size();
	}

	/**
	 * Generates a color map from cells in the "platforms" layer of a map to
	 * colours. Connected platforms all receieve the same colour, and each
	 * platform receives a random colour.
	 * 
	 * @param map
	 *        The map, containing a layer called "platforms",
	 * @return
	 */
	public static HashMap<Cell, Color> generatePlatformColours(TiledMap map) {
		HashMap<Cell, Color> colourMap = new HashMap<Cell, Color>();

		CBColour platColour = null;
		boolean samePlatform = false;

		TiledMapTileLayer platformLayer = (TiledMapTileLayer) map.getLayers().get("platforms");

		if (platformLayer == null) {
			throw new IllegalArgumentException("Invalid map passed to generate platform colours; no platforms layer.");
		}

		final int WIDTH_IN_TILES = platformLayer.getWidth();
		final int HEIGHT_IN_TILES = platformLayer.getHeight();

		for (int y = 0; y < HEIGHT_IN_TILES; y++) {
			for (int x = 0; x < WIDTH_IN_TILES; x++) {
				Cell c = platformLayer.getCell(x, y);

				if (c != null) {
					// found a cell, start of platform?
					if (!samePlatform) {
						samePlatform = true;
						platColour = new CBColour();
					}

					colourMap.put(c, platColour.toGdxColour());
				} else {
					if (samePlatform) {
						// come to the end of a platform
						samePlatform = false;
						platColour = null;
					}
				}
			}
		}

		return colourMap;
	}

	/**
	 * Gets the currently used tiled map.
	 * 
	 * @return The map which is currently in use, or null if no map is currently
	 *         in use.
	 */
	public TiledMap getCurrentMap() {
		return levels.get(currentLevel).map;
	}

	/**
	 * Should be called before this object is destroyed. Invalidates references
	 * to levels in this factory.
	 */
	@Override
	public void dispose() {
		if (levels != null && !levels.isEmpty()) {
			for (LevelDetails level : levels) {
				level.dispose();
			}

			levels.clear();
			levels = null;
		}
	}

	/**
	 * Used internally to store level details.
	 * 
	 * @author Ashley Davis (SgtCoDFish)
	 */
	private class LevelDetails implements Disposable {
		public TiledMap						map				= null;
		public HashMap<Cell, Color>			platformColours	= null;
		public CBOrthogonalTiledMapRenderer	renderer		= null;

		@Override
		public void dispose() {
			if (map != null) {
				map.dispose();
				map = null;
			}

			if (platformColours != null) {
				platformColours.clear();
			}

			if (renderer != null) {
				renderer.dispose();
			}
		}
	}
}
