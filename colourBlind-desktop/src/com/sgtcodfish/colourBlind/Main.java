package com.sgtcodfish.colourBlind;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "ColorBlind :: Global Game Jam";
		cfg.useGL20 = true;
		cfg.width = 800;
		cfg.height = 600;

		boolean debug = false;
		boolean playSound = true;
		boolean glow = false;

		for (String s : args) {
			if ("--debug".equals(s)) {
				debug = true;
			} else if ("--shut-up".equals(s) || "--quiet".equals(s)) {
				playSound = false;
			} else if ("--glow".equals(s)) {
				glow = true;
			}
		}

		new LwjglApplication(new ColourBlindGame(debug, playSound, glow), cfg);
	}
}
