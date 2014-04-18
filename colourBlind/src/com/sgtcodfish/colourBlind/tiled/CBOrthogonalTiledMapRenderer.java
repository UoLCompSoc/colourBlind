package com.sgtcodfish.colourBlind.tiled;

import static com.badlogic.gdx.graphics.g2d.Batch.C1;
import static com.badlogic.gdx.graphics.g2d.Batch.C2;
import static com.badlogic.gdx.graphics.g2d.Batch.C3;
import static com.badlogic.gdx.graphics.g2d.Batch.C4;
import static com.badlogic.gdx.graphics.g2d.Batch.U1;
import static com.badlogic.gdx.graphics.g2d.Batch.U2;
import static com.badlogic.gdx.graphics.g2d.Batch.U3;
import static com.badlogic.gdx.graphics.g2d.Batch.U4;
import static com.badlogic.gdx.graphics.g2d.Batch.V1;
import static com.badlogic.gdx.graphics.g2d.Batch.V2;
import static com.badlogic.gdx.graphics.g2d.Batch.V3;
import static com.badlogic.gdx.graphics.g2d.Batch.V4;
import static com.badlogic.gdx.graphics.g2d.Batch.X1;
import static com.badlogic.gdx.graphics.g2d.Batch.X2;
import static com.badlogic.gdx.graphics.g2d.Batch.X3;
import static com.badlogic.gdx.graphics.g2d.Batch.X4;
import static com.badlogic.gdx.graphics.g2d.Batch.Y1;
import static com.badlogic.gdx.graphics.g2d.Batch.Y2;
import static com.badlogic.gdx.graphics.g2d.Batch.Y3;
import static com.badlogic.gdx.graphics.g2d.Batch.Y4;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class CBOrthogonalTiledMapRenderer extends OrthogonalTiledMapRenderer {
	private static final float		DEFAULT_UNIT_SCALE	= 1.0f;
	private float[]					vertices			= new float[20];

	private HashMap<Cell, Color>	platformColours		= null;

	public CBOrthogonalTiledMapRenderer(HashMap<Cell, Color> platformColours, TiledMap map) {
		this(platformColours, map, DEFAULT_UNIT_SCALE, new SpriteBatch());
	}

	public CBOrthogonalTiledMapRenderer(HashMap<Cell, Color> platformColours, TiledMap map, Batch batch) {
		this(platformColours, map, DEFAULT_UNIT_SCALE, batch);
	}

	public CBOrthogonalTiledMapRenderer(HashMap<Cell, Color> platformColours, TiledMap map, float unitScale) {
		this(platformColours, map, unitScale, new SpriteBatch());
	}

	public CBOrthogonalTiledMapRenderer(HashMap<Cell, Color> platformColours, TiledMap map, float unitScale, Batch batch) {
		super(map, unitScale, batch);
		if (platformColours == null) {
			throw new IllegalArgumentException(
					"Trying to create CBOrthogonalTiledMapRenderer with no platform colours. Did you want a regular OrthogonalTiledMapRenderer?");
		} else {
			this.platformColours = platformColours;
		}
	}

	/**
	 * An overridden render function for handling rendering of "coloured"
	 * platforms which only reveal their colour under a shader-driven
	 * flashlight.
	 */
	@Override
	public void renderTileLayer(TiledMapTileLayer layer) {
		final Color batchColor = spriteBatch.getColor();
		final float color = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b,
				batchColor.a * layer.getOpacity());

		final int layerWidth = layer.getWidth();
		final int layerHeight = layer.getHeight();

		final float layerTileWidth = layer.getTileWidth() * unitScale;
		final float layerTileHeight = layer.getTileHeight() * unitScale;

		final int col1 = Math.max(0, (int) (viewBounds.x / layerTileWidth));
		final int col2 = Math.min(layerWidth,
				(int) ((viewBounds.x + viewBounds.width + layerTileWidth) / layerTileWidth));

		final int row1 = Math.max(0, (int) (viewBounds.y / layerTileHeight));
		final int row2 = Math.min(layerHeight,
				(int) ((viewBounds.y + viewBounds.height + layerTileHeight) / layerTileHeight));

		float y = row1 * layerTileHeight;
		float xStart = col1 * layerTileWidth;
		final float[] vertices = this.vertices;

		for (int row = row1; row < row2; row++) {
			float x = xStart;
			for (int col = col1; col < col2; col++) {
				final Cell cell = layer.getCell(col, row);
				if (cell == null) {
					x += layerTileWidth;
					continue;
				}
				final TiledMapTile tile = cell.getTile();

				if (tile != null) {
					final boolean flipX = cell.getFlipHorizontally();
					final boolean flipY = cell.getFlipVertically();
					final int rotations = cell.getRotation();

					TextureRegion region = tile.getTextureRegion();

					float x1 = x;
					float y1 = y;
					float x2 = x1 + region.getRegionWidth() * unitScale;
					float y2 = y1 + region.getRegionHeight() * unitScale;

					float u1 = region.getU();
					float v1 = region.getV2();
					float u2 = region.getU2();
					float v2 = region.getV();

					Color cb = platformColours.get(cell);
					if (cb != null) {
						final float platF = Color.toFloatBits(cb.r, cb.g, cb.b, cb.a * layer.getOpacity());
						vertices[C1] = platF;
						vertices[C2] = platF;
						vertices[C3] = platF;
						vertices[C4] = platF;
					} else {
						vertices[C1] = color;
						vertices[C2] = color;
						vertices[C3] = color;
						vertices[C4] = color;
					}

					vertices[X1] = x1;
					vertices[Y1] = y1;
					vertices[U1] = u1;
					vertices[V1] = v1;

					vertices[X2] = x1;
					vertices[Y2] = y2;
					vertices[U2] = u1;
					vertices[V2] = v2;

					vertices[X3] = x2;
					vertices[Y3] = y2;
					vertices[U3] = u2;
					vertices[V3] = v2;

					vertices[X4] = x2;
					vertices[Y4] = y1;
					vertices[U4] = u2;
					vertices[V4] = v1;

					if (flipX) {
						float temp = vertices[U1];
						vertices[U1] = vertices[U3];
						vertices[U3] = temp;
						temp = vertices[U2];
						vertices[U2] = vertices[U4];
						vertices[U4] = temp;
					}
					if (flipY) {
						float temp = vertices[V1];
						vertices[V1] = vertices[V3];
						vertices[V3] = temp;
						temp = vertices[V2];
						vertices[V2] = vertices[V4];
						vertices[V4] = temp;
					}
					if (rotations != 0) {
						switch (rotations) {
						case Cell.ROTATE_90: {
							float tempV = vertices[V1];
							vertices[V1] = vertices[V2];
							vertices[V2] = vertices[V3];
							vertices[V3] = vertices[V4];
							vertices[V4] = tempV;

							float tempU = vertices[U1];
							vertices[U1] = vertices[U2];
							vertices[U2] = vertices[U3];
							vertices[U3] = vertices[U4];
							vertices[U4] = tempU;
							break;
						}
						case Cell.ROTATE_180: {
							float tempU = vertices[U1];
							vertices[U1] = vertices[U3];
							vertices[U3] = tempU;
							tempU = vertices[U2];
							vertices[U2] = vertices[U4];
							vertices[U4] = tempU;
							float tempV = vertices[V1];
							vertices[V1] = vertices[V3];
							vertices[V3] = tempV;
							tempV = vertices[V2];
							vertices[V2] = vertices[V4];
							vertices[V4] = tempV;
							break;
						}
						case Cell.ROTATE_270: {
							float tempV = vertices[V1];
							vertices[V1] = vertices[V4];
							vertices[V4] = vertices[V3];
							vertices[V3] = vertices[V2];
							vertices[V2] = tempV;

							float tempU = vertices[U1];
							vertices[U1] = vertices[U4];
							vertices[U4] = vertices[U3];
							vertices[U3] = vertices[U2];
							vertices[U2] = tempU;
							break;
						}
						}
					}
					spriteBatch.draw(region.getTexture(), vertices, 0, 20);
				}
				x += layerTileWidth;
			}
			y += layerTileHeight;
		}
	}
}
