package com.sgtcodfish.colourBlind.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.sgtcodfish.colourBlind.components.Position;
import com.sgtcodfish.colourBlind.components.TiledRenderable;

/**
 * Handles rendering of TiledRenderable-component-including Entities.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class TiledMapRenderingSystem extends EntityProcessingSystem {
	@Mapper
	ComponentMapper<Position>			pm		= null;

	@Mapper
	ComponentMapper<TiledRenderable>	trm		= null;

	public Batch						batch	= null;

	/**
	 * Creates a new TiledMapRenderingSystem with the given aspect and batch.
	 * You probably want the TiledMapRenderingSystem(Batch) form, not this.
	 * 
	 * @param aspect
	 *        The aspect to use.
	 * @param batch
	 *        The batch to draw to.
	 */
	public TiledMapRenderingSystem(Aspect aspect, Batch batch) {
		super(aspect);

		if (batch != null) {
			this.batch = batch;
		} else {
			throw new IllegalArgumentException(
					"Call to TiledMapRenderingSystem(Aspect, Batch) with null batch. Did you use the wrong constructor?");
		}
	}

	/**
	 * Preferred constructor, use this one.
	 * 
	 * Creates a TiledMapRenderingSystem with the given sprite batch to render
	 * to.
	 * 
	 * @param batch
	 *        The batch to which the level should be rendered.
	 */
	@SuppressWarnings("unchecked")
	public TiledMapRenderingSystem(Batch batch) {
		this(Aspect.getAspectForAll(TiledRenderable.class, Position.class), batch);
	}

	/**
	 * Throws an exception, do not use.
	 */
	public TiledMapRenderingSystem() {
		this(null, null);
	}

	@Override
	protected void process(Entity e) {
		TiledRenderable t = trm.get(e);
		Vector2 p = pm.get(e).position;
		TiledMapTileLayer platformLayer = null;

		batch.begin();
		batch.setShader(null);
		for (MapLayer layer : t.map.getLayers()) {
			if ("platforms".equals(layer.getName())) {
				platformLayer = (TiledMapTileLayer) layer;
			} else {
				t.renderer.renderTileLayer((TiledMapTileLayer) layer);
			}
		}

		batch.setShader(colourShader);
		t.renderer.renderTileLayer(platformLayer);
		batch.end();

	}

}
