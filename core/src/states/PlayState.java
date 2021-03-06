package states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import entities.BoxBody;
import entities.EnemyBody;
import entities.PlayerBody;
import handlers.ContListener;
import managers.GameStateManager;
import newpackage.Box2DMain;
import static utils.Constants.PPM;
import utils.TiledObjectUtil;
import entities.PlayerBody;
import states.SplashState;

public class PlayState extends GameState {

    Vector3 position = camera.position;
    private final float fSCALE = 2f;
    private PlayerBody bbPlayer;
    private EnemyBody[] arebEnemies = new EnemyBody[100];
    private OrthogonalTiledMapRenderer tmr;
    private TiledMap map;
    private float fSpeed = 0, fGravity = -0.1f;
    private Box2DDebugRenderer b2dr;
    private World world;
    private Boolean isLeft = false;
    private Texture texture;
    private int nJump = 0;
    private int height = Box2DMain.V_HEIGHT, width = Box2DMain.V_WIDTH;
    private BitmapFont font, fontRed;
    Music mp3Music = Gdx.audio.newMusic(Gdx.files.internal("Super Mario Bros. Theme Song.mp3"));
    
    // private boolean bDead;

    public PlayState(GameStateManager gsm) {
        super(gsm);
        this.world = new World(new Vector2(0, -9.8f), false);
        this.world.setContactListener(new ContListener());
        b2dr = new Box2DDebugRenderer();
//        ebPlayer = new EnemyBody(world, 20f, 400f ,20,20,false, 0f,utils.Constants.Bit_Player, 8);
        for (int i = 0; i < 100; i++) {
            arebEnemies[i] = new EnemyBody(world, 30f * i, 400f, 10, 10, false, 0f, utils.Constants.Bit_Enemy, 8);
        }
        bbPlayer = new PlayerBody(world, "Player", 20, 450, 16, 20, utils.Constants.Bit_Player);
        texture = new Texture("Luigi.png");
        map = new TmxMapLoader().load("TiledMap.tmx");
        tmr = new OrthogonalTiledMapRenderer(map);
        TiledObjectUtil.parseTiledObjectLayer(world, map);
        resize(width, height);

        //Font//


        FileHandle fontFile = Gdx.files.internal("arial.ttf");

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 10;
        parameter.color = Color.BLACK;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        font = generator.generateFont(parameter);
        generator.dispose();
        
 mp3Music.play();
        
       

        


    }

    @Override
    public void update(float delta) {
        world.step(1 / 60f, 6, 2);
        cameraUpdate();
        bbPlayer.inputUpdate(delta);
        tmr.setView(camera);

        if (entities.PlayerBody.bDead == true) {
            System.out.println("Reset");
            
           // mp3Music.stop();
            gsm.setState(GameStateManager.State.PLAY);
            entities.PlayerBody.bDead = false;
            states.SplashState.nLives--;
        }
        if (states.SplashState.nLives < 0) {
             
            gsm.setState(GameStateManager.State.SPLASH);
            //entities.PlayerBody.bDead = false;
           
            states.SplashState.nLives = 3;
        }
        //   System.out.println(states.SplashState.nLives);

        System.out.println(" camera coordinates " + position.x + " " + position.y);
        System.out.println(" lives position " + position.x + 100);
    }

    @Override
    public void render() {
        System.out.println(entities.PlayerBody.bDead);
        Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        tmr.render();
        world = bbPlayer.Death(world);
        batch.begin();






        if (!bbPlayer.bDead) {
            if (bbPlayer.bLeft) {
                batch.draw(texture, bbPlayer.body.getPosition().x * PPM - 8, bbPlayer.body.getPosition().y * PPM - 10,
                        16, 20, 0, 0, 16, 20, true, false);
            }
            if (!bbPlayer.bLeft) {
                batch.draw(texture, bbPlayer.body.getPosition().x * PPM - 8, bbPlayer.body.getPosition().y * PPM - 10,
                        16, 20, 0, 0, 16, 20, false, false);
            }
        }
        for (int i = 0; i < 100; i++) {
            if (!arebEnemies[i].bDead) {
                batch.draw(arebEnemies[i].tEnemy, arebEnemies[i].body1.getPosition().x * PPM - 8, arebEnemies[i].body1.getPosition().y * PPM - 5, 16, 16);
            }
            world = arebEnemies[i].Action(world, bbPlayer);
        }
        font.draw(batch, "lives " + states.SplashState.nLives, position.x + 135, position.y + 110);

        batch.end();
        b2dr.render(world, camera.combined.scl(PPM));
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / 2f, height / 2);
    }

    @Override
    public void dispose() {
        b2dr.dispose();
        world.dispose();
        tmr.dispose();
        map.dispose();
        mp3Music.dispose();

    }

    public void cameraUpdate() {

        // a + (b - a) * lerp
        // b = target 
        // a = current camera position
        if (bbPlayer.body.getPosition().x >= 180 / PPM) {
            position.x = camera.position.x + (bbPlayer.body.getPosition().x * PPM - camera.position.x) * 0.5f;
        }
        if (bbPlayer.body.getPosition().y > (280 / PPM)) {
            position.y = camera.position.y + (bbPlayer.body.getPosition().y * PPM - camera.position.y) * 0.5f;
        }

        camera.position.set(position);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }
}
