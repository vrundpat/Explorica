package RenderEngine;

import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import Models.TexturedModel;
import Shaders.StaticShader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainRenderer {

    private StaticShader shader = new StaticShader();
    private Renderer renderer = new Renderer(shader);

    private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();

    public void render(Light light, Camera camera) {
        renderer.prepare(); // Prepare renderer (clear window & add depth testing)
        shader.start(); // Start shader program
        shader.loadLight(light); // Load light variables into the shader code
        shader.loadViewMatrix(camera); // Load view matrix based on the position of the camera

        renderer.render(entities); // Render all entities

        shader.stop(); // Stop the shader program
        entities.clear(); // Clear the list of all entities
    }

    /* This method will add a given entity to its corresponding list if it exists or create a new list if it doesn't */
    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);

        if(batch != null) {
            batch.add(entity);
        }
        else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    // Clean up the shader program
    public void cleanUp() {
        shader.cleanUp();
    }
}
