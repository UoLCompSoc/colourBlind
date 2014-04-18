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
			String message = "Non-directory detected by level loader, attempting to load files from list in: "
					+ levelFolder;
			Gdx.app.debug("LOAD_LEVELS", message);
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
		return null;
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

	// public final int HEIGHT_IN_TILES, WIDTH_IN_TILES, TILE_WIDTH,
	// TILE_HEIGHT;
	//
	// public HashMap<Cell, CBColour> platformColourCache = null;
	//
	// public Rectangle doorRect = null;
	//
	// /**
	// * Creates a new level, loading the tmx file called "levelFileName" in the
	// * "data/maps" directory.
	// *
	// * @param levelFileName
	// * The file name of the level to load.
	// */
	// public LevelEntityFactory(String levelFileName) {
	// // needs to be done before we load; ideally it wouldn't be this hacky
	// // but I want to get it done
	// platformColourCache = new HashMap<Cell, CBColour>();
	//
	// String fullFileName = "data/maps/" + levelFileName;
	// FileHandle levelHandle = Gdx.files.internal(fullFileName);
	//
	// Gdx.app.debug("LEVEL_LOAD", "Level file \"" + levelFileName +
	// "\" exists: " + levelHandle.exists());
	// tiledMap = new TmxMapLoader().load(fullFileName);
	// renderer = new CBOrthogonalTiledMapRenderer(tiledMap, 1.0f / 32.0f);
	//
	// MapLayers layers = tiledMap.getLayers();
	// Gdx.app.debug("LEVEL_LOAD", "Level layers: " + layers.getCount());
	//
	// boolean levelLayerFound = false, platformsLayerFound = false,
	// doorLayerFound = false;
	//
	// for (int i = 0; i < layers.getCount(); i++) {
	// String layerName = layers.get(i).getName();
	// Gdx.app.debug("LEVEL_LOAD", "Layer " + i + " name: " + layerName);
	//
	// if (layerName.equals("level")) {
	// levelLayerFound = true;
	// } else if (layerName.equals("platforms")) {
	// platformsLayerFound = true;
	// } else if (layerName.equals("door")) {
	// doorLayerFound = true;
	// }
	// }
	//
	// if (!(levelLayerFound && platformsLayerFound && doorLayerFound)) {
	// // we're missing a layer, we need to abort
	// Gdx.app.debug("LEVEL_LOAD", "Level layer missing: " +
	// (!levelLayerFound));
	// Gdx.app.debug("LEVEL_LOAD", "Platforms layer missing: " +
	// (!platformsLayerFound));
	// Gdx.app.debug("LEVEL_LOAD", "Door layer missing: " + (!doorLayerFound));
	//
	// throw new
	// GdxRuntimeException("Unable to find all of \"door\", \"level\" and \"platforms\" layers in "
	// + fullFileName + ".");
	// }
	//
	// TiledMapTileLayer platformLayer = (TiledMapTileLayer)
	// tiledMap.getLayers().get("platforms");
	// HEIGHT_IN_TILES = platformLayer.getHeight();
	// WIDTH_IN_TILES = platformLayer.getWidth();
	// TILE_WIDTH = (int) platformLayer.getTileWidth();
	// TILE_HEIGHT = (int) platformLayer.getTileHeight();
	//
	// /*
	// * Set up the platform colours; this could be done more efficiently, no
	// * doubt, but for the sake of readability I'll just do several for
	// * loops.
	// */
	//
	// CBColour platColour = null;
	// boolean samePlatform = false;
	//
	// for (int y = 0; y < HEIGHT_IN_TILES; y++) {
	// for (int x = 0; x < WIDTH_IN_TILES; x++) {
	// Cell c = platformLayer.getCell(x, y);
	//
	// if (c != null) {
	// // found a cell, start of platform?
	// if (!samePlatform) {
	// samePlatform = true;
	// platColour = new CBColour();
	// }
	// platformColourCache.put(c, platColour);
	// } else {
	// if (samePlatform) {
	// // come to the end of a platform
	// samePlatform = false;
	// platColour = null;
	// }
	// }
	// }
	// }
	//
	// // load the door
	// int startX = -1, startY = -1, endX = -1, endY = -1;
	// boolean found = false;
	//
	// TiledMapTileLayer doorLayer = (TiledMapTileLayer)
	// tiledMap.getLayers().get("door");
	// for (int y = 0; y < HEIGHT_IN_TILES; y++) {
	// for (int x = 0; x < WIDTH_IN_TILES; x++) {
	// if (!found) {
	// if (doorLayer.getCell(x, y) != null) {
	// startX = x;
	// startY = y;
	// found = true;
	// }
	// } else {
	// // found
	// if (doorLayer.getCell(x, y) == null) {
	// endX = x - 1;
	// endY = y;
	// }
	// }
	// }
	// }
	//
	// Gdx.app.debug("LEVEL_LOAD", "Door startX = " + startX + ", endX = " +
	// endX);
	// Gdx.app.debug("LEVEL_LOAD", "Door startY = " + startY + ", endY = " +
	// endY);
	//
	// doorRect = new Rectangle((float) startX, (float) startY, (float) (endX -
	// startX), (float) (endY - startY));
	// }
	//
	// /**
	// * Renders the whole level at once. Useful for lighting projections. Does
	// * not require a call to SpriteBatch.begin().
	// *
	// * @param camera
	// * The camera in which to render.
	// */
	// public void renderAll(OrthographicCamera camera) {
	// renderer.setView(camera);
	// renderer.render();
	// }
	//
	// /**
	// * Renders only the platforms on the level, which is the layer called
	// * "platforms".
	// *
	// * @param camera
	// * The camera in which to render.
	// *
	// * @param shader
	// * The shader program to use to render the platforms. This must
	// */
	// public void renderPlatforms(OrthographicCamera camera) {
	// renderer.getSpriteBatch().setColor(1.0f, 1.0f, 0.0f, 1.0f);
	// TiledMapTileLayer platformLayer = (TiledMapTileLayer)
	// tiledMap.getLayers().get("platforms");
	// renderer.renderTileLayer(platformLayer);
	// }
	//
	// /**
	// * Renders only the non-platform collidable blocks in the level, which is
	// * the layer called "level". REQUIRES a call to SpriteBatch.begin() prior
	// to
	// * calling.
	// *
	// * @param camera
	// * The camera in which to render.
	// */
	// public void renderLevel(OrthographicCamera camera) {
	// renderer.setView(camera);
	// TiledMapTileLayer levelLayer = (TiledMapTileLayer)
	// tiledMap.getLayers().get("level");
	// renderer.renderTileLayer(levelLayer);
	// }
	//
	// public void renderDoor(OrthographicCamera camera) {
	// renderer.setView(camera);
	// TiledMapTileLayer doorLayer = (TiledMapTileLayer)
	// tiledMap.getLayers().get("door");
	// renderer.renderTileLayer(doorLayer);
	// }
	//
	// /**
	// * Returns an array of rectangles representing the tiles in the specified
	// * ranges. Useful for collision.
	// *
	// * @param sx
	// * The starting x tile-coordinate.
	// * @param sy
	// * The starting y tile-coordinate.
	// * @param ex
	// * The ending x tile-coordinate.
	// * @param ey
	// * The ending y tile-coordinate.
	// * @return An array of tiles, or null if no rects were found.
	// */
	// public Array<Rectangle> getTilesAsRectArray(int sx, int sy, int ex, int
	// ey) {
	// Array<Rectangle> tiles = new Array<Rectangle>();
	//
	// TiledMapTileLayer layer = (TiledMapTileLayer)
	// tiledMap.getLayers().get("level");
	//
	// int foundCount = 0;
	//
	// for (int y = sy; y <= ey; y++) {
	// for (int x = sx; x <= ex; x++) {
	// Cell cell = layer.getCell(x, y);
	//
	// if (cell != null) {
	// Rectangle rect = new Rectangle();
	// rect.set(x, y, 1, 1);
	// tiles.add(rect);
	// foundCount++;
	// }
	// }
	// }
	//
	// return (foundCount > 0 ? tiles : null);
	// }
	//
	// public CBColour getPlatformCellColour(Cell c) {
	// return platformColourCache.get(c);
	// }
	//
	// public void dispose() {
	// if (tiledMap != null)
	// tiledMap.dispose();
	// if (renderer != null)
	// renderer.dispose();
	// }
}
