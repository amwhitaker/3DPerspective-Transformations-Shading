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
 * The PComp class takes care of all painting components including drawing x,y plane; drawing edges;
 * restricting drawing to frame size (aka restricting scaling+translating -but not rotation- 
 * to frame size in a probably somewhat inefficient manner...); and accounts for frame resize.
 * This class also does the math for perspective and backface culling. 
 * 
 **************************/

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.*;

public class PComp extends JComponent
{
	//declarations
	
	//array to hold 3 pyramid objects
	Triangle[] tri = new Triangle[]{new Triangle(), new Triangle(), new Triangle()};
	
	Color[] edgeColor;
	int[][] edge;
	Dimension framesize;
	int d = 700;
	double vertices[][];
	boolean bfc = false; //keep track if backface culling is on
	int pcheck = 0; //keep track of polyfill mode
	boolean zbuf = false; //keep track of z buffer
	
	//integer to keep track of object selected
	int selected;
	

	public void paintComponent(Graphics g)
	{	
		//grab size of frame
		framesize = this.getSize();

		g.setColor(Color.BLACK);
		//Coordinate plane display: y axis
		g.drawLine(framesize.width / 2, 0, framesize.width / 2, framesize.height);
		//Coordinate plane display: x axis
		g.drawLine(0, framesize.height / 2, framesize.width , framesize.height / 2);
	
		//z buffer
		Double[][] zbuffer;
		//color
		Color[][] colorgrid;
		
		if(zbuf)
		{
			//zbuffer size of frame
			zbuffer = new Double[framesize.width][framesize.height];
			//color grid size of frame
			colorgrid = new Color[framesize.width][framesize.height];
		}
		else
		{
			//so it doesn't complain for empty array
			zbuffer = new Double[1][1];
			colorgrid = new Color[1][1];
		}
		
		//loop through array to get vertices for each triangle
		for(int i = 0; i < tri.length; i++)
		{
			//grab vertices from Triangle
			vertices = tri[i].getV();
			edge = tri[i].getEdges();
			edgeColor = tri[i].getEdgeColor();
			
			//For each vertex, calculate perspective projection			
			for(int point = 0; point < vertices.length; point++)
			{
				//x = d*x/(d+z) equivalent
				vertices[point][0] = framesize.width / 2 + (vertices[point][0] * d / (vertices[point][2] + d));
				
				//y = d*y/(d+z) equivalent
				vertices[point][1] = framesize.height / 2 - (vertices[point][1] * d / (vertices[point][2] + d));	
				
				//z = z/d+z equivalent
				vertices[point][2] = vertices[point][2]/(d + vertices[point][2]);
			}
		
			
				/*
				 * For loop that goes through each edge (or "line"). Finds appropriate x,y values for 
				 * current edge in loop. Finds appropriate color for current edge in loop. 
				 * Draws line for current edge in loop.
				 */
				for(int line = 0; line < edge.length; line++)
				{

					double x1 = vertices[edge[line][0]][0];
					double y1 = vertices[edge[line][0]][1];
					double z1 = vertices[edge[line][0]][2];
					
					double x2 = vertices[edge[line][1]][0];
					double y2 = vertices[edge[line][1]][1];
					double z2 = vertices[edge[line][1]][2];
					
					double x3 = vertices[edge[line][2]][0];
					double y3 = vertices[edge[line][2]][1];
					double z3 = vertices[edge[line][2]][2];
					
					//organize by y values
					//set some defaults, check them against each other, replace accordingly
					//y is inverted, so highest is lowest
					double highestx = x1;
					double highesty = y1;
					double highestz = z1 * d;
					double lowestx = x2;
					double lowesty = y2;
					double lowestz = z2 * d;
					double midx = x3;
					double midy = y3;
					double midz = z3 * d;
					
					if(y2 < highesty)
					{
						highesty = y2;
						highestx = x2;
						highestz = z2 * d;
					}

					if(y3 < highesty)
					{
						highesty = y3;
						highestx = x3;
						highestz = z3 * d;
					}
					
					if(y1 > lowesty)
					{
						lowesty = y1;
						lowestx = x1;
						lowestz = z1 * d;
					}
					
					if(y3 > lowesty)
					{
						lowesty = y3;
						lowestx = x3;
						lowestz = z3 * d;
					}
					
					if(y1 != highesty && y1 != lowesty)
					{
						midy = y1;
						midx = x1;
						midz = z1 * d;
					}

					if(y2 != highesty && y2 != lowesty)
					{
						midy = y2;
						midx = x2;
						midz = z2 * d;
					}
					
					if(y3 != highesty && y3 != lowesty)
					{
						midy = y3;
						midx = x3;
						midz = z3 * d;
					}

					
					
					double[][] orderxyz = {{highestx, highesty, highestz}, {midx, midy, midz}, {lowestx, lowesty, lowestz}};
					
					//0 highest to lowest
					//1 highest to mid
					//2 mid to lowest
					//slope holds INVERTED slopes (so, run/rise)
					double[] slope = {((highestx - lowestx)/(highesty - lowesty)), ((highestx - midx)/(highesty - midy)), ((midx - lowestx)/(midy - lowesty))};
					

					//if backface culling is active
					if(bfc)
					{
						double A = y1 * (z2 - z3) + y2 * (z3 - z1) + y3 * (z1 - z2);
						double B = z1 * (x2 - x3) + z2 * (x3 - x1) + z3 * (x1 - x2);
						double C = x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2);
						double D = x1 * (y2 * z3 - y3 * z2) + x2 * (y3 * z1 - y1 * z3) + x3 * (y1 * z2 - y2 * z1);
						
						//since accounting for perspective, we compute c and d since x+y is 0 (since 0,0,d)
						double val = C * (-d) - D;
					
						
						if(val >= 0 && pcheck != 2) //as long as polyfill without edges is not on
						{
							if(pcheck == 1) //polyfill with edges
							{
								PolyFill(slope, orderxyz, g, zbuffer, colorgrid);
							}
							
							if(i == selected)
								g.setColor(edgeColor[line]);
							else
								g.setColor(Color.BLUE);
							
							g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
							g.drawLine((int)x2, (int)y2, (int)x3, (int)y3);
							g.drawLine((int)x1, (int)y1, (int)x3, (int)y3);
							
						}
						else if(val >= 0 && pcheck == 2) //polyfill without edges on
						{
							PolyFill(slope, orderxyz, g, zbuffer, colorgrid);
						}
						
					}//if bfc statement end
					
					else if (pcheck != 2) //if bfc not selected - if in wireframe mode + pcheck not 2
					{
						if(pcheck == 1)//so, fill and draw edges
						{
							PolyFill(slope, orderxyz, g, zbuffer, colorgrid);
						}
							
							if(i == selected)
								g.setColor(edgeColor[line]);
							else
								g.setColor(Color.BLUE);

							g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
							g.drawLine((int)x2, (int)y2, (int)x3, (int)y3);
							g.drawLine((int)x1, (int)y1, (int)x3, (int)y3);
					}//end else
					
					else if (pcheck == 2)//fill with no edges
					{
						PolyFill(slope, orderxyz, g, zbuffer, colorgrid);
					}
				}//for loop
			
			
		}//for loop	
		
		//for loop to iterate through zbuffer
		//iterate through x
		for(int i = 0; i < zbuffer.length; i++)
		{
			//iterate through y
			for(int j = 0; j < zbuffer[i].length; j++)
			{
				if(zbuffer[i][j] != null)
				{
//					g.setColor(new Color((int)Math.abs(i/2.7) % 256), (int) Math.abs((j/2.7)%256), (int)Math.abs(zbuffer[i][j]*350)%256);
				//	g.setColor(new Color((int)Math.abs(i/2.7) % 256), (int) Math.abs((j/2.7)%256), (int)Math.abs(zbuffer[i][j]*350)%256);
					g.setColor(colorgrid[i][j]);
					g.drawLine(i, j, i, j);
				}
			}
		}
		
	}//paint component
	
	
	
	//Constructor
	public PComp()
	{
		//calls constructor of parent class for default values of instances
		super();	
	}

	/*
	 * Function inherited from Triangle class.
	 * Boolean used as a flagger to tell when the edge of the frame is hit.
	 * Various for loops/if statements used to calculate if a vertex has hit the edge of frame.
	 * 
	 * NOTE: The boundary checks ONLY work for the third object.
	 */
	public void translate(int d, int a)
	{
		boolean b = false;
		
		//if distance is positive
		if(d > 0)
		{
			switch(a)
			{
				//if vertex hits right side of frame
				case 0:

						for(int i = 0; i < vertices.length; i++)
						{
							if(vertices[i][a] > framesize.width)
							{
								b = true;
								break;
							}
						}break;
			
				//if vertex hits top of frame
				case 1:
					for(int i = 0; i < vertices.length; i++)
					{
						if(vertices[i][a] < 0)
						{
							b = true;
							break;
						}
					}break;
					
			}
		}		
		
		//if distance is negative
		else
		{
			switch(a)
			{
				//if vertex hits left of frame
				case 0:
					for(int i = 0; i < vertices.length; i++)
					{
						if(vertices[i][a] < 0)
						{
							b = true;
							break;
						}
					}break;
				
				//if vertex hits bottom of frame
				case 1:
					for(int i = 0; i < vertices.length; i++)
					{
						if(vertices[i][a] > framesize.height)
						{
							b = true;
							break;
						}
					}break;
				
				//Essentially checks z axis for backward movement
				case 2:
					for(int i = 0; i < vertices.length; i++)
					{
						if(0 > vertices[i][0] || framesize.width < vertices[i][0] || 0 > vertices[i][1] || framesize.height < vertices[i][1])
						{
							b = true;
							break;
						}
					}break;
					
			}
		}
		
		//If no flags, go ahead and translate
		if(!b)
		{
			tri[selected].translate(d, a);
		}
	}

	//Function inherited from Triangle class - same note about boundary check
	public void scale(float v)
	{
		//Only check if scaled up (scale down should never exceed frame)
		if(v > 1)
		{
			boolean b = false;
			for(int i = 0; i < vertices.length; i++)
			{
				//Compare vertex against max width, height, 0 width, height - 
				//essentially checks z axis for scaling up
				if(0 > vertices[i][0] || framesize.width < vertices[i][0] || 0 > vertices[i][1] || framesize.height < vertices[i][1])
				{
					b = true;
					break;
				}
			}
			//If no flags, continue to scale
			if(!b)
			{
				tri[selected].scale(v);
			}
		//If scaled down, go ahead and scale
		}else
		{
			tri[selected].scale(v);
		}
	}

	//Function inherited from Triangle class
	public void rotate(float theta, int a)
	{
		tri[selected].rotate(theta, a);
	}

	//Resent function: Creates new instance of Triangle objects
	public void Reset()
	{
		for(int i = 0; i < 3; i++)
		{
			tri[i] = new Triangle();
		}
	}


	//Selection function: Used to set selected variable
	public void selection(int s)
	{
		if(s == 0)
			selected = 0;
		if(s == 1)
			selected = 1;
		if(s == 2)
			selected = 2;
	}


	
	public void PolyFill(double[] slope, double[][] orderxyz, Graphics g, Double[][] zbuffer, Color[][] colorgrid)
	{
		//orderxyz[0][0] highest x value
		//orderxyz[0][1] highest y value
		//orderxyz[1][2] middle z value
		//and so on...
		//keeps track of where to begin and end the current scan line - set to highest x value
		double leftx = orderxyz[0][0];
		double rightx = orderxyz[0][0];
		
		//yz slope
		//double[] slopeyz = {((highesty - lowesty)/(highestz - lowestz)), ((highesty - midy)/(highestz - midz)), ((midy - lowesty)/(midz - lowestz))};
	//	double[] slopeyz = {(orderxyz[0][1] - orderxyz[2][1])/(orderxyz[0][2] - orderxyz[2][1]), (orderxyz[0][1] - orderxyz[1][1])/(orderxyz[0][2] - orderxyz[1][2]), (orderxyz[1][1] - orderxyz[2][1])/(orderxyz[1][2] - orderxyz[2][2])};
		double[] slopeyz = {(orderxyz[0][1] - orderxyz[2][1])/(orderxyz[0][2] - orderxyz[2][2]), (orderxyz[0][1] - orderxyz[1][1])/(orderxyz[0][2] - orderxyz[1][2]),(orderxyz[1][1] - orderxyz[2][1])/(orderxyz[1][2] - orderxyz[2][2])};
		
		
		double leftz = orderxyz[0][2];
		double rightz = orderxyz[0][2];
		
		
		//draws the top point
		//g.drawLine((int) orderxyz[0][0], (int) orderxyz[0][1], (int) orderxyz[0][0], (int) orderxyz[0][1]);
		
		//slope[0] = top point to bottom point
		//slope[1] = top point to mid point
		//slope[2] = mid point to bottom point

		//FIRST EDGE TO EDGE
		//loop to go down the face
		//lowest y value minus highest y value - top point to mid point for face
		
		for(int i = 1; i <= (orderxyz[2][1] - orderxyz[0][1]); i++)
		{
			//middle y minus highest y
			if(i < (orderxyz[1][1] - orderxyz[0][1]))
			{
				//middle x > lowest x
				if(orderxyz[1][0] > orderxyz[2][0])
				{
					//change scan line leftx to highest x + counter * top to bottom slope - change with edge
					//change scan line rightx to highest x + counter * top to mid slope - change with edge
					leftx = orderxyz[0][0] + i * slope[0];
					rightx = orderxyz[0][0] + i * slope[1];
					
					leftz = orderxyz[0][2] + i / slopeyz[0];
					rightz = orderxyz[0][2] + i / slopeyz[1];
	
				}
				
				//middle x < lowest x
				if(orderxyz[1][0] < orderxyz[2][0])
				{
					//leftx = highestx + counter * top to mid slope
					//rightx = highestx + counter * top to bottom slope
					leftx = orderxyz[0][0] + i * slope[1];
					rightx = orderxyz[0][0] + i * slope[0];
					
					leftz = orderxyz[0][2] + i / slopeyz[1];
					rightz = orderxyz[0][2] + i / slopeyz[0];
				}
				
				//run/rise z/x
				double slopexz = ((rightz - leftz)/(rightx - leftx));				
//				double slopexz = (leftx - rightx)/(leftz - rightz);
				
				//loop to color each scan line row
				for(int j = 0; j < (rightx - leftx); j++)
				{
	//				Double z = new Double(leftz - slopexz / j);
					Double z = new Double(leftz + j * slopexz);
//					Double z = new Double(leftz + j / slopexz);
					
					if(zbuf)
					{
						if(zbuffer[(int)leftx + j][(int)orderxyz[0][1] + i] == null || zbuffer[(int)leftx + j][(int)orderxyz[0][1] + i].compareTo((Double)z) > 0)
						{
							//taking z values and placing into grid
							zbuffer[(int)leftx + j][(int)orderxyz[0][1] + i] = z;
							//set color
							colorgrid[(int)leftx + j][(int)orderxyz[0][1] + i] = new Color((int)((((j + leftx) *0.2)%256)), Math.abs((int)z.doubleValue())%256, 0);
						//	colorgrid[(int)leftx + j][(int)orderxyz[2][1] + i] = new Color(Math.abs((int)(((leftx + j)/2.7)%256)), 0, 0);

						}
					}
					else
					{
						//RBG values - each time you draw a pixel, you change the R value
//						g.setColor(new Color((int)(((j*0.9)%256)), 0, 0));
						g.setColor(new Color(Math.abs((int)(((leftx + j)/2.7)%256)), 0, 0));
						
						//drawpixel
						g.drawLine((int) leftx + j, (int) orderxyz[0][1] + i, (int) leftx + j, (int) orderxyz[0][1] + i);
					}
					
					
				}//end for loop
				
				

			}
		}
		
		//SECOND EDGE TO EDGE
		//lowest y minus mid y - loop from bottom point to mid point for face
		//k distance above point
		for (int k = 1; k <= (int)(orderxyz[2][1] - orderxyz[1][1]); k++)
		{
			//if k is less than (lowest y - mid y)
			if (k < (orderxyz[2][1] - orderxyz[1][1]))
			{
				//if mid x greater than lowest x
				if (orderxyz[1][0] > orderxyz[2][0])
				{
					//leftx = lowestx - (counter * top to bottom slope)
					//rightx = lowestx - (counter * mid to bottom slope)
					leftx = orderxyz[2][0] - k * slope[0];
					rightx = orderxyz[2][0] - k * slope[2];
					
					leftz = orderxyz[2][2] - k / slopeyz[0];
					rightz = orderxyz[2][2] - k / slopeyz[2];

				}
				//if mid x less than lowest x
				else
				{
					//leftx = lowestx - (counter * mid to bottom slope)
					//rightx = lowestx - (counter * top to bottom slope)
					leftx = orderxyz[2][0] - k * slope[2];
					rightx = orderxyz[2][0] - k * slope[0];
					
					leftz = orderxyz[2][2] - k / slopeyz[2];
					rightz = orderxyz[2][2] - k / slopeyz[0];

				}
				
				double slopexz = ((rightz - leftz)/(rightx - leftx));
				
//				double slopexz = (leftx - rightx)/(leftz - rightz);
				
				//loop to draw each pixel in scan line
				for(int j = 0; j < (rightx - leftx); j++)
				{
					
//					Double z = new Double(leftz - slopexz / j);
//					Double z = new Double(leftz - j / slopexz);
					Double z = new Double(leftz + slopexz * j);
					
					if(zbuf)
					{
						if(zbuffer[(int)leftx + j][(int)orderxyz[2][1] - k] == null || zbuffer[(int)leftx + j][(int)orderxyz[2][1] - k].compareTo((Double)z) >= 0)
						{
							//taking z values and placing into grid
							zbuffer[(int)leftx + j][(int)orderxyz[2][1] - k] = z;
							//set color
							colorgrid[(int)leftx + j][(int)orderxyz[2][1] - k] = new Color((int)((((j + leftx)*0.2)%256)), Math.abs((int)z.doubleValue())%256, 0);
						//	colorgrid[(int)leftx + j][(int)orderxyz[2][1] - k] = new Color(Math.abs((int)(((leftx + j)/2.7)%256)), 0, 0);
						}
					}
					else
					{
						//RBG values
//						g.setColor(new Color((int)(((j*0.9)%256)), 0, 0));
						g.setColor(new Color(Math.abs((int)(((leftx + j)/2.7)%256)), 0, 0));

						//drawpixel ( - k because bottom to mid)
						g.drawLine((int) leftx + j, (int) orderxyz[2][1] - k, (int) leftx + j, (int) orderxyz[2][1] -k);
					}
				}	
			}
		}
		
	}	//end poly
	
}