package squirrels.core;

import static playn.core.PlayN.*;

import java.util.HashMap;
import java.util.Map;

import playn.core.Game;
import playn.core.ImmediateLayer;
import playn.core.Json;
import playn.core.Key;
import playn.core.Keyboard;
import playn.core.Pointer;
import playn.core.Surface;
import playn.core.util.Callback;

public class Squirrels implements Game, Keyboard.Listener {
    private static final int NUM_STARS = 10;

    private static Map<Key, Integer> ADD_TILE_KEYS = new HashMap<Key, Integer>();

    static {
        int idx = 0;
        for ( Key key : new Key[]{
                Key.K1, Key.K2, Key.K3, Key.K4, Key.K5, Key.K6, Key.K7, Key.K8,
                Key.W, Key.D, Key.S, Key.A, Key.T, Key.Y, Key.H, Key.N, Key.B, Key.V, Key.F, Key.R } ) {
            ADD_TILE_KEYS.put( key, idx++ );
        }
    }

    private ImmediateLayer gameLayer;
    private float frameAlpha;

    private SquirrelWorld world;
    private CuteObject catGirl;
    private CuteObject[] stars;

    private boolean controlLeft, controlRight, controlUp, controlDown;
    private boolean controlJump;
    private float touchVectorX, touchVectorY;

    @Override
    public void init() {
        // graphics().setSize(800, 600);

        keyboard().setListener( this );
        pointer().setListener( new Pointer.Listener() {
            @Override
            public void onPointerEnd( Pointer.Event event ) {
                touchVectorX = touchVectorY = 0;
            }

            @Override
            public void onPointerDrag( Pointer.Event event ) {
                touchMove( event.x(), event.y() );
            }

            @Override
            public void onPointerStart( Pointer.Event event ) {
                touchMove( event.x(), event.y() );
            }
        } );

        // TODO(jgw): Until net is filled in everywhere, create a simple grass world.

        /*
        platform().net().get("/rpc?map", new Callback<String>() {
          @Override
          public void onSuccess(String json) {
            DataObject data = platform().parseData(json);
            world = new CuteWorld(plat, data);
            initStuff();
          }

          @Override
          public void onFailure(Throwable error) {
            platform().log("error loading map");
          }
        });
        */

        world = new SquirrelWorld( 16, 16 );

        // Grass.
        for ( int y = 0; y < 16; ++y ) {
            for ( int x = 0; x < 16; ++x ) {
                world.addTile( x, y, 2 );
            }
        }

        // And a little house.
        for ( int i = 0; i < 2; ++i ) {
            world.addTile( 4, 4, 7 );
            world.addTile( 5, 4, 7 );
            world.addTile( 6, 4, 7 );
            world.addTile( 4, 5, 7 );
            world.addTile( 5, 5, 7 );
            world.addTile( 6, 5, 7 );
            world.addTile( 4, 6, 7 );
            world.addTile( 5, 6, 3 );
            world.addTile( 6, 6, 7 );
        }

        world.addTile( 4, 4, 19 );
        world.addTile( 5, 4, 12 );
        world.addTile( 6, 4, 13 );
        world.addTile( 4, 5, 18 );
        world.addTile( 5, 5, 5 );
        world.addTile( 6, 5, 14 );
        world.addTile( 4, 6, 17 );
        world.addTile( 5, 6, 16 );
        world.addTile( 6, 6, 15 );

        // create an immediate layer that handles all of our rendering
        gameLayer = graphics().createImmediateLayer( new ImmediateLayer.Renderer() {
            public void render( Surface surface ) {
                world.setViewOrigin( catGirl.x( frameAlpha ), catGirl.y( frameAlpha ), catGirl.z( frameAlpha ) );
                surface.clear();
                world.paint( surface, frameAlpha );
            }
        } );
        graphics().rootLayer().add( gameLayer );

        initStuff();
    }

    private void initStuff() {
        catGirl = new CuteObject( assets().getImage( "images/character_cat_girl.png" ) );
        catGirl.setPos( 2, 2, 1 );
        catGirl.r = 0.3;
        world.addObject( catGirl );

        stars = new CuteObject[ NUM_STARS ];
        for ( int i = 0; i < NUM_STARS; ++i ) {
            stars[ i ] = new CuteObject( assets().getImage( "images/star.png" ) );
            stars[ i ].setPos( Math.random() * world.worldWidth(), Math.random()
                    * world.worldHeight(), 10 );
            world.addObject( stars[ i ] );
        }
    }

    @Override
    public void onKeyDown( Keyboard.Event event ) {
        Integer tileIdx = ADD_TILE_KEYS.get( event.key() );
        if ( tileIdx != null ) {
            addTile( ( int ) catGirl.x, ( int ) catGirl.y, tileIdx );
            return;
        }

        switch ( event.key() ) {
            case SPACE:
                controlJump = true;
                break;
            case ESCAPE:
                removeTopTile( ( int ) catGirl.x, ( int ) catGirl.y );
                break;
            case LEFT:
                controlLeft = true;
                break;
            case UP:
                controlUp = true;
                break;
            case RIGHT:
                controlRight = true;
                break;
            case DOWN:
                controlDown = true;
                break;
        }
    }

    @Override
    public void onKeyTyped( Keyboard.TypedEvent event ) {
        // nada
    }

    @Override
    public void onKeyUp( Keyboard.Event event ) {
        switch ( event.key() ) {
            case LEFT:
                controlLeft = false;
                break;
            case UP:
                controlUp = false;
                break;
            case RIGHT:
                controlRight = false;
                break;
            case DOWN:
                controlDown = false;
                break;
        }
    }

    @Override
    public void update( float delta ) {
        if ( world == null ) {
            return;
        }

        catGirl.setAcceleration( 0, 0, 0 );

        if ( catGirl.isResting() ) {
            // Keyboard control.
            if ( controlLeft ) {
                catGirl.ax = -1.0;
            }
            if ( controlRight ) {
                catGirl.ax = 1.0;
            }
            if ( controlUp ) {
                catGirl.ay = -1.0;
            }
            if ( controlDown ) {
                catGirl.ay = 1.0;
            }

            // Mouse Control.
            catGirl.ax += touchVectorX;
            catGirl.ay += touchVectorY;

            // Jump Control.
            if ( controlJump ) {
                catGirl.vz = 0.2;
                controlJump = false;
            }
        }

        world.updatePhysics( delta / 1000 );
    }

    @Override
    public void paint( float alpha ) {
        // save this, as we'll use it in our immediate layer renderer
        frameAlpha = alpha;
    }

    private void touchMove( float x, float y ) {
        float cx = graphics().width() / 2;
        float cy = graphics().height() / 2;

        touchVectorX = ( x - cx ) * 1.0f / cx;
        touchVectorY = ( y - cy ) * 1.0f / cy;
    }

    private void addTile( int x, int y, int type ) {
        world.addTile( x, y, type );

        Json.Writer w = json().newWriter();
        w.object();
        w.value( "op", "addTop" );
        w.value( "x", x );
        w.value( "y", y );
        w.value( "type", type );
        w.end();

        post( w.write() );
    }

    private void removeTopTile( int x, int y ) {
        world.removeTopTile( x, y );

        Json.Writer w = json().newWriter();
        w.object();
        w.value( "op", "removeTop" );
        w.value( "x", x );
        w.value( "y", y );
        w.end();

        post( w.write() );
    }

    private void post( String payload ) {
        net().post( "/rpc", payload, new Callback<String>() {
            @Override
            public void onSuccess( String response ) {
                // Nada.
            }

            @Override
            public void onFailure( Throwable error ) {
                // TODO
            }
        } );
    }

    @Override
    public int updateRate() {
        return 33;
    }
}
