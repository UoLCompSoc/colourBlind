package com.sgtcodfish.colourBlind;

import java.util.ArrayList;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.artemis.Entity;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.sgtcodfish.colourBlind.tiled.CBOrthogonalTiledMapRenderer;

/**
 * Handles loading levels from data, storing them ready for use in entities, and
 * the creation of those entities.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class LevelFactory {
	public CBOrthogonalTiledMapRenderer	renderer	= null;
	public ArrayList<TiledMap>			levels		= null;

	/**
	 * Creates a LevelFactory, loading all the levels in the given folder, and a
	 * renderer with the given batch.
	 * 
	 * @param batch
	 *        The {@link Batch} (probably {@link SpriteBatch}) to use to render
	 *        the level.
	 * @param levelFolder
	 *        The folder where the levels for this LevelFactory are located.
	 */
	public LevelFactory(Batch batch, String levelFolder) {
		loadLevelsFromFolder(levelFolder);
		renderer = new CBOrthogonalTiledMapRenderer(levels.get(0), batch);
	}

	protected LevelFactory() {

	}

	/**
	 * Called to generate the entity for the next level. Will invalidate the
	 * current level and dispose it, and create the next level for use.
	 * 
	 * @return The next level to be played. If there is no level loaded, the
	 *         first level will be returned. If there are no further levels,
	 *         null will be returned.
	 */
	public Entity generateLevelEntity() {

		return null;
	}

	/**
	 * Loads all the levels in a specified folder. For internal use; construct a
	 * new LevelFactory to load the levels in a new folder.
	 * 
	 * @param levelFolder
	 *        The folder containing the level files.
	 */
	protected void loadLevelsFromFolder(String levelFolder) {
		FileHandle handle = Gdx.files.internal(levelFolder);

		if (!handle.isDirectory()) {
			String message = "Non-directory passed to level loader: " + levelFolder;
			Gdx.app.debug("LOAD_LEVELS", message);
			throw new IllegalArgumentException(message);
		}

		FileHandle[] levelHandles = handle.list(".tmx");
		if (levelHandles.length == 0) {
			String message = "No levels found in folder: " + levelFolder;
			Gdx.app.debug("LOAD_LEVELS", message);
			throw new IllegalArgumentException(message);
		} else {
			Gdx.app.debug("LOAD_LEVELS", String.valueOf(levelHandles.length) + " levels found in " + levelFolder + ".");
		}

		levels = new ArrayList<TiledMap>(levelHandles.length);
		TmxMapLoader loader = new TmxMapLoader();

		for (FileHandle fh : levelHandles) {
			TiledMap map = loader.load(fh.path());
			setupPlatformColours(map);
			levels.add(map);
		}

		if (Gdx.app.getLogLevel() == Application.LOG_DEBUG) {
			String levelNameDebug = "Following levels were loaded: ";
			String delim = ", ";

			for (FileHandle fh : levelHandles) {
				levelNameDebug += fh.name() + ", ";
			}

			levelNameDebug = levelNameDebug.substring(0, levelNameDebug.length() - delim.length());
			Gdx.app.debug("LOAD_LEVELS", levelNameDebug);
		}
	}

	/**
	 * For internal use: sets up the colours for each distinct platform in a
	 * level, given as a TiledMap objcet.
	 * 
	 * @param map
	 *        The map, containing a "platforms" layer, whose platforms will be
	 *        assigned random colours.
	 */
	protected void setupPlatformColours(TiledMap map) {
		throw new NotImplementedException();
	}

	public void dispose() {
		if (renderer != null) {
			renderer.dispose();
		}

		if (levels != null && !levels.isEmpty()) {
			for (TiledMap map : levels) {
				map.dispose();
			}

			levels.clear();
			levels = null;
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
	// public LevelFactory(String levelFileName) {
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
