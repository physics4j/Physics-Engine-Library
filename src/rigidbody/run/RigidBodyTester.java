package rigidbody.run;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Group;
import javax.media.j3d.Transform3D;
import javax.swing.JFrame;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import rigidbody.RigidBody;
import rigidbody.run.objects.Testable;
import _lib.LinkedList;
import _math.Real;

import com.sun.j3d.utils.universe.SimpleUniverse;

public class RigidBodyTester extends JFrame implements Runnable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * physics world for rigidbodies
	 */
	protected RigidBodyWorld m_world = new RigidBodyWorld();
	
	/**
	 * stores the list of objects to be drawn
	 */
	private LinkedList < Testable > m_objects = new LinkedList < Testable > ();
	
	/**
	 * displays graphics
	 */
	private SimpleUniverse m_universe = new SimpleUniverse();
	
	/**
	 * holds the Java 3D objects to be drawn
	 */
	private BranchGroup m_group = new BranchGroup();
	
	/**
	 * the x coordinate of the eye
	 */
	private float m_eyeX = 0;
	
	/**
	 * the y coordinate of the eye
	 */
	private float m_eyeY = 100;
	
	/**
	 * the z coordinate of the eye
	 */
	private float m_eyeZ = -10;
	
	/**
	 * creates a new <code>RigidBodyTester</code> for displaying a Java 3D <code>SimpleUniverse</code>.
	 * the default eye location is [ 0 , 100 , -10 ]
	 */
	public RigidBodyTester() {
		setLayout( new BorderLayout() );
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas = new Canvas3D( config );
		add( canvas , BorderLayout.CENTER );
		
		//create the scene
		createScene();
		this.m_group.setCapability( Group.ALLOW_CHILDREN_EXTEND );
		
		this.m_universe = new SimpleUniverse( canvas );
		Transform3D move = lookFromPointToPoint( new Point3d( this.m_eyeX , this.m_eyeY , this.m_eyeZ ) , new Point3d( this.m_eyeX , this.m_eyeY , this.m_eyeZ + 5 ) );
		updateEye( move );
		this.m_universe.addBranchGraph( this.m_group );
		
		this.setFocusable( true );
		this.addKeyListener( new RigidBodyTestKeyListener() );
		
		
		setSize(500, 500);
		setVisible(true);
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	}
	
	/**
	 * sets up the scene
	 */
	public void createScene() {
		for ( Testable object : this.m_objects ) {
			this.m_group.addChild( object.getGroup() );
		}
	}
	
	@Override
	public void run() {
		while ( Thread.currentThread().isInterrupted() == false ) {
			try {
				this.m_world.runPhysics( new Real( 0.001 ) );
				this.updateScene();
				Thread.sleep( 1 );
			} catch ( InterruptedException e ) {
				break;
			}
		}
	}
	
	/**
	 * redraws every object that is in the scene
	 */
	public void updateScene() {
		for ( Testable object : this.m_objects ) {
			object.draw();
		}
	}
	
	/**
	 * sets the view at <code>observationPoint</code> to look towards <code>targetPoint</code>
	 * 
	 * @param observationPoint			the location of the observer
	 * @param targetPoint				the location to which the observer looks
	 * @return							a <code>Transform3D</code> that can be used to modify the view to
	 * 									be at the observation point looking at the target point
	 */
	public static Transform3D lookFromPointToPoint( Point3d observationPoint , Point3d targetPoint ) {
		Transform3D move = new Transform3D();
		Vector3d up = new Vector3d( observationPoint.x , observationPoint.y + 1 , observationPoint.z );
		move.lookAt( observationPoint , targetPoint , up );
		move.invert();
		return move;
	}

	/**
	 * adds an object with a graphical representation dependent on other
	 * <code>Testable</code> objects already added to this tester
	 * 
	 * @param object			the testable, graphical object dependent on other bodies
	 * 							in this tester
	 */
	public void addObject( Testable object ) {
		this.m_objects.add( object );
		BranchGroup groupToAdd = new BranchGroup();
		groupToAdd.addChild( object.getGroup() );
		this.m_group.addChild( groupToAdd );
	}
	
	/**
	 * adds the given rigidbody and graphical representation as a <code>Testable</code>
	 * object to the world
	 * 
	 * @param body			the rigidbody to which physics is applied
	 * @param object		the testable, graphical object representing the rigidbody
	 */
	public void addObject( RigidBody body , Testable object ) {
		this.m_world.addRigidBody( body );
		addObject( object );
	}
	
	public void removeObject( Testable object ) {
		this.m_objects.remove( object );
	}
	
	/**
	 * modifies the view position given a new position. the parameter for this method
	 * is generated by <code>lookFromPointToPoint</code>
	 * 
	 * @param eyePosition			the new position for the view
	 * @see 						#lookFromPointToPoint(Point3d, Point3d)
	 */
	public void updateEye( Transform3D eyePosition ) {
		this.m_universe.getViewingPlatform().getViewPlatformTransform().setTransform( eyePosition );
	}
	
	/**
	 * updates the location of the eye based on <code>m_eyeX</code>, <code>m_eyeY</code>, and <code>m_eyeZ</code>
	 * 
	 * @see			#m_eyeX
	 * @see			#m_eyeY
	 * @see			#m_eyeZ
	 */
	public void updateEye() {
		Transform3D move = lookFromPointToPoint( new Point3d( this.m_eyeX , this.m_eyeY , this.m_eyeZ ) , new Point3d( this.m_eyeX , this.m_eyeY , this.m_eyeZ + 5 ) );
		updateEye( move );
	}
	
	/**
	 * moves the eye forward in the z direction 
	 */
	public void moveForward() {
		this.m_eyeZ ++;
		updateEye();
	}
	
	/**
	 * moves the eye backwards in the z direction
	 */
	public void moveBackward() {
		this.m_eyeZ --;
		updateEye();
	}
	
	/**
	 * moves the eye left in the x direction
	 */
	public void moveLeft() {
		this.m_eyeX ++;
		updateEye();
	}
	
	/**
	 * moves the eye right in the x direction
	 */
	public void moveRight() {
		this.m_eyeX --;
		updateEye();
	}
	
	/**
	 * moves the eye up in the y direction
	 */
	public void moveUp() {
		this.m_eyeY ++;
		updateEye();
	}
	
	/**
	 * moves the eye down in the y direction
	 */
	public void moveDown() {
		this.m_eyeY --;
		updateEye();
	}
	
	public void handleKeyPressed( int keyCode ) {
		if ( keyCode == KeyEvent.VK_W ) {
			moveForward();
		} else if ( keyCode == KeyEvent.VK_S ) {
			moveBackward();
		} else if ( keyCode == KeyEvent.VK_A ) {
			moveLeft();
		} else if ( keyCode == KeyEvent.VK_D ) {
			moveRight();
		} else if ( keyCode == KeyEvent.VK_UP ) {
			moveUp();
		} else if ( keyCode == KeyEvent.VK_DOWN ) {
			moveDown();
		}
	}
	
	protected class RigidBodyTestKeyListener implements KeyListener {

		public RigidBodyTestKeyListener() {
			
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			handleKeyPressed( e.getKeyCode() );
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			//ignore
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			//ignore
		}
		
	}
}
