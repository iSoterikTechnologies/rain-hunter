package com.mgdx.examples.rain_hunter;

import com.isoterik.mgdx.MinGdxGame;
import com.isoterik.mgdx.Scene;
import com.isoterik.mgdx.m2d.scenes.transition.SceneTransitions;

public class RainHunter extends MinGdxGame {
	@Override
	protected Scene initGame() {
		splashTransition = SceneTransitions.fade(1f);
		return new Scene();
	}
}
