package com.sgtcodfish.colourBlind;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Holds a single level, loaded from a TMX file created using Tiled.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class Level {
	public OrthogonalTiledMapRenderer	renderer	= null;
	private TiledMap					tiledMap	= null;

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

		for (int i = 0; i < layers.getCount(); i++) {
			Gdx.app.debug("LEVEL_LOAD", "Layer " + i + " name: "
					+ layers.get(i).getName());
		}
	}

	public void render(OrthographicCamera camera) {
		renderer.setView(camera);
		renderer.render();
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
	public Array<Rectangle> getTiles(int sx, int sy, int ex, int ey) {
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
		tiledMap.dispose();
		renderer.dispose();
	}
}
