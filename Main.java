/****************************
 * Anna Whitaker
 * 102-20-343
 * 01/30/15
 * Assignment 3
 * 
 * This program creates multiple 3D pyramids which can be viewed in wireframe form with the option
 * of implementing backface culling. The program also implements transformations and perspective viewing
 * techniques.
 * 
 * If all goes well, this program will also implement z-buffering and polygon filling.
 * 
 * The Main class takes care of keyboard functionality and canvas settings.
 * 
 **************************/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JFrame;


public class Main extends JPanel
{
	//declarations
	PComp p;
	
	public Main()
	{
		//initialize new instance of paint component
		p = new PComp();
		//initialize canvas, set a few characteristics, add object to canvas
		JFrame canvas = new JFrame("Fancy Title");
		canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas.setSize(800, 800);
		canvas.setVisible(true);
		canvas.add(p);
		
		//Translate: Maps keyboard input to class action
		p.getInputMap().put(KeyStroke.getKeyStroke('q'), ("forward"));
		p.getActionMap().put("forward", new ForwardAction());

		p.getInputMap().put(KeyStroke.getKeyStroke('w'), ("backward"));
		p.getActionMap().put("backward", new BackwardAction());

		p.getInputMap().put(KeyStroke.getKeyStroke('l'), ("left"));
		p.getActionMap().put("left", new LeftAction());
		
		p.getInputMap().put(KeyStroke.getKeyStroke('r'), ("right"));
		p.getActionMap().put("right", new RightAction());

		p.getInputMap().put(KeyStroke.getKeyStroke('u'), ("up"));
		p.getActionMap().put("up", new UpAction());

		p.getInputMap().put(KeyStroke.getKeyStroke('d'), ("down"));
		p.getActionMap().put("down", new DownAction());

		
		//Scaling: Maps keyboard input to class action
		p.getInputMap().put(KeyStroke.getKeyStroke("shift UP"), ("scale up"));
		p.getActionMap().put("scale up", new ScaleUpAction());

		p.getInputMap().put(KeyStroke.getKeyStroke("shift DOWN"), ("scale down"));
		p.getActionMap().put("scale down", new ScaleDAction());
		
		
		//Rotate: Maps keyboard input to class action
		p.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), ("+y rot clockwise"));
		p.getActionMap().put("+y rot clockwise", new RotatePYAction());
		
		p.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), ("y rot counterclockwise"));
		p.getActionMap().put("y rot counterclockwise", new RotateNYAction());

		p.getInputMap().put(KeyStroke.getKeyStroke("UP"), ("+x rot clockwise"));
		p.getActionMap().put("+x rot clockwise", new RotatePXAction());

		p.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), ("x rot counterclockwise"));
		p.getActionMap().put("x rot counterclockwise", new RotateNXAction());
		
		p.getInputMap().put(KeyStroke.getKeyStroke("shift COMMA"), ("+z rotate counterclockwise"));
		p.getActionMap().put("+z rotate counterclockwise", new RotatePZAction());
		
		p.getInputMap().put(KeyStroke.getKeyStroke("shift PERIOD"), ("rotate clockwise"));
		p.getActionMap().put("rotate clockwise", new RotateNZAction());

		
		//Reset: Maps keyboard input to class action
		p.getInputMap().put(KeyStroke.getKeyStroke('k'), ("reset"));
		p.getActionMap().put("reset", new ResetAction());
		
		
		//Selecting objects
		p.getInputMap().put(KeyStroke.getKeyStroke("1"), ("select0"));
		p.getActionMap().put("select0", new Selection0Action());

		p.getInputMap().put(KeyStroke.getKeyStroke("2"), ("select1"));
		p.getActionMap().put("select1", new Selection1Action());

		p.getInputMap().put(KeyStroke.getKeyStroke("3"), ("select2"));
		p.getActionMap().put("select2", new Selection2Action());
		
		
		//Backface toggle
		p.getInputMap().put(KeyStroke.getKeyStroke('b'), ("backface"));
		p.getActionMap().put("backface", new BackFaceToggle());

		//polyfill toggle
		p.getInputMap().put(KeyStroke.getKeyStroke('f'), ("polyfill"));
		p.getActionMap().put("polyfill", new PolyFillToggle());
		
		//z buffer
		p.getInputMap().put(KeyStroke.getKeyStroke('z'), ("zbuf"));
		p.getActionMap().put("zbuf", new ZBufToggle());
		
	}
	
	
	//Main function
	public static void main(String[] args)
	{
		//puts runnable object into queue which will run main constructor
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				new Main().setVisible(true);
			}
		});
	}
	
	
	/*
	 * The following classes are used in correspondence to keyboard mapping.
	 * Each class will call the proper function then redraw the pyramid.
	 * Further information for each class can be found within the class.
	 */
	public class ForwardAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			//translates with distance of 20 about z axis
			p.translate(20, 2);
			p.repaint();
		}
	}

	public class BackwardAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			//translates distance of -20 about z axis
			p.translate(-20, 2);
			p.repaint();
		}
	}

	public class LeftAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			//translates distance of -20 about x axis
			p.translate(-20, 0);
			p.repaint();
		}
	}

	public class RightAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			//translates distance of 20 about x axis
			p.translate(20, 0);
			p.repaint();
		}
	}

	public class UpAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			//translates distance of 20 about y axis
			p.translate(20, 1);
			p.repaint();
		}
	}

	public class DownAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			//translates distance of -20 about y
			p.translate(-20, 1);
			p.repaint();
		}
	}

	public class ScaleUpAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			//scales up by factor of 1.01
			p.scale(1.01f);
			p.repaint();
		}
	}
	
	public class ScaleDAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			//scales down by factor of 0.99
			p.scale(0.99f);
			p.repaint();
		}
	}	
	
	public class RotatePZAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			//Rotates given theta (3.14/124) about z axis
			p.rotate((float)Math.PI / 124 , 2);
			p.repaint();
		}
	}	
	
	public class RotateNZAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			////Rotates given theta (3.14/-124) about z axis
			p.rotate((float)Math.PI / -124 , 2);
			p.repaint();
		}
	}	
	
	public class RotatePXAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			////Rotates given theta (3.14/124) about x axis
			p.rotate((float)Math.PI / 124 , 0);
			p.repaint();
		}
	}	
	
	public class RotateNXAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			////Rotates given theta (3.14/-124) about x axis
			p.rotate((float)Math.PI / -124 , 0);
			p.repaint();
		}
	}	
	
	public class RotatePYAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			////Rotates given theta (3.14/124) about y axis
			p.rotate((float)Math.PI / 124 , 1);
			p.repaint();
		}
	}	
	
	public class RotateNYAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			////Rotates given theta (3.14/-124) about y axis
			p.rotate((float)Math.PI / -124 , 1);
			p.repaint();
		}
	}	

	public class ResetAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			//Resets
			//Note: Reset function can be located in PComp class
			p.Reset();
			p.repaint();
		}
	}
	
	
	public class Selection0Action extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			//Calls selection function in PComp class then repaints the selected object
			p.selection(0);
	//		p.selected(0); ?
			p.repaint();
			
		}
	}		
	
	public class Selection1Action extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			//Calls selection function in PComp class then repaints the selected object
			p.selection(1);
			p.repaint();
			
		}
	}	
	
	public class Selection2Action extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			//Calls selection function in PComp class then repaints the selected object
			p.selection(2);
			p.repaint();
		}
	}	
	
	
	public class BackFaceToggle extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			//toggles backface and wireframe
			if(p.bfc)
				p.bfc = false;
			else
				p.bfc = true;
			
			p.repaint();
		}
	}	


	public class PolyFillToggle extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			//toggles polyfill
			//0 - polygon fill off with edges drawn
			//1 - polygon fill with edges drawn
			//2 - polygon fill without edges drawn
			if(p.pcheck == 0)
				p.pcheck = 1;
			else if(p.pcheck == 1)
				p.pcheck = 2;
			else if(p.pcheck == 2)
				p.pcheck = 0;
			
			p.repaint();
			
		}
	}	

	public class ZBufToggle extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			//toggles z buffer
			if(p.zbuf)
				p.zbuf = false;
			else
				p.zbuf = true;

			p.repaint();
		}
	}	
	
	

}