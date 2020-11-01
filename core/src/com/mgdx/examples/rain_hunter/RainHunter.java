package com.mgdx.examples.rain_hunter;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;
import com.isoterik.mgdx.*;
import com.isoterik.mgdx.audio.AudioManager;
import com.isoterik.mgdx.input.KeyCodes;
import com.isoterik.mgdx.io.GameAssetsLoader;
import com.isoterik.mgdx.m2d.scenes.transition.SceneTransitions;
import com.isoterik.mgdx.utils.WorldUnits;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class RainHunter extends MinGdxGame {
	@Override
	protected Scene initGame() {
		GameAssetsLoader assetsLoader = MinGdx.instance().assets;
		assetsLoader.enqueueAsset("bucket.png", Texture.class);
		assetsLoader.enqueueAsset("droplet.png", Texture.class);
		assetsLoader.enqueueAsset("drop.wav", Sound.class);
		assetsLoader.enqueueAsset("rain.mp3", Music.class);

		assetsLoader.loadAssetsNow();

		splashTransition = SceneTransitions.fade(1f);
		return new GamePlayScene();
	}

	private static class GamePlayScene extends Scene {
		private long lastDropTime;

		private final WorldUnits worldUnits;
		private final GameAssetsLoader assetsLoader;
		private final GameObject bucket;

		private final AudioManager audioManager;
		private final Sound dropSound;
		private final Music rainMusic;

		public GamePlayScene() {
			setBackgroundColor(new Color(0.1f, 0.1f, 0.2f, 1.0f));

			worldUnits = new WorldUnits(800f, 480f, 64f);
			mainCamera.setup(new ExtendViewport(worldUnits.getWorldWidth(),
					worldUnits.getWorldHeight(), mainCamera.getCamera()), worldUnits);

			assetsLoader = MinGdx.instance().assets;

			bucket = newSpriteObject(assetsLoader.getTexture("bucket.png"));
			Transform bucketTransform = bucket.transform;
			bucketTransform.position.x = (worldUnits.getWorldWidth() - bucketTransform.size.x) / 2f;
			bucketTransform.position.y = worldUnits.toWorldUnit(20f);

			bucket.addComponent(new BucketController());
			addGameObject(bucket);

			spawnRaindrop();

			rainMusic = assetsLoader.getMusic("rain.mp3");
			dropSound = assetsLoader.getSound("drop.wav");

			audioManager = AudioManager.instance();
			audioManager.playMusic(rainMusic, 1f, true);
		}

		private void spawnRaindrop() {
			final GameObject raindrop = newSpriteObject(assetsLoader.getTexture("droplet.png"));
			final Transform transform = raindrop.transform;

			transform.position.x = MathUtils.random(0,
					worldUnits.getWorldWidth() - transform.size.x);
			transform.position.y = worldUnits.getWorldHeight();

			addGameObject(raindrop);

			final Rectangle bucketBounds = new Rectangle(bucket.transform.position.x,
					bucket.transform.position.y, bucket.transform.size.x,
					bucket.transform.size.y);
			final Rectangle raindropBounds = new Rectangle(transform.position.x, transform.position.y,
					transform.size.x, transform.size.y);

			raindrop.addComponent(new Component(){
				@Override
				public void update(float deltaTime) {
					float speed = worldUnits.toWorldUnit(200);
					transform.position.y -= speed * deltaTime;

					if (transform.position.y + transform.size.y < 0)
						removeGameObject(raindrop);

					if (TimeUtils.timeSinceMillis(lastDropTime) > 1000)
						spawnRaindrop();
				}

				@Override
				public void lateUpdate(float deltaTime) {
					bucketBounds.x = bucket.transform.position.x;
					bucketBounds.y = bucket.transform.position.y;

					raindropBounds.x = raindrop.transform.position.x;
					raindropBounds.y = raindrop.transform.position.y;

					if (raindropBounds.overlaps(bucketBounds)) {
						removeGameObject(raindrop);

						audioManager.playSound(dropSound, 1f);
					}
				}
			});

			lastDropTime = TimeUtils.millis();
		}
	}

	private static class BucketController extends Component {
		@Override
		public void update(float deltaTime) {
			Transform transform = gameObject.transform;
			WorldUnits worldUnits = scene.getMainCamera().getWorldUnits();

			if (input.isTouched()) {
				float touchX = input.getTouchedX();
				touchX -= transform.size.x / 2f;

				transform.position.x = touchX;
			}

			float moveSpeed = worldUnits.toWorldUnit(200);

			if (input.isKeyDown(KeyCodes.LEFT))
				transform.position.x -= moveSpeed * deltaTime;
			if (input.isKeyDown(KeyCodes.RIGHT))
				transform.position.x += moveSpeed * deltaTime;

			if (transform.position.x < 0)
				transform.position.x = 0;
			if (transform.position.x > worldUnits.getWorldWidth() - transform.size.x)
				transform.position.x = worldUnits.getWorldWidth() - transform.size.x;
		}
	}
}