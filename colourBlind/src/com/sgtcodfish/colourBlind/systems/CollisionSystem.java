package com.sgtcodfish.colourBlind.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.sgtcodfish.colourBlind.components.Position;
import com.sgtcodfish.colourBlind.components.Solid;
import com.sgtcodfish.colourBlind.components.Velocity;

/**
 * Checks to see if solid entities are in contact with other solid entities.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class CollisionSystem extends EntityProcessingSystem {
	@Mapper
	ComponentMapper<Position>	pm					= null;

	@Mapper
	ComponentMapper<Velocity>	vm					= null;

	@Mapper
	ComponentMapper<Solid>		sm					= null;

	public boolean[]			staticCollidables	= null;
	public boolean[]			collisionMap		= null;

	private TiledMap			map					= null;

	/**
	 * Use this constructor; creates a CollisionSystem and sets the collision
	 * map based on the given level.
	 * 
	 * @param map
	 *        The level with which to initiate the component map. All the tiles
	 *        from the "level" layer will be added to the staticCollidables.
	 */
	@SuppressWarnings("unchecked")
	public CollisionSystem(TiledMap map) {
		this(map, Aspect.getAspectForAll(Position.class, Solid.class));
	}

	public CollisionSystem(TiledMap map, Aspect aspect) {
		super(aspect);
		setMap(map);
	}

	public void setMap(TiledMap map) {
		this.map = map;
		initCollidables();
	}

	private void initCollidables() {
		TiledMapTileLayer levelLayer = ((TiledMapTileLayer) map.getLayers().get("level"));
		final int widthInTiles = levelLayer.getWidth();
		final int heightInTiles = levelLayer.getHeight();

		final int size = widthInTiles * heightInTiles;

		staticCollidables = new boolean[size];

		// for every tile in the level layer, make it collidable always.
		for (int y = 0; y < heightInTiles; y++) {
			for (int x = 0; x < widthInTiles; x++) {
				staticCollidables[(y * widthInTiles) + x] = (levelLayer.getCell(x, y) != null);
			}
		}
	}

	@Override
	protected void process(Entity e) {
		Vector2 p = pm.get(e).position;
		Vector2 v = vm.get(e).velocity;
		Solid s = sm.get(e);

		if (p.x <= 32.0f || p.x >= 800.0f) {
			v.x = 0.0f;
			p.x = 32.0f;
		}

		if (p.y <= 32.0f) {
			v.y = 0.0f;
			s.grounded = true;
			p.y = 32.0f;
		} else {
			s.grounded = false;
		}
	}
}
