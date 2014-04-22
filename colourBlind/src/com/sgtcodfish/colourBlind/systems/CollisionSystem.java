package com.sgtcodfish.colourBlind.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.sgtcodfish.colourBlind.components.Position;
import com.sgtcodfish.colourBlind.components.Solid;

/**
 * Checks to see if solid entities are in contact with other solid entities.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class CollisionSystem extends EntityProcessingSystem {
	@Mapper
	ComponentMapper<Position>	pm					= null;
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

	}

	@Override
	protected void process(Entity e) {
	}

}
