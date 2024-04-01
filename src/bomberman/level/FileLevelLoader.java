package bomberman.level;

import bomberman.Board;
import bomberman.Game;
import bomberman.entities.LayeredEntity;
import bomberman.entities.character.Bomber;
import bomberman.entities.character.enemy.Balloon;
import bomberman.entities.character.enemy.Oneal;
import bomberman.entities.character.enemy.Doll;
import bomberman.entities.character.enemy.Kondoria;
import bomberman.entities.tile.Grass;
import bomberman.entities.tile.Portal;
import bomberman.entities.tile.Wall;
import bomberman.entities.tile.destroyable.Brick;
import bomberman.entities.tile.item.BombItem;
import bomberman.entities.tile.item.FlameItem;
import bomberman.entities.tile.item.SpeedItem;
import bomberman.exceptions.LoadLevelException;
import bomberman.graphics.Screen;
import bomberman.graphics.Sprite;

import java.io.*;
import java.net.URL;

public class FileLevelLoader extends LevelLoader {
	private static char[][] _map;
	
	public FileLevelLoader(Board board, int level) throws LoadLevelException {
		super(board, level);
	}
	
	@Override
	public void loadLevel(int level) throws LoadLevelException {
		BufferedReader br = null;
		URL Path = FileLevelLoader.class.getResource("/levels/Level" + level + ".txt");
		try {
			br = new BufferedReader(new InputStreamReader(Path.openStream()));
			String[] firstLine = br.readLine().split(" ");
			_level = Integer.parseInt(firstLine[0]);
			_height = Integer.parseInt(firstLine[1]);
			_width = Integer.parseInt(firstLine[2]);

			_map = new char[_height][_width];
			String line;
			for (int i = 0; i < _height; i ++) {
				line = br.readLine();
				for (int j = 0; j < _width; j ++) _map[i][j] = line.charAt(j);
			}
		} catch (IOException e) {
			throw new LoadLevelException("Error loading level " + level, e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void createEntities() {
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				addEnties( _map[y][x], x, y );
			}
		}
	}

	public void addEnties(char c, int x, int y) {
		int pos = x + y * getWidth();

		switch(c) {
			case '#':
				_board.addEntity(pos, new Wall(x, y, Sprite.wall));
				break;
			case 'b':
				_board.addEntity(pos,
						new LayeredEntity(x, y,
								new Grass(x ,y, Sprite.grass),
								new BombItem(x, y, Sprite.powerup_bombs),
								new Brick(x, y, Sprite.brick)
						)
				);
				break;
			case 's':
				_board.addEntity(pos,
						new LayeredEntity(x, y,
								new Grass(x ,y, Sprite.grass),
								new SpeedItem(x, y, Sprite.powerup_speed),
								new Brick(x, y, Sprite.brick)
						)
				);
				break;
			case 'f':
				_board.addEntity(pos,
						new LayeredEntity(x, y,
								new Grass(x ,y, Sprite.grass),
								new FlameItem(x, y, Sprite.powerup_flames),
								new Brick(x, y, Sprite.brick)
						)
				);
				break;
			case '*':
				_board.addEntity(pos, new LayeredEntity(x, y,
						new Grass(x ,y, Sprite.grass),
						new Brick(x ,y, Sprite.brick)) );
				break;
			case 'x':
				_board.addEntity(pos, new LayeredEntity(x, y,
						new Grass(x ,y, Sprite.grass),
						new Portal(x ,y, _board, Sprite.portal),
						new Brick(x ,y, Sprite.brick)) );
				break;
			case ' ':
				_board.addEntity(pos, new Grass(x, y, Sprite.grass) );
				break;
			// thêm Bomber
			case 'p':
				_board.addCharacter( new Bomber(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board) );
				Screen.setOffset(0, 0);

				_board.addEntity(pos, new Grass(x, y, Sprite.grass) );
				break;
			// thêm Enemy
			case '1':
				_board.addCharacter( new Balloon(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
				_board.addEntity(pos, new Grass(x, y, Sprite.grass) );
				break;
			case '2':
				_board.addCharacter( new Oneal(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
				_board.addEntity(pos, new Grass(x, y, Sprite.grass) );
				break;
			case '3':
				_board.addCharacter( new Doll(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
				_board.addEntity(pos, new Grass(x, y, Sprite.grass) );
				break;
			case '4':
				_board.addCharacter( new Kondoria(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
				_board.addEntity(pos, new Grass(x, y, Sprite.grass) );
				break;
			default:
				_board.addEntity(pos, new Grass(x, y, Sprite.grass) );
				break;
		}
	}
}
