package com.mgdx.examples.rain_hunter.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mgdx.examples.rain_hunter.RainHunter;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Rain Hunter";
		config.width = 800;
		config.height = 480;

		new LwjglApplication(new RainHunter(), config);
	}
}