package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import util.ColorConverter;

/**
 * @author Jake Boychenko
 * @version 1, (10/21/2019)
 * 
 *          Description: In its most basic form, a MunsellColor stores a hue,
 *          value, and chroma. Additional methods return convert it to RGB,
 *          convert to String, and return a variety of algorithmically-similar
 *          colors.
 */
public class MunsellColor
{
	private Hue hue; // The hue of the color.
	private float chroma; // The chroma of the color.
	private float value; // The value of the color.

	/**
	 * Creates a MunsellColor with all its properties.
	 * 
	 * @param hue    the hue of the color.
	 * @param chroma the chroma of the color.
	 * @param value  the value of the color.
	 */
	public MunsellColor(Hue hue, float value, float chroma)
	{
		this.hue = hue;
		this.chroma = chroma;
		this.value = value;

	}

	/**
	 * Returns a MunsellColor constructed from the given Swing color.
	 * 
	 * @param color the color to use to generate the MunsellColor.
	 * @return the closest MunsellColor to the given color.
	 */
	public static MunsellColor fromRGB(Color color)
	{
		if (color == null)
			throw new NullPointerException();
		if (color.getRed() == color.getGreen() && color.getGreen() == color.getBlue())
		{
			return MunsellColor.n(color.getRed() / 255f * 10);
		}
		return ColorConverter.fromRGB(color);
	}

	/**
	 * Converts the MunsellColor to a Color and returns it.
	 * 
	 * @return a Swing Color that represents this color.
	 */
	public Color toColor()
	{
		if (hue.isGrayscale())
		{
			float num = 1 - ((10 - value) / 10);
			num = Math.min(num, 1);
			num = Math.max(num, 0);
			return new Color(num, num, num);
		}
		return ColorConverter.fromMunsell(this);
	}

	/**
	 * Returns a grayscale MunsellColor with the given n value.
	 * 
	 * @param n a value from 0-10.
	 * @return the MunsellColor.
	 */
	public static MunsellColor n(float n)
	{
		return new MunsellColor(new Hue("N", 0), n, 0);
	}

	/**
	 * Returns the mix of the colors in the colors list, with the associated
	 * weights.
	 * 
	 * @param colors  the list of colors to mix.
	 * @param weights the list of weights for each of the colors.
	 * @return the munsell color with the mix. Null if the lists aren't the same
	 *         size, the lists are empty, there is a negative weight, or the total
	 *         weight is 0.
	 */
	public static MunsellColor mix(List<MunsellColor> colors, List<Double> weights)
	{
		List<Color> rgbColors = new ArrayList<Color>();
		for (MunsellColor c : colors)
		{
			rgbColors.add(c.toColor());
		}
		return fromRGB(mixRGB(rgbColors, weights));
	}

	/**
	 * Mixes an array of RGB colors based on the provided list of weights for each
	 * respective color.
	 * 
	 * @param colors  the array of RGB colors to mix.
	 * @param weights the weights of each color.
	 * @return the resulting mixed color.
	 */
	public static Color mixRGB(List<Color> colors, List<Double> weights)
	{
		// If the lists are empty or the lists aren't the same size, return null.
		if (colors.isEmpty() || colors.size() != weights.size())
			throw new IllegalArgumentException("Improper arguments in mixRGB");

		// Initialize variables.
		double averageRed = 0;
		double averageGreen = 0;
		double averageBlue = 0;
		double weight = 0;

		// Go through each color and add its rgb values to the variables.
		for (int i = 0; i < colors.size(); i++)
		{
			Color color = colors.get(i);
			double currWeight = weights.get(i);

			// If a weight is negative, return null.
			if (currWeight < 0)
				return null;

			averageRed += color.getRed() * currWeight;
			averageGreen += color.getGreen() * currWeight;
			averageBlue += color.getBlue() * currWeight;

			weight += currWeight;
		}

		// If the total weight is 0, return null.
		if (weight == 0)
			return null;

		// Divide each of the values by the weight.
		averageRed /= weight;
		averageGreen /= weight;
		averageBlue /= weight;

		// Return the MunsellColor with the given average RGB values.
		return new Color((int) averageRed, (int) averageGreen, (int) averageBlue);
	}

	/**
	 * Get the distance between two Munsell colors by converting to RGB colors, and
	 * calculating the distance between both color's R, G, and B values.
	 * 
	 * @param color1 the first color.
	 * @param color2 the second color.
	 * @return the distance between the two colors.
	 */
	public static double getColorDistance(Color color1, Color color2)
	{
		// Check the passed passed parameters for null.
		if (color1 == null || color2 == null)
			return -1.0;

		// Get the distance between the two colors.
		double distance = Math.sqrt(
				(Math.pow(color1.getRed() - color2.getRed(), 2) + Math.pow(color1.getGreen() - color2.getGreen(), 2)
						+ Math.pow(color1.getBlue() - color2.getBlue(), 2)));

		return distance;
	}

	/**
	 * Find the mixing needed based on the passed palette of colors to get to the
	 * desired color specified.
	 * 
	 * @param colors the palette colors to mix with
	 * @param wanted the desired color to achieve
	 * @return a list of mixing weights to obtain the wanted color
	 */
	public static List<Double> getMixingWeights(ArrayList<MunsellColor> colors, MunsellColor wanted)
	{
		// Ensure wanted is not null.
		if (wanted == null)
			return null;

		Color rgbWanted = wanted.toColor();
		List<Color> rgbColors = new ArrayList<>();

		List<Double> currWeights; // List of the current weights
		List<Double> bestWeights; // List of the best weights found for mixing
		double bestDist = Double.MAX_VALUE; // Set the best distance found to +infinity

		// Initialize the lists.
		currWeights = new ArrayList<Double>();
		bestWeights = new ArrayList<Double>();

		List<Integer> colorIndices = new ArrayList<>(); // List of the index each color is at in the coarse weights.

		// Initialize the starting weights.
		for (int i = 0; i < colors.size(); i++)
		{
			currWeights.add(0.0);
			bestWeights.add(0.0);
			rgbColors.add(colors.get(i).toColor());
			colorIndices.add(0);
		}

		double[] coarseWeights = { 0, 2, 5, 8, 14 }; // Coarse weights to initially test the colors with.

		// Continue this loop until all coarseWeight permutations have been attempted.
		BruteForce: while (true)
		{

			// Adjust the index to go to the next test.
			for (int i = colorIndices.size() - 1; i >= 0; i--)
			{
				// If this index is at its highest, set it back to zero and continue the loop.
				if (colorIndices.get(i) == coarseWeights.length - 1)
				{
					// If this is the last index (meaning all previous indices have been the max
					// value), break the loop.
					if (i == 0)
					{
						break BruteForce;
					}

					colorIndices.set(i, 0); // Set the current index to 0.
					currWeights.set(i, coarseWeights[0]);
				} else
				{
					colorIndices.set(i, colorIndices.get(i) + 1); // Increment the index by 1.
					currWeights.set(i, coarseWeights[colorIndices.get(i)]);
					break;
				}
			}

			// Get the current distance.
			double dist = getColorDistance(rgbWanted, mixRGB(rgbColors, currWeights));

			// If all the weights were 0, continue.
			if (dist == -1)
				continue;

			// If a the resulting mixing gets closer, then set the results into the
			// current weights. Otherwise, reset the test weight.
			if (dist < bestDist)
			{
				bestDist = dist;
				bestWeights = new ArrayList<Double>(currWeights);
			}
		}

		// Fine-tune the results by looking at 4 similar weights for each weight.
		double[][] weightMatrix = new double[colors.size()][5]; // Holds the 5 nearest values EACH current color will
																// now test.

		// Find the values for the weight matrix by looking at the best weights.
		for (int i = 0; i < bestWeights.size(); i++)
		{
			double weight = bestWeights.get(i);

			// Special exception for the weight of 0.
			if (weight == 0)
			{
				weightMatrix[i] = new double[] { 0, 0.2, 0.5, 1, 2 };
			} else
			{
				weightMatrix[i] = new double[5];

				// Add 'j' to the weight 5 times to get the fine-tuned weights to try.
				for (int j = -2; j <= 2; j++)
				{
					weightMatrix[i][j + 2] = weight + j;
				}
			}
		}

		// Repeat the BruteForce loop with the fine-tuned weights.
		BruteForce: while (true)
		{

			// Adjust the index to go to the next test.
			for (int i = colorIndices.size() - 1; i >= 0; i--)
			{
				// If this index is at its highest, set it back to zero and continue the loop.
				if (colorIndices.get(i) == weightMatrix[i].length - 1)
				{
					// If this is the last index (meaning all previous indices have been the max
					// value), break the loop.
					if (i == 0)
					{
						break BruteForce;
					}

					colorIndices.set(i, 0); // Set the current index to 0.
					currWeights.set(i, weightMatrix[i][0]);
				} else
				{
					colorIndices.set(i, colorIndices.get(i) + 1); // Increment the index by 1.
					currWeights.set(i, weightMatrix[i][colorIndices.get(i)]);
					break;
				}
			}

			// Get the current distance.
			double dist = getColorDistance(rgbWanted, mixRGB(rgbColors, currWeights));

			// If all the weights were 0, continue.
			if (dist == -1)
				continue;

			// If a the resulting mixing gets closer, then set the results into the
			// current weights. Otherwise, reset the test weight.
			if (dist < bestDist)
			{
				bestDist = dist;
				bestWeights = new ArrayList<Double>(currWeights);
			}
		}

		return bestWeights;
	}

//	/**
//	 * Generates a list of complimentary colors and returns it.
//	 * 
//	 * @return the list of compliementary colors.
//	 */
//	public List<MunsellColor> getComplimentaryColors()
//	{
//		// TODO
//		return null;
//	}
//
//	/**
//	 * Generates a list of analogous colors and returns it.
//	 * 
//	 * @return the list of analogous colors.
//	 */
//	public List<MunsellColor> getAnalogousColors()
//	{
//		// TODO
//		return null;
//	}
//
//	/**
//	 * Generates a list of triad colors and returns it.
//	 * 
//	 * @return the list of triad colors.
//	 */
//	public List<MunsellColor> getTriadColors()
//	{
//		// TODO
//		return null;
//	}
//
//	/**
//	 * Generates a list of split-complementary colors and returns it.
//	 * 
//	 * @return the list of split-complementary colors.
//	 */
//	public List<MunsellColor> getSplitComplementaryColors()
//	{
//		// TODO
//		return null;
//	}
//
//	/**
//	 * Generates a list of tetrad colors and returns it.
//	 * 
//	 * @return the list of tetrad colors.
//	 */
//	public List<MunsellColor> getTetradColors()
//	{
//		// TODO
//		return null;
//	}
//
//	/**
//	 * Generates a list of square colors and returns it.
//	 * 
//	 * @return the list of square colors.
//	 */
//	public List<MunsellColor> getSquareColors()
//	{
//		// TODO
//		return null;
//	}
	/**
	 * Generates a complimentary color to a specified color and returns it.
	 * 
	 * @param color the color to find complimentary colors from.
	 * @return the list of complimentary colors.
	 */
	public static MunsellColor getComplimentaryColor(MunsellColor color)
	{
		// A compliment of a given color is the same value and chroma, and a hue
		// which is +50 and mod 100.
		float newHueVal = (color.getHue().getHueTotalValue() + 50) % 100;
		Hue newHue = new Hue(newHueVal);
		MunsellColor compliment = new MunsellColor(newHue, color.value, color.chroma);
		return compliment;
	}

	/**
	 * Generates a list of analogous colors and returns it.
	 * 
	 * The returned hashmap - Integer is the distance in hue from the original
	 * munsell color.. ex: HashMap<1, MunsellColor> is 1 away +1 and -1 from
	 * original. MunsellColor[] is the pair of colors at the given integer key
	 * distance. MunsellColor[0] is the color at the negative hue direction from
	 * original. MunsellColor[1] is the color at the positive hue direction from
	 * original.
	 * 
	 * @param color the color to obtain analogous colors to.
	 * @return the list of analogous colors.
	 */
	public static HashMap<Integer, ArrayList<MunsellColor>> getAnalogousColors(MunsellColor color)
	{
		// This is a hashmap which stores an integer as the distance from the original
		// color's hue
		// and a MunsellColor array which stores the pair at the positive and negative
		// direction
		// of that integer key.
		HashMap<Integer, ArrayList<MunsellColor>> result = new HashMap<>();

		// gets 5 analogous color pairs
		for (int i = 1; i <= 5; i++)
		{

			ArrayList<MunsellColor> colorPair = new ArrayList<>();
			if ((color.getHue().getHue() - i) >= 0)
			{
				Hue analogous1Hue = new Hue(color.getHue().getHuePrefix(), color.getHue().getHue() - i);
				MunsellColor analogous1 = new MunsellColor(analogous1Hue, color.getValue(), color.getChroma());
				colorPair.add(analogous1);
			}

			if ((color.getHue().getHue() + i) <= 10)
			{
				Hue analogous2Hue = new Hue(color.getHue().getHuePrefix(), color.getHue().getHue() + i);
				MunsellColor analogous2 = new MunsellColor(analogous2Hue, color.getValue(), color.getChroma());
				colorPair.add(analogous2);
			}

			result.put(i, colorPair);
		}

		// The analogous of a color is the same value and chroma, and a hue
		// which is equally distant from the given color in opposite directions.
		return result;
	}

//	/**
//	 * Generates a list of triad colors and returns it.
//	 * 
//	 * @return the list of triad colors.
//	 */
//	public List<MunsellColor> getTriadColors()
//	{
//		// TODO
//		return null;
//	}

	/**
	 * Generates a list of split-complementary colors and returns it.
	 * 
	 * @param color the color to find the split list of colors from.
	 * @return the list of split-complementary colors.
	 */
	public static HashMap<Integer, ArrayList<MunsellColor>> getSplitComplementaryColors(MunsellColor color)
	{
		// The split complements of a color are any two
		// colors analogous to the complement of a color.
		MunsellColor complement = getComplimentaryColor(color);
		HashMap<Integer, ArrayList<MunsellColor>> split = getAnalogousColors(complement);

		return split;
	}

	/**
	 * @return the full String representation of the Color.
	 */
	public String toString()
	{
		if (hue.isGrayscale())
		{
			return "N" + (int) value;
		}

		return hue.toString() + ", " + (int) value + ", " + (int) chroma;
	}

	/**
	 * @return the hue.
	 */
	public Hue getHue()
	{
		return hue;
	}

	/**
	 * @return the chroma.
	 */
	public float getChroma()
	{
		return chroma;
	}

	/**
	 * @return the value.
	 */
	public float getValue()
	{
		return value;
	}

	/**
	 * Check if an object contains the same attributes as this object.
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof MunsellColor)
		{
			MunsellColor test = (MunsellColor) o;
			return hue.equals(test.hue) && value == test.value && chroma == test.chroma;
		}
		return false;
	}

	/**
	 * Implemented to remove Checkstyle error. Unused for the functionality of this
	 * class.
	 */
	@Override
	public int hashCode()
	{
		int result = 17;
		result = 31 * result + hue.hashCode();
		result = 31 * result + Double.hashCode(chroma);
		result = 31 * result + Double.hashCode(value);
		return result;
	}

	/**
	 * Checks whether this munsell color is valid or not.
	 * 
	 * @return true if hue, value, chroma carry valid values
	 */
	public boolean isValidMunsellColor()
	{
		if (hue.isValidHue() && value <= 10 && chroma <= 40)
		{
			return true;
		}
		return false;
	}
}
