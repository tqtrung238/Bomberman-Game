package bomberman.entities.tile;


import bomberman.entities.Entity;
import bomberman.graphics.Sprite;

public class Grass extends Tile {

	public Grass(int x, int y, Sprite sprite) {
		super(x, y, sprite);
	}

	@Override
	public boolean collide(Entity e) {
		return true;
	}
}
