package squirrels.android;

import playn.android.GameActivity;
import playn.core.PlayN;

import squirrels.core.Squirrels;

public class SquirrelsActivity extends GameActivity {

  @Override
  public void main(){
    platform().assets().setPathPrefix("squirrels/resources");
    PlayN.run(new Squirrels());
  }
}
