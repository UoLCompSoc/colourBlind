package com.sgtcodfish.colourBlind.tiled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.maps.tiled.TmxMapHelper;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class CBTmxMapLoader extends TmxMapLoader {
	public CBTmxMapLoader() {
		super();
	}

	public CBTmxMapLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	/**
	 * See superclass implementation.
	 * 
	 * Basically copy pasted, with the addition of using CBCells instead of
	 * cells.
	 */
	@Override
	protected void loadTileLayer(TiledMap map, Element element) {
		Gdx.app.debug("CB_TMXLOADER", "In CBTmxMapLoader->loadTileLayer");
		if (element.getName().equals("layer")) {
			String name = element.getAttribute("name", null);
			int width = element.getIntAttribute("width", 0);
			int height = element.getIntAttribute("height", 0);
			int tileWidth = element.getParent().getIntAttribute("tilewidth", 0);
			int tileHeight = element.getParent().getIntAttribute("tileheight",
					0);
			boolean visible = element.getIntAttribute("visible", 1) == 1;
			float opacity = element.getFloatAttribute("opacity", 1.0f);
			TiledMapTileLayer layer = new TiledMapTileLayer(width, height,
					tileWidth, tileHeight);
			layer.setVisible(visible);
			layer.setOpacity(opacity);
			layer.setName(name);
			boolean loadingPlatforms = false;

			if (layer.getName().equals("platforms")) {
				Gdx.app.debug("CB_TMXLOADER", "Loading platforms layer");
				loadingPlatforms = true;
			}

			int[] ids = TmxMapHelper.getTileIds(element, width, height);
			TiledMapTileSets tilesets = map.getTileSets();
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int id = ids[y * width + x];
					boolean flipHorizontally = ((id & FLAG_FLIP_HORIZONTALLY) != 0);
					boolean flipVertically = ((id & FLAG_FLIP_VERTICALLY) != 0);
					boolean flipDiagonally = ((id & FLAG_FLIP_DIAGONALLY) != 0);

					TiledMapTile tile = tilesets.getTile(id & ~MASK_CLEAR);
					if (tile != null) {
						Cell cell = createTileLayerCell(flipHorizontally,
								flipVertically, flipDiagonally);
						cell.setTile(tile);

						// If we're loading the platform level, we also add a
						// random colour to it.
						if (loadingPlatforms) {
							// cell.setColour(new CBColour());
						}

						layer.setCell(x, yUp ? height - 1 - y : y, cell);
					}
				}
			}

			Element properties = element.getChildByName("properties");
			if (properties != null) {
				loadProperties(layer.getProperties(), properties);
			}
			map.getLayers().add(layer);
		}
	}
}
