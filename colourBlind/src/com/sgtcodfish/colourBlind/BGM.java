package com.sgtcodfish.colourBlind;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class BGM {
	private Music	music	= null;

	public void create() {
		String fname = "data/audio/Techno_Dream.mp3";
		FileHandle fh = Gdx.files.internal(fname);
		if (fh.exists()) {
			Gdx.app.debug("AUDIO_LOAD", "Audio file exists: " + fname);
		}

		music = Gdx.audio.newMusic(fh);
		if (music == null) {
			throw new GdxRuntimeException("Could not load " + fname + ".");
		} else {
			Gdx.app.debug("AUDIO_LOAD", "Loaded audio file.");

			music.setLooping(true);
			music.setVolume(0.5f);
		}
	}

	public void toggle() {
		if (music != null) {
			if (music.isPlaying()) {
				music.pause();
			} else {
				music.play();
			}
		}
	}

	public void play() {
		if (music != null) {
			music.play();
		}
	}

	public void pause() {
		if (music != null) {
			music.pause();
		}
	}

	public void dispose() {
		if (music != null) {
			music.dispose();
		}
	}
}
