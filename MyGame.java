import shapes.*;
import java.awt.Color;

public class MyGame extends Game {
  private Circle hero;
  private Circle[] evil;

  @Override
  public void setup() {
    evil = new Circle[3];
    evil[0] = new Circle();
    evil[1] = new Circle();
    evil[2] = new Circle();
    evil[0].setCenter(0, 0);
    evil[1].setCenter(300, 300);
    evil[2].setCenter(0, 500);

    for (Circle c : evil) {
      c.setColor(Color.BLACK);
      c.setSolid(true);
      getCanvas().addShape(c);
    }

    hero = new MyCircle();

    getCanvas().addShape(hero);

    evil[1].setSolid(false);
  }

  @Override
  public void update() {
    for (Circle c : evil) {
      c.move(c.towards(hero), 2);
      if (c.contains(hero)) {
        c.say("Nom!", 20);
      }
    }
  }

  public static void main(String[] args) {
    new MyGame();
  }
}
