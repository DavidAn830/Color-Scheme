package tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import model.Hue;
import model.MunsellColor;
import util.ColorConverter;

/**
 * Test cases for MunsellColor class.
 *
 * @author andt
 * @version Oct 22, 2019
 */
class MunsellColorTest
{

	private MunsellColor mColor;
	private MunsellColor mColor2;

	/*
	 * @Test void fromRGBtest() {
	 * 
	 * //mColor = MunsellColor.fromRGB(null); // test null cases here. what should
	 * hue, chroma, and value be?
	 * 
	 * // takes Color whose RGB is (0, 34, 17) and returns corresponding // munsell
	 * color Color c = new Color(0, 34, 17); mColor = MunsellColor.fromRGB(c);
	 * assertEquals(3.83, mColor.getChroma()); // Correct chroma? assertEquals(1,
	 * mColor.getValue()); // Correct value? assertEquals("1.36G",
	 * mColor.getHue().toString()); // Correct hue? }
	 * 
	 * @Test void toColortest() {
	 * 
	 * // Color c = mColor.toColor(); // assertNull(c);
	 * 
	 * mColor = new MunsellColor(new Hue("R", 2.5f), 2f, 1f); Color c =
	 * mColor.toColor(); assertEquals(45, c.getRed()); // Correct red value?
	 * assertEquals(21, c.getGreen()); // Correct green value? assertEquals(31,
	 * c.getBlue()); // Correct blue value?
	 * 
	 * mColor = new MunsellColor(new Hue("N", 2.5f), 2f, 1f); c = mColor.toColor();
	 * assertEquals(0, c.getRed()); assertEquals(0, c.getGreen()); assertEquals(0,
	 * c.getBlue()); }
	 */
	@Test
	void grayscaleTest()
	{

//		mColor = MunsellColor.N(-2); // too small value
//		assertNull(mColor);
//		
//		mColor = MunsellColor.N(11); // too large value
//		assertNull(mColor);

		mColor = MunsellColor.n(4); // acceptable value
		assertEquals("0N", mColor.getHue().toString());
		assertEquals(0, mColor.getChroma());
		assertEquals(4, mColor.getValue());
		assertEquals(0, mColor.getHue().getHueTotalValue());
	}

	@Test
	void getterTest()
	{

		mColor = new MunsellColor(new Hue("PB", 7.5f), 2f, 22f);

		// Test getHue()
		assertEquals("7.5PB", mColor.getHue().toString());

		// Test getValue()
		assertEquals(2, mColor.getValue());

		// Test getChroma()
		assertEquals(22, mColor.getChroma());
	}

	@Test
	void toStringTest()
	{

		mColor = new MunsellColor(new Hue("N", 2.5f), 2f, 22f);
		assertEquals("N2", mColor.toString()); // grayscale to toString

		mColor = new MunsellColor(new Hue("PB", 2.5f), 2f, 22f);
		assertEquals("2.5PB, 2, 22", mColor.toString()); // normal
	}

	@Test
	void equalsTest()
	{

		mColor = new MunsellColor(new Hue("N", 2.5f), 2f, 22f);
		mColor2 = new MunsellColor(new Hue("N", 2.5f), 2f, 22f);
		Object o = new Object();
		// testing that two munsell colors are equal.
		assertTrue(mColor.equals(mColor2));
		// This tests the branch for not passing a munsell color in the equals method.
		assertFalse(mColor.equals(o));

	}

	@Test
	void fromRGBTest()
	{

		ColorConverter.buildCSVMaps();

		Color given = new Color(0, 34, 17);
		MunsellColor expected = new MunsellColor(new Hue("G", (float) 1.36), (float) 1.00, (float) 3.83);

		assertEquals(expected, MunsellColor.fromRGB(given));

		Color edgeCase = new Color(10, 10, 10);
		MunsellColor test = MunsellColor.n(edgeCase.getRed() / 255f * 10);

		assertEquals(MunsellColor.fromRGB(edgeCase), test);
	}

	@Test
	void toColorTest()
	{

		// testing grayscale munsell color to RGB.
		mColor = MunsellColor.n(4); // acceptable value
		Color c = mColor.toColor();
		assertEquals(mColor.toColor(), new Color(0.4f, 0.4f, 0.4f));

		// testing normal munsell color to RGB.
		Color given = new Color(45, 21, 31);
		MunsellColor expected = new MunsellColor(new Hue("R", (float) 2.5), (float) 1, (float) 2);
		assertEquals(given, expected.toColor());

	}

	@Test
	void validMunsellColorTest()
	{

		MunsellColor expected = new MunsellColor(new Hue("R", (float) 2.5), (float) 1, (float) 2);
		assertTrue(expected.isValidMunsellColor());
		assertEquals(2.5, expected.getHue().getHueTotalValue());

		expected = new MunsellColor(new Hue("R", (float) 3), (float) 11, (float) 2);
		assertFalse(expected.isValidMunsellColor());
	}

	@Test
	void testCloseColors()
	{
		ColorConverter.buildCSVMaps();

		MunsellColor closeToRed = new MunsellColor(new Hue("R", 6f), 17f, 22f);
		MunsellColor actualRed = new MunsellColor(new Hue("R", 5f), 10f, 20f);
		assertEquals(closeToRed.toColor(), actualRed.toColor());

		Color redClose = new Color(254, 1, 1);
		Color red = Color.red;
		assertEquals(MunsellColor.fromRGB(redClose), MunsellColor.fromRGB(red));

	}

	@Test
	void testGetComplimentaryColor()
	{
		MunsellColor color = new MunsellColor(new Hue("R", (float) 3), (float) 2, (float) 2);
		MunsellColor complimentaryColor = MunsellColor.getComplimentaryColor(color);
		assertEquals("0.53R", complimentaryColor.getHue().toString());
	}

	@Test
	void testGetAnalogousColors()
	{
		MunsellColor color = new MunsellColor(new Hue("R", (float) 3), (float) 2, (float) 2);
		HashMap<Integer, ArrayList<MunsellColor>> result = MunsellColor.getAnalogousColors(color);
		assertEquals(1, result.get(2).get(0).getHue().getHue());
	}

	@Test
	void testGetSplitComplementaryColors()
	{
		MunsellColor color = new MunsellColor(new Hue("R", (float) 3), (float) 2, (float) 2);
		HashMap<Integer, ArrayList<MunsellColor>> result = MunsellColor.getSplitComplementaryColors(color);
		assertEquals("1.53R", result.get(1).get(0).getHue().toString());
	}

	@Test
	void testMix()
	{

		ColorConverter.buildCSVMaps();

		ArrayList<MunsellColor> colorList = new ArrayList<>();
		ArrayList<Double> weightList = new ArrayList<>();
		MunsellColor expected = new MunsellColor(new Hue("BG", 9.79f), 5f, 6f);

		colorList.add(new MunsellColor(new Hue("G", 7.5f), 5f, 16f));
		colorList.add(new MunsellColor(new Hue("B", 7.5f), 4f, 14f));
		weightList.add(1d);
		weightList.add(1d);

		MunsellColor actual = MunsellColor.mix(colorList, weightList);

		assertEquals(expected.toString(), actual.toString());

	}

	@Test
	void testGetColorDistance()
	{
		ColorConverter.buildCSVMaps();

		MunsellColor mc1 = new MunsellColor(new Hue("G", 7.5f), 5f, 16f);
		MunsellColor mc2 = new MunsellColor(new Hue("B", 7.5f), 4f, 14f);
		assertTrue(Math.abs(94.43 - MunsellColor.getColorDistance(mc1.toColor(), mc2.toColor())) < 0.01);

	}

	@Test
	void testGetMixingWeights()
	{
		ArrayList<MunsellColor> colorList = new ArrayList<>();
		MunsellColor wanted = new MunsellColor(new Hue("BG", 9.79f), 5f, 6f);
		colorList.add(new MunsellColor(new Hue("G", 7.5f), 5f, 16f));
		colorList.add(new MunsellColor(new Hue("B", 7.5f), 4f, 14f));
		assertEquals(Arrays.asList(2d, 2d).toString(), MunsellColor.getMixingWeights(colorList, wanted).toString());

	}

	@Test
	void testGetHuesCC()
	{
		ColorConverter.buildCSVMaps();

		assertEquals(40, ColorConverter.getHues().size());
	}

	@Test
	void testGetHightestChromaInHueCC()
	{
		ColorConverter.buildCSVMaps();
		Hue hue = new Hue("R", 2.5f);
		MunsellColor expected = new MunsellColor(new Hue("R", 2.5f), 5f, 20f);
		assertTrue(expected.equals(ColorConverter.getHighestChromaInHue(hue)));
	}

	@Test
	void testGetColorsMatrix()
	{
		ColorConverter.buildCSVMaps();

		Hue hue = new Hue("R", 2.5f);
		assertEquals(9, ColorConverter.getColorsMatrix(hue).size());
	}
}
