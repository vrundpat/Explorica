package GameEngine;

import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import Models.TexturedModel;
import RenderEngine.*;
import Models.RawModel;
import Textures.ModelTexture;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class MainLoop {
    public static void main(String[] args) {

        // Create the Display
        DisplayManager.createDisplay();


        // Instantiate a loader
        Loader loader = new Loader();

        RawModel model = OBJLoader.loadObjModel("dragon", loader);

        TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("blue")));
        ModelTexture texture = staticModel.getTexture();
        texture.setShineDamper(10);
        texture.setReflectivity(1);

        Entity entity = new Entity(staticModel, new Vector3f(0, -4, -20), 0, 0, 0, 1);
        Light light = new Light(new Vector3f(200, 200, 100), new Vector3f(1, 1, 1));
        Camera camera = new Camera();


        MainRenderer renderer = new MainRenderer();

        List<Entity> entities = new ArrayList<>();
        entities.add(entity);

        // Loop until the 'X' is clicked on the game window
        while(!Display.isCloseRequested()) {

            // This Main Game Loop has 3 vital tasks:
            //  1. Impose Game Logic
            //  2. Update Game Entities
            //  3. Render updated entities

            entity.increaseRotation(0, 1, 0);
            camera.move();

            for(Entity object : entities) {
                renderer.processEntity(object);
            }

            renderer.render(light, camera);

            // Step 2
            DisplayManager.updateDisplay();
        }

        renderer.cleanUp();
        loader.cleanUp();

        // When the game loop terminates, we want to do any clean up and close the window
        DisplayManager.closeDisplay();
    }
}
