package com.mayer.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mayer.game.FlappyDemo;
import com.mayer.game.sprites.Bird;
import com.mayer.game.sprites.Tube;
import com.mayer.game.sprites.Number;

public class PlayState extends State {
    private static final int TUBE_SPACING = 125;
    private static final int TUBE_COUNT = 4;
    private static final int GROUND_Y_OFFSET = -50;
    private boolean alive = true;
    private boolean play_sound = true;
    private boolean allow_score = true;
    private int score = 0;

    private Bird bird;
    private Texture bg;
    private Texture ground;
    private Texture gameOver;
    private Vector2 groundPos1, groundPos2;
    private Number number;
    private Sound death;

    private Array<Tube> tubes;

    public PlayState(GameStateManager gsm) {
        super(gsm);
        bird = new Bird(50, 300);
        cam.setToOrtho(false, FlappyDemo.WIDTH/2f, FlappyDemo.HEIGHT/2f);
        bg = new Texture("bg.png");
        ground = new Texture("ground.png");
        gameOver = new Texture("gameover.png");
        groundPos1 = new Vector2(cam.position.x - cam.viewportWidth/2, GROUND_Y_OFFSET);
        groundPos2 = new Vector2(cam.position.x - cam.viewportWidth/2 + ground.getWidth(), GROUND_Y_OFFSET);

        number = new Number();
        tubes = new Array<Tube>();

        for(int i = 1; i <= TUBE_COUNT; i++){
            tubes.add(new Tube(i*(TUBE_SPACING + Tube.TUBE_WIDTH)));
        }
        death = Gdx.audio.newSound(Gdx.files.internal("sfx_dead.ogg"));
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()){
            if(alive) bird.jump();
            else gsm.set(new PlayState(gsm));
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        if(alive) {
            updateGround();
            bird.update(dt);
            cam.position.x = bird.getPosition().x + 80;

            for (int i = 0; i < tubes.size; i++) {
                Tube tube = tubes.get(i);
                if (cam.position.x - (cam.viewportWidth / 2) > tube.getPosTopTube().x + tube.getTopTube().getWidth()) {
                    tube.reposition(tube.getPosTopTube().x + ((Tube.TUBE_WIDTH + TUBE_SPACING) * TUBE_COUNT));
                    allow_score = true;
                }

                if (bird.getPosition().x > tube.getPosBotTube().x + tube.getBottomTube().getWidth()){
                    if(allow_score) {
                        score++;

                        allow_score = false;
                    }
                }

                if (tube.collides(bird.getBounds())) alive = false;
            }

            if (bird.getPosition().y <= ground.getHeight() + GROUND_Y_OFFSET)
                alive = false;

            cam.update();
            play_sound = true;
        }
        else {
            if(play_sound) {
                death.play(0.5f);
                play_sound = false;
            }
            allow_score = true;
            //System.out.println("Score : " + score);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bg, cam.position.x - (cam.viewportWidth/2), 0);
        sb.draw(bird.getBird(), bird.getPosition().x, bird.getPosition().y);
        for(Tube tube : tubes) {
            sb.draw(tube.getTopTube(), tube.getPosTopTube().x, tube.getPosTopTube().y);
            sb.draw(tube.getBottomTube(), tube.getPosBotTube().x, tube.getPosBotTube().y);
        }
        sb.draw(ground, groundPos1.x, groundPos1.y);
        sb.draw(ground, groundPos2.x, groundPos2.y);

        sb.draw(number.getNumber(score/100), cam.position.x - 1.5f * number.getWidth(), 1.7f * cam.position.y);
        sb.draw(number.getNumber((score%100)/10), cam.position.x - 0.5f * number.getWidth(), 1.7f * cam.position.y);
        sb.draw(number.getNumber((score%100)%10), cam.position.x + 0.5f * number.getWidth(), 1.7f * cam.position.y);

        if(!alive) sb.draw(gameOver, cam.position.x - gameOver.getWidth()/2f, cam.position.y - gameOver.getHeight()/2f);

        sb.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
        bird.dispose();
        for (Tube tube : tubes) tube.dispose();
        ground.dispose();
        death.dispose();
        System.out.println("Play State Disposed");
    }

    private void updateGround(){
        if(cam.position.x - cam.viewportWidth/2 > groundPos1.x + ground.getWidth())
            groundPos1.add(ground.getWidth()*2, 0);
        if(cam.position.x - cam.viewportWidth/2 > groundPos2.x + ground.getWidth())
            groundPos2.add(ground.getWidth()*2, 0);
    }
}
