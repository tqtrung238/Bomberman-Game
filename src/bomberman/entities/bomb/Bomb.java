package bomberman.entities.bomb;

import bomberman.Board;
import bomberman.Game;
import bomberman.audio.MyAudioPlayer;
import bomberman.entities.AnimatedEntitiy;
import bomberman.entities.Entity;
import bomberman.entities.character.Bomber;
import bomberman.entities.character.Character;
import bomberman.graphics.Screen;
import bomberman.graphics.Sprite;
import bomberman.level.Coordinates;

public class Bomb extends AnimatedEntitiy {

	@Override
	public boolean collide(Entity e) {
		if(e instanceof Bomber) {
			double diffX = e.getX() - Coordinates.tileToPixel(getX());
			double diffY = e.getY() - Coordinates.tileToPixel(getY());

			if(!(diffX >= -10 && diffX < 16 && diffY >= 1 && diffY <= 28)) _allowedToPassThru = false;

			return _allowedToPassThru;
		}
		if(e instanceof Flame) {
			_timeToExplode = 0;
			return true;
		}

		return false;
	}

	protected double _timeToExplode = 120;
	public int _timeAfter = 20;
	
	protected Board _board;
	protected Flame[] _flames;
	protected boolean _exploded = false;
	protected boolean _allowedToPassThru = true;
	
	public Bomb(int x, int y, Board board) {
		_x = x;
		_y = y;
		_board = board;
		_sprite = Sprite.bomb;
	}
	
	@Override
	public void update() {
		if(_timeToExplode > 0) 
			_timeToExplode--;
		else {
			if(!_exploded) 
				explode();
			else
				updateFlames();
			
			if(_timeAfter > 0) 
				_timeAfter--;
			else
				remove();
		}
			
		animate();
	}
	
	@Override
	public void render(Screen screen) {
		if(_exploded) {
			_sprite =  Sprite.bomb_exploded2;
			renderFlames(screen);
		} else
			_sprite = Sprite.movingSprite(Sprite.bomb, Sprite.bomb_1, Sprite.bomb_2, _animate, 60);
		
		int xt = (int)_x << 4;
		int yt = (int)_y << 4;
		
		screen.renderEntity(xt, yt , this);
	}
	
	public void renderFlames(Screen screen) {
		for (int i = 0; i < _flames.length; i++) {
			_flames[i].render(screen);
		}
	}
	
	public void updateFlames() {
		for (int i = 0; i < _flames.length; i++) {
			_flames[i].update();
		}
	}


	protected void explode() {
		_exploded = true;

		Character a = _board.getCharacterAt(_x, _y);
		if(a != null)  {
			a.kill();
		}

		_allowedToPassThru = true;
		_flames = new Flame[4];

		for (int i = 0; i < _flames.length; i++) {
			_flames[i] = new Flame((int)_x, (int)_y, i, Game.getBombRadius(), _board);
		}

		MyAudioPlayer explodeAudio = new MyAudioPlayer(MyAudioPlayer.EXPLOSION);
		explodeAudio.play();
	}
	
	public FlameSegment flameAt(int x, int y) {
		if(!_exploded) return null;
		
		for (int i = 0; i < _flames.length; i++) {
			if(_flames[i] == null) return null;
			FlameSegment e = _flames[i].flameSegmentAt(x, y);
			if(e != null) return e;
		}
		
		return null;
	}


}
