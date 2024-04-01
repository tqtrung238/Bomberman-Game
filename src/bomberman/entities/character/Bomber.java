package bomberman.entities.character;

import bomberman.Board;
import bomberman.Game;
import bomberman.audio.MyAudioPlayer;
import bomberman.entities.Entity;
import bomberman.entities.Message;
import bomberman.entities.bomb.Bomb;
import bomberman.entities.bomb.Flame;
import bomberman.entities.character.enemy.Enemy;
import bomberman.graphics.Screen;
import bomberman.graphics.Sprite;
import bomberman.input.Keyboard;
import bomberman.level.Coordinates;

import bomberman.entities.LayeredEntity;
import bomberman.entities.tile.Wall;
import bomberman.entities.tile.destroyable.Brick;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

public class Bomber extends Character {

    private List<Bomb> _bombs;
    protected Keyboard _input;

    protected int _timeBetweenPutBombs = 0;

    public Bomber(int x, int y, Board board) {
        super(x, y, board);
        _bombs = _board.getBombs();
        _input = _board.getInput();
        _sprite = Sprite.player_right;
    }

    private void chooseSprite() {
        switch (_direction) {
            case 0:
                _sprite = Sprite.player_up;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_up_1, Sprite.player_up_2, _animate, 20);
                }
                break;
            case 1:
                _sprite = Sprite.player_right;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
                }
                break;
            case 2:
                _sprite = Sprite.player_down;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_down_1, Sprite.player_down_2, _animate, 20);
                }
                break;
            case 3:
                _sprite = Sprite.player_left;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_left_1, Sprite.player_left_2, _animate, 20);
                }
                break;
            default:
                _sprite = Sprite.player_right;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
                }
                break;
        }
    }

    @Override
    public void update() {
        clearBombs();
        if (!_alive) {
            afterKill();
            return;
        }

        if (_timeBetweenPutBombs < -7500) _timeBetweenPutBombs = 0;
        else _timeBetweenPutBombs--;

        animate();

        calculateMove();

        detectPlaceBomb();
    }

    @Override
    public void render(Screen screen) {
        calculateXOffset();

        if (_alive)
            chooseSprite();
        else
            _sprite = Sprite.player_dead1;

        screen.renderEntity((int) _x, (int) _y - _sprite.SIZE, this);
    }

    public void calculateXOffset() {
        int xScroll = Screen.calculateXOffset(_board, this);
        Screen.setOffset(xScroll, 0);
    }

    private void detectPlaceBomb() {
        if (_input.space && Game.getBombRate() > 0 && _timeBetweenPutBombs < 0) {

            int x = Coordinates.pixelToTile(_x + _sprite.getSize() / 2);
            int y = Coordinates.pixelToTile( (_y + _sprite.getSize() / 2) - _sprite.getSize() );

            placeBomb(x,y);
            Game.addBombRate(-1);

            _timeBetweenPutBombs = 30;
        }
    }

    protected void placeBomb(int x, int y) {
        Bomb b = new Bomb(x, y, _board);
        _board.addBomb(b);

        MyAudioPlayer placeSound = new MyAudioPlayer(MyAudioPlayer.PLACE_BOMB);
        placeSound.play();
    }

    private void clearBombs() {
        Iterator<Bomb> bs = _bombs.iterator();

        Bomb b;
        while (bs.hasNext()) {
            b = bs.next();
            if (b.isRemoved()) {
                bs.remove();
                Game.addBombRate(1);
            }
        }

    }

    @Override
    public void kill() {
        if (!_alive) return;
        _alive = false;

        _board.addLives(-1);

        Message msg = new Message("-1 LIVE", getXMessage(), getYMessage(), 2, Color.white, 14);
        _board.addMessage(msg);

        _board.getMusicPlayer().stop();

        MyAudioPlayer deadAudio = new MyAudioPlayer(MyAudioPlayer.DEAD);
        deadAudio.play();
    }

    @Override
    protected void afterKill() {
        if (_timeAfter > 0) --_timeAfter;
        else {
            if(_bombs.size() == 0) {
                if(_board.getLives() == 0)
                    _board.endGame();
                else
                    _board.restartLevel();
            }
        }
    }

    @Override
    protected void calculateMove() {
        int xa = 0, ya = 0;
        if(_input.up) ya--;
        if(_input.down) ya++;
        if(_input.left) xa--;
        if(_input.right) xa++;

        if(xa != 0 || ya != 0)  {
            move(xa * Game.getBomberSpeed(), ya * Game.getBomberSpeed());
            _moving = true;
        } else _moving = false;
    }

    public void moveCenterX() {
        int pixelOfEntity = Coordinates.tileToPixel(1);
        double centerX = _x + _sprite.getRealWidth() / 2;
        int tileCenterX = Coordinates.pixelToTile(centerX);
        _x = Coordinates.tileToPixel(tileCenterX) + pixelOfEntity / 2 - _sprite.getRealWidth() / 2;
    }

    public void moveCenterY() {
        int pixelOfEntity = Coordinates.tileToPixel(1);
        double centerY = _y - _sprite.getRealHeight() / 2;
        int tileCenterY = Coordinates.pixelToTile(centerY);
        _y = Coordinates.tileToPixel(tileCenterY) + pixelOfEntity / 2 + _sprite.getRealHeight() / 2;
    }

    public void autoMoveCenter() {
        int pixelOfEntity = Coordinates.tileToPixel(1);
        double centerX = _x + _sprite.getRealWidth() / 2;
        double centerY = _y - _sprite.getRealHeight() / 2;

        boolean contactTop = !canMove(centerX, centerY - pixelOfEntity / 2);
        boolean contactDown = !canMove(centerX, centerY + pixelOfEntity / 2);
        boolean contactLeft = !canMove(centerX - pixelOfEntity / 2, centerY);
        boolean contactRight = !canMove(centerX + pixelOfEntity / 2, centerY);

        if (_direction != 0 && contactDown) {
            moveCenterY();
        }
        if (_direction != 1 && contactLeft) {
            moveCenterX();
        }
        if (_direction != 2 && contactTop) {
            moveCenterY();
        }
        if (_direction != 3 && contactRight) {
            moveCenterX();
        }
    }

    @Override
    public boolean canMove(double x, double y) {
        int tileX = Coordinates.pixelToTile(x);
        int tileY = Coordinates.pixelToTile(y);
        Entity nextEntity = _board.getEntity(tileX, tileY, this);
        return collide(nextEntity);
    }

    @Override
    public void move(double xa, double ya) {
        double centerX = _x + _sprite.getRealWidth() / 2;
        double centerY = _y - _sprite.getRealHeight() / 2;

        if (xa > 0) {
            _direction = 1;
        }
        if (xa < 0) {
            _direction = 3;
        }
        if (ya > 0) {
            _direction = 2;
        }
        if (ya < 0) {
            _direction = 0;
        }
        if (canMove(centerX + xa, centerY + ya)) {
            _x += xa;
            _y += ya;
        }

        autoMoveCenter();
    }

    @Override
    public boolean collide(Entity e) {
        if (e instanceof Flame) {
            kill();
            return false;
        }
        if (e instanceof Enemy) {
            kill();
            return false;
        }
        if (e instanceof Wall) {
            return false;
        }
        if (e instanceof Brick) {
            return false;
        }
        if (e instanceof LayeredEntity) {
            return e.collide(this);
        }
        return true;
    }


}