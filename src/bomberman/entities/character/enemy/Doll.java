package bomberman.entities.character.enemy;

import bomberman.Board;
import bomberman.Game;
import bomberman.entities.character.enemy.ai.AILow;
import bomberman.graphics.Sprite;

public class Doll extends Enemy {

    @Override
    protected void chooseSprite() {
        switch(_direction) {
            case 0:
            case 1:
                _sprite = Sprite.movingSprite(Sprite.doll_right1, Sprite.doll_right2, Sprite.doll_right3, _animate, 60);
                break;
            case 2:
            case 3:
                _sprite = Sprite.movingSprite(Sprite.doll_left1, Sprite.doll_left2, Sprite.doll_left3, _animate, 60);
                break;
        }
    }

    public Doll(int x, int y, Board board) {
        super(x, y, board, Sprite.doll_dead, 2*Game.getBomberSpeed(), 300);

        _sprite = Sprite.doll_left1;

        _ai = new AILow();
        _direction = _ai.calculateDirection();
    }


}
