package model;

import java.awt.Color;
import java.util.ArrayList;

/**
 * 
 * @author Jake Boychenko
 * @version 1, (11/11/2019)
 * 
 *          Description: Stores information regarding the global palette the
 *          user is creating throughout the project. It is based on the
 *          singleton model and stores a list of colors along with helper
 *          functions to use it.
 */
public class Palette
{
	private static Palette instance; 		// The singleton instance of this palette.
	private ArrayList<MunsellColor> colors; // The colors of this palette.

	/**
	 * Initialize the singleton when statically loaded.
	 */
	static
	{
		instance = new Palette();
	}

	/**
	 * A private constructor. When run, create the initial list of selected colors.
	 */
	public Palette()
	{
		// Initialize the palette.
		colors = new ArrayList<>();
	}

	/**
	 * Finds the closest color in the palette to the given color and returns it.
	 * 
	 * @param color the color to look for.
	 * @return the closest color. Null if none exist.
	 */
	public MunsellColor getClosestColor(MunsellColor color)
	{
		Color targetColor = color.toColor();

		MunsellColor closest = null;
		Color closestColor = null;

		for (MunsellColor test : colors)
		{
			if (closest == null)
			{
				closest = test;
				closestColor = closest.toColor();
			} else
			{
				Color testColor = test.toColor();

				int diff1 = colorDiff(targetColor, closestColor);
				int diff2 = colorDiff(targetColor, testColor);

				if (diff1 > diff2)
				{
					closest = test;
					closestColor = closest.toColor();
				}
			}
		}
		return closest;
	}

	/**
	 * Returns the difference between the two colors.
	 * 
	 * @param color1 the first color to test.
	 * @param color2 the second color to test.
	 * @return the difference. 0 if the same. Always positive.
	 */
	private int colorDiff(Color color1, Color color2)
	{
		int redDiff = Math.abs(color1.getRed() - color2.getRed());
		int greenDiff = Math.abs(color1.getGreen() - color2.getGreen());
		int blueDiff = Math.abs(color1.getBlue() - color2.getBlue());

		return redDiff + greenDiff + blueDiff;
	}

	/**
	 * @return the colors of the palette.
	 */
	public ArrayList<MunsellColor> getColors()
	{
		return colors;
	}

	/**
	 * Adds a color to the palette to select from.
	 * 
	 * @param color the color to add.
	 */
	public void addColor(MunsellColor color)
	{
		// Do a null error check.
		if (color == null)
			System.err.println("addColor given a null parameter!");
		
		// Check if the color is not already present in the list.
		// If not, add the new color and remove the item at the end of the list.
		if (!colors.contains(color))
		{
			colors.add(color);
			
			// If the palette is full, remove the last color to slide in the new one.
			if (colors.size() > 10) 
			{
				colors.remove(colors.size() - 2);
				colors.add(color);
			}
		}
	}
	
	/**
	 * Remove a certain color from the palette.
	 * 
	 * @param color the color to remove
	 */
	public void removeColor(MunsellColor color)
	{
		// Do a null error check.
		if (color == null)
			System.err.println("addColor given a null parameter!");
		
		// Find the color in the palette and remove it.
		if (colors.contains(color))
		{
			colors.remove(colors.indexOf(color));
		}
	}

	/**
	 * Mixes an array of Munsell Colors.
	 * Converts to RGB colors, finds the weighted average of the RGB colors,
	 * then mixes the two colors together for the new mixed color.
	 * 
	 * @param colorArr first color to mix
	 * @param weightedArr second color to mix
	 * @return the resulting mixed color.
	 */
	public static MunsellColor mixColor(ArrayList<MunsellColor> colorArr, 
										ArrayList<Double> weightedArr)
	{
		return MunsellColor.mix(colorArr, weightedArr);
	}

	/**
	 * Standard getter for the instance of the palette.
	 * 
	 * @return the instance of the palette.
	 */
	public static Palette getInstance()
	{
		return instance;
	}
}
