package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import util.ColorConverter;

/**
 * 
 * @author Jedidiah Keplinger
 * @version 2, (10/28/2019)
 * 
 *          Description: Generating the main GUI for the Spectrum project.
 *          Initializes the main window for usage with other classes pertaining
 *          to Spectrum.
 */
public class MunsellWindow
{
	private static JTabbedPane tabs; // The tabbed pane.
	private static DetailsTab detailsTab; // The details tab.
	public final static String[] colleges = new String[] { "", "James Madison University" };
	public static String scheme;

	/**
	 * When invoked will begin the main GUI program.
	 */
	private static void display()
	{
		JFrame frame; // the main window
		tabs = new JTabbedPane(); // the window's tabs

		// Initialize the main window
		frame = new JFrame("Spectrum");

		// Changes JFrame Icon
		// https://www.google.com/url?sa=i&source=images&cd=&ved=2ahUKEwiqzLqjrPXlAhVDnuAKHdSsD-wQjRx6BAgBEAQ&url=https%3A%2F%2Fwww.w3schools.com%2Fcolors%2Fcolors_picker.asp&psig=AOvVaw0WuQfbAcShRkAWKTt5AvwK&ust=1574220691223723
		ImageIcon image = new ImageIcon("FrameIcon.png");
		frame.setIconImage(image.getImage());

		// Initialize the detailsTab.
		detailsTab = new DetailsTab();

		// Add the tabs.
		tabs.addTab("Arranged", new ArrangedColorsTab());
		tabs.addTab("Details", detailsTab);
		tabs.addTab("Image", new ImageTab());
		tabs.addTab("Palette", new PaletteTab());
		tabs.addTab("3D", new JFXTab());

		// Add tabs to the main window frame
		frame.add(tabs, BorderLayout.CENTER);

		// Start the GUI
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setPreferredSize(new Dimension(1150, 650));
		frame.setSize(1150, 650);
		frame.setResizable(false);
		frame.getContentPane().setBackground(Color.WHITE);
		Icon icon = null;
		scheme = (String) JOptionPane.showInputDialog(frame, "Choose theme", "Select Theme", JOptionPane.PLAIN_MESSAGE,
				icon, colleges, "");
		if (scheme == null)
		{
			System.exit(0);
		}
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	/**
	 * Sets the tab of the munsell window to the given index.
	 * 
	 * @param index the index to set the tabbed pane to.
	 */
	public static void setTab(int index)
	{
		tabs.setSelectedIndex(index);
	}

	/**
	 * @return the details tab.
	 */
	public static DetailsTab getDetailsTab()
	{
		return detailsTab;
	}

	/**
	 * Main is used to call other methods to start the GUI.
	 * 
	 * @param args is unused
	 */
	public static void main(String[] args)
	{
		// Build the CSV converter maps before we display the GUI.
		ColorConverter.buildCSVMaps();

		/**
		 * Prompt OS for GUI process. Create and show the GUI when the job runs.
		 */
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				display();
			}
		});
	}
}
