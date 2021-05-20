package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Color;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import model.Hue;
import model.MunsellColor;
import util.ColorConverter;

public class ColorConverterTest
{

	Hue testHue;
	Hue testHue2;
	MunsellColor testMunsellColor;
	MunsellColor testMunsellColor2;
	Color testColor;
	Color testColor2;

	public void setup()
	{

		ColorConverter.buildCSVMaps();

		// constructor: prefix, hueNumber
		testHue = new Hue("R", (float) 2.5);
		testHue2 = new Hue("G", (float) 1.36);
		// constructor: hue, value, chroma
		testMunsellColor = new MunsellColor(testHue, 1, 2);
		testMunsellColor2 = new MunsellColor(testHue2, (float) 1.00, (float) 3.83);
		// constructor: red, green, blue
		testColor = new Color(45, 21, 31);
		testColor2 = new Color(0, 34, 17);

	}

	@Test
	void testWithoutSetup()
	{
		assertThrows(NullPointerException.class, () -> ColorConverter.fromRGB(Color.red));
	}

	@Test
	void fromMunsellandRGBTest()
	{

		setup();

		// this tests the first item in the Munsell2RGB CSV file...
		assertEquals(ColorConverter.fromMunsell(testMunsellColor), testColor);
		assertEquals(ColorConverter.fromRGB(testColor2), testMunsellColor2);

	}

	@Test
	void testGetHues()
	{
		setup();

		ArrayList<Hue> allHues = ColorConverter.getHues();
		assertEquals(allHues.size(), 40);
	}

	@Test
	void testColorMatrix()
	{
		setup();

		ArrayList<ArrayList<MunsellColor>> redSlice = ColorConverter.getColorsMatrix(new Hue("R", 5));
		assertEquals(redSlice.size(), 9);
		assertEquals(redSlice.get(0).size(), 4);
	}

	@Test
	void testHighestChromaInHue()
	{
		setup();

		MunsellColor highestRed = ColorConverter.getHighestChromaInHue(new Hue("R", 5));
		assertEquals(highestRed.getChroma(), 20);
	}

}
