package Space.INvaders;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen extends ScreenAdapter {
    private final Game game;
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;
    private Sprite backgroundSprite;

    private float titleSize = 5.f;
    private float textSize = 3.f;
    public MainMenuScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        try {
            skin = new Skin(Gdx.files.internal("neon-ui.json"));
        } catch (Exception e) {
            Gdx.app.error("MainMenuScreen", "Failed to load skin: " + e.getMessage());
            skin = new Skin();
            skin.add("font", new com.badlogic.gdx.graphics.g2d.BitmapFont());
        }

        // Initialize batch and background
        batch = new SpriteBatch();
        Texture backgroundTexture = new Texture("stars.png"); // Ensure this exists in assets
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Title label with large font scale and initial color
        Label title = new Label("SPACE INVADERS", skin);
        title.setAlignment(Align.center);
        title.setFontScale(titleSize);
        title.setColor(Color.CYAN);

        // Color transition for the title (rainbow glow)
        title.addAction(Actions.forever(
            Actions.sequence(
                Actions.color(Color.CYAN, 1.5f),
                Actions.color(Color.PINK, 1.5f),
                Actions.color(Color.YELLOW, 1.5f),
                Actions.color(new Color(0.4f, 0.4f, 1f, 1f), 1.5f), // Soft blue
                Actions.color(new Color(1f, 0.3f, 1f, 1f), 1.5f),   // Magenta
                Actions.color(Color.CYAN, 1.5f)
            )
        )) ;

        // Neon button style (fallback to default if not present)
        TextButton.TextButtonStyle neonStyle = skin.has("neon", TextButton.TextButtonStyle.class)
            ? skin.get("neon", TextButton.TextButtonStyle.class)
            : skin.get("default", TextButton.TextButtonStyle.class);

        // Buttons with large text
        TextButton playButton = new TextButton("PLAY", neonStyle);
        TextButton optionsButton = new TextButton("OPTIONS", neonStyle);
        TextButton exitButton = new TextButton("EXIT", neonStyle);

        for (TextButton btn : new TextButton[]{playButton, optionsButton, exitButton}) {
            btn.getLabel().setFontScale(textSize);
            btn.setTransform(true);
            // Animate button color through neon hues
            btn.getLabel().addAction(Actions.forever(
                Actions.sequence(
                    Actions.color(Color.CYAN, 1.5f),
                    Actions.color(Color.PINK, 1.5f),
                    Actions.color(Color.YELLOW, 1.5f),
                    Actions.color(new Color(0.4f, 0.4f, 1f, 1f), 1.5f), // Soft blue
                    Actions.color(new Color(1f, 0.3f, 1f, 1f), 1.5f),   // Magenta
                    Actions.color(Color.CYAN, 1.5f)
                )
            ));
        }

        // Button listeners with scale animation on hover
        addButtonAnimation(playButton, () -> game.setScreen(new MainGameScreen(game)));
        addButtonAnimation(optionsButton, () -> game.setScreen(new OptionsScreen(game)));
        addButtonAnimation(exitButton, Gdx.app::exit);

        // Layout with extra width and spacing
        float buttonWidth = 600f;
        float buttonHeight = 120f;

        table.add(title).padBottom(100).row();
        table.add(playButton).width(buttonWidth).height(buttonHeight).pad(25).row();
        table.add(optionsButton).width(buttonWidth).height(buttonHeight).pad(25).row();
        table.add(exitButton).width(buttonWidth).height(buttonHeight).pad(25);

        stage.addActor(table);
    }

    // Helper method for button animation and click
    private void addButtonAnimation(final TextButton button, final Runnable onClick) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                onClick.run();
            }
            @Override
            public void enter(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                button.addAction(Actions.scaleTo(1.15f, 1.15f, 0.15f));
                button.addAction(Actions.alpha(1f, 0.15f));
            }
            @Override
            public void exit(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                button.addAction(Actions.scaleTo(1f, 1f, 0.15f));
                button.addAction(Actions.alpha(0.85f, 0.15f));
            }
        });
        // Initial subtle pulse
        button.addAction(Actions.forever(
            Actions.sequence(
                Actions.alpha(0.85f, 0.8f),
                Actions.alpha(1f, 0.8f)
            )
        ));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.02f, 0.02f, 0.05f, 1f); // Deep space background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        backgroundSprite.draw(batch);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        backgroundSprite.setSize(width, height);
    }

    @Override
    public void hide() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
        backgroundSprite.getTexture().dispose();
    }
}
