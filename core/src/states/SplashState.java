package states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import managers.GameStateManager;

public class SplashState extends GameState {

    float acc = 0f;
    Texture tex;
    public static int nLives = 3;
   

    public SplashState(GameStateManager gsm) {
        super(gsm);
        tex = new Texture("GameScreen.png");
        


    }

    public void update(float delta) {

        
        if (Gdx.input.isButtonPressed(Input.Keys.ENTER)) {
            gsm.setState(GameStateManager.State.PLAY);

        }
       
    
}
public void render() {

        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Vector3 position = camera.position;
        camera.position.x = 0;
        camera.position.y = 0;
        camera.position.set(position);
        camera.update();
        batch.setProjectionMatrix(camera.combined);



        batch.begin();


        batch.draw(tex, -150, -100, Gdx.graphics.getWidth() / 3 + 50, Gdx.graphics.getHeight() / 3 + 50);

        batch.end();
    }

    public void dispose() {
        
    }
}
