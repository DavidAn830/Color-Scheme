package model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Jake Boychenko
 * @version 1, (10/23/2019)
 * 
 *          Description: Represents a hue by either a hue value (0-100) or a hue
 *          prefix followed by a hue number (RP 5).
 */
public class Hue
{
	private String prefix; // The prefix of the hue.
	private float hue; // The hue value (from 0-10).

	// Valid hue prefixes
	public static final ArrayList<String> huePrefixes = new ArrayList<>(
			Arrays.asList("R", "YR", "Y", "GY", "G", "BG", "B", "PB", "P", "RP", "N"));

	/**
	 * Creates a hue with the given prefix and hue value.
	 * 
	 * @param prefix the prefix of the color.
	 * @param hue    the hue value. Must be between 0-100.
	 */
	public Hue(String prefix, float hue)
	{
		if (prefix == null)
			throw new NullPointerException("Hue prefix cannot be null!");

		this.prefix = prefix;
		this.hue = hue;

		if (!isValidHue())
		{
			throw new IllegalArgumentException("Created hue was not valid!");
		}
	}

	/**
	 * Creates a hue with the given total hue (0-100). Parses the total hue to a
	 * prefix and value from 0-10.
	 * 
	 * @param totalHue
	 */
	public Hue(float totalHue)
	{
		if (totalHue < 0 || totalHue > 100)
			throw new IllegalArgumentException("Total hue should be between 0 and 100!");
		prefix = huePrefixes.get((int) (totalHue - 0.1) / 10);
		hue = (totalHue - 0.5f) % 10 + 0.5f;
	}

	/**
	 * Creates a hue with the given hueName (i.e. 1.36G).
	 * 
	 * @param hueName the hueName to use.
	 */
	public Hue(String hueName)
	{
		if (hueName == null)
			throw new NullPointerException("Hue prefix cannot be null!");

		// Gather the numbers from the hueName.
		String hueNum = "";
		int i;
		for (i = 0; i < hueName.length(); i++)
		{
			char c = hueName.charAt(i);
			if (!(Character.isDigit(c) || c == '.'))
			{
				break;
			}

			hueNum += c;
		}

		hue = Float.parseFloat(hueNum);
		prefix = hueName.substring(i); // The prefix is the rest of the string.

		if (!isValidHue())
		{
			throw new IllegalArgumentException("Created hue was not valid!");
		}
	}

	/**
	 * @return the hue's value from 0-100. Hues with 'N' prefix are given 0.
	 */
	public float getHueTotalValue()
	{
		if (prefix.equals("N"))
			return 0;

		int index = huePrefixes.indexOf(prefix);

		// If the hue prefix was not found, throw an exception.
		if (index == -1)
			throw new IllegalArgumentException("Invalid hue!");

		return index * 10 + hue;

	}
	
	/**
	 * 
	 */
	public Hue getHuefromTotal() {
		
		Hue result;
		
		if (hue == 0) {
			
			result = new Hue("N", 0);
			return result;
			
		} else {
		
			//for loop should exclude the last entry of hue prefixes (N)
		for (int i = 0; i < huePrefixes.size() - 1; i++) {
			
			if (hue - (i * 10) == 2.5) {
				
				String huePrefix = huePrefixes.get(i);
				float hueNum = (float) 2.5;
				result = new Hue(huePrefix, hueNum);
				return result;

			}
			
			if (hue - (i * 10) == 5.0) {
				
				String huePrefix = huePrefixes.get(i);
				float hueNum = (float) 5.0;
				result = new Hue(huePrefix, hueNum);
				return result;

			}
			
			if (hue - (i * 10) == 7.5) {
				
				String huePrefix = huePrefixes.get(i);
				float hueNum = (float) 7.5;
				result = new Hue(huePrefix, hueNum);
				return result;
				
			}
			
			if (hue - (i * 10) == 10.0) {
				
				String huePrefix = huePrefixes.get(i);
				float hueNum = (float) 10.0;
				result = new Hue(huePrefix, hueNum);
				return result;

			}
			
		}
		
		return null;
			
		}
		
				
	}

	/**
	 * @return the hue prefix.
	 */
	public String getHuePrefix()
	{
		return prefix;
	}

	/**
	 * @return the hue prefix followed by the hue number.
	 */
	public String toString()
	{
		// Prevent the decimal places to go past five.
		DecimalFormat df = new DecimalFormat("###.#####");
		
		if (hue == (long) hue)
		{
			return String.format("%d%s", (long) hue, prefix);
		}
		
		return String.format("%s%s", df.format(hue), prefix);
	}

	/**
	 * @return the hue value.
	 */
	public float getHue()
	{
		return hue;
	}

	/**
	 * @return true if the hue is a grayscale.
	 */
	public boolean isGrayscale()
	{
		return prefix.equals("N");
	}

	/**
	 * @param o The object to check with
	 * @return Returns whether the given objects are equal.
	 */
	public boolean equals(Object o)
	{
		if (!(o instanceof Hue))
			return false;

		// If the object is a hue, check if its values are the same.
		Hue h = (Hue) o;
		return prefix.equals(h.prefix) && hue == h.hue;
	}

	/**
	 * Override the hash code so that the HashMaps in ColorConverter correctly
	 * reference duplicate Hues.
	 * 
	 * @return the hash code of a given hue.
	 */
	public int hashCode()
	{
		int result = 17;
		result = 31 * result + prefix.hashCode();
		result = 31 * result + (int) hue * 100;
		return result;
	}

	/**
	 * Checks validity of hue.
	 *
	 * @return true if prefix and hue carry correct values
	 */
	public boolean isValidHue()
	{
		return huePrefixes.contains(prefix) && hue <= 10;
	}
}
