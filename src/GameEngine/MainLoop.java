package GameEngine;

import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import Models.TexturedModel;
import RenderEngine.DisplayManager;
import RenderEngine.Loader;
import Models.RawModel;
import RenderEngine.OBJLoader;
import RenderEngine.Renderer;
import Shaders.StaticShader;
import Textures.ModelTexture;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

public class MainLoop {
    public static void main(String[] args) {

        // Create the Display
        DisplayManager.createDisplay();


        // Instantiate a loader and a renderer
        Loader loader = new Loader();
        StaticShader shader = new StaticShader();
        Renderer renderer = new Renderer(shader);

        RawModel model = OBJLoader.loadObjModel("dragon", loader);
        ModelTexture texture = new ModelTexture(loader.loadTexture("stallTexture"));
        TexturedModel staticModel = new TexturedModel(model, texture);
        Entity entity = new Entity(staticModel, new Vector3f(0, -5, -20), 0, 0, 0, 1);
        Light light = new Light(new Vector3f(-20, 10, -20), new Vector3f(1, 1, 1));
        Camera camera = new Camera();

        // Loop until the 'X' is clicked on the game window
        while(!Display.isCloseRequested()) {

            // This Main Game Loop has 3 vital tasks:
            //  1. Impose Game Logic
            //  2. Update Game Entities
            //  3. Render updated entities

            entity.increaseRotation(0, 1, 0);
            camera.move();

            renderer.prepare(); // Must be called on every frame; Resets/Clears the game window
            shader.start(); // Start the shader before rendering
            shader.loadLight(light); // Set the light's position and colour into the correct variables in the shader code
            shader.loadViewMatrix(camera); // Set the viewMatrix variable in the shader code to the updated view matrix
            renderer.render(entity, shader); // Render the hardcoded rectangle
            shader.stop(); // Stop the shader after rendering

            // Step 2
            DisplayManager.updateDisplay();
        }

        shader.cleanUp();
        loader.cleanUp();

        // When the game loop terminates, we want to do any clean up and close the window
        DisplayManager.closeDisplay();
    }
}
