package com.sgtcodfish.colourBlind;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

public class CBCell extends Cell {
	protected CBColour	colour	= null;

	public CBCell() {
		super();
	}

	public CBCell(CBColour nCol) {
		colour = nCol;
	}

	public boolean hasColour() {
		return colour != null;
	}

	public void setColour(CBColour col) {
		colour = col;
	}

	public CBColour getColour() {
		return colour;
	}
}
