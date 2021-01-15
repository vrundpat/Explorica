package RenderEngine;

import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import Models.TexturedModel;
import Shaders.StaticShader;
import Shaders.TerrainShader;
import Terrains.Terrain;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainRenderer {

    // For the projection matrix
    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private final Vector3f SKY_COLOUR = new Vector3f(0.5f, 0.5f, 0.5f);

    private Matrix4f projectionMatrix;

    private StaticShader shader = new StaticShader();
    private EntityRenderer renderer;

    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();

    private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
    private List<Terrain> terrains = new ArrayList<>();

    public MainRenderer() {
        enableCulling(); // Enable culling
        createProjectionMatrix();
        renderer = new EntityRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
    }

    // Prepare for rendering
    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Allow OpenGL to monitor overlapping vertices and render accordingly
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // Clear the screen from the previous frame & allow depth buffer testing
        GL11.glClearColor(SKY_COLOUR.x, SKY_COLOUR.y, SKY_COLOUR.z, 1); // Fill the window with a simple red color
    }

    public static void enableCulling() {
        // Enabling cull facing on the Back Face prevents rendering of faces on a model that are not in view
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling() {
        // Disable culling
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    public void render(Light light, Camera camera) {
        prepare(); // Prepare renderer (clear window & add depth testing)
        shader.start(); // Start shader program

        // Load in the fog
        shader.loadSkyColour(SKY_COLOUR.x, SKY_COLOUR.y, SKY_COLOUR.z);

        shader.loadLight(light); // Load light variables into the shader code
        shader.loadViewMatrix(camera); // Load view matrix based on the position of the camera

        renderer.render(entities); // Render all entities
        shader.stop(); // Stop the shader program

        // Render the terrains
        terrainShader.start();
        terrainShader.loadLight(light);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();

        entities.clear(); // Clear the list of all entities
        terrains.clear(); // Clear the list of all terrains
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
        terrainShader.cleanUp();
    }

    /* Project math converted to code using an online reference, forgot to copy link so now I don't wanna go looking for it...*/
    private void createProjectionMatrix() {
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }
}
