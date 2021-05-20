package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import model.Hue;

/**
 * Test cases for Hue class.
 *
 * @author David An
 * @version Oct 22, 2019
 */
class HueTest
{
	String prefix = "R";
	float hueNum = (float) 2.5;
	Hue hue1 = new Hue(prefix, hueNum);

	Hue hue1total = new Hue((float) 2.5);

	String prefix2 = "YR";
	float hueNum2 = (float) 2.5;
	Hue hue2 = new Hue(prefix2, hueNum2);

	Hue hue2total = new Hue((float) 12.5);

	String prefix3 = "RP";
	float hueNum3 = (float) 10.0;
	Hue hue3 = new Hue(prefix3, hueNum3);

	Hue hue3total = new Hue((float) 100.0);

	private Hue hue;

	public static final ArrayList<String> huePrefixes = new ArrayList<>(
			Arrays.asList("R", "YR", "Y", "GY", "G", "BG", "B", "PB", "P", "RP", "N"));

	@Test
	void fromNameTest()
	{
//		
//		hue = new Hue("PB", -5f); // hue is null when hue # is < 0
//		assertNull(hue);
//		
//		hue = new Hue("PB", 15f); // hue is null when hue # is > 100
//		assertNull(hue);
//		
//		hue = new Hue(null, 5f); // hue is null when prefix is null
//		assertNull(hue);		

		// hue with correct inputs
		hue = new Hue("PB", 7.5f);
		assertEquals("7.5PB", hue.toString());
	}

	@Test
	void getterTest()
	{

		hue = new Hue("G", 2.5f);

		// test getHuePrefix()
		assertEquals("G", hue.getHuePrefix());

		// test getHueNumber()
		assertEquals(2.5, hue.getHue());
	}

	@Test
	void isGrayscaleTest()
	{

		hue = new Hue("N", 2.5f);
		assertTrue(hue.isGrayscale()); // hue is gray scale

		hue = new Hue("G", 2.5f);
		assertFalse(hue.isGrayscale()); // hue is not gray scale
	}

	@Test
	void toStringTest()
	{

		hue = new Hue("R", 2f);
		assertEquals("2R", hue.toString());

		hue = new Hue("R", 2.5f);
		assertEquals("2.5R", hue.toString());
	}

	@Test
	void testGetHueTotalValue()
	{

		assertEquals(100.0, hue3.getHueTotalValue());
		assertEquals(2.5, hue1.getHueTotalValue());
		assertEquals(12.5, hue2.getHueTotalValue());

	}

	@Test
	void testTotalValuetoHue()
	{

		assertEquals(hue3total, hue3);
		assertEquals(hue1total, hue1);
		assertEquals(hue2total, hue2);

	}

}
