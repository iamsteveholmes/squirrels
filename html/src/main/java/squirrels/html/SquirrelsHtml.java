package squirrels.html;

import playn.core.PlayN;
import playn.html.HtmlGame;
import playn.html.HtmlPlatform;

import squirrels.core.Squirrels;

public class SquirrelsHtml extends HtmlGame {

  @Override
  public void start() {
    HtmlPlatform platform = HtmlPlatform.register();
    platform.assets().setPathPrefix("squirrels/");
    PlayN.run(new Squirrels());
  }
}
