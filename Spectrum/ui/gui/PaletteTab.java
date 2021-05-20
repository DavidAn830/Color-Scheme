package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import model.MunsellColor;
import model.Palette;

/**
 * 
 * @author Jedidiah Keplinger
 * @version 1, (11/11/2019) Description: Creates a tab to add to the main window
 *          (MunsellWindow) to enable the use and functionality of a Palette for
 *          Spectrum.
 *
 */
public class PaletteTab extends JPanel
{
	private static final String ERROR_MESSAGE_CLOESST = "Error:\nThe colors in the Palette can "
			+ "not be mixed to obtain the wanted " + "color.\nTry adding additional colors to " + "your Palette.";
	private static final String ERROR_MESSAGE_EMPTY = "Error:\nThe color mixing array is empty.\n"
			+ "Please add a color to your mixer.";

	private static JPanel palette; // Displaying the colors in the palette
	private static JPanel ui; // Container for the palette and options
	private static JPanel mixTab; // Container tab for mixing two colors
	private static JPanel desiredColorTab; // Container tab for seeing how to mix colors
	private static JTabbedPane tabs; // Displaying multiple sections of the palette
	private static MunsellColorBlock result; // The resulting color from mixing
	private static MunsellColorBlock desired; // The specified desired result from mixing
	private static MunsellColor wanted; // The Munsell color given by the desired block
	private static JPanel mixer; // container for selected colors
	private static JPanel results; // container for the result color
	private static JPanel wantedColor; // container for the specified desired color
	private static List<Double> mixingWeights; // mixing the palette to acheive a certain color
	private static JPanel buttonPanel;
	private static JButton mix;
	private static JButton clearMixer;

	private static JLabel addMessage;

	// Arrays for various computations and layout arrangement.
	private static ArrayList<MunsellColorBlock> colorBlockArr;
	private static ArrayList<Double> weightedArr;
	private static ArrayList<MunsellColor> colorArr;
	private static final String ERROR_MSG = "You can't have more than 18 colors in Mixer";

	/**
	 * The serial ID for this GUI tab.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default no-arg constructor. Will create the primary window for the palette
	 * display.
	 */
	public PaletteTab()
	{
		// Create the interface gui and set its layout.
		ui = new JPanel();
		palette = new JPanel();
		mixTab = new JPanel();
		mixTab.setOpaque(false);
		desiredColorTab = new JPanel();
		tabs = new JTabbedPane();
		addMessage = new JLabel("Add colors to mix from Arranged Tab!");
		mixer = new JPanel(); // container for mixing colors
		mixer.add(addMessage);
		mixingWeights = new ArrayList<Double>();

		buttonPanel = new JPanel();

		ui.setLayout(new BorderLayout());
		ui.setBackground(Color.LIGHT_GRAY);

		// Initialize some default display attributes for the slots and result.
//		result = new MunsellColorBlock(MunsellColor.n(9));
		desired = new MunsellColorBlock(MunsellColor.n(10));
		wanted = desired.getMunsellColor();

		colorBlockArr = new ArrayList<>();
		weightedArr = new ArrayList<>();
		colorArr = new ArrayList<>();

		// Set the tab's default color scheme and attributes.
		setLayout(new BorderLayout());
		setBackground(Color.LIGHT_GRAY);

		// Initialize the palette and options display.
		initializeOptions();
		initializeMixTab();
		initializeDesiredColorTab();

		tabs.add("Mixing", mixTab);
		tabs.add("Desired Color Mixing", desiredColorTab);
		add(tabs, BorderLayout.CENTER);
	}

	/**
	 * Adds selected colors to mixer panel.
	 *
	 * @param color selected
	 */
	public static void setMixerColor(MunsellColor color)
	{
		if (colorBlockArr.size() < 18)
		{
			MunsellColorBlock color3 = new MunsellColorBlock(color);
			if (colorArr.size() == 0)
			{
				mixer.removeAll();
				mixer.revalidate();
				mixer.repaint();
			}
			mixer.add(color3);
			colorBlockArr.add(color3);
			weightedArr.add(1d);
			colorArr.add(color);
		} else
		{
			JOptionPane.showMessageDialog(new JFrame(), ERROR_MSG);
		}
	}

	/**
	 * Enable the use of selecting colors to display on the palette tab.
	 * 
	 * @param color the color to set
	 * @param cell  the cell to change
	 */
	public static void setColors(MunsellColor color, String cell)
	{
		if (color == null || cell == null)
		{
			System.err.println("Null parameter passed to setColors method in PaletteTab.java");
		}

		if (cell.contentEquals("result"))
		{
			result = new MunsellColorBlock(color, 150, 150, true, true, false, 0);
		} else if (cell.contains("desired"))
		{
			desired = new MunsellColorBlock(color, 200, 150, true, true, false, 0);
			wanted = desired.getMunsellColor();
		}

		initializeMixTab();
		initializeDesiredColorTab();
	}

	/**
	 * Creates and adds the options to the gui container to be added to the main tab
	 * window.
	 */
	private void initializeOptions()
	{
		JPanel options = new JPanel(); // container for option buttons
		mix = new JButton("mix"); // for mixing two selected colors
		clearMixer = new JButton("clear"); // clear mixer panel

		// Clear the panel for refreshing
		buttonPanel.removeAll();
		ui.removeAll();

		buttonPanel.add(mix); // mix button
		buttonPanel.add(clearMixer); // clear button for mixing colors

		// Set the options container attributes.
		options.setLayout(new BoxLayout(options, BoxLayout.X_AXIS));
		options.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		mix.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				updateMixedColor();
			}
		});

		clearMixer.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				mixer.removeAll();
				colorBlockArr.clear();
				weightedArr.clear();
				colorArr.clear();

				results.removeAll();

				mixer.add(addMessage);
				mixer.validate();
				mixer.repaint();
				results.validate();
				results.repaint();
			}
		});

		// Add the container to the gui container to display in the tab.
		ui.add(options, BorderLayout.CENTER);
	}

	/**
	 * Creates a display for selected colors, and the resulting color from combining
	 * colors.
	 */
	public static void initializeMixTab()
	{

		ImageIcon jmulogo = new ImageIcon("JMUicon.png");
		JPanel grid = new JPanel();
		MunsellColorBlock cell; // a cell for displaying a color

		results = new JPanel()
		{
			/**
			 * deafult serial
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g)
			{
				if (MunsellWindow.scheme.equals("James Madison University"))
				{
					g.drawImage(jmulogo.getImage(), -20, -312, 1000, 800, null); // this one is centered to tab
					super.paintComponent(g);
					mix.setBackground(new Color(218, 204, 230));
					clearMixer.setBackground(new Color(244, 239, 225));
				}

			}
		}; // container for the result color
		results.setOpaque(false); // panel for arranging results

		// Clear the current display.
		mixTab.removeAll();
		mixTab.setLayout(new BorderLayout());
		grid.setLayout(new GridLayout(3, 1));

		// Clear the current palette to rebuild it.
		palette.removeAll();

		// Initialize the layout of the component.
		palette.setLayout(new GridLayout(2, 5));

		// Fill in the palette with the available color slots.
		for (MunsellColor color : Palette.getInstance().getColors())
		{
			JPanel gridCell = new JPanel();
			cell = new MunsellColorBlock(color, 20, 20, false, true, false, 0);
			gridCell.setBorder(BorderFactory.createLineBorder(Color.BLACK));

			// Add the new color cell to layout with borders.
			gridCell.add(cell);

			// Add the grid cell to the container.
			palette.add(gridCell);
		}

		// Fill in empty slots if there are not 10 colors present in the
		// palette.
		if (Palette.getInstance().getColors().size() < 10)
		{
			for (int i = Palette.getInstance().getColors().size(); i < 10; i++)
			{
				JPanel gridCell = new JPanel();
				gridCell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				gridCell.setPreferredSize(new Dimension(35, 35));

				// Add the grid cell to the container.
				palette.add(gridCell);
			}
		}

		// Update the desired color tab's array.
		initializeDesiredColorTab();

		// Initialize the mixing colors to a default display.
		mixer.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		for (MunsellColorBlock e : colorBlockArr)
		{
			mixer.add(e);
		}

		// Initialize the result color to a default display.
		results.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		if (result != null)
		{
			results.add(result);
		}

		// Add the colors to the grid.
		grid.add(mixer);
		grid.add(results);
		grid.add(buttonPanel);

		// Add the palette panel to the gui.
		ui.add(palette, BorderLayout.NORTH);

		// Add the color displays to the display container.
		mixTab.add(grid, BorderLayout.CENTER);
		mixTab.add(ui, BorderLayout.EAST);

		mixTab.validate();
		mixTab.repaint();
		ui.validate();
		ui.repaint();
	}

	/**
	 * Create the tab for displaying a list of colors, a desired color, and how to
	 * mix the selected colors to achieve the desired color.
	 */
	private static void initializeDesiredColorTab()
	{
		MunsellColorBlock cell; // a cell for displaying a color
		JPanel upperGUI; // the higher portion of the tab's GUI
		JPanel middleGUI; // the center portion of the tab's GUI
		JPanel paletteGrid; // a grid layout for the colors array
		JPanel weightsGrid; // a grid for laying out the needed color weights
		JButton mixingButton; // when used, display how to mix colors
		ImageIcon jmulogo = new ImageIcon("JMUicon.png");

		// Clear the current tab to rebuild it.
		desiredColorTab.removeAll();

		// Initialize all components for this tab.
		wantedColor = new JPanel();
		paletteGrid = new JPanel();
		weightsGrid = new JPanel();
		mixingButton = new JButton("Find Mixing Weights");
		upperGUI = new JPanel();
		middleGUI = new JPanel()
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
					g.drawImage(jmulogo.getImage(), 63, -142, 1000, 800, null);
					super.paintComponent(g);
					mixingButton.setBackground(new Color(218, 204, 230));
				}

			}
		}; // container for the result color
		middleGUI.setOpaque(false); // panel for arranging results

		// Initialize the layouts of the tab and containers.
		upperGUI.setLayout(new BorderLayout());
		middleGUI.setLayout(new BorderLayout());
		desiredColorTab.setLayout(new BorderLayout());
		upperGUI.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		middleGUI.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		// Add the palette colors to the container panel.
		for (MunsellColor color : Palette.getInstance().getColors())
		{
			JPanel paletteCell = new JPanel();
			cell = new MunsellColorBlock(color, 20, 20, false, true, false, 0);
			paletteCell.setBorder(BorderFactory.createLineBorder(Color.BLACK));

			// Add the new color cell to layout with borders.
			paletteCell.add(cell);

			// Add the grid cell to the container.
			paletteGrid.add(paletteCell);
		}

		// Set the specified goal color.
		wantedColor.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		wantedColor.add(desired);

		// Set a mouse listener for the mixing button.
		mixingButton.addMouseListener(new MouseListener()
		{

			@Override
			public void mouseClicked(MouseEvent e)
			{
				MunsellColorBlock mixColorChip; // the mixing color chips for display
				MunsellColorBlock resultColorChip; // the result from mixing color chip
				JPanel weightCell; // mixing color display
				JPanel resultCell; // result color display

				// Clear the current grid display to refresh it.
				weightsGrid.removeAll();

				// Obtain the needed mixing weights for obtaining a desired color from the
				// palette.
				mixingWeights = MunsellColor.getMixingWeights(Palette.getInstance().getColors(), wanted);

				// Check if the size of the palette is equal to one.
				// If so, and that color matches the
				// desired color, display the needed mixture.
				if (Palette.getInstance().getColors().size() == 1
						&& Palette.getInstance().getColors().get(0).equals(wanted))
				{
					// Initialize the new values
					weightCell = new JPanel();
					resultCell = new JPanel();
					mixColorChip = new MunsellColorBlock(Palette.getInstance().getColors().get(0), 50, 50, false, true,
							true, 1.0);

					resultColorChip = new MunsellColorBlock(Palette.getInstance().getColors().get(0), 50, 50, false,
							true, true, 1.0);

					// Add the new color cell to layout with borders.
					weightCell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					resultCell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					mixColorChip.add(weightCell);
					resultCell.add(resultColorChip);

					// Add the cell to the grid layout, and add the results to the container tab.
					weightsGrid.add(mixColorChip);
					middleGUI.add(weightsGrid, BorderLayout.CENTER);
					middleGUI.add(resultCell, BorderLayout.SOUTH);
					desiredColorTab.add(middleGUI, BorderLayout.CENTER);

					// Refresh the tab.
					desiredColorTab.validate();
					desiredColorTab.repaint();
				}
				// If a mixture can not be obtained, print an error message.
				else if (mixingWeights == null)
				{
					JOptionPane.showMessageDialog(new JFrame(), ERROR_MESSAGE_CLOESST);
				}
				// Otherwise, initialize the display for the needed mixtures, weights, and the
				// resulting color.
				else
				{
					// Initialize the new values
					resultCell = new JPanel();
					resultColorChip = new MunsellColorBlock(
							MunsellColor.mix(Palette.getInstance().getColors(), mixingWeights), 50, 50, false, true,
							false, 0.0);

					// Built the layout for how to mix colors to obtain the desired color.
					for (int i = 0; i < Palette.getInstance().getColors().size(); i++)
					{
						weightCell = new JPanel();
						mixColorChip = new MunsellColorBlock(Palette.getInstance().getColors().get(i), 50, 50, false,
								true, true, mixingWeights.get(i));

						weightCell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						mixColorChip.add(weightCell);

						weightsGrid.add(mixColorChip);
					}

					// Add the new color cell to layout with borders.
					resultCell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					resultCell.add(resultColorChip);

					// Add the cell to the grid layout, and add the results to the container tab.
					middleGUI.add(weightsGrid, BorderLayout.CENTER);
					middleGUI.add(resultCell, BorderLayout.SOUTH);
					desiredColorTab.add(middleGUI, BorderLayout.CENTER);

					// Refresh the tab.
					desiredColorTab.validate();
					desiredColorTab.repaint();
				}
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
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

		// Add components to the various regions of the GUIs.
		// For the upperGUI layer, add some padding to arrange the palette display.
		JPanel padding = new JPanel();
		padding.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 140));

		upperGUI.add(paletteGrid, BorderLayout.CENTER);
		upperGUI.add(mixingButton, BorderLayout.EAST);
		upperGUI.add(padding, BorderLayout.WEST);
		middleGUI.add(wantedColor, BorderLayout.NORTH);

		// Add the completed GUI components to the main tab.
		desiredColorTab.add(upperGUI, BorderLayout.NORTH);
		desiredColorTab.add(middleGUI, BorderLayout.CENTER);

		// Refresh the tab.
		desiredColorTab.validate();
		desiredColorTab.repaint();
	}

	/**
	 * Actionlistener for mix button.
	 */
	private void updateMixedColor()
	{
		// Check if the array of colors is empty. If so, print an error message.
		// Otherwise mix the colors in the mixer.
		if (colorArr.isEmpty())
		{
			JOptionPane.showMessageDialog(new JFrame(), ERROR_MESSAGE_EMPTY);
		} else
		{
			result = new MunsellColorBlock(Palette.mixColor(colorArr, weightedArr));
		}

		results.removeAll();
		results.add(result);

		results.validate();
		results.repaint();

		// Refresh the tab.
		initializeMixTab();
	}
}
