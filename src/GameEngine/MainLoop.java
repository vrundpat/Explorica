package GameEngine;

import RenderEngine.DisplayManager;
import RenderEngine.Loader;
import RenderEngine.RawModel;
import RenderEngine.Renderer;
import Shaders.StaticShader;
import org.lwjgl.opengl.Display;

public class MainLoop {
    public static void main(String[] args) {

        // Create the Display
        DisplayManager.createDisplay();


        // Instantiate a loader and a renderer
        Loader loader = new Loader();
        Renderer renderer = new Renderer();
        StaticShader shader = new StaticShader();

        // Simple Rectangle
        float[] vertices = {
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0,
                0.5f, 0.5f, 0
        };

        // Indices to use to draw the rectangle
        int[] indices = {
                0, 1, 3,
                3, 1, 2
        };

        RawModel model = loader.loadToVAO(vertices, indices);

        // Loop until the 'X' is clicked on the game window
        while(!Display.isCloseRequested()) {

            // This Main Game Loop has 3 vital tasks:
            //  1. Impose Game Logic
            //  2. Update Game Entities
            //  3. Render updated entities

            shader.start(); // Start the shader before rendering
            renderer.prepare(); // Must be called on every frame; Resets/Clears the game window
            renderer.render(model); // Render the hardcoded rectangle
            shader.start(); // Stop the shader after rendering

            // Step 2
            DisplayManager.updateDisplay();
        }

        shader.cleanUp();
        loader.cleanUp();

        // When the game loop terminates, we want to do any clean up and close the window
        DisplayManager.closeDisplay();
    }
}
