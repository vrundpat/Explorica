package GameEngine;

import Entities.Camera;
import Entities.Entity;
import Models.TexturedModel;
import RenderEngine.DisplayManager;
import RenderEngine.Loader;
import Models.RawModel;
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

        float[] vertices = {
                -0.5f,0.5f,0,
                -0.5f,-0.5f,0,
                0.5f,-0.5f,0,
                0.5f,0.5f,0,

                -0.5f,0.5f,1,
                -0.5f,-0.5f,1,
                0.5f,-0.5f,1,
                0.5f,0.5f,1,

                0.5f,0.5f,0,
                0.5f,-0.5f,0,
                0.5f,-0.5f,1,
                0.5f,0.5f,1,

                -0.5f,0.5f,0,
                -0.5f,-0.5f,0,
                -0.5f,-0.5f,1,
                -0.5f,0.5f,1,

                -0.5f,0.5f,1,
                -0.5f,0.5f,0,
                0.5f,0.5f,0,
                0.5f,0.5f,1,

                -0.5f,-0.5f,1,
                -0.5f,-0.5f,0,
                0.5f,-0.5f,0,
                0.5f,-0.5f,1

        };

        float[] textureCoords = {

                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0


        };

        int[] indices = {
                0,1,3,
                3,1,2,
                4,5,7,
                7,5,6,
                8,9,11,
                11,9,10,
                12,13,15,
                15,13,14,
                16,17,19,
                19,17,18,
                20,21,23,
                23,21,22

        };


        RawModel model = loader.loadToVAO(vertices, textureCoords, indices);
        ModelTexture texture = new ModelTexture(loader.loadTexture("image"));
        TexturedModel staticModel = new TexturedModel(model, texture);
        Entity entity = new Entity(staticModel, new Vector3f(0, 0, -5), 0, 0, 0, 1);
        Camera camera = new Camera();

        // Loop until the 'X' is clicked on the game window
        while(!Display.isCloseRequested()) {

            // This Main Game Loop has 3 vital tasks:
            //  1. Impose Game Logic
            //  2. Update Game Entities
            //  3. Render updated entities

            entity.increaseRotation(1, 1, 0);
            camera.move();

            renderer.prepare(); // Must be called on every frame; Resets/Clears the game window
            shader.start(); // Start the shader before rendering
            shader.loadViewMatrix(camera);
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
