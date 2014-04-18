package com.sgtcodfish.colourBlind.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
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

	public OrthographicCamera			camera	= null;
	public Batch						batch	= null;
	public ShaderProgram				program	= null;

	/**
	 * Creates a new TiledMapRenderingSystem with the given aspect, camera,
	 * shader and batch. You probably want the
	 * TiledMapRenderingSystem(OrthographicCamera, Batch, ShaderProgram) form,
	 * not this.
	 * 
	 * @param aspect
	 *        The aspect to use.
	 * @param camera
	 *        The camera that describes the view of the scene.
	 * @param batch
	 *        The batch to draw to.
	 * @param program
	 *        The shader to use, should support rendering platforms.
	 */
	public TiledMapRenderingSystem(Aspect aspect, OrthographicCamera camera, Batch batch, ShaderProgram program) {
		super(aspect);

		if (batch != null) {
			this.batch = batch;
		} else {
			throw new IllegalArgumentException(
					"Call to TiledMapRenderingSystem(Aspect, OrthographicCamera, Batch, ShaderProgram) with null batch. Did you use the wrong constructor?");
		}

		if (program != null) {
			this.program = program;
		} else {
			throw new IllegalArgumentException(
					"Call to TiledMapRenderingSystem(Aspect, OrthographicCamera, Batch, ShaderProgram) with null program. Did you use the wrong constructor?");
		}

		if (camera != null) {
			this.camera = camera;
		} else {
			throw new IllegalArgumentException(
					"Call to TiledMapRenderingSystem(Aspect, OrthographicCamera, Batch, ShaderProgram) with null camera. Did you use the wrong constructor?");
		}
	}

	/**
	 * Preferred constructor, use this one.
	 * 
	 * Creates a TiledMapRenderingSystem with the given sprite batch to render
	 * to, and the given camera and shader to use.
	 * 
	 * @param camera
	 *        The camera which describes the view of the scene.
	 * @param batch
	 *        The batch to which the level should be rendered.
	 * @param program
	 *        The shader to use, should support rendering platforms.
	 */
	@SuppressWarnings("unchecked")
	public TiledMapRenderingSystem(OrthographicCamera camera, Batch batch, ShaderProgram program) {
		this(Aspect.getAspectForAll(TiledRenderable.class, Position.class), camera, batch, program);
	}

	/**
	 * Throws an exception, do not use.
	 */
	public TiledMapRenderingSystem() {
		this(null, null, null, null);
	}

	@Override
	protected void process(Entity e) {
		TiledRenderable t = trm.get(e);

		batch.begin();
		batch.setShader(null);

		for (TiledMapTileLayer layer : t.regularLayers) {
			t.renderer.renderTileLayer((TiledMapTileLayer) layer);
		}

		batch.setShader(program);
		world.getSystem(FlashlightSystem.class).setupShaderUniforms(program);
		t.renderer.renderTileLayer(t.platformLayer);
		batch.end();
	}

	@Override
	protected void begin() {
		batch.setProjectionMatrix(camera.combined);
	}
}
