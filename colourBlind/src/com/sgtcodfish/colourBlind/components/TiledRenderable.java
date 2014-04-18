package com.sgtcodfish.colourBlind.components;

import java.util.HashMap;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.sgtcodfish.colourBlind.tiled.CBOrthogonalTiledMapRenderer;

/**
 * Holds a TiledMap, which will be rendered in the ColourBlind fashion - that
 * is, all layers except the layer named "platform" are rendered normally, while
 * the "platform" layer is rendered using a specialised shader for showing
 * "true" colours.
 * 
 * Note that the creator of the component is responsible for disposing of the
 * map when finished with it.
 * 
 * It is assumed that the map passed is a valid map; i.e.
 * LevelEntityFactory.isValidMap would return true for the map. If the map is
 * invalid, the behaviour is undefined.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class TiledRenderable extends Component {
	public static final float					DEFAULT_UNIT_SCALE	= 1.0f;

	public final TiledMap						map;
	public final CBOrthogonalTiledMapRenderer	renderer;
	public final float							unitScale;
	public final HashMap<Cell, Color>			platformColours;

	public final TiledMapTileLayer[]			regularLayers;
	public final TiledMapTileLayer				platformLayer;

	/**
	 * Use TiledRenderable(TiledMap); it handles the linking of the map to a
	 * renderer. Use TiledRenderable(TiledMap, float) to set a unit scale.
	 */
	protected TiledRenderable() {
		throw new IllegalStateException(
				"In TiledRenderable() - the component should be immutable, use TiledRenderable(HashMap<>, TiledMap) or TiledRenderable(HashMap<>, TiledMap, float).");
	}

	/**
	 * Creates a TiledRenderable with the given map, and a default scaling
	 * factor which will leave the map unchanged.
	 * 
	 * @param platformColours
	 *        A hash map which contains mappings for platform-layer cells to
	 *        their respective colours. Can be created by
	 *        LevelEntityFactory.generatePlatformColours(TiledMap).
	 * @param map
	 *        The map to render.
	 */
	public TiledRenderable(HashMap<Cell, Color> platformColours, TiledMap map, CBOrthogonalTiledMapRenderer renderer) {
		this(platformColours, map, renderer, DEFAULT_UNIT_SCALE);
	}

	/**
	 * Creates a TiledRenderable component with the given map and unit scale.
	 * Note that the creator of the component is responsible for disposing of
	 * the map when finished with it.
	 * 
	 * @param platformColours
	 *        A hash map which contains mappings for platform-layer cells to
	 *        their respective colours. Can be created by
	 *        LevelEntityFactory.generatePlatformColours(TiledMap).
	 * @param map
	 *        The map which will be rendered.
	 * @param unitScale
	 *        The scaling factor for the map.
	 */
	public TiledRenderable(HashMap<Cell, Color> platformColours, TiledMap map, CBOrthogonalTiledMapRenderer renderer,
			float unitScale) {
		this.map = map;
		this.unitScale = unitScale;
		this.platformColours = platformColours;
		this.renderer = renderer;

		// We do some code theatrics to get the members to all stay final, but
		// it's worth it.
		MapLayers layers = map.getLayers();
		platformLayer = (TiledMapTileLayer) map.getLayers().get("platforms");

		if (platformLayer == null) {
			throw new GdxRuntimeException("No platform layer found in map passed to TiledRenderable. Don't do that.");
		}

		regularLayers = new TiledMapTileLayer[(layers.getCount() - 1)];
		boolean platFound = false;

		for (int i = 0; i < layers.getCount(); i++) {
			if ("platforms".equals(layers.get(i).getName())) {
				if (platFound) {
					throw new GdxRuntimeException(
							"Two platform layers found in map passed to TiledRenderable. Don't do that.");
				} else {
					platFound = true;
				}

				continue;
			} else {
				regularLayers[i - (platFound ? 1 : 0)] = (TiledMapTileLayer) layers.get(i);
			}
		}
	}
}
