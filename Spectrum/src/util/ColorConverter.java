package util;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import model.Hue;
import model.MunsellColor;

/**
 * 
 * @author Jake Boychenko
 * @version 2, (11/01/2019)
 * 
 *          Description: This class holds utility methods related to the
 *          Spectrum project. Utilities such as a Munsell Color -> RGB, and RGB
 *          -> Munsell Color conversion algorithms.
 */
public class ColorConverter
{
	/**
	 * Stores conversion information from Munsell to RGB.
	 */
	private static LinkedHashMap<Hue, LinkedHashMap<Integer, LinkedHashMap<Integer, 
									Color>>> munsellToRGB = new LinkedHashMap<>();

	/**
	 * Stores conversion information from RGB to Munsell.
	 */
	private static LinkedHashMap<Integer, LinkedHashMap<Integer, LinkedHashMap<Integer, 
									MunsellColor>>> rgbToMunsell = new LinkedHashMap<>();

	/**
	 * Scans through the CSVs and builds the LinkedHashMaps.
	 */
	public static void buildCSVMaps()
	{
		buildMunsellToRGBMap();
		buildRGBToMunsellMap();

		// Print out the munsellToRGB information for debugging.
//		for (Hue hue : munsellToRGB.keySet())
//		{
//			System.out.println(hue.toString());
//
//			for (int value : munsellToRGB.get(hue).keySet())
//			{
//				System.out.println("\t" + value);
//
//				for (int chroma : munsellToRGB.get(hue).get(value).keySet())
//				{
//					System.out.println("\t\t" + chroma + " =>  (" + 
//						munsellToRGB.get(hue).get(value).get(chroma) + ")");
//				}
//			}
//		}
	}

	/**
	 * Returns a list of hues that were found in the CSV by scanning through the
	 * keyset of munsellToRGB.
	 * 
	 * @return the list of hues.
	 */
	public static ArrayList<Hue> getHues()
	{
		ArrayList<Hue> hues = new ArrayList<>();

		for (Hue hue : munsellToRGB.keySet())
			hues.add(hue);

		return hues;
	}

	/**
	 * Converts the MunsellColor to a Color and returns it.
	 * 
	 * @param munsell the munsellColor to convert.
	 * @return the closest Color to the given color.
	 */
	public static Color fromMunsell(MunsellColor munsell)
	{
		LinkedHashMap<Integer, LinkedHashMap<Integer, Color>> layerOneLinkedHashMap = 
							getMunsellToRGBLayerOneLinkedHashMap(munsell);

		LinkedHashMap<Integer, Color> layerTwoLinkedHashMap = 
							getMunsellToRGBLayerTwoLinkedHashMap(munsell,layerOneLinkedHashMap);

		return getMunsellToRGBLayerThreeColor(munsell, layerTwoLinkedHashMap);
	}

	/**
	 * Converts the color to a MunsellColor and returns it.
	 * 
	 * @param color the color to convert.
	 * @return the closest MunsellColor to the given color.
	 */
	public static MunsellColor fromRGB(Color color)
	{
		LinkedHashMap<Integer, LinkedHashMap<Integer, MunsellColor>> layerOneLinkedHashMap = 
							getRGBToMunsellLayerOneLinkedHashMap(color);

		LinkedHashMap<Integer, MunsellColor> layerTwoLinkedHashMap = 
							getRGBToMunsellLayerTwoLinkedHashMap(color, layerOneLinkedHashMap);

		return getRGBToMunsellLayerThreeColor(color, layerTwoLinkedHashMap);
	}

	/**
	 * Returns a 2D list of all the colors represented by the given hue. This is
	 * ordered with descending value up -> down and ascending chroma right -> left
	 * 
	 * @param hue the hue to look through.
	 * @return the 2D list.
	 */
	public static ArrayList<ArrayList<MunsellColor>> getColorsMatrix(Hue hue)
	{
		// Create the output list.
		ArrayList<ArrayList<MunsellColor>> output = new ArrayList<>();

		// Get the layer one map.
		LinkedHashMap<Integer, LinkedHashMap<Integer, Color>> layerOne = munsellToRGB.get(hue);

		// The count of how many times we went through the loop.
		int count = 0;

		// Get the values.
		for (int value : layerOne.keySet())
		{
			// The current row we are working on.
			ArrayList<MunsellColor> row = new ArrayList<>();

			// Add the grayscale color first.
			row.add(MunsellColor.n(count + 1));

			// Get the key values.
			for (int chroma : layerOne.get(value).keySet())
			{
				row.add(new MunsellColor(hue, value, chroma));
			}

			// Add the row to the first element of the output list. The way the values are
			// sorted in the dictionary is opposite of what we want.
			output.add(0, row);

			count++;
		}

		return output;
	}

	/**
	 * Finds the munsell color with the highest chroma in the given hue.
	 * 
	 * @param hue the hue to look through.
	 * @return the munsell color with the highest chroma.
	 */
	public static MunsellColor getHighestChromaInHue(Hue hue)
	{
		MunsellColor highest = null; // Color with the highest Chroma.

		// Get the layer one map.
		LinkedHashMap<Integer, LinkedHashMap<Integer, Color>> layerOne = munsellToRGB.get(hue);

		// Get the values.
		for (int value : layerOne.keySet())
		{
			// Get the key values.
			for (int chroma : layerOne.get(value).keySet())
			{
				MunsellColor color = new MunsellColor(hue, value, chroma);
				if (highest == null)
				{
					highest = color;
				} else if (color.getChroma() >= highest.getChroma())
				{
					highest = color;
				}
			}
		}

		return highest;
	}

	/**
	 * Returns the first layer of the munsellToRGB LinkedHashMap by finding the
	 * closest Hue.
	 * 
	 * @param munsell the MunsellColor to use.
	 * @return the closest LinkedHashMap.
	 */
	private static LinkedHashMap<Integer, LinkedHashMap<Integer, Color>> 
							getMunsellToRGBLayerOneLinkedHashMap(MunsellColor munsell)
	{

		// If the munsellToRGB hash contains the given hue, use it. Otherwise, find the
		// closest hue in the LinkedHashMap and use it instead.
		if (munsellToRGB.containsKey(munsell.getHue()))
		{
			return munsellToRGB.get(munsell.getHue());
		}

		// Find the closest hue to the one given in the MunsellColor.
		Hue closestHue = null;
		for (Hue hue : munsellToRGB.keySet())
		{
			if (hue.getHuePrefix().equals(munsell.getHue().getHuePrefix()))
			{
				// Set closestHue to the Hue that is closer to the Munsell's hue: the previously
				// closest hue or the new hue we are iterating over.
				closestHue = getCloserHue(munsell.getHue(), closestHue, hue);

			}
		}

		if (closestHue == null)
		{
			System.out.println("Hue was invalid: " + munsell.getHue());
			return null;
		}

		// Return the entry in the LinkedHashMap with the closest hue.
		return munsellToRGB.get(closestHue);
	}

	/**
	 * Returns the second layer of the munsellToRGB LinkedHashMap by finding the
	 * closest value.
	 * 
	 * @param munsell  the MunsellColor to use.
	 * @param layerOne the first layer of the hashmaps.
	 * @return the closest LinkedHashMap.
	 */
	private static LinkedHashMap<Integer, Color> getMunsellToRGBLayerTwoLinkedHashMap(
			MunsellColor munsell, LinkedHashMap<Integer, LinkedHashMap<Integer, Color>> layerOne)
	{

		// If the layerOne hash contains the given value, use it. Otherwise, find the
		// closest value in the LinkedHashMap and use it instead.
		if (layerOne.containsKey((int) munsell.getValue()))
		{
			return layerOne.get((int) munsell.getValue());
		}

		// Find the closest value to the one given in the MunsellColor.
		int closestValue = -1;
		for (int value : layerOne.keySet())
		{
			// Set closestValue to the value that is closer to the Munsell's value: the
			// previously closest value or the new value we are iterating over.
			if (closestValue == -1)
			{
				closestValue = value;
			} else
			{

				// Find the closest value.
				float diff1 = Math.abs(munsell.getValue() - closestValue);
				float diff2 = Math.abs(munsell.getValue() - value);

				// If the first diff is greater than the second diff, adjust the closest value.
				if (diff1 > diff2)
				{
					closestValue = value;
				}
			}

		}

		// Return the entry in the LinkedHashMap with the closest value.
		return layerOne.get(closestValue);
	}

	/**
	 * Returns the color of the munsellToRGB LinkedHashMap by finding the closest
	 * chroma.
	 * 
	 * @param munsell  the MunsellColor to use.
	 * @param layerTwo the second layer of the hashmap
	 * @return the closest LinkedHashMap.
	 */
	private static Color getMunsellToRGBLayerThreeColor(MunsellColor munsell, 
													LinkedHashMap<Integer, Color> layerTwo)
	{

		// If the layerTwo hash contains the given chroma, use it. Otherwise, find the
		// closest chroma in the LinkedHashMap and use it instead.
		if (layerTwo.containsKey((int) munsell.getChroma()))
		{
			return layerTwo.get((int) munsell.getChroma());
		}

		// Find the closest chroma to the one given in the MunsellColor.
		int closestChroma = -1;
		for (int chroma : layerTwo.keySet())
		{
			// Set closestValue to the chroma that is closer to the Munsell's chroma: the
			// previously closest value or the new chroma we are iterating over.
			if (closestChroma == -1)
			{
				closestChroma = chroma;
			} else
			{

				// Find the closest chroma.
				float diff1 = Math.abs(munsell.getChroma() - closestChroma);
				float diff2 = Math.abs(munsell.getChroma() - chroma);

				// If the first diff is greater than the second diff, adjust the closest value.
				if (diff1 > diff2)
				{
					closestChroma = chroma;
				}
			}

		}

		// Return the color in the LinkedHashMap with the closest chroma.
		return layerTwo.get(closestChroma);
	}

	/**
	 * Returns the first layer of the rgbToMunsell LinkedHashMap by finding the
	 * closest red value.
	 * 
	 * @param color the RGB color to use.
	 * @return the closest LinkedHashMap.
	 */
	private static LinkedHashMap<Integer, LinkedHashMap<Integer, 
						MunsellColor>> getRGBToMunsellLayerOneLinkedHashMap(Color color)
	{

		// If the layerOne hash contains the given value, use it. Otherwise, find the
		// closest value in the LinkedHashMap and use it instead.
		if (rgbToMunsell.containsKey(color.getRed()))
		{
			return rgbToMunsell.get(color.getRed());
		}

		// Find the closest value to the one given in the MunsellColor.
		int closestValue = -1;
		for (int value : rgbToMunsell.keySet())
		{
			// Set closestValue to the value that is closer to the Munsell's value: the
			// previously closest value or the new value we are iterating over.
			if (closestValue == -1)
			{
				closestValue = value;
			} else
			{

				// Find the closest value.
				float diff1 = Math.abs(color.getRed() - closestValue);
				float diff2 = Math.abs(color.getRed() - value);

				// If the first diff is greater than the second diff, adjust the closest value.
				if (diff1 > diff2)
				{
					closestValue = value;
				}
			}

		}

		// Return the entry in the LinkedHashMap with the closest value.
		return rgbToMunsell.get(closestValue);

	}

	/**
	 * Returns the first layer of the rgbToMunsell LinkedHashMap by finding the
	 * closest green value.
	 * 
	 * @param color    the RGB color to use.
	 * @param layerOne the first layer of the hashmap.
	 * @return the closest LinkedHashMap.
	 */
	private static LinkedHashMap<Integer, MunsellColor> getRGBToMunsellLayerTwoLinkedHashMap(
			Color color, LinkedHashMap<Integer, LinkedHashMap<Integer, MunsellColor>> layerOne)
	{

		// If the layerOne hash contains the given value, use it. Otherwise, find the
		// closest value in the LinkedHashMap and use it instead.
		if (layerOne.containsKey(color.getGreen()))
		{
			return layerOne.get(color.getGreen());
		}

		// Find the closest value to the one given in the MunsellColor.
		int closestValue = -1;
		for (int value : layerOne.keySet())
		{
			// Set closestValue to the value that is closer to the Munsell's value: the
			// previously closest value or the new value we are iterating over.
			if (closestValue == -1)
			{
				closestValue = value;
			} else
			{

				// Find the closest value.
				float diff1 = Math.abs(color.getGreen() - closestValue);
				float diff2 = Math.abs(color.getGreen() - value);

				// If the first diff is greater than the second diff, adjust the closest value.
				if (diff1 > diff2)
				{
					closestValue = value;
				}
			}

		}

		// Return the entry in the LinkedHashMap with the closest value.
		return layerOne.get(closestValue);
	}

	/**
	 * Returns the first layer of the rgbToMunsell LinkedHashMap by finding the
	 * closest blue value.
	 * 
	 * @param color    the RGB color to use.
	 * @param layerTwo the second layer of the hashmap.
	 * @return the closest LinkedHashMap.
	 */
	private static MunsellColor getRGBToMunsellLayerThreeColor(Color color,
			LinkedHashMap<Integer, MunsellColor> layerTwo)
	{

		// If the layerOne hash contains the given value, use it. Otherwise, find the
		// closest value in the LinkedHashMap and use it instead.
		if (layerTwo.containsKey(color.getBlue()))
		{
			return layerTwo.get(color.getBlue());
		}

		// Find the closest value to the one given in the MunsellColor.
		int closestValue = -1;
		for (int value : layerTwo.keySet())
		{
			// Set closestValue to the value that is closer to the Munsell's value: the
			// previously closest value or the new value we are iterating over.
			if (closestValue == -1)
			{
				closestValue = value;
			} else
			{

				// Find the closest value.
				float diff1 = Math.abs(color.getBlue() - closestValue);
				float diff2 = Math.abs(color.getBlue() - value);

				// If the first diff is greater than the second diff, adjust the closest value.
				if (diff1 > diff2)
				{
					closestValue = value;
				}
			}

		}

		// Return the entry in the LinkedHashMap with the closest value.
		return layerTwo.get(closestValue);
	}

	/**
	 * Returns the hue that is closer to the wantedHue. We know that all the hues
	 * have the exact same Hue Prefix so we only compare their hue values.
	 * 
	 * @param wantedHue  the hue we want to find.
	 * @param hueChoice1 the first choice.
	 * @param hueChoice2 the second choice.
	 * @return either hueChoice1 or hueChoice2, whichever one is closer to
	 *         wantedHue.
	 */
	private static Hue getCloserHue(Hue wantedHue, Hue hueChoice1, Hue hueChoice2)
	{
		if (hueChoice1 == null && hueChoice2 == null)
			return null;
		if (hueChoice1 == null)
			return hueChoice2;
		if (hueChoice2 == null)
			return hueChoice1;

		float diff1 = Math.abs(wantedHue.getHue() - hueChoice1.getHue());
		float diff2 = Math.abs(wantedHue.getHue() - hueChoice2.getHue());

		return diff1 > diff2 ? hueChoice2 : hueChoice1;
	}

	/**
	 * Builds the MunsellToRGB LinkedHashMap.
	 */
	private static void buildMunsellToRGBMap()
	{
		try
		{
			// Get the file reader.
			BufferedReader br = new BufferedReader(new FileReader("res/Munsell2RGB.csv"));

			String line = br.readLine(); // Skip the first line. It is a header file.

			// Go through each of the lines and add it to the Map.
			while ((line = br.readLine()) != null)
			{
				// Split the line by comma.
				String[] split = line.split(",");

				// Get MunsellColor information.
				Hue hue = new Hue(split[2], Float.parseFloat(split[3]));
				int value = Integer.parseInt(split[4]);
				int chroma = Integer.parseInt(split[5]);

				// Get RGB information.
				int red = Integer.parseInt(split[6]);
				int green = Integer.parseInt(split[7]);
				int blue = Integer.parseInt(split[8]);
				Color color = new Color(red, green, blue);

				// If chroma or value is 0, something is wrong, so continue...
				if (chroma == 0 || value == 0)
				{
					continue;
				}

				// Add the information from the line to the map.
				addMunsellColorToMap(hue, value, chroma, color);
			}

			br.close();
		} catch (IOException e)
		{
			System.err.println("Error finding munsell->rgb csv. File not found!");
		}
	}

	/**
	 * Builds the RGBToMunsell LinkedHashMap.
	 */
	private static void buildRGBToMunsellMap()
	{
		try
		{
			// Get the file reader.
			BufferedReader br = new BufferedReader(new FileReader("res/RGB2Munsell.csv"));

			String line = br.readLine(); // Skip the first line. It is a header file.

			// Go through each of the lines and add it to the Map.
			while ((line = br.readLine()) != null)
			{
				// Split the line by comma.
				String[] split = line.split(",");

				// Get RGB information.
				int red = Integer.parseInt(split[0]);
				int green = Integer.parseInt(split[1]);
				int blue = Integer.parseInt(split[2]);

				// Get MunsellColor information.
				Hue hue = new Hue(split[3]);
				float value = Float.parseFloat(split[4]);
				float chroma = Float.parseFloat(split[5]);
				MunsellColor color = new MunsellColor(hue, value, chroma);

				// Add the information from the line to the map.
				addRGBToMap(red, green, blue, color);
			}

			br.close();
		} catch (IOException e)
		{
			System.err.println("Error finding munsell->rgb csv. File not found!");
		}
	}

	/**
	 * Adds the given MunsellColor information to the munsellToRGB map.
	 * 
	 * @param hue    the hue to use.
	 * @param value  the value to use.
	 * @param chroma the chroma to use.
	 * @param color  the color to add.
	 */
	private static void addMunsellColorToMap(Hue hue, int value, int chroma, Color color)
	{
		// The first layer of the master map.
		LinkedHashMap<Integer, LinkedHashMap<Integer, Color>> layerOne;

		// If the master map contains the hue, return the layer with the hue.
		if (munsellToRGB.containsKey(hue))
		{
			layerOne = munsellToRGB.get(hue);
		} else
		{
			// Otherwise create a new layer and add the hue to the master map.
			layerOne = new LinkedHashMap<>();
			munsellToRGB.put(hue, layerOne);
		}

		// The second layer of the master map.
		LinkedHashMap<Integer, Color> layerTwo;

		// If the first layer contains the value, return the layer with the value.
		if (layerOne.containsKey(value))
		{
			layerTwo = layerOne.get(value);
		} else
		{
			// Otherwise create a new layer and add the value to the master map.
			layerTwo = new LinkedHashMap<>();
			layerOne.put(value, layerTwo);
		}

		// If the second layer contains the chroma, a duplicate has been found.
		if (layerTwo.containsKey(chroma))
		{
			return;
		}

		// Add the color to the given chroma.
		layerTwo.put(chroma, color);
	}

	/**
	 * Adds the given Swing Color to the rgbToMunsell map.
	 * 
	 * @param red   the red value to use.
	 * @param green the green value to use.
	 * @param blue  the blue value to use.
	 * @param color the MunsellColor to add.
	 */
	private static void addRGBToMap(int red, int green, int blue, MunsellColor color)
	{
		// The first layer of the master map.
		LinkedHashMap<Integer, LinkedHashMap<Integer, MunsellColor>> layerOne;

		// If the master map contains the red value, return the layer with the red
		// value.
		if (rgbToMunsell.containsKey(red))
		{
			layerOne = rgbToMunsell.get(red);
		} else
		{
			// Otherwise create a new layer and add the red value to the master map.
			layerOne = new LinkedHashMap<>();
			rgbToMunsell.put(red, layerOne);
		}

		// The second layer of the master map.
		LinkedHashMap<Integer, MunsellColor> layerTwo;

		// If the first layer contains the green value, return the layer with the green
		// value.
		if (layerOne.containsKey(green))
		{
			layerTwo = layerOne.get(green);
		} else
		{
			// Otherwise create a new layer and add the green value to the master map.
			layerTwo = new LinkedHashMap<>();
			layerOne.put(green, layerTwo);
		}

		// If the second layer contains the blue value, a duplicate has been found.
		if (layerTwo.containsKey(blue))
		{
			return;
		}

		// Add the color to the given blue value.
		layerTwo.put(blue, color);
	}
}
