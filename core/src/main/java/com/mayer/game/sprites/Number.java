package com.mayer.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Number {
    private Array<TextureRegion> numbers;
    private Texture texture;

    public Number(){
        numbers = new Array<>();
        texture = new Texture("number.png");
        for(int i = 0; i <= 2; i++){
            for(int j = 0; j <= 3; j++){
                numbers.add(new TextureRegion(texture, j * texture.getWidth()/4, i * texture.getHeight()/3, texture.getWidth()/4, texture.getHeight()/3));
            }
        }
    }

    public TextureRegion getNumber(int number){
        return numbers.get(number);
    }

    public int getWidth(){
        return texture.getWidth()/4;
    }

    public void dispose(){
        texture.dispose();
    }

}
