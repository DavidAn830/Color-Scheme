package gui;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JPanel;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point3D;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import model.Hue;
import model.MunsellColor;
import util.ColorConverter;

/**
 * 
 * @author Jake Boychenko
 * @version 1, (11/18/2019)
 * 
 *          Description: Displays the 3D representation of the Munsell color
 *          space.
 * 
 *          All 3D-rotation related code was used and modified from
 *          https://stackoverflow.com/questions/46176489/javafx-3d-rotation-around-scene-fixed-axes
 */
public class JFXTab extends JPanel
{

	private static final int SPHERE_RADIUS = 15; // The radius of a sphere.

	/**
	 * All of these variables are for rotating, dragging, and zooming.
	 */
	double mouseStartPosX, mouseStartPosY;
	double mousePosX, mousePosY;
	double mouseOldX, mouseOldY;
	double mouseDeltaX, mouseDeltaY;

	/**
	 * All these variables are for camera configuration.
	 */
	private static double CAMERA_INITIAL_DISTANCE = -1450;
	private static double CAMERA_NEAR_CLIP = 80;
	private static double CAMERA_FAR_CLIP = 10000.0;
	private static double MOUSE_SPEED = 0.1;
	private static double ROTATION_SPEED = 2.0;

	XformBox cameraXform = new XformBox(); // Holder of the camera.

	PerspectiveCamera camera = new PerspectiveCamera(true); // The camera to use.

	/**
	 * Creates a new JFXTab.
	 */
	public JFXTab()
	{
		super();
		setLayout(new BorderLayout());

		// Create the panel to display the JavaFX.
		JFXPanel jfx = new JFXPanel();
		add(BorderLayout.CENTER, jfx);

		// Setup the JavaFX scene.
		Platform.runLater(() -> {

			// Initialize all the JavaFX stuff.
			Group root = new Group();
			XformBox spheres = new XformBox();
			spheres.setDepthTest(DepthTest.ENABLE);

			// Create the light.
			PointLight light = new PointLight();
			light.setColor(Color.WHITE);
			light.setTranslateX(0);
			light.setTranslateY(0);
			light.setTranslateZ(-1000);

			// Add the light to the scene.
			root.getChildren().add(light);

			// Add all the Munsell Color spheres.
			addSpheres(spheres);

			// Add the spheres to the scene.
			root.getChildren().add(spheres);

			// Create the scene.
			Scene scene = new Scene(root, 500, 500, true, SceneAntialiasing.BALANCED);
			scene.setFill(Color.BLACK); // Set the background color to black.

			// Interact with mouse presses.
			scene.setOnMousePressed(me -> {
				mouseStartPosX = me.getSceneX();
				mouseStartPosY = me.getSceneY();
				mousePosX = me.getSceneX();
				mousePosY = me.getSceneY();
				mouseOldX = me.getSceneX();
				mouseOldY = me.getSceneY();
			});

			// When mouse is dragged, either rotate the camera or pan it around.
			scene.setOnMouseDragged(me -> {
				mouseOldX = mousePosX;
				mouseOldY = mousePosY;
				mousePosX = me.getSceneX();
				mousePosY = me.getSceneY();
				mouseDeltaX = (mousePosX - mouseOldX);
				mouseDeltaY = (mousePosY - mouseOldY);

				if (me.isPrimaryButtonDown())
				{
					spheres.addRotation(-mouseDeltaX * MOUSE_SPEED * ROTATION_SPEED, Rotate.Y_AXIS);
					spheres.addRotation(mouseDeltaY * MOUSE_SPEED * ROTATION_SPEED, Rotate.X_AXIS);
				} else if (me.isSecondaryButtonDown())
				{
					spheres.setTranslateX(spheres.getTranslateX() + mouseDeltaX);
					spheres.setTranslateY(spheres.getTranslateY() + mouseDeltaY);
				}
			});

			// Zoom on scroll.
			scene.setOnScroll(e -> {
				camera.setTranslateZ(camera.getTranslateZ() + e.getDeltaY());
			});

			// Initialize the scene's camera.
			root.getChildren().add(camera);
			cameraXform.getChildren().add(camera);
			camera.setNearClip(CAMERA_NEAR_CLIP);
			camera.setFarClip(CAMERA_FAR_CLIP);
			camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
			camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
			camera.setTranslateY(180);
			cameraXform.addRotation(-10, Rotate.Z_AXIS);
			camera.setDepthTest(DepthTest.ENABLE);

			// Set the camera.
			scene.setCamera(camera);

			// Set the scene.
			jfx.setScene(scene);
		});
	}

	/**
	 * Add the spheres to the given group.
	 * 
	 * @param root the root to add the spheres to.
	 */
	private void addSpheres(Group root)
	{
		// Get all the hues.
		ArrayList<Hue> hues = ColorConverter.getHues();

		// Go through each hue and create a 2D slice. Each slice is rotated about the
		// Y-axis evenly.
		int counter = 0;
		for (Hue hue : ColorConverter.getHues())
		{
			addColorSlice(hue, Math.PI * 2 / hues.size() * counter++, root);
		}
	}

	/**
	 * Add the given hue to the root group with the given rotation.
	 * 
	 * @param hue      the hue to get the color slice from.
	 * @param rotation the rotation about the y-axis to rotate around.
	 * @param root     the group to add the color slice to.
	 */
	private void addColorSlice(Hue hue, double rotation, Group root)
	{
		// Get the color slice from the given hue.
		ArrayList<ArrayList<MunsellColor>> colorSlice = ColorConverter.getColorsMatrix(hue);

		// Go through each color in the hue and add a color sphere to the root.
		for (int i = 0; i < colorSlice.size(); i++)
		{
			ArrayList<MunsellColor> row = colorSlice.get(i);
			for (int j = 0; j < row.size(); j++)
			{
				root.getChildren().add(createColorSphere(row.get(j), j * 45, i * 45, rotation));
			}
		}
	}

	/**
	 * Creates a color sphere from the given color at the given x and y coordinate
	 * with the given rotation around the y-axis.
	 * 
	 * @param color    the color to model.
	 * @param x        the x coordinate to go to.
	 * @param y        the y coordinate to go to.
	 * @param rotation the rotation around the y-axis to rotate around.
	 * @return the sphere.
	 */
	private Sphere createColorSphere(MunsellColor color, double x, double y, double rotation)
	{
		java.awt.Color awtColor = color.toColor(); // Get the RGB color.

		// Create the material for the sphere.
		final PhongMaterial material = new PhongMaterial(
				new Color(awtColor.getRed() / 255.0, awtColor.getGreen() / 255.0, awtColor.getBlue() / 255.0, 1));

		// Create the sphere.
		Sphere sphere = new Sphere(SPHERE_RADIUS);
		sphere.setTranslateX(Math.cos(rotation) * x + Math.signum(Math.cos(rotation)));
		sphere.setTranslateY(y + SPHERE_RADIUS);
		sphere.setTranslateZ(Math.sin(rotation) * x + Math.signum(Math.sin(rotation)));
		sphere.setMaterial(material);

		// When the sphere is double-clicked, show the details of the color in the
		// details tab.
		sphere.setOnMouseClicked(e -> {
			if (e.getClickCount() == 2)
			{
				MunsellWindow.setTab(1);
				MunsellWindow.getDetailsTab().showDetailsOfColor(color);
			}
		});

		return sphere;
	}

	/**
	 * 
	 * @author Stack Overflow
	 * @version 1, (11/18/2019)
	 * 
	 *          Description: A helper container that has some rotational helper
	 *          methods. I am not the author, it was adapted from
	 *          https://stackoverflow.com/questions/46176489/javafx-3d-rotation-around-scene-fixed-axes
	 */
	class XformBox extends Group
	{

		/**
		 * Creates an Xformbox.
		 */
		XformBox()
		{
			super();
			getTransforms().add(new Affine());
		}

		/**
		 * Accumulate rotation about specified axis
		 *
		 * @param angle the angle to rotate.
		 * @param axis  the axis to rotate around.
		 */
		public void addRotation(double angle, Point3D axis)
		{
			Rotate r = new Rotate(angle, axis);
			/**
			 * This is the important bit and thanks to bronkowitz in this post
			 * https://stackoverflow.com/questions/31382634/javafx-3d-rotations for getting
			 * me to the solution that the rotations need accumulated in this way
			 */
			getTransforms().set(0, r.createConcatenation(getTransforms().get(0)));
		}

		/**
		 * Reset transform to identity transform
		 */
		public void reset()
		{
			getTransforms().set(0, new Affine());
		}
	}

}
