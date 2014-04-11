package com.sgtcodfish.colourBlind.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
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
	public ShaderProgram				program	= null;

	/**
	 * Creates a new TiledMapRenderingSystem with the given aspect and batch.
	 * You probably want the TiledMapRenderingSystem(Batch) form, not this.
	 * 
	 * @param aspect
	 *        The aspect to use.
	 * @param batch
	 *        The batch to draw to.
	 * @param program
	 *        The shader to use, should support rendering platforms.
	 */
	public TiledMapRenderingSystem(Aspect aspect, Batch batch, ShaderProgram program) {
		super(aspect);

		if (batch != null) {
			this.batch = batch;
		} else {
			throw new IllegalArgumentException(
					"Call to TiledMapRenderingSystem(Aspect, Batch, ShaderProgram) with null batch. Did you use the wrong constructor?");
		}

		if (program != null) {
			this.program = program;
		} else {
			throw new IllegalArgumentException(
					"Call to TiledMapRenderingSystem(Aspect, Batch, ShaderProgram) with null program. Did you use the wrong constructor?");
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
	 * @param program
	 *        The shader to use, should support rendering platforms.
	 */
	@SuppressWarnings("unchecked")
	public TiledMapRenderingSystem(Batch batch, ShaderProgram program) {
		this(Aspect.getAspectForAll(TiledRenderable.class, Position.class), batch, program);
	}

	/**
	 * Throws an exception, do not use.
	 */
	public TiledMapRenderingSystem() {
		this(null, null, null);
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

		batch.setShader(program);
		t.renderer.renderTileLayer(platformLayer);
		batch.end();

	}

}
