package com.sgtcodfish.colourBlind;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.sgtcodfish.colourBlind.tiled.CBOrthogonalTiledMapRenderer;

/**
 * Holds a single level, loaded from a TMX file created using Tiled.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class Level {
	public CBOrthogonalTiledMapRenderer	renderer			= null;
	private TiledMap					tiledMap			= null;

	public final int					HEIGHT_IN_TILES, WIDTH_IN_TILES,
			TILE_WIDTH, TILE_HEIGHT;

	public HashMap<Cell, CBColour>		platformColourCache	= null;

	public Rectangle					doorRect			= null;

	/**
	 * Creates a new level, loading the tmx file called "levelFileName" in the
	 * "data/maps" directory.
	 * 
	 * @param levelFileName
	 *            The file name of the level to load.
	 */
	public Level(String levelFileName) {
		// needs to be done before we load; ideally it wouldn't be this hacky
		// but I want to get it done
		platformColourCache = new HashMap<Cell, CBColour>();

		String fullFileName = "data/maps/" + levelFileName;
		FileHandle levelHandle = Gdx.files.internal(fullFileName);

		Gdx.app.debug("LEVEL_LOAD", "Level file \"" + levelFileName
				+ "\" exists: " + levelHandle.exists());
		tiledMap = new TmxMapLoader().load(fullFileName);
		renderer = new CBOrthogonalTiledMapRenderer(tiledMap, 1.0f / 32.0f);

		MapLayers layers = tiledMap.getLayers();
		Gdx.app.debug("LEVEL_LOAD", "Level layers: " + layers.getCount());

		boolean levelLayerFound = false, platformsLayerFound = false, doorLayerFound = false;

		for (int i = 0; i < layers.getCount(); i++) {
			String layerName = layers.get(i).getName();
			Gdx.app.debug("LEVEL_LOAD", "Layer " + i + " name: " + layerName);

			if (layerName.equals("level")) {
				levelLayerFound = true;
			} else if (layerName.equals("platforms")) {
				platformsLayerFound = true;
			} else if (layerName.equals("door")) {
				doorLayerFound = true;
			}
		}

		if (!(levelLayerFound && platformsLayerFound && doorLayerFound)) {
			// we're missing a layer, we need to abort
			Gdx.app.debug("LEVEL_LOAD", "Level layer missing: "
					+ (!levelLayerFound));
			Gdx.app.debug("LEVEL_LOAD", "Platforms layer missing: "
					+ (!platformsLayerFound));
			Gdx.app.debug("LEVEL_LOAD", "Door layer missing: "
					+ (!doorLayerFound));

			throw new GdxRuntimeException(
					"Unable to find all of \"door\", \"level\" and \"platforms\" layers in "
							+ fullFileName + ".");
		}

		TiledMapTileLayer platformLayer = (TiledMapTileLayer) tiledMap
				.getLayers().get("platforms");
		HEIGHT_IN_TILES = platformLayer.getHeight();
		WIDTH_IN_TILES = platformLayer.getWidth();
		TILE_WIDTH = (int) platformLayer.getTileWidth();
		TILE_HEIGHT = (int) platformLayer.getTileHeight();

		/*
		 * Set up the platform colours; this could be done more efficiently, no
		 * doubt, but for the sake of readability I'll just do several for
		 * loops.
		 */

		CBColour platColour = null;
		boolean samePlatform = false;

		for (int y = 0; y < HEIGHT_IN_TILES; y++) {
			for (int x = 0; x < WIDTH_IN_TILES; x++) {
				Cell c = platformLayer.getCell(x, y);

				if (c != null) {
					// found a cell, start of platform?
					if (!samePlatform) {
						samePlatform = true;
						platColour = new CBColour();
					}
					platformColourCache.put(c, platColour);
				} else {
					if (samePlatform) {
						// come to the end of a platform
						samePlatform = false;
						platColour = null;
					}
				}
			}
		}

		// load the door
		int startX = -1, startY = -1, endX = -1, endY = -1;
		boolean found = false;

		TiledMapTileLayer doorLayer = (TiledMapTileLayer) tiledMap.getLayers()
				.get("door");
		for (int y = 0; y < HEIGHT_IN_TILES; y++) {
			for (int x = 0; x < WIDTH_IN_TILES; x++) {
				if (!found) {
					if (doorLayer.getCell(x, y) != null) {
						startX = x;
						startY = y;
						found = true;
					}
				} else {
					// found
					if (doorLayer.getCell(x, y) == null) {
						endX = x - 1;
						endY = y;
					}
				}
			}
		}

		Gdx.app.debug("LEVEL_LOAD", "Door startX = " + startX + ", endX = "
				+ endX);
		Gdx.app.debug("LEVEL_LOAD", "Door startY = " + startY + ", endY = "
				+ endY);

		doorRect = new Rectangle((float) startX, (float) startY,
				(float) (endX - startX), (float) (endY - startY));
	}

	/**
	 * Renders the whole level at once. Useful for lighting projections. Does
	 * not require a call to SpriteBatch.begin().
	 * 
	 * @param camera
	 *            The camera in which to render.
	 */
	public void renderAll(OrthographicCamera camera) {
		renderer.setView(camera);
		renderer.render();
	}

	/**
	 * Renders only the platforms on the level, which is the layer called
	 * "platforms".
	 * 
	 * @param camera
	 *            The camera in which to render.
	 * 
	 * @param shader
	 *            The shader program to use to render the platforms. This must
	 */
	public void renderPlatforms(OrthographicCamera camera) {
		renderer.getSpriteBatch().setColor(1.0f, 1.0f, 0.0f, 1.0f);
		TiledMapTileLayer platformLayer = (TiledMapTileLayer) tiledMap
				.getLayers().get("platforms");
		renderer.renderTileLayer(platformLayer);
	}

	/**
	 * Renders only the non-platform collidable blocks in the level, which is
	 * the layer called "level". REQUIRES a call to SpriteBatch.begin() prior to
	 * calling.
	 * 
	 * @param camera
	 *            The camera in which to render.
	 */
	public void renderLevel(OrthographicCamera camera) {
		renderer.setView(camera);
		TiledMapTileLayer levelLayer = (TiledMapTileLayer) tiledMap.getLayers()
				.get("level");
		renderer.renderTileLayer(levelLayer);
	}

	public void renderDoor(OrthographicCamera camera) {
		renderer.setView(camera);
		TiledMapTileLayer doorLayer = (TiledMapTileLayer) tiledMap.getLayers()
				.get("door");
		renderer.renderTileLayer(doorLayer);
	}

	/**
	 * Returns an array of rectangles representing the tiles in the specified
	 * ranges. Useful for collision.
	 * 
	 * @param sx
	 *            The starting x tile-coordinate.
	 * @param sy
	 *            The starting y tile-coordinate.
	 * @param ex
	 *            The ending x tile-coordinate.
	 * @param ey
	 *            The ending y tile-coordinate.
	 * @return An array of tiles, or null if no rects were found.
	 */
	public Array<Rectangle> getTilesAsRectArray(int sx, int sy, int ex, int ey) {
		Array<Rectangle> tiles = new Array<Rectangle>();

		TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(
				"level");

		int foundCount = 0;

		for (int y = sy; y <= ey; y++) {
			for (int x = sx; x <= ex; x++) {
				Cell cell = layer.getCell(x, y);

				if (cell != null) {
					Rectangle rect = new Rectangle();
					rect.set(x, y, 1, 1);
					tiles.add(rect);
					foundCount++;
				}
			}
		}

		return (foundCount > 0 ? tiles : null);
	}

	public CBColour getPlatformCellColour(Cell c) {
		return platformColourCache.get(c);
	}

	public void dispose() {
		if (tiledMap != null)
			tiledMap.dispose();
		if (renderer != null)
			renderer.dispose();
	}
}
