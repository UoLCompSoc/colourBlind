package com.sgtcodfish.colourBlind.components;

import com.artemis.Component;
import com.badlogic.gdx.maps.tiled.TiledMap;
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
 * @author Ashley Davis (SgtCoDFish)
 */
public class TiledRenderable extends Component {
	public static final float					DEFAULT_UNIT_SCALE	= 1.0f;

	public final TiledMap						map;
	public final CBOrthogonalTiledMapRenderer	renderer;
	public final float							unitScale;

	/**
	 * Use TiledRenderable(TiledMap); it handles the linking of the map to a
	 * renderer. Use TiledRenderable(TiledMap, float) to set a unit scale.
	 */
	protected TiledRenderable() {
		throw new IllegalStateException(
				"In TiledRenderable() - the component should be immutable, use TiledRenderable(TiledMap) or TiledRenderable(TiledMap, float).");
	}

	/**
	 * Creates a TiledRenderable with the given map, and a default scaling
	 * factor which will leave the map unchanged.
	 * 
	 * @param map
	 *        The map to render.
	 */
	public TiledRenderable(TiledMap map) {
		this(map, DEFAULT_UNIT_SCALE);
	}

	/**
	 * Creates a TiledRenderable component with the given map and unit scale.
	 * Note that the creator of the component is responsible for disposing of
	 * the map when finished with it.
	 * 
	 * @param map
	 *        The map which will be rendered.
	 * @param unitScale
	 *        The scaling factor for the map.
	 */
	public TiledRenderable(TiledMap map, float unitScale) {
		this.map = map;
		this.unitScale = unitScale;
		this.renderer = new CBOrthogonalTiledMapRenderer(map, unitScale);
	}
}
