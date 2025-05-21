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
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class OptionsScreen extends ScreenAdapter {
    private final Game game;
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;
    private Sprite backgroundSprite;

    private float titleSize = 4.5f;
    private float textSize = 2.5f;

    public OptionsScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        try {
            skin = new Skin(Gdx.files.internal("neon-ui.json"));
        } catch (Exception e) {
            Gdx.app.error("OptionsScreen", "Failed to load skin: " + e.getMessage());
            skin = new Skin();
            skin.add("font", new com.badlogic.gdx.graphics.g2d.BitmapFont());
        }

        // Fondo estrellado
        batch = new SpriteBatch();
        Texture backgroundTexture = new Texture("stars.png");
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Título con transición de color
        Label title = new Label("OPCIONES", skin);
        title.setAlignment(Align.center);
        title.setFontScale(titleSize);
        title.setColor(Color.CYAN);
        title.addAction(Actions.forever(
            Actions.sequence(
                Actions.color(Color.CYAN, 1.5f),
                Actions.color(Color.PINK, 1.5f),
                Actions.color(Color.YELLOW, 1.5f),
                Actions.color(new Color(0.4f, 0.4f, 1f, 1f), 1.5f),
                Actions.color(new Color(1f, 0.3f, 1f, 1f), 1.5f),
                Actions.color(Color.CYAN, 1.5f)
            )
        ));

        // Sliders neon (más gruesos usando height en el layout)
        Slider soundSlider = new Slider(0, 1, 0.01f, false, skin);
        Slider musicSlider = new Slider(0, 1, 0.01f, false, skin);
        musicSlider.setValue(Main.backgroundMusic != null ? Main.backgroundMusic.getVolume() : 1f);
        soundSlider.setValue(Main.sfxVolume);

        // Add listener to update music volume in real-time
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (Main.backgroundMusic != null) {
                    Main.backgroundMusic.setVolume(musicSlider.getValue());
                }
            }
        });

        // Add listener to update SFX volume in real-time
        soundSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                Main.sfxVolume = soundSlider.getValue();
            }
        });

        // Etiquetas neon para sliders
        Label soundLabel = createNeonLabel("VOLUMEN SFX");
        Label musicLabel = createNeonLabel("VOLUMEN MÚSICA");

        // Botón neon para volver
        TextButton.TextButtonStyle neonStyle = skin.has("neon", TextButton.TextButtonStyle.class)
            ? skin.get("neon", TextButton.TextButtonStyle.class)
            : skin.get("default", TextButton.TextButtonStyle.class);

        TextButton backButton = new TextButton("VOLVER", neonStyle);
        backButton.getLabel().setFontScale(textSize);
        backButton.setTransform(true);
        addButtonAnimation(backButton, () -> game.setScreen(new MainMenuScreen(game)));

        // Layout
        float sliderWidth = 600f;
        float sliderHeight = 120f;
        float buttonWidth = 600f;
        float buttonHeight = 120f;

        table.add(title).padBottom(80).colspan(2).row();
        table.add(soundLabel).padRight(20).right();
        table.add(soundSlider).width(sliderWidth).height(sliderHeight).pad(25).row();
        table.add(musicLabel).padRight(20).right();
        table.add(musicSlider).width(sliderWidth).height(sliderHeight).pad(25).row();
        table.add(backButton).colspan(2).width(buttonWidth).height(buttonHeight).padTop(80);

        stage.addActor(table);
    }

    private Label createNeonLabel(String text) {
        Label label = new Label(text, skin);
        label.setFontScale(textSize);
        label.setAlignment(Align.right);
        label.addAction(Actions.forever(
            Actions.sequence(
                Actions.color(Color.CYAN, 1.5f),
                Actions.color(Color.PINK, 1.5f),
                Actions.color(Color.YELLOW, 1.5f),
                Actions.color(new Color(0.4f, 0.4f, 1f, 1f), 1.5f),
                Actions.color(new Color(1f, 0.3f, 1f, 1f), 1.5f),
                Actions.color(Color.CYAN, 1.5f)
            )
        ));
        return label;
    }

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
        button.addAction(Actions.forever(
            Actions.sequence(
                Actions.alpha(0.85f, 0.8f),
                Actions.alpha(1f, 0.8f)
            )
        ));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.02f, 0.02f, 0.05f, 1f);
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
