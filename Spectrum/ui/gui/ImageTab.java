package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.MunsellColor;
import model.Palette;

/**
 * Tab for displaying an image, choosing a pixel, and seeing the MunsellColor
 * equivalent.
 * 
 * @author Jake Boychenko
 * @version 1, (10/28/2019)
 * 
 *          Description: Generates a tab that allows for a picture to be
 *          uploaded into the GUI. Clicking on a pixel in the photo will display
 *          the closest matching Munsell color for that given pixel.
 */
public class ImageTab extends JPanel
{

	private static final int IMAGE_MAX_WIDTH = 600; // The maximum width of the image.
	private static final int IMAGE_MAX_HEIGHT = 400; // The maximum height of the image.
	private static final String ERROR_MESSAGE_EMPTY = "Error:\nYour palette is empty.\n"
			+ "Please add colors to your palette.";

	/**
	 * The serial ID for this GUI tab.
	 */
	private static final long serialVersionUID = 1L;

	JLabel imageLabel; // The labels that displays the image.
	JPanel colorPanel; // The panel that displays the MunsellColorBlock.
	BufferedImage image; // The current image being shown. Null if none.

	/**
	 * When the tab is initialized, run the creation for the Image tab.
	 */
	public ImageTab()
	{
		setLayout(new BorderLayout());

		// Initialize the button that will posterize the image.
		JButton posterizeButton = new JButton("Posterize");
		posterizeButton.setVisible(false);
		posterizeButton.addActionListener(e -> {
			BufferedImage posterized = new BufferedImage(image.getWidth(), image.getHeight(),
					BufferedImage.TYPE_INT_RGB);

			// Check if the palette is empty before proceeding.
			// If so, report an error message and return. Otherwise proceed.
			if (Palette.getInstance().getColors().isEmpty())
			{
				JOptionPane.showMessageDialog(new JFrame(), ERROR_MESSAGE_EMPTY);
				return;
			}

			for (int i = 0; i < image.getWidth(); i++)
			{
				for (int j = 0; j < image.getHeight(); j++)
				{
					posterized.setRGB(i, j, Palette.getInstance()
							.getClosestColor(MunsellColor.fromRGB(new Color(image.getRGB(i, j)))).toColor().getRGB());
				}
			}

			image = posterized;
			imageLabel.setIcon(new ImageIcon(image));
			repaint();
		});

		// Initialize the upload button.
		JButton uploadButton = new JButton("Upload Image...");
		uploadButton.addActionListener(e -> {

			// Let the user choose an image.
			BufferedImage newImage = getImage();

			// Leave if the user did not choose an image.
			if (newImage == null)
				return;

			// Set the global variable.
			image = newImage;

			// Show the new image in the image panel.
			imageLabel.setIcon(new ImageIcon(image));
			posterizeButton.setVisible(true);

			imageLabel.addMouseListener(new MouseListener()
			{

				@Override
				public void mouseClicked(MouseEvent e)
				{

					// Get the color of the clicked pixel.
					Color pixelColor = new Color(image.getRGB(e.getX(), e.getY()));

					// Add the MunsellColorBlock to the panel.
					colorPanel.removeAll();
					colorPanel.add(new MunsellColorBlock(MunsellColor.fromRGB(pixelColor)));
					validate();
					repaint();
				}

				@Override
				public void mouseEntered(MouseEvent e)
				{
				}

				@Override
				public void mouseExited(MouseEvent e)
				{
				}

				@Override
				public void mousePressed(MouseEvent e)
				{
				}

				@Override
				public void mouseReleased(MouseEvent e)
				{
				}
			});
		});

		// Initialize the panel that shows the image.
		imageLabel = new JLabel((ImageIcon) null);
		imageLabel.setBackground(Color.black);

		ImageIcon jmulogo = new ImageIcon("JMUicon.png");
		JPanel imagePanel = new JPanel()
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
					g.drawImage(jmulogo.getImage(), 65, -125, 1000, 800, null);
					super.paintComponent(g);
				}

			}
		};

		imagePanel.setOpaque(false);
		imagePanel.add(imageLabel);

		// Initialize the panel that displays the color block.

		colorPanel = new JPanel();
		colorPanel.setMinimumSize(new Dimension(30, 280));

		JPanel top = new JPanel(new FlowLayout());
		top.add(uploadButton);
		top.add(posterizeButton);

		// Add the components.
		add(top, BorderLayout.NORTH);
		add(imagePanel, BorderLayout.CENTER);
		add(colorPanel, BorderLayout.SOUTH);
	}

	/**
	 * Opens the Swing File Chooser, allows the user to choose an image file, and
	 * returns the Swing Image of it.
	 * 
	 * @return the Image chosen, or null if the user cancelled or if an exception
	 *         happened.
	 */
	private BufferedImage getImage()
	{
		// Allow the user to choose a file.
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(this);

		// If they chose a file successfully...
		if (result == JFileChooser.APPROVE_OPTION)
		{

			// Get the file and convert it to an image.
			File file = fileChooser.getSelectedFile();
			try
			{
				BufferedImage imageUpload = ImageIO.read(file);

				// Remove the alpha channel from the image.
				BufferedImage copy = new BufferedImage(Math.min(imageUpload.getWidth(), IMAGE_MAX_WIDTH),
						Math.min(imageUpload.getHeight(), IMAGE_MAX_HEIGHT), BufferedImage.TYPE_INT_RGB);

				Graphics2D g2d = copy.createGraphics();
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, copy.getWidth(), copy.getHeight());
				g2d.drawImage(imageUpload, 0, 0, Math.min(imageUpload.getWidth(), IMAGE_MAX_WIDTH),
						Math.min(imageUpload.getHeight(), IMAGE_MAX_HEIGHT), null);
				g2d.dispose();

				return copy;
			} catch (IOException e)
			{
				// If there was an exception (not a valid image file), return null.
				return null;
			}
		}

		// Return null if anything failed.
		return null;
	}
}
