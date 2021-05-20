package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import model.Hue;
import model.MunsellColor;

/**
 * @author David An
 * @version 1, (11/05/2019)
 * 
 *          Description: Tab for displaying MunsellColor that corresponds with
 *          user's input.
 */
public class DetailsTab extends JPanel
{

	/**
	 * The default serial ID for this GUI tab.
	 */
	private static final long serialVersionUID = 1L;

	// The error message to print if values are entered incorrectly.
	private final String errorMessage = "Wrong Inputs!\n\n" + "Hue Number must be less than 10.\n"
			+ "Values must be lower than 10.\n" + "Chroma must be lower than 40.\n";
	private final String rgbErrorMessage = "Wrong Inputs!\n\nAll RGB values need to be between 0 and 255.\n";

	private JPanel textPanel; // The main text fields panel
	private JPanel colorPanel; // The main color panel for the color block
	private JPanel rgbToMun; // The main panel that converts color from rgb to munsell
	private JPanel rgbToMunLogoPanel; // Panel that prints school logos
	private JPanel complPanel; // The main panel for the complementary color
	private JPanel analogousPanel; // The main panel for the analogous colors
	private JPanel analogousGrid; // the grid for displaying the analogous colors
	private JPanel analogousMainPanel; // main logo panel
	private JButton enterButton; // Used when the 'enter' key is pressed
	private JButton clearButton; // Used when the 'clear'key is pressed
	private Hue hue; // The entered Hue
	private float value; // The entered Value
	private float chroma; // the entered Chroma
	private MunsellColorBlock cell; // A color cell for displaying an entered color
	private MunsellColorBlock cellDup; // A duplicate cell for the entered color
	private MunsellColorBlock cellDup2; // An additional duplicate cell for the entered color
	private MunsellColorBlock compCell; // A color cell for displaying the complementary color
	private MunsellColor mc; // For setting the entered Munsell color information
	private JTabbedPane tabs; // additional tabs within this tab
	private JPanel detailsTab; // the details tab
	private JPanel complemTab; // the complementary color tab
	private JPanel analogousTab; // the analogous colors tab
	private JPanel rgbToMunTab; // tab that converts rgb values to munsell color

	// Below are labels for various text fields
	private JLabel huePrefixLabel;
	private JLabel hueNumLabel;
	private JLabel valueLabel;
	private JLabel chromaLabel;
	private JTextField huePrefixText;
	private JTextField hueNumText;
	private JTextField valueText;
	private JTextField chromaText;

	// Below are labels and buttons for converting rgb to munsell
	private JLabel red;
	private JLabel green;
	private JLabel blue;
	private JTextField redText;
	private JTextField greenText;
	private JTextField blueText;
	private JButton convertButton;

	private final ImageIcon jmulogo = new ImageIcon("JMUicon.png");

	/**
	 * Create the details tab, and add respective text fields.
	 */
	public DetailsTab()
	{
		// Initialize the tabs and their layouts.
		tabs = new JTabbedPane();
		complemTab = new JPanel();
		complemTab.setLayout(new BorderLayout());
		analogousTab = new JPanel();
		analogousTab.setLayout(new BorderLayout());
		analogousGrid = new JPanel();
		analogousPanel = new JPanel();
		analogousPanel.setLayout(new GridBagLayout());
		detailsTab = new JPanel();
		detailsTab.setLayout(new BorderLayout());
		rgbToMunTab = new JPanel();
		rgbToMunTab.setLayout(new BorderLayout());

		// Set the primary tab's default color scheme and attributes.
		setLayout(new BorderLayout());
		// setBackground(Color.LIGHT_GRAY);

		setVariables();
		setRGBtoMunVar();
		addToPanel();
		addToRGBPanel();
		createMunsellColor();

		// Add the tabs to their various containers.
		detailsTab.add(textPanel, BorderLayout.NORTH);
		detailsTab.add(colorPanel, BorderLayout.CENTER);
		complemTab.add(complPanel, BorderLayout.CENTER);
		analogousTab.add(analogousMainPanel, BorderLayout.CENTER);
		rgbToMunTab.add(rgbToMun, BorderLayout.NORTH);
		rgbToMunTab.add(rgbToMunLogoPanel, BorderLayout.CENTER);

		// Add the container tabs to the main tab.
		tabs.add("Color Details", detailsTab);
		tabs.add("Complementary Color", complemTab);
		tabs.add("Analogous Colors", analogousTab);
		tabs.add("RGB to Munsell", rgbToMunTab);
		add(tabs);
	}

	/**
	 * Initializes labels, text fields, and button.
	 */
	private void setVariables()
	{
		huePrefixLabel = new JLabel("Hue Prefix");
		hueNumLabel = new JLabel("Hue Number");
		valueLabel = new JLabel("Value");
		chromaLabel = new JLabel("Chroma");

		huePrefixText = new JTextField(5);
		huePrefixText.addActionListener(e -> setMunsellColor());

		hueNumText = new JTextField(5);
		hueNumText.addActionListener(e -> setMunsellColor());

		valueText = new JTextField(5);
		valueText.addActionListener(e -> setMunsellColor());

		chromaText = new JTextField(5);
		chromaText.addActionListener(e -> setMunsellColor());

		enterButton = new JButton("Get Color");
		clearButton = new JButton("Clear");
	}

	/**
	 * Initializing variables for rgbToMun tab.
	 */
	private void setRGBtoMunVar()
	{
		rgbToMun = new JPanel();

		red = new JLabel("Red");
		redText = new JTextField(5);
		redText.addActionListener(e -> convertRGB());

		green = new JLabel("Green");
		greenText = new JTextField(5);
		greenText.addActionListener(e -> convertRGB());

		blue = new JLabel("Blue");
		blueText = new JTextField(5);
		blueText.addActionListener(e -> convertRGB());

		convertButton = new JButton("Convert");
	}

	/**
	 * Fills in the details of the color in the textfields and shows the details of
	 * the color.
	 * 
	 * @param color the color to show details of.
	 */
	public void showDetailsOfColor(MunsellColor color)
	{
		huePrefixText.setText(color.getHue().getHuePrefix());
		hueNumText.setText("" + color.getHue().getHue());
		valueText.setText("" + color.getValue());
		chromaText.setText("" + color.getChroma());

		// Path back to the first tab when a color is double clicked.
		tabs.setSelectedIndex(0);
		setMunsellColor();
	}

	/**
	 * Adds components (labels) to the panel.
	 */
	private void addToPanel()
	{

		textPanel = new JPanel();

		// Add the JMU logo to the background.
		colorPanel = new JPanel()
		{

			/**
			 * The default serial ID for this panel.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g)
			{
				if (MunsellWindow.scheme.equals("James Madison University"))
				{
					g.drawImage(jmulogo.getImage(), 63, -150, 1000, 800, null);
					super.paintComponent(g);
					enterButton.setBackground(new Color(218, 204, 230));
					clearButton.setBackground(new Color(244, 239, 225));
				}

			}
		};

		colorPanel.setOpaque(false);
		rgbToMun = new JPanel();
		complPanel = new JPanel()
		{

			/**
			 * The default serial ID for this panel.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g)
			{
				if (MunsellWindow.scheme.equals("James Madison University"))
				{
					g.drawImage(jmulogo.getImage(), 63, -114, 1000, 800, null);
					super.paintComponent(g);
				}

			}
		};
		complPanel.setOpaque(false);

		analogousMainPanel = new JPanel()
		{

			/**
			 * The default serial ID for this panel.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g)
			{
				if (MunsellWindow.scheme.equals("James Madison University"))
				{
					g.drawImage(jmulogo.getImage(), 63, -114, 1000, 800, null);
					super.paintComponent(g);
				}

			}
		};

		analogousMainPanel.setOpaque(false);
		analogousMainPanel.setLayout(new BorderLayout());

		textPanel.add(huePrefixLabel);
		textPanel.add(huePrefixText);
		textPanel.add(hueNumLabel);
		textPanel.add(hueNumText);
		textPanel.add(valueLabel);
		textPanel.add(valueText);
		textPanel.add(chromaLabel);
		textPanel.add(chromaText);
		textPanel.add(enterButton);
		textPanel.add(clearButton);
	}

	/**
	 * Add rgb conversion tab variables to the panel.
	 */
	private void addToRGBPanel()
	{
		rgbToMunLogoPanel = new JPanel()
		{
			/**
			 * The default serial ID for this panel.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g)
			{
				if (MunsellWindow.scheme.equals("James Madison University"))
				{
					g.drawImage(jmulogo.getImage(), 63, -150, 1000, 800, null);
					super.paintComponent(g);
					convertButton.setBackground(new Color(218, 204, 230));
				}

			}
		};

		rgbToMunLogoPanel.setOpaque(false);

		rgbToMun.add(red);
		rgbToMun.add(redText);
		rgbToMun.add(green);
		rgbToMun.add(greenText);
		rgbToMun.add(blue);
		rgbToMun.add(blueText);
		rgbToMun.add(convertButton);
	}

	/**
	 * Action listener for the 'enter' key.
	 */
	private void createMunsellColor()
	{

		enterButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setMunsellColor();
			}
		});

		clearButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				colorPanel.removeAll();
				complPanel.removeAll();
				analogousMainPanel.removeAll();

				colorPanel.validate();
				colorPanel.repaint();
				complPanel.validate();
				complPanel.repaint();
				analogousMainPanel.validate();
				analogousMainPanel.repaint();

				huePrefixText.setText("");
				hueNumText.setText("");
				valueText.setText("");
				chromaText.setText("");
			}
		});

		convertButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				convertRGB();
			}
		});

		/**
		 * Add a listener to the 'enter' key on the keyboard.
		 */
		enterButton.addKeyListener(new KeyListener()
		{

			/**
			 * When the enter key is entered, attempt to set the color based on the fields
			 * entered.
			 */
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					setMunsellColor();
				}
			}

			/**
			 * This method is unused.
			 */
			@Override
			public void keyTyped(KeyEvent e)
			{
			}

			/**
			 * This method is unused.
			 */
			@Override
			public void keyReleased(KeyEvent e)
			{
			}
		});

		/**
		 * Add a listener to the 'enter' key on the keyboard.
		 */
		convertButton.addKeyListener(new KeyListener()
		{

			/**
			 * When the enter key is entered, attempt to set the color based on the fields
			 * entered.
			 */
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					convertRGB();
				}
			}

			/**
			 * This method is unused.
			 */
			@Override
			public void keyTyped(KeyEvent e)
			{
			}

			/**
			 * This method is unused.
			 */
			@Override
			public void keyReleased(KeyEvent e)
			{
			}
		});

	}

	/**
	 * Sets the munsell color of the block.
	 */
	private void setMunsellColor()
	{
		hue = new Hue(huePrefixText.getText().toUpperCase(), Float.parseFloat(hueNumText.getText()));
		value = Float.parseFloat(valueText.getText());
		chroma = Float.parseFloat(chromaText.getText());
		mc = new MunsellColor(hue, value, chroma);

		if (mc.isValidMunsellColor())
		{
			cell = new MunsellColorBlock(mc, 250, 250, false, false, false, 0);
			JPanel t = new JPanel();
			t.add(cell);

			colorPanel.removeAll();
			colorPanel.add(t);

			colorPanel.validate();
			colorPanel.repaint();

			complementaryTab(mc);
			analogousTab(mc);
		} else
		{
			colorPanel.removeAll();
			colorPanel.validate();
			colorPanel.repaint();
			JOptionPane.showMessageDialog(new JFrame(), errorMessage);
		}
	}

	private void convertRGB()
	{
		if (isValidRGB(redText.getText(), greenText.getText(), blueText.getText()))
		{
			// making rgb color based on the inputs
			Color nc = new Color(Integer.parseInt(redText.getText()), Integer.parseInt(greenText.getText()),
					Integer.parseInt(blueText.getText()));
			// creating munsellColor based on 'nc'
			MunsellColor mc = MunsellColor.fromRGB(nc);

			huePrefixText.setText(mc.getHue().getHuePrefix());
			hueNumText.setText("" + mc.getHue().getHue());
			valueText.setText("" + mc.getValue());
			chromaText.setText("" + mc.getChroma());

			// Path back to the first tab when a color is double clicked.
			tabs.setSelectedIndex(0);
			setMunsellColor();
		} else
		{
			JOptionPane.showMessageDialog(new JFrame(), rgbErrorMessage);
		}
	}

	/**
	 * Initializes a tab for displaying complementary color to the color found in
	 * the details tab.
	 * 
	 * @param color the specified color.
	 */
	private void complementaryTab(MunsellColor color)
	{
		// Temporary panel for adding a new color.
		JPanel dupCell = new JPanel();
		dupCell.setOpaque(false);
		JPanel complColor = new JPanel();
		complColor.setOpaque(false);
		if (color == null)
		{
			System.err.println("Error: null paramater passed to complementaryTab.");
			return;
		}

		// Clear the current tab.
		complPanel.removeAll();

		// Set and add components to the tab.
		complPanel.setLayout(new BorderLayout());
		cellDup = new MunsellColorBlock(color, 150, 150, true, true, false, 0);
		compCell = new MunsellColorBlock(MunsellColor.getComplimentaryColor(color), 150, 150, true, true, false, 0);

		// Add components.
		dupCell.add(cellDup);
		complColor.add(compCell);
		complPanel.add(dupCell, BorderLayout.NORTH);
		complPanel.add(complColor, BorderLayout.CENTER);

		// Refresh the tab.
		complPanel.validate();
		complPanel.repaint();
	}

	/**
	 * Initializes a tab for displaying the analogous colors to the color found in
	 * the details tab.
	 * 
	 * @param color the specified color.
	 */
	private void analogousTab(MunsellColor color)
	{
		JPanel colorCell = new JPanel(); // Temporary panel for selected color
		colorCell.setOpaque(false);
		analogousGrid.setOpaque(false);
		analogousPanel.setOpaque(false);
		MunsellColorBlock analogousCell; // A color cell for analogous colors
		HashMap<Integer, ArrayList<MunsellColor>> colorMap; // the map of analogous colors
		ArrayList<MunsellColor> colorList; // list of colors from the map
		int layoutPadding; // arranging the colors

		if (color == null)
		{
			System.err.println("Error: null paramater passed to analogousTab.");
			return;
		}

		// Obtain the list of analogous colors.
		colorMap = MunsellColor.getAnalogousColors(color);

		// Clear the tab.
		analogousMainPanel.removeAll();
		analogousPanel.removeAll();
		analogousGrid.removeAll();

		// Set the grid layout to function off of the x-axis.
		analogousGrid.setLayout(new BoxLayout(analogousGrid, BoxLayout.Y_AXIS));
		analogousGrid.setAlignmentY(0);
		analogousGrid.setOpaque(false);

		/*
		 * Build the analogous color display. Retrieve the hashmap of analogous colors
		 * based on the specified color, and add to a boxlayout for display on this tab.
		 */
		layoutPadding = 75;
		for (int i = 5; i > 0; i--)
		{

			// Reset the integer variable.
			layoutPadding = (int) (250 * Math.sqrt(i));

			JPanel layoutX = new JPanel(); // a new panel to add to the main panel
			layoutX.setOpaque(false);

			// Grab the list of colors at the index of i.
			layoutX.removeAll();
			colorList = colorMap.get(i);

			// Initialize the layout of the panel.
			layoutX.setLayout(new BoxLayout(layoutX, BoxLayout.X_AXIS));
			layoutX.setAlignmentX(Component.CENTER_ALIGNMENT);

			// Iterate through the pairs of colors and place the color chips into the panel
			// for display.
			if (colorList.size() > 1)
			{
				analogousCell = new MunsellColorBlock(colorList.get(0), 77, 75, true, true, false, 0.0);
				layoutX.add(analogousCell);

				// Add padding.
				layoutX.add(Box.createRigidArea(new Dimension(layoutPadding, 0)));

				analogousCell = new MunsellColorBlock(colorList.get(1), 77, 75, true, true, false, 0.0);
				layoutX.add(analogousCell);
			} else
			{
				analogousCell = new MunsellColorBlock(colorList.get(0), 77, 75, true, true, false, 0.0);

				if (colorList.get(0).getHue().getHueTotalValue() < color.getHue().getHueTotalValue())
				{
					layoutX.add(analogousCell);
					layoutPadding += 77;
					layoutX.add(Box.createRigidArea(new Dimension(layoutPadding, 0)));
				} else
				{
					layoutPadding += 77;
					layoutX.add(Box.createRigidArea(new Dimension(layoutPadding, 0)));
					layoutX.add(analogousCell);
				}
			}

			// Add the grid to the layout grid container.
			analogousGrid.add(Box.createRigidArea(new Dimension(0, 5)));
			analogousGrid.add(layoutX);
			analogousMainPanel.repaint();

		}

		// Create a duplicate of the specified color.
		cellDup2 = new MunsellColorBlock(color, 105, 105, true, true, false, 0);
		colorCell.add(cellDup2);

		// Add components to the tab.
		analogousMainPanel.add(colorCell, BorderLayout.SOUTH);
		analogousPanel.add(analogousGrid);
		analogousMainPanel.add(analogousPanel, BorderLayout.CENTER);

		// Refresh the tab.
		analogousPanel.validate();
		analogousPanel.repaint();
		analogousGrid.validate();
		analogousGrid.repaint();
		analogousMainPanel.validate();
		analogousMainPanel.repaint();
		analogousTab.validate();
		analogousTab.repaint();
	}

	/**
	 * Checks whether given rgb values are valid
	 *
	 * @param r red
	 * @param g green
	 * @param b blue
	 * @return true if valid
	 */
	private boolean isValidRGB(String r, String g, String b)
	{

		try
		{
			if (r == null || g == null || b == null)
			{
				return false;
			}
			if (Integer.parseInt(r) < 0 || Integer.parseInt(r) > 255)
			{
				return false;
			} else if (Integer.parseInt(b) < 0 || Integer.parseInt(b) > 255)
			{
				return false;
			} else if (Integer.parseInt(g) < 0 || Integer.parseInt(g) > 255)
			{
				return false;
			}
		} catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(new JFrame(), "RGB values must be numbers!\n");
			System.err.println("RGB inputs include some characters");
		}

		return true;
	}
}
