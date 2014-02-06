package com.sgtcodfish.colourBlind;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
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

	// private HashMap<Cell, CBColour> platformColourCache = null;
	// private HashMap<Cell, Integer> firstCells = null;
	// private HashMap<Cell, Vector3> firstCellCoords = null;
	private ArrayList<Platform>			platforms					= null;

	public final int					HEIGHT_IN_TILES, WIDTH_IN_TILES,
			TILE_WIDTH, TILE_HEIGHT;

	private HashMap<Cell, CBColour>		platformColourCache			= null;

	private TextureRegion				platformStartTexture		= null;
	private TextureRegion				platformMiddleTexture		= null;
	private TextureRegion				platformEndTexture			= null;

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

		platforms = new ArrayList<Platform>();

		// initialise the platform textures
		boolean foundPlat = false;
		int width = 1;

		for (int y = 0; y < HEIGHT_IN_TILES; y++) {
			for (int x = 0; x < WIDTH_IN_TILES; x++) {
				Cell c = platformLayer.getCell(x, y);

				if (!foundPlat) {
					if (c != null) {
						Gdx.app.debug("LEVEL_LOAD",
								"Platform textures coming from platform starting at (x,y)=("
										+ x + "," + y + ")");
						// found a platform now
						foundPlat = true;
						platformStartTexture = c.getTile().getTextureRegion();
						platformMiddleTexture = platformLayer.getCell(x + 1, y)
								.getTile().getTextureRegion();
						width = 1;
					}
				} else {
					// found a platform already, looking for the end
					if (c == null) {
						Gdx.app.debug("LEVEL_LOAD", "Texture platform width = "
								+ width);
						platformEndTexture = platformLayer.getCell(x - 1, y)
								.getTile().getTextureRegion();
						break;
					} else {
						width++;
					}
				}
			}

			if (foundPlat)
				break;
		}

		if (platformStartTexture == null || platformEndTexture == null
				|| platformMiddleTexture == null) {
			throw new GdxRuntimeException(
					"Failed to initialise platform textures for level "
							+ levelFileName
							+ "; likely malformed first platform.");
		}

		/*
		 * Set up the platform colours; this could be done more efficiently, no
		 * doubt, but for the sake of readability I'll just do several for
		 * loops.
		 */
		platformColourCache = new HashMap<Cell, CBColour>();
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
						Gdx.app.debug(
								"LEVEL_LOAD",
								"Platform found, colour: "
										+ CBColour.GameColour
												.asString(platColour
														.getColour()) + ".");
					}

					platformColourCache.put(c, platColour);
				}
			}
		}

		Gdx.app.debug("LEVEL_LOAD", "Loaded a total of " + platforms.size()
				+ " platforms.");

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
	 * "platforms". REQUIRES a call to SpriteBatch.begin() prior to calling.
	 * 
	 * @param camera
	 *            The camera in which to render.
	 * 
	 * @param shader
	 *            The shader program to use to render the platforms. This must
	 */
	public void renderPlatforms(OrthographicCamera camera, ShaderProgram shader) {
		SpriteBatch sb = renderer.getSpriteBatch();

		renderer.setView(camera);
		sb.setShader(shader);

		Player player = ColourBlindGame.getInstance().getPlayer();

		shader.setUniformf("flashLightSize",
				(float) ColourBlindGame.LIGHT_SIZE / 2);
		shader.setUniformf("flashLight", (player.isLightOn() ? 1.0f : 0.0f));
		shader.setUniformf("platform", 1.0f);
		shader.setUniformf("lightCoord", player.position.x, player.position.y);

		for (Platform p : platforms) {
			p.render(sb, camera, shader);
		}
		// TiledMapTileLayer platformLayer = (TiledMapTileLayer) tiledMap
		// .getLayers().get("platforms");
		// renderer.renderTileLayer(platformLayer);
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

	public TextureRegion getPlatformStartTexture() {
		return platformStartTexture;
	}

	public TextureRegion getPlatformMiddleTexture() {
		return platformMiddleTexture;
	}

	public TextureRegion getPlatformEndTexture() {
		return platformEndTexture;
	}

	public void dispose() {
		platformColourTexture = null;
		if (platformColourFrameBuffer != null) {
			platformColourFrameBuffer.dispose();
		}

		platformStartTexture = null;
		platformEndTexture = null;
		platformMiddleTexture = null;

		platforms.clear();
		if (tiledMap != null)
			tiledMap.dispose();
		if (renderer != null)
			renderer.dispose();
	}
}
