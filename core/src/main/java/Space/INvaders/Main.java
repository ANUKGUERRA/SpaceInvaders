package Space.INvaders;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class Main extends Game {
    public static Music backgroundMusic; // Static to persist across screens
    public static Sound shootSound; // Static sound for shooting
    public static float sfxVolume = 1.0f; // Static variable to store SFX volume

    @Override
    public void create() {
        // Load and start background music
        try {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Glorious_Morning.mp3"));
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(1.0f); // Default volume
            backgroundMusic.play();
        } catch (Exception e) {
            Gdx.app.error("Main", "Failed to load background music: " + e.getMessage());
        }

        // Load shooting sound effect
        try {
            shootSound = Gdx.audio.newSound(Gdx.files.internal("shootSound.mp3"));
        } catch (Exception e) {
            Gdx.app.error("Main", "Failed to load shoot sound: " + e.getMessage());
        }

        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
        // Dispose of music to free resources
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }
        // Dispose of shoot sound
        if (shootSound != null) {
            shootSound.dispose();
        }
    }
}
