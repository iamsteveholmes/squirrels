package squirrels.java;

import playn.core.PlayN;
import playn.java.JavaPlatform;

import squirrels.core.Squirrels;

public class SquirrelsJava {

  public static void main(String[] args) {
    JavaPlatform platform = JavaPlatform.register();
    platform.assets().setPathPrefix("squirrels/resources");
    PlayN.run(new Squirrels());
  }
}
