package bomberman.entities.tile.item;

import bomberman.Game;
import bomberman.audio.MyAudioPlayer;
import bomberman.entities.Entity;
import bomberman.entities.character.Bomber;
import bomberman.graphics.Sprite;

public class FlameItem extends Item {
	protected boolean _active;

	public FlameItem(int x, int y, Sprite sprite) {
		super(x, y, sprite);
		_active = false;
	}

	@Override
	public boolean collide(Entity e) {
		if(e instanceof Bomber) {
			MyAudioPlayer powerUpAudio = new MyAudioPlayer(MyAudioPlayer.POWER_UP);
			powerUpAudio.play();

			if (!_active) {
				_active = true;
				Game.addBombRadius(1);
			}
			remove();
			return true;
		}
		return false;
	}

}
