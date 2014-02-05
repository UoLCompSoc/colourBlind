package com.sgtcodfish.colourBlind;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Holds a single level, loaded from a TMX file created using Tiled.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class Level {
	public OrthogonalTiledMapRenderer	renderer					= null;
	private TiledMap					tiledMap					= null;

	private HashMap<Cell, CBColour>		platformColourCache			= null;
	private HashMap<Cell, Integer>		firstCells					= null;
	private HashMap<Cell, Vector2>		firstCellCoords				= null;

	public final int					HEIGHT_IN_TILES, WIDTH_IN_TILES,
			TILE_WIDTH, TILE_HEIGHT;

	public Rectangle					doorRect					= null;

	public FrameBuffer					platformColourFrameBuffer	= null;
	public TextureRegion				platformColourTexture		= null;

	/**
	 * Creates a new level, loaded the tmx file called "levelFileName" in the
	 * "data/maps" directory.
	 * 
	 * @param levelFileName
	 *            The file name of the level to load.
	 */
	public Level(String levelFileName) {
		String fullFileName = "data/maps/" + levelFileName;
		FileHandle levelHandle = Gdx.files.internal(fullFileName);

		Gdx.app.debug("LEVEL_LOAD", "Level file \"" + levelFileName
				+ "\" exists: " + levelHandle.exists());
		tiledMap = new TmxMapLoader().load(fullFileName);
		renderer = new OrthogonalTiledMapRenderer(tiledMap, 1.0f / 32.0f);

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
					"Unable to find \"door\", \"level\" and \"platforms\" layers in "
							+ fullFileName + ".");
		}

		TiledMapTileLayer platformLayer = (TiledMapTileLayer) tiledMap
				.getLayers().get("platforms");
		HEIGHT_IN_TILES = platformLayer.getHeight();
		WIDTH_IN_TILES = platformLayer.getWidth();
		TILE_WIDTH = (int) platformLayer.getTileWidth();
		TILE_HEIGHT = (int) platformLayer.getTileHeight();

		// create a hashmap of all the cells in the platforms level
		// and cache them for checking colours quickly in collisions code
		boolean samePlatform = false;
		CBColour platColour = null;
		int platformsFound = 0;
		int platformWidth = 0;
		Cell first = null;

		firstCells = new HashMap<Cell, Integer>();
		firstCellCoords = new HashMap<TiledMapTileLayer.Cell, Vector2>();
		platformColourCache = new HashMap<Cell, CBColour>();

		int firstX = 0, firstY = 0;

		for (int y = 0; y < HEIGHT_IN_TILES; y++) {
			for (int x = 0; x < WIDTH_IN_TILES; x++) {
				Cell c = platformLayer.getCell(x, y);

				if (c != null) {
					// found a cell, start of platform?
					if (!samePlatform) {
						first = c;
						samePlatform = true;
						platformsFound++;
						platformWidth += 1;
						platColour = new CBColour();
						Gdx.app.debug(
								"LEVEL_LOAD",
								"Platform found, colour will be "
										+ CBColour.GameColour
												.asString(platColour
														.getColour()) + ".");

						firstX = x;
						firstY = y;

					}

					platformColourCache.put(c, platColour);
				} else {
					if (samePlatform) {
						// come to the end of the platform
						samePlatform = false;
						platColour = null;

						firstCells.put(first, platformWidth);
						firstCellCoords.put(first, new Vector2(firstX, firstY));

						platformWidth = 0;
						firstX = 0;
						firstY = 0;
					}
				}
			}
		}

		Gdx.app.debug("LEVEL_LOAD", "Loaded a total of " + platformsFound
				+ " platforms.");

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
	 * "platforms". REQUIRES a call to SpriteBatch.begin() prior to calling.
	 * 
	 * @param camera
	 *            The camera in which to render.
	 */
	public void renderPlatforms(OrthographicCamera camera) {
		renderer.setView(camera);
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

	public void renderColourTexture(OrthographicCamera camera) {
		platformColourFrameBuffer.begin();
		platformColourFrameBuffer.end();
	}

	public CBColour getPlatformCellColour(Cell c) {
		return platformColourCache.get(c);
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

	public void dispose() {
		platformColourTexture = null;
		if (platformColourFrameBuffer != null) {
			platformColourFrameBuffer.dispose();
		}
		firstCellCoords.clear();
		firstCells.clear();
		platformColourCache.clear();
		platformColourCache = null;
		if (tiledMap != null)
			tiledMap.dispose();
		if (renderer != null)
			renderer.dispose();
	}
}
