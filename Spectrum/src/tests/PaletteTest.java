package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import model.Hue;
import model.MunsellColor;
import model.Palette;
import util.ColorConverter;

class PaletteTest
{


	private MunsellColor c1 = new MunsellColor(new Hue("R", 2.5f), 5f, 3f);
	private MunsellColor c2 = new MunsellColor(new Hue("R", 5f), 5f, 3f);
	private MunsellColor c3 = new MunsellColor(new Hue("R", 7.5f), 5f, 3f);
	private MunsellColor c4 = new MunsellColor(new Hue("R", 10f), 5f, 3f);
	
	@Test
	void testAddColor()
	{
	
		
		Palette colors = new Palette();
		
		colors.addColor(c1);
		colors.addColor(c2);
		colors.addColor(c3);
		colors.addColor(c4);
		
		assertEquals(4, colors.getColors().size());
		
		MunsellColor c5 = new MunsellColor(new Hue("Y", 2.5f), 5f, 3f);
		MunsellColor c6 = new MunsellColor(new Hue("Y", 2.5f), 5f, 4f);
		MunsellColor c7 = new MunsellColor(new Hue("Y", 2.5f), 5f, 5f);
		MunsellColor c8 = new MunsellColor(new Hue("Y", 2.5f), 5f, 6f);
		MunsellColor c9 = new MunsellColor(new Hue("Y", 2.5f), 5f, 7f);
		MunsellColor c10 = new MunsellColor(new Hue("Y", 2.5f), 5f, 8f);
		MunsellColor overMax = new MunsellColor(new Hue("Y", 2.5f), 5f, 9f);
		
		colors.addColor(c5);
		colors.addColor(c6);
		colors.addColor(c7);
		colors.addColor(c8);
		colors.addColor(c9);
		colors.addColor(c10);
		colors.addColor(overMax);
		
		assertEquals(overMax.toString(), colors.getColors().get(9).toString());
	}
	
	@Test
	void testGetColors()
	{
		Palette colors = new Palette();
		
		colors.addColor(c1);
		colors.addColor(c2);
		colors.addColor(c3);
		colors.addColor(c4);
		
		assertTrue(c2.equals(colors.getColors().get(1)));
	}
	
	@Test
	void testGetClosetColor()
	{
		ColorConverter.buildCSVMaps();
		Palette colors = new Palette();
		
		colors.addColor(c1);
		colors.addColor(c2);
		colors.addColor(c3);
		colors.addColor(c4);
		
		assertTrue(c4.equals(colors.getClosestColor(new MunsellColor(new Hue("Y", 2.5f), 5f, 5f))));
	}
	
	@Test
	void testRemoveColor()
	{
		Palette colors = new Palette();
		
		colors.addColor(c1);
		colors.addColor(c2);
		colors.addColor(c3);
		colors.addColor(c4);
		
		colors.removeColor(c2);
		
		assertTrue(c3.equals(colors.getColors().get(1)));
	}

	@Test
	void testMixColor() 
	{
		ColorConverter.buildCSVMaps();
		
		ArrayList<MunsellColor> colorList = new ArrayList<>();
		ArrayList<Double> weightList = new ArrayList<>();
		MunsellColor expected = new MunsellColor(new Hue("BG", 9.79f), 5f, 6f);
		
		colorList.add(new MunsellColor(new Hue("G", 7.5f), 5f, 16f));
		colorList.add(new MunsellColor(new Hue("B", 7.5f), 4f, 14f));
		weightList.add(1d);
		weightList.add(1d);
		
		MunsellColor actual = Palette.mixColor(colorList, weightList);
		
		assertEquals(expected.toString(), actual.toString());
	}
}
