package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import model.MunsellColor;
import model.Palette;

/**
 * @author Jake Boychenko
 * @version 2, (10/22/2019)
 * 
 *          Description: A JComponent that shows the color and information of a
 *          MunsellColor.
 */
public class MunsellColorBlock extends JComponent
{

	/**
	 * default serial version ID number.
	 */
	private static final long serialVersionUID = 1L;

	private boolean showString; // If the color string will be shown.
	private boolean showWeight; // If the weight of a color will be shown.
	private double colorWeight; // The weight of a color for mixing.
	private int width; // The width of a MunsellColorBlock.
	private int height; // The height of a MunsellColorBlock.
	private MunsellColor munsellColor; // The color this color block will show.
	private boolean canNavigateToDetails; // Whether double clicking on it will
											// navigate to the details tab.

	/**
	 * Creates a MunsellColorBlock that represents a MunsellColor. Default
	 * constructor that will be called if no color size attributes are specified.
	 * 
	 * @param munsellColor the color to display
	 */
	public MunsellColorBlock(MunsellColor munsellColor)
	{
		// Call the other constructor with default size attributes
		this(munsellColor, 100, 70, true, true, false, 0);
	}

	/**
	 * Constructor to set specific attributes when a MunsellColorBlock is created.
	 * 
	 * @param munsellColor      the color object to assign
	 * @param width             the width of the color block
	 * @param height            the height of the color block
	 * @param showString        if the string representation will be shown
	 * @param navigateToDetails whether double clicking on this will navigate to the
	 *                          details tab.
	 * @param displayWeight     whether the weight of a specific color will be
	 *                          displayed
	 * @param weight            the weight of a color for mixing
	 */
	public MunsellColorBlock(MunsellColor munsellColor, int width, int height, boolean showString,
			boolean navigateToDetails, boolean displayWeight, double weight)
	{
		// Initialize attributes of a color block.
		super();
		this.showString = showString;
		this.munsellColor = munsellColor;
		this.width = width;
		this.height = height;
		this.canNavigateToDetails = navigateToDetails;
		this.showWeight = displayWeight;
		this.colorWeight = weight;

		// Set size of the block.
		setMinimumSize(new Dimension(width, height));
		setMaximumSize(new Dimension(width, height));
		setPreferredSize(new Dimension(width, height));

		// Add a tool tip text for each MunsellColorBlock to show the
		// details of that color when it's moused over.
		setToolTipText(munsellColor.toString());

		addMouseListener(new MouseListener()
		{

			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (canNavigateToDetails)
				{
					if (e.getClickCount() == 2)
					{
						MunsellWindow.setTab(1);
						MunsellWindow.getDetailsTab().showDetailsOfColor(munsellColor);
					}
				}

			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					doPop(e);
				}

			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					doPop(e);
				}

			}

			@Override
			public void mouseEntered(MouseEvent e)
			{

			}

			@Override
			public void mouseExited(MouseEvent e)
			{

			}

		});
	}

	/**
	 * When either the right click button is pressed or released, display the menu.
	 * 
	 * @param e the action of the mouse.
	 */
	public void doPop(MouseEvent e)
	{
		// Create a new menu object
		PopUpMenu menu = new PopUpMenu();

		// Display the menu
		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	/**
	 * 
	 * @author Jedidiah Keplinger Inner class for implementation of a pop-up menu
	 *         for right clicking.
	 */
	public class PopUpMenu extends JPopupMenu
	{
		/**
		 * default serial version ID number.
		 */
		private static final long serialVersionUID = 1L;

		JMenuItem addToPalette; // adding to the palette
		JMenuItem removeFromPalette; // removing an item from the palette
		JMenuItem paletteSlotOne; // add to the first palette slot
		JMenuItem paletteSlotTwo; // add to the second palette slot
		JMenuItem paletteAddToMixer; // add a color to a list of colors for mixing
		JMenuItem desiredColor; // specifying a desired color from mixing

		/**
		 * Default no-arg constructor for when this class is called. Will initialize the
		 * menu items.
		 */
		public PopUpMenu()
		{
			// Set the right-click menu items.
			addToPalette = new JMenuItem("Add to Palette");

			removeFromPalette = new JMenuItem("Remove from Palette");
			paletteAddToMixer = new JMenuItem("Add to Mixer");
			desiredColor = new JMenuItem("Set as Desired Color");

			// Add an action for when the Add to Palette button is pressed.
			addToPalette.addMouseListener(new MouseListener()
			{

				@Override
				public void mouseClicked(MouseEvent e)
				{
				}

				@Override
				public void mousePressed(MouseEvent e)
				{
					Palette.getInstance().addColor(munsellColor);
					PaletteTab.initializeMixTab();
				}

				@Override
				public void mouseReleased(MouseEvent e)
				{
				}

				@Override
				public void mouseEntered(MouseEvent e)
				{
				}

				@Override
				public void mouseExited(MouseEvent e)
				{
				}
			});

			paletteAddToMixer.addMouseListener(new MouseListener()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
				}

				@Override
				public void mousePressed(MouseEvent e)
				{
					PaletteTab.setMixerColor(munsellColor);
					PaletteTab.initializeMixTab();
				}

				@Override
				public void mouseReleased(MouseEvent e)
				{
				}

				@Override
				public void mouseEntered(MouseEvent e)
				{
				}

				@Override
				public void mouseExited(MouseEvent e)
				{
				}
			});

			// Add an action for when "Set as Desired Color" is pressed.
			desiredColor.addMouseListener(new MouseListener()
			{

				@Override
				public void mouseClicked(MouseEvent e)
				{
				}

				@Override
				public void mousePressed(MouseEvent e)
				{
					PaletteTab.setColors(munsellColor, "desired");
				}

				@Override
				public void mouseReleased(MouseEvent e)
				{
				}

				@Override
				public void mouseEntered(MouseEvent e)
				{
				}

				@Override
				public void mouseExited(MouseEvent e)
				{
				}

			});

			// Add a mouse listener for when the remove from palette button is pressed.
			removeFromPalette.addMouseListener(new MouseListener()
			{

				@Override
				public void mouseClicked(MouseEvent e)
				{
				}

				@Override
				public void mousePressed(MouseEvent e)
				{
					Palette.getInstance().removeColor(munsellColor);
					PaletteTab.initializeMixTab();
				}

				@Override
				public void mouseReleased(MouseEvent e)
				{
				}

				@Override
				public void mouseEntered(MouseEvent e)
				{
				}

				@Override
				public void mouseExited(MouseEvent e)
				{
				}
			});

			add(addToPalette);

			add(removeFromPalette);
			add(paletteAddToMixer);
			add(desiredColor);
		}
	}

	/**
	 * Override JComponent's paintComponent in order to draw the MunsellColorBlock.
	 * 
	 * @param g the component to add a string representation of a color to.
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// Convert to Graphics2D for superior drawing methods.
		Graphics2D g2d = (Graphics2D) g;

		// Fill background color.
		g2d.setColor(munsellColor.toColor());
		g2d.fillRect(0, 0, width, height);

		// Write text.
		g2d.setColor(textContrastColor(munsellColor.toColor()));

		// Check to see if the string representation should be shown.
		if (showString && !showWeight)
		{
			g2d.drawString(munsellColor.toString(), 0, height - g2d.getFontMetrics().getHeight() / 2);
		} else if (showWeight)
		{
			g2d.drawString(Double.toString(colorWeight) + "x", 0, height - g2d.getFontMetrics().getHeight() / 2);
		}
	}

	/**
	 * Returns the text color needed to contrast with the given color.
	 * 
	 * @param color the background color to contrast with.
	 * @return the text color. This is either white or black.
	 */
	private Color textContrastColor(Color color)
	{

		// This value was found on
		// https://stackoverflow.com/questions/1855884/
		// determine-font-color-based-on-background-color
		double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;

		// Return color based on the luminance of the background.
		if (luminance > 0.5)
		{
			return Color.black;
		} else
		{
			return Color.white;
		}
	}

	/**
	 * @return the munsell color used.
	 */
	public MunsellColor getMunsellColor()
	{
		return munsellColor;
	}

	/**
	 * Sets the munsell color.
	 * 
	 * @param munsellColor the color to set.
	 */
	public void setMunsellColor(MunsellColor munsellColor)
	{
		this.munsellColor = munsellColor;
		repaint();
	}
}
