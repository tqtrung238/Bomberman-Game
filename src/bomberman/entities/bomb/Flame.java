package bomberman.entities.bomb;

import bomberman.Board;
import bomberman.entities.Entity;
import bomberman.entities.character.Bomber;
import bomberman.entities.character.Character;
import bomberman.entities.character.enemy.Enemy;
import bomberman.graphics.Screen;

public class Flame extends Entity {

	@Override
	public boolean collide(Entity e) {
		if (e instanceof Bomber) {
			((Bomber) e).kill();
			return false;
		}

		if (e instanceof Enemy) {
			((Enemy) e).kill();
			return false;
		}

		return true;
	}

	protected Board _board;
	protected int _direction;
	private int _radius;
	protected int xOrigin, yOrigin;
	protected FlameSegment[] _flameSegments = new FlameSegment[0];

	public Flame(int x, int y, int direction, int radius, Board board) {
		xOrigin = x;
		yOrigin = y;
		_x = x;
		_y = y;
		_direction = direction;
		_radius = radius;
		_board = board;
		createFlameSegments();
	}

	private void createFlameSegments() {
		_flameSegments = new FlameSegment[calculatePermitedDistance()];

		boolean last = false;

		int x = (int)_x;
		int y = (int)_y;
		for (int i = 0; i < _flameSegments.length; i++) {
			last = i == _flameSegments.length -1 ? true : false;

			switch (_direction) {
				case 0: y--; break;
				case 1: x++; break;
				case 2: y++; break;
				case 3: x--; break;
			}
			_flameSegments[i] = new FlameSegment(x, y, _direction, last);
		}
	}

	private int calculatePermitedDistance() {
		int radius = 0;
		int x = (int)_x;
		int y = (int)_y;
		while(radius < _radius) {
			if(_direction == 0) y--;
			if(_direction == 1) x++;
			if(_direction == 2) y++;
			if(_direction == 3) x--;

			Entity a = _board.getEntity(x, y, null);

			if(a instanceof Character) ++radius;

			if(a.collide(this) == false)
				break;

			++radius;
		}
		return radius;
	}
	
	public FlameSegment flameSegmentAt(int x, int y) {
		for (int i = 0; i < _flameSegments.length; i++) {
			if(_flameSegments[i].getX() == x && _flameSegments[i].getY() == y)
				return _flameSegments[i];
		}
		return null;
	}

	@Override
	public void update() {}
	
	@Override
	public void render(Screen screen) {
		for (int i = 0; i < _flameSegments.length; i++) {
			_flameSegments[i].render(screen);
		}
	}


}
