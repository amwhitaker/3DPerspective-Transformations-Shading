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
 * The Triangle class defines the functions translate, rotate, and scale, 
 * as well as defining the vertices, edges, and center point of the pyramid.
 * 
 **************************/

import java.awt.*;
import java.util.Random;

import javax.swing.*;


public class Triangle extends JPanel
{
	
	double[][] vertices;
	int[][] edge;
	int[] center;
	private Color[] edgeColor;
		

	//constructor
	public Triangle()
	{	
		//Random to get a random starting position for each pyramid object
		Random r = new Random();
		int low = -200;
		int high = 200;
		int ranx = r.nextInt(high - low) + low;
		
		//center of pyramid
		center = new int[] {ranx, 0, 200};
		//All vertices of pyramid in [x,y,z] format relative to center
		vertices = new double[][] { {0, 150, 0}, {-150, -150, -150}, {150, -150, -150}, {-150, -150, 150}, {150, -150, 150} };
		//edges relative to vertices
		//CONVERTED TO DRAW IN TRIANGLES (as opposed to first two assignments) - counterclockwise
		edge = new int[][] { {0, 1, 2}, {0, 2, 4}, {0, 4, 3}, {0, 3, 1}, {1, 3, 2}, {2, 3, 4}};
		/*edge color defined in accordance with instructions:
		 * Base triangle 1 - black
		 * Base triangle 2 - red
		 * Other: green
		 */
		edgeColor = new Color[] {Color.green, Color.green, Color.green, Color.green, Color.black, Color.red};
	}
	

	//Function that calculates the vertices of pyramid in relation to center point
	public double[][] getV()
	{
		double[][] c = new double[vertices.length][vertices[0].length];
		for(int point = 0; point < vertices.length; point++)
		{
			c[point][0] = center[0] + vertices[point][0];
			c[point][1] = center[1] + vertices[point][1];
			c[point][2] = center[2] + vertices[point][2];
		}
		//return new vertices
		return c;
	}
	
	//Function that returns the proper edge color such that transformations maintain correct coloring
	public Color[] getEdgeColor()
	{
		return edgeColor;
	}

	//Function that returns the proper edge
	public int[][] getEdges()
	{
		return this.edge;
	}
	
	//Function that calculates new vertices after scaling calculation
	public void scale(float v)
	{
		for(int i = 0; i < vertices.length; i++)
		{
			vertices[i][0] *= v; //x = x*s
			vertices[i][1] *= v; //y = y*s
			vertices[i][2] *= v; //z = z*s
		}
	}
	
	//Function that calculates "new vertices" after translation -
	//Increments the center point in accordance to translation factor
	public void translate(int d, int a)
	{
		center[a] += d;
	}
	
	//Function that calculates new vertices after rotation calculations
	public void rotate(float theta, int a)
	{
		//temp variable used to temporarily hold values during math calculation to fix rotation error
		double temp;
		
		switch(a)
		{
			//x rotation
			case 0:		
				for(int i = 0; i < vertices.length; i++)
				{
					temp = vertices[i][1];
					vertices[i][1] = (float) (temp * Math.cos(theta) - vertices[i][2] * Math.sin(theta));
					//y = y * cos(theta) - z * sin(theta)
					
					vertices[i][2] = (float) (temp * Math.sin(theta) + vertices[i][2] * Math.cos(theta));
					//z = y * sin(theta) + z * cos(theta)
				}
				break;
				
			//y rotation
			case 1:
				for(int i = 0; i < vertices.length; i++)
				{
					temp = vertices[i][0];
					vertices[i][0] = (float) (temp * Math.cos(theta) + vertices[i][2] * Math.sin(theta));
					//x = x * cos(theta) + z * sin(theta)
					
					vertices[i][2] = (float) (-temp * Math.sin(theta) + vertices[i][2] * Math.cos(theta));
					//z = -x * sin(theta) + z * cos(theta)
				}
				break;
				
			//z rotation
			case 2:
				for(int i = 0; i < vertices.length; i++)
				{
					temp = vertices[i][0];
					vertices[i][0] = (float) (temp * Math.cos(theta) - vertices[i][1] * Math.sin(theta));
					//x = x * cos(theta) - y * sin(theta)
					
					vertices[i][1] = (float) (temp * Math.sin(theta) + vertices[i][1] * Math.cos(theta));
					//y = x * sin(theta) + y * cos(theta)
				}
				break;
			}
	}	
	
}
