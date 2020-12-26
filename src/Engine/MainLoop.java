package Engine;

import RenderEngine.DisplayManager;
import RenderEngine.Loader;
import RenderEngine.RawModel;
import RenderEngine.Renderer;
import org.lwjgl.opengl.Display;

public class MainLoop {
    public static void main(String[] args) {

        // Create the Display
        DisplayManager.createDisplay();


        // Instantiate a loader and a renderer
        Loader loader = new Loader();
        Renderer renderer = new Renderer();

        // Simple Rectangle
        float[] vertices = {
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0,

                0.5f, -0.5f, 0,
                0.5f, 0.5f, 0,
                -0.5f, 0.5f, 0,
        };

        RawModel model = loader.loadToVAO(vertices);

        // Loop until the 'X' is clicked on the game window
        while(!Display.isCloseRequested()) {

            // This Main Game Loop has 3 vital tasks:
            //  1. Impose Game Logic
            //  2. Update Game Entities
            //  3. Render updated entities

            renderer.prepare(); // Must be called on every frame; Resets/Clears the game window
            renderer.render(model); // Render the hardcoded rectangle

            // Step 2
            DisplayManager.updateDisplay();
        }

        loader.cleanUp();

        // When the game loop terminates, we want to do any clean up and close the window
        DisplayManager.closeDisplay();
    }
}
