package gui;

import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import model.Hue;
import model.MunsellColor;
import util.ColorConverter;

/**
 * @author Jake Boychenko & Jedidiah Keplinger
 * @version 1, (11/01/2019)
 * 
 *          Description: Tab for displaying MunsellColors in an arranged fashion
 *          with the ability to select a hue and see more colors based on that
 *          hue.
 */
public class ArrangedColorsTab extends JPanel
{

	/**
	 * The serial ID for this GUI tab.
	 */
	private static final long serialVersionUID = 1L;
	private Hue selectedHue; // The currently selected hue.
	private JPanel colorSlicePanel; // The panel that contains the color slices.
	private JPanel huePickerPanel; // The panel for displaying color selection options

	/**
	 * Create the arranged color tabs for displaying a 2D "slice" of a selected
	 * Munsell Color.
	 */
	public ArrangedColorsTab()
	{
		// Set the initial layout of this tab.
		setLayout(new BorderLayout());
		ImageIcon jmulogo = new ImageIcon("JMUicon.png");
		// Initialize the color slice panel.
		colorSlicePanel = new JPanel()
		{
			/**
			 * default serial
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g)
			{
				if (MunsellWindow.scheme.equals("James Madison University"))
				{
					g.drawImage(jmulogo.getImage(), 65, -89, 1000, 800, null);
					super.paintComponent(g);
				}

			}
		};
		colorSlicePanel.setOpaque(false);
		colorSlicePanel.setLayout(new BoxLayout(colorSlicePanel, BoxLayout.Y_AXIS));

		// Initialize the hue picker panel.
		huePickerPanel = new JPanel();

		// Initialize the main panel
		selectHue(new Hue("B", 7.5f));
		arrangeHuePickerPanel();

		// Add the components to the tab.
		add(colorSlicePanel, BorderLayout.CENTER);
		add(huePickerPanel, BorderLayout.SOUTH);
	}

	/**
	 * Selects a hue and arranges the color slice accordingly.
	 * 
	 * @param hue the hue to select.
	 */
	private void selectHue(Hue hue)
	{
		selectedHue = hue;
		arrageColorSlice();
	}

	/**
	 * Arranges the colors of the color slice to the selected hue.
	 */
	private void arrageColorSlice()
	{
		MunsellColorBlock cell; // A cell for a color

		// Remove the slice to replace it
		colorSlicePanel.removeAll();

		// Obtain the 2D array and convert it into a GUI layout for display.
		for (ArrayList<MunsellColor> list : ColorConverter.getColorsMatrix(selectedHue))
		{
			JPanel layoutX = new JPanel(); // A new panel to add to the main panel.

			// Set the panel layout.
			layoutX.setLayout(new BoxLayout(layoutX, BoxLayout.X_AXIS));
			layoutX.setAlignmentX(0);

			/*
			 * Iterate over the 2D list and create colored cells and add them to the panel.
			 */
			for (MunsellColor innerList : list)
			{
				cell = new MunsellColorBlock(innerList, 57, 55, false, true, false, 0);

				layoutX.add(cell);
			}

			// Add the new panel
			colorSlicePanel.add(layoutX);
		}

		// Refresh the page to show the new slice
		validate();
		repaint();
	}

	/**
	 * Set up the hue picker panel for selecting a new color to display.
	 */
	private void arrangeHuePickerPanel()
	{
		JPanel layout = new JPanel(); // The selection representation
		ArrayList<Hue> hues = ColorConverter.getHues(); // Obtain hues from CSV
		MunsellColorBlock cell; // A cell for a color

		// Obtain the highest chroma value to display them as colored cells.
		for (Hue hue : hues)
		{
			cell = new MunsellColorBlock(ColorConverter.getHighestChromaInHue(hue), 12, 12, false, false, false, 0);
			layout.add(new HuePicker(cell));
		}

		// Add the hue picker panel to the main panel.
		huePickerPanel.add(layout);
	}

	/**
	 * 
	 * @author Jake Boychenko
	 * @version 1, (11/01/2019)
	 * 
	 *          Private inner class for enabling clicking on a color in the hue
	 *          picker panel to display a new 2D slice.
	 */
	private class HuePicker extends JPanel
	{
		/**
		 * The default serial ID for this GUI tab.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * default constructor for passing the colored block to obtain its hue key.
		 * 
		 * @param block the colored block to use
		 */
		public HuePicker(MunsellColorBlock block)
		{

			JPanel coverPanel = new JPanel();
			coverPanel.setSize(new Dimension(12, 12));
			coverPanel.setOpaque(false);

			// Add a tool tip text for each MunsellColorBlock to show the
			// details of that color when it's moused over.
			coverPanel.setToolTipText(block.getMunsellColor().toString());

			coverPanel.addMouseListener(new MouseListener()
			{

				/**
				 * When a color is selected, set the new hue based on the selected color's key
				 * (i.e. 2.5R), and set the display to that new color.
				 */
				@Override
				public void mouseClicked(MouseEvent e)
				{
					if (e.getButton() == MouseEvent.BUTTON1)
					{
						selectHue(block.getMunsellColor().getHue());

						if (e.getClickCount() == 2)
						{
							MunsellWindow.setTab(1);
							MunsellWindow.getDetailsTab().showDetailsOfColor(block.getMunsellColor());
						}
					}
				}

				/**
				 * This method is unused.
				 */
				@Override
				public void mousePressed(MouseEvent e)
				{
					if (e.isPopupTrigger())
					{
						block.doPop(e);
					}
				}

				/**
				 * This method is unused.
				 */
				@Override
				public void mouseReleased(MouseEvent e)
				{
					if (e.isPopupTrigger())
					{
						block.doPop(e);
					}
				}

				/**
				 * This method is unused.
				 */
				@Override
				public void mouseEntered(MouseEvent e)
				{
				}

				/**
				 * This method is unused.
				 */
				@Override
				public void mouseExited(MouseEvent e)
				{
				}
			});
			block.add(coverPanel);

			// Add the block, and a mouse listener to it.
			add(block);
		}
	}
}
