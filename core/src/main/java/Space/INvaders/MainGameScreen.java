package Space.INvaders;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputAdapter;

public class MainGameScreen extends ScreenAdapter {

    private Game game;

    public MainGameScreen(Game game) {
        this.game = game;
    }

    private SpriteBatch batch;

    // Player
    private Texture playerTexture;
    private Sprite playerSprite;

    // Enemy
    private Texture enemyTexture1;
    private Texture enemyTexture2;
    private Texture bossTexture;
    private Array<Enemy> enemies;
    private float enemyFallSpeed = 1.5f;
    private int enemiesDestroyed = 0;
    private final int ENEMIES_FOR_POWERUP = 5;

    private Texture explosion1, explosion2, explosion3;
    private Array<Texture> explosionTextures;

    private Texture spark1, spark2, spark3, spark4, spark5, spark6, spark7;
    private Array<Texture> sparkTextures;

    // Projectiles
    private Texture red1, red2, red3, red4, red5, red6;
    private Array<Texture> redProjectileTextures;
    private Texture blue1, blue2, blue3, blue4, blue5, blue6;
    private Array<Texture> blueProjectileTextures;

    // Power-up (first)
    private Texture powerupTexture;
    private Sprite powerupSprite;
    private boolean hasFirstPowerup = false;
    private boolean isPowerupAvailable = false;
    private float powerupFallSpeed = 2.0f;

    // Second power-up logic
    private boolean hasSecondPowerup = false;
    private int secondPowerupObtained = 0;
    private final int MAX_SECOND_POWERUP = 6;
    private boolean secondPowerupActive = false;
    private int secondPowerupEnemyIndex = -1;
    private Sprite secondPowerupSprite;
    private boolean isSecondPowerupAvailable = false;
    private float secondPowerupFallSpeed = 2.0f;

    // Boss
    private Sprite bossSprite;
    private boolean isBossActive = false;
    private int bossHealth = 10;
    private float bossMoveTimer = 0f;
    private float bossMoveSpeed = 2f;

    private class Enemy {
        Sprite sprite;
        boolean isZigzag; // Changed from isType2 to isZigzag for clarity
        float initialX;
        float moveTimer;

        public Enemy(Sprite sprite, boolean isZigzag, float initialX) {
            this.sprite = sprite;
            this.isZigzag = isZigzag;
            this.initialX = initialX;
            this.moveTimer = 0f;
        }
    }

    private class Projectile {
        Sprite sprite;
        float animTimer;
        int currentTextureIndex;
        boolean isBlue;

        public Projectile(Sprite sprite, boolean isBlue) {
            this.sprite = sprite;
            this.animTimer = 0f;
            this.currentTextureIndex = 0;
            this.isBlue = isBlue;
        }
    }
    private Array<Projectile> projectiles;
    private float projectileSpeed = 5f;
    private float shootCooldown = 0.2f;
    private float shootTimer = 0f;
    private float projectileAnimationSpeed = 0.15f;
    private int shotsFired = 0;

    private class Explosion {
        Sprite sprite;
        float animTimer;
        int currentTextureIndex;
        boolean isSpark;
        Vector2 position;
        float explosionRadius;

        public Explosion(Vector2 position, boolean isSpark) {
            this.sprite = new Sprite(isSpark ? sparkTextures.get(0) : explosionTextures.get(0));
            this.sprite.setSize(isSpark ? 1.0f : 0.8f, isSpark ? 1.0f : 0.8f);
            this.sprite.setPosition(position.x - this.sprite.getWidth() / 2, position.y - this.sprite.getHeight() / 2);
            this.animTimer = 0f;
            this.currentTextureIndex = 0;
            this.isSpark = isSpark;
            this.position = position;
            this.explosionRadius = isSpark ? 1.5f : 0.8f;
        }
    }
    private Array<Explosion> explosions;
    private float explosionAnimationSpeed = 0.1f;

    private FitViewport viewport;
    private Vector2 touchPos;
    private final int ENEMY_COUNT = 6;

    // UI
    private Stage uiStage;
    private Skin skin;
    private TextButton pauseButton;
    private Label livesLabel, pointsLabel, waveLabel;
    private int lives = 3;
    private int points = 0;
    private int wave = 1;

    // Pause Panel
    private Table pausePanel;
    private boolean isPaused = false;
    private Slider musicSlider, sfxSlider;

    @Override
    public void show() {
        batch = new SpriteBatch();

        // Textures
        try {
            playerTexture = new Texture("Ship_5.png");
            enemyTexture1 = new Texture("Ship_3.png");
            enemyTexture2 = new Texture("Ship_4.png");
            bossTexture = new Texture("Ship_2.png");
            powerupTexture = new Texture("powerUp.png");

            red1 = new Texture("red1.png"); red2 = new Texture("red2.png"); red3 = new Texture("red3.png");
            red4 = new Texture("red4.png"); red5 = new Texture("red5.png"); red6 = new Texture("red6.png");
            blue1 = new Texture("blue1.png"); blue2 = new Texture("blue2.png"); blue3 = new Texture("blue3.png");
            blue4 = new Texture("blue4.png"); blue5 = new Texture("blue5.png"); blue6 = new Texture("blue6.png");

            spark1 = new Texture("spark1.png");
            spark2 = new Texture("spark2.png");
            spark3 = new Texture("spark3.png");
            spark4 = new Texture("spark4.png");
            spark5 = new Texture("spark5.png");
            spark6 = new Texture("spark6.png");
            spark7 = new Texture("spark7.png");
            sparkTextures = new Array<>();
            sparkTextures.addAll(spark1, spark2, spark3, spark4, spark5, spark6, spark7);
        } catch (Exception e) {
            Gdx.app.error("MainGameScreen", "Failed to load textures: " + e.getMessage());
        }

        explosions = new Array<>();

        redProjectileTextures = new Array<>();
        redProjectileTextures.addAll(red1, red2, red3, red4, red5, red6);
        blueProjectileTextures = new Array<>();
        blueProjectileTextures.addAll(blue1, blue2, blue3, blue4, blue5, blue6);

        playerSprite = new Sprite(playerTexture);
        playerSprite.setSize(1, 1);
        playerSprite.setPosition(4 - 0.5f, 0.5f);

        powerupSprite = new Sprite(powerupTexture);
        powerupSprite.setSize(0.7f, 0.7f);
        secondPowerupSprite = new Sprite(powerupTexture);
        secondPowerupSprite.setSize(0.7f, 0.7f);

        bossSprite = new Sprite(bossTexture);
        bossSprite.setSize(2f, 2f);
        bossSprite.setOriginCenter();
        bossSprite.setRotation(180f);

        viewport = new FitViewport(8, 5);
        touchPos = new Vector2();

        // UI Setup
        uiStage = new Stage(new ScreenViewport());

        try {
            skin = new Skin(Gdx.files.internal("neon-ui.json"));
        } catch (Exception e) {
            Gdx.app.error("MainGameScreen", "Failed to load skin: " + e.getMessage());
            skin = new Skin();
            skin.add("font", new com.badlogic.gdx.graphics.g2d.BitmapFont());
        }

        livesLabel = new Label("Lives: " + lives, skin);
        livesLabel.setFontScale(2.5f);
        pointsLabel = new Label("Points: " + points, skin);
        pointsLabel.setFontScale(2.5f);
        waveLabel = new Label("Wave: " + wave, skin);
        waveLabel.setFontScale(2.5f);

        TextButton.TextButtonStyle neonStyle = skin.has("neon", TextButton.TextButtonStyle.class)
            ? skin.get("neon", TextButton.TextButtonStyle.class)
            : skin.get("default", TextButton.TextButtonStyle.class);

        pauseButton = new TextButton("PAUSE", neonStyle);
        pauseButton.getLabel().setFontScale(2.2f);
        pauseButton.setTransform(true);
        pauseButton.setColor(Color.CYAN);
        pauseButton.setTouchable(Touchable.enabled);

        addButtonAnimation(pauseButton, () -> {
            Gdx.app.log("MainGameScreen", "Pause button clicked");
            isPaused = true;
            createPausePanel();
        });

        Table uiTable = new Table();
        uiTable.setFillParent(true);
        uiTable.top().padTop(20).padLeft(20).padRight(20);
        uiTable.add(livesLabel).left().padRight(40);
        uiTable.add(pointsLabel).left().padRight(40);
        uiTable.add(waveLabel).left().expandX();
        uiTable.add(pauseButton).right().width(200).height(80);

        uiStage.addActor(uiTable);
        uiTable.toFront();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        multiplexer.addProcessor(new GameInputProcessor());
        Gdx.input.setInputProcessor(multiplexer);

        updateUI();

        enemies = new Array<>();
        spawnEnemyHorde();

        projectiles = new Array<>();
    }

    private class GameInputProcessor extends InputAdapter {
        @Override
        public boolean keyDown(int keycode) {
            if (keycode == Input.Keys.P && !isPaused) {
                Gdx.app.log("MainGameScreen", "P key pressed, opening pause menu");
                isPaused = true;
                createPausePanel();
                return true;
            } else if (keycode == Input.Keys.ESCAPE && isPaused) {
                Gdx.app.log("MainGameScreen", "ESC key pressed, resuming game");
                isPaused = false;
                if (pausePanel != null) pausePanel.remove();
                return true;
            }
            return false;
        }
    }

    private void addButtonAnimation(final TextButton button, final Runnable onClick) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainGameScreen", "Button clicked: " + button.getText());
                onClick.run();
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                Gdx.app.log("MainGameScreen", "Mouse entered button: " + button.getText());
                button.addAction(Actions.scaleTo(1.15f, 1.15f, 0.15f));
                button.addAction(Actions.alpha(1f, 0.15f));
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                Gdx.app.log("MainGameScreen", "Mouse exited button: " + button.getText());
                button.addAction(Actions.scaleTo(1f, 1f, 0.15f));
                button.addAction(Actions.alpha(0.85f, 0.15f));
            }
        });
        button.addAction(Actions.forever(
            Actions.sequence(
                Actions.alpha(0.85f, 0.8f),
                Actions.alpha(1f, 0.8f)
            )
        ));
        button.getLabel().addAction(Actions.forever(
            Actions.sequence(
                Actions.color(Color.CYAN, 1.5f),
                Actions.color(Color.PINK, 1.5f),
                Actions.color(Color.YELLOW, 1.5f),
                Actions.color(new Color(0.4f, 0.4f, 1f, 1f), 1.5f),
                Actions.color(new Color(1f, 0.3f, 1f, 1f), 1.5f),
                Actions.color(Color.CYAN, 1.5f)
            )
        ));
    }

    @Override
    public void render(float delta) {
        if (isPaused) {
            draw();
            uiStage.act(delta);
            uiStage.draw();
            return;
        }

        handleInput();
        updateEnemies();
        updateProjectiles();
        updatePowerUp();
        updateExplosions(delta);
        draw();

        uiStage.act(delta);
        uiStage.draw();
    }

    @Override
    public void pause() {
        if (!isPaused) {
            Gdx.app.log("MainGameScreen", "Game paused due to app losing focus");
            isPaused = true;
            createPausePanel();
        }
    }

    @Override
    public void resume() {
        Gdx.app.log("MainGameScreen", "Game resumed, but staying paused until user action");
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        playerTexture.dispose();
        enemyTexture1.dispose();
        enemyTexture2.dispose();
        bossTexture.dispose();
        powerupTexture.dispose();
        red1.dispose(); red2.dispose(); red3.dispose(); red4.dispose(); red5.dispose(); red6.dispose();
        blue1.dispose(); blue2.dispose(); blue3.dispose(); blue4.dispose(); blue5.dispose(); blue6.dispose();
        if (explosion1 != null) explosion1.dispose();
        if (explosion2 != null) explosion2.dispose();
        if (explosion3 != null) explosion3.dispose();
        if (spark1 != null) spark1.dispose();
        if (spark2 != null) spark2.dispose();
        if (spark3 != null) spark3.dispose();
        if (spark4 != null) spark4.dispose();
        if (spark5 != null) spark5.dispose();
        if (spark6 != null) spark6.dispose();
        if (spark7 != null) spark7.dispose();
        uiStage.dispose();
        if (skin != null) skin.dispose();
    }

    private void createPausePanel() {
        if (pausePanel != null) pausePanel.remove();

        pausePanel = new Table(skin);
        pausePanel.setBackground(skin.getDrawable("window"));
        pausePanel.setSize(500, 350);
        pausePanel.setPosition(
            (Gdx.graphics.getWidth() - pausePanel.getWidth()) / 2f,
            (Gdx.graphics.getHeight() - pausePanel.getHeight()) / 2f
        );

        Label pauseTitle = new Label("PAUSED", skin);
        pauseTitle.setFontScale(2.5f);
        pauseTitle.setColor(Color.CYAN);

        TextButton.TextButtonStyle neonStyle = skin.has("neon", TextButton.TextButtonStyle.class)
            ? skin.get("neon", TextButton.TextButtonStyle.class)
            : skin.get("default", TextButton.TextButtonStyle.class);

        TextButton resumeButton = new TextButton("Resume", neonStyle);
        resumeButton.getLabel().setFontScale(2.2f);
        resumeButton.setTransform(true);
        resumeButton.setTouchable(Touchable.enabled);
        addButtonAnimation(resumeButton, () -> {
            isPaused = false;
            if (Main.backgroundMusic != null) {
                Main.backgroundMusic.setVolume(musicSlider.getValue());
            }
            Main.sfxVolume = sfxSlider.getValue();
            pausePanel.remove();
        });

        TextButton menuButton = new TextButton("Main Menu", neonStyle);
        menuButton.getLabel().setFontScale(2.2f);
        menuButton.setTransform(true);
        menuButton.setTouchable(Touchable.enabled);
        addButtonAnimation(menuButton, () -> game.setScreen(new MainMenuScreen(game)));

        musicSlider = new Slider(0f, 1f, 0.01f, false, skin);
        sfxSlider = new Slider(0f, 1f, 0.01f, false, skin);
        musicSlider.setValue(Main.backgroundMusic != null ? Main.backgroundMusic.getVolume() : 1f);
        sfxSlider.setValue(Main.sfxVolume);

        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (Main.backgroundMusic != null) {
                    Main.backgroundMusic.setVolume(musicSlider.getValue());
                }
            }
        });

        sfxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                Main.sfxVolume = sfxSlider.getValue();
            }
        });

        Label musicLabel = new Label("Music Volume", skin);
        musicLabel.setColor(Color.CYAN);
        Label sfxLabel = new Label("SFX Volume", skin);
        sfxLabel.setColor(Color.CYAN);

        Table slidersTable = new Table();
        slidersTable.add(musicLabel).left().padRight(10);
        slidersTable.add(musicSlider).width(200).row();
        slidersTable.add(sfxLabel).left().padRight(10).padTop(15);
        slidersTable.add(sfxSlider).width(200).padTop(15).row();

        pausePanel.pad(20);
        pausePanel.add(pauseTitle).colspan(2).center().padBottom(30).row();
        pausePanel.add(resumeButton).width(180).padBottom(20).colspan(2).row();
        pausePanel.add(menuButton).width(180).padBottom(30).colspan(2).row();
        pausePanel.add(slidersTable).colspan(2).center();

        pausePanel.addAction(Actions.forever(
            Actions.sequence(
                Actions.color(new Color(1f, 1f, 1f, 0.85f), 1.5f),
                Actions.color(new Color(1f, 1f, 1f, 1f), 1.5f)
            )
        ));

        pausePanel.setVisible(true);
        uiStage.addActor(pausePanel);
        pausePanel.toFront();
    }

    private void updateUI() {
        livesLabel.setText("Lives: " + lives);
        pointsLabel.setText("Points: " + points);
        waveLabel.setText("Wave: " + wave);
    }

    private void spawnEnemyHorde() {
        enemies.clear();
        float spacing = 0.2f;
        float enemyWidth = 1f;
        float totalWidth = ENEMY_COUNT * enemyWidth + (ENEMY_COUNT - 1) * spacing;
        float startX = (viewport.getWorldWidth() - totalWidth) / 2f;
        float y = viewport.getWorldHeight() - enemyWidth - 0.2f;

        // Alternate enemy types based on wave number
        boolean isZigzagHorde = (wave % 2 == 0); // Even waves: zigzag enemies, Odd waves: straight-down enemies
        Texture enemyTexture = isZigzagHorde ? enemyTexture2 : enemyTexture1;

        for (int i = 0; i < ENEMY_COUNT; i++) {
            Sprite enemy = new Sprite(enemyTexture);
            enemy.setSize(enemyWidth, enemyWidth);
            enemy.setOriginCenter(); // Center origin for rotation
            float x = startX + i * (enemyWidth + spacing);
            enemy.setPosition(x, y);
            enemy.setRotation(180f); // Apply rotation as per previous request
            enemies.add(new Enemy(enemy, isZigzagHorde, x));
        }

        if (hasFirstPowerup && secondPowerupObtained < MAX_SECOND_POWERUP && !isSecondPowerupAvailable) {
            secondPowerupActive = true;
            secondPowerupEnemyIndex = MathUtils.random(0, ENEMY_COUNT - 1);
            Enemy enemy = enemies.get(secondPowerupEnemyIndex);
            secondPowerupSprite.setPosition(enemy.sprite.getX(), enemy.sprite.getY());
        } else {
            secondPowerupActive = false;
            secondPowerupEnemyIndex = -1;
        }

        wave++;
        updateUI();
    }

    private void spawnPowerUp(float x, float y) {
        if (!isPowerupAvailable && !hasFirstPowerup) {
            powerupSprite.setPosition(x, y);
            isPowerupAvailable = true;
        }
    }

    private void handleInput() {
        float delta = Gdx.graphics.getDeltaTime();
        float speed = 4f;
        boolean isMoving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerSprite.translateX(-speed * delta);
            isMoving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerSprite.translateX(speed * delta);
            isMoving = true;
        }

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            playerSprite.setCenterX(touchPos.x);
            isMoving = true;
        }

        float maxX = viewport.getWorldWidth() - playerSprite.getWidth();
        playerSprite.setX(MathUtils.clamp(playerSprite.getX(), 0, maxX));

        shootTimer -= delta;
        if ((isMoving || Gdx.input.isKeyPressed(Input.Keys.SPACE)) && shootTimer <= 0f) {
            shootProjectile();
            shootTimer = shootCooldown;
        }
    }

    private void updateEnemies() {
        float delta = Gdx.graphics.getDeltaTime();

        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.moveTimer += delta;

            if (enemy.isZigzag) {
                // Zigzag movement: oscillate horizontally while moving down
                float amplitude = 0.5f; // How far left/right the enemy moves
                float frequency = 2f; // Speed of oscillation
                float xOffset = amplitude * (float) Math.sin(enemy.moveTimer * frequency);
                enemy.sprite.setX(enemy.initialX + xOffset);
                enemy.sprite.translateY(-enemyFallSpeed * delta * 0.8f); // Slower descent for zigzag enemies
            } else {
                // Straight-down movement (original behavior)
                enemy.sprite.translateY(-enemyFallSpeed * delta);
            }

            // Collision with player
            if (enemy.sprite.getBoundingRectangle().overlaps(playerSprite.getBoundingRectangle())) {
                enemies.removeIndex(i);
                lives--;
                updateUI();
                if (lives <= 0) {
                    game.setScreen(new MainMenuScreen(game));
                    return;
                }
                continue;
            }

            // Check if enemy moves off-screen
            if (enemy.sprite.getY() + enemy.sprite.getHeight() < 0) {
                float x = MathUtils.random(0, viewport.getWorldWidth() - enemy.sprite.getWidth());
                enemy.sprite.setPosition(x, viewport.getWorldHeight());
                enemy.sprite.setOriginCenter();
                enemy.sprite.setRotation(180f);
                enemy.initialX = x; // Update initialX for zigzag enemies
                lives--;
                updateUI();
                if (lives <= 0) {
                    game.setScreen(new MainMenuScreen(game));
                    return;
                }
            }
        }

        if (secondPowerupActive && secondPowerupEnemyIndex >= 0 && secondPowerupEnemyIndex < enemies.size) {
            Enemy enemy = enemies.get(secondPowerupEnemyIndex);
            secondPowerupSprite.setPosition(enemy.sprite.getX(), enemy.sprite.getY());
        }

        if (enemies.size == 0) {
            spawnEnemyHorde();
        }
    }

    private void updatePowerUp() {
        if (isPowerupAvailable) {
            float delta = Gdx.graphics.getDeltaTime();
            powerupSprite.translateY(-powerupFallSpeed * delta);

            if (powerupSprite.getBoundingRectangle().overlaps(playerSprite.getBoundingRectangle())) {
                hasFirstPowerup = true;
                isPowerupAvailable = false;
            }

            if (powerupSprite.getY() + powerupSprite.getHeight() < 0) {
                isPowerupAvailable = false;
            }
        }

        if (isSecondPowerupAvailable) {
            float delta = Gdx.graphics.getDeltaTime();
            secondPowerupSprite.translateY(-secondPowerupFallSpeed * delta);

            if (secondPowerupSprite.getBoundingRectangle().overlaps(playerSprite.getBoundingRectangle())) {
                hasSecondPowerup = true;
                secondPowerupObtained++;
                isSecondPowerupAvailable = false;
            }

            if (secondPowerupSprite.getY() + secondPowerupSprite.getHeight() < 0) {
                isSecondPowerupAvailable = false;
            }
        }
    }

    private void updateExplosions(float delta) {
        for (int i = explosions.size - 1; i >= 0; i--) {
            Explosion explosion = explosions.get(i);
            explosion.animTimer += delta;
            if (explosion.animTimer >= explosionAnimationSpeed) {
                explosion.animTimer -= explosionAnimationSpeed;
                explosion.currentTextureIndex++;
                if (explosion.currentTextureIndex >= (explosion.isSpark ? sparkTextures.size : explosionTextures.size)) {
                    if (explosion.isSpark) {
                        for (int j = enemies.size - 1; j >= 0; j--) {
                            Enemy enemy = enemies.get(j);
                            float distance = explosion.position.dst(enemy.sprite.getX() + enemy.sprite.getWidth() / 2, enemy.sprite.getY() + enemy.sprite.getHeight() / 2);
                            if (distance <= explosion.explosionRadius) {
                                enemies.removeIndex(j);
                                enemiesDestroyed++;
                                points += 50;
                                updateUI();
                                Gdx.app.log("MainGameScreen", "Spark AoE hit enemy, total enemies destroyed: " + enemiesDestroyed);
                            }
                        }
                    }
                    explosions.removeIndex(i);
                    Gdx.app.log("MainGameScreen", "Explosion removed, remaining: " + explosions.size);
                    continue;
                }
                explosion.sprite.setTexture(explosion.isSpark ? sparkTextures.get(explosion.currentTextureIndex) : explosionTextures.get(explosion.currentTextureIndex));
            }
        }
    }

    private void draw() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        playerSprite.draw(batch);
        for (Enemy enemy : enemies) {
            enemy.sprite.draw(batch);
        }
        for (Projectile projectile : projectiles) {
            projectile.sprite.draw(batch);
        }
        for (Explosion explosion : explosions) {
            explosion.sprite.draw(batch);
            Gdx.app.log("MainGameScreen", "Drawing explosion at: " + explosion.position);
        }
        if (isPowerupAvailable) {
            powerupSprite.draw(batch);
        }
        if (secondPowerupActive && !isSecondPowerupAvailable && secondPowerupSprite != null) {
            secondPowerupSprite.draw(batch);
        }
        if (isSecondPowerupAvailable && secondPowerupSprite != null) {
            secondPowerupSprite.draw(batch);
        }
        batch.end();
    }

    private void updateProjectiles() {
        float delta = Gdx.graphics.getDeltaTime();
        for (int i = projectiles.size - 1; i >= 0; i--) {
            Projectile proj = projectiles.get(i);
            float angle = proj.sprite.getRotation() + 90;
            float radians = (float) Math.toRadians(angle);
            float velocityX = (float) Math.sin(-radians) * projectileSpeed * delta;
            float velocityY = (float) Math.cos(-radians) * projectileSpeed * delta;
            proj.sprite.translate(velocityX, velocityY);

            proj.animTimer += delta;
            if (proj.animTimer >= projectileAnimationSpeed) {
                proj.animTimer -= projectileAnimationSpeed;
                if (proj.isBlue) {
                    proj.currentTextureIndex = (proj.currentTextureIndex + 1) % blueProjectileTextures.size;
                    proj.sprite.setTexture(blueProjectileTextures.get(proj.currentTextureIndex));
                } else {
                    proj.currentTextureIndex = (proj.currentTextureIndex + 1) % redProjectileTextures.size;
                    proj.sprite.setTexture(redProjectileTextures.get(proj.currentTextureIndex));
                }
            }

            boolean hitEnemy = false;
            for (int j = enemies.size - 1; j >= 0; j--) {
                Enemy enemy = enemies.get(j);
                if (proj.sprite.getBoundingRectangle().overlaps(enemy.sprite.getBoundingRectangle())) {
                    if (proj.isBlue) {
                        explosions.add(new Explosion(new Vector2(enemy.sprite.getX() + enemy.sprite.getWidth() / 2, enemy.sprite.getY() + enemy.sprite.getHeight() / 2), true));
                        Gdx.app.log("MainGameScreen", "Spark explosion added at: " + enemy.sprite.getX() + ", " + enemy.sprite.getY());
                    }

                    if (secondPowerupActive && j == secondPowerupEnemyIndex) {
                        secondPowerupSprite.setPosition(enemy.sprite.getX(), enemy.sprite.getY());
                        isSecondPowerupAvailable = true;
                        secondPowerupActive = false;
                        secondPowerupEnemyIndex = -1;
                    }
                    enemies.removeIndex(j);
                    enemiesDestroyed++;
                    points += 100;
                    updateUI();

                    if (enemiesDestroyed % ENEMIES_FOR_POWERUP == 0 && !isPowerupAvailable && !hasFirstPowerup) {
                        spawnPowerUp(enemy.sprite.getX(), enemy.sprite.getY());
                    }
                    hitEnemy = true;
                    break;
                }
            }

            if (hitEnemy ||
                proj.sprite.getY() > viewport.getWorldHeight() ||
                proj.sprite.getY() < 0 ||
                proj.sprite.getX() < 0 ||
                proj.sprite.getX() > viewport.getWorldWidth()) {
                projectiles.removeIndex(i);
            }
        }
    }

    private int getBlueProjectileFrequency() {
        if (secondPowerupObtained >= 5) return 1;
        return Math.max(5 - secondPowerupObtained, 1);
    }

    private void shootProjectile() {
        if (Main.shootSound != null) {
            Main.shootSound.play(Main.sfxVolume);
        }

        boolean isBlueCenter = false;
        shotsFired++;
        int blueFrequency = getBlueProjectileFrequency();
        if (hasSecondPowerup && (shotsFired % blueFrequency == 0)) {
            isBlueCenter = true;
        }

        Sprite centerProjectile = new Sprite(isBlueCenter ? blue1 : red1);
        centerProjectile.setSize(0.7f, 0.7f);
        centerProjectile.setCenter(playerSprite.getX() + playerSprite.getWidth() / 2f, playerSprite.getY() + playerSprite.getHeight());
        centerProjectile.setOriginCenter();
        centerProjectile.setRotation(-90);
        projectiles.add(new Projectile(centerProjectile, isBlueCenter));

        if (hasFirstPowerup) {
            boolean blueSide = (secondPowerupObtained >= 6);

            Sprite leftProjectile = new Sprite(blueSide ? blue1 : red1);
            leftProjectile.setSize(0.7f, 0.7f);
            leftProjectile.setCenter(playerSprite.getX() + playerSprite.getWidth() / 2f, playerSprite.getY() + playerSprite.getHeight());
            leftProjectile.setOriginCenter();
            leftProjectile.setRotation(-135);
            projectiles.add(new Projectile(leftProjectile, blueSide));

            Sprite rightProjectile = new Sprite(blueSide ? blue1 : red1);
            rightProjectile.setSize(0.7f, 0.7f);
            rightProjectile.setCenter(playerSprite.getX() + playerSprite.getWidth() / 2f, playerSprite.getY() + playerSprite.getHeight());
            rightProjectile.setOriginCenter();
            rightProjectile.setRotation(-45);
            projectiles.add(new Projectile(rightProjectile, blueSide));
        }
    }
}
