package RenderEngine;

import Entities.Entity;
import Models.RawModel;
import Models.TexturedModel;
import Shaders.StaticShader;
import Textures.ModelTexture;
import Tools.Maths;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;

import java.util.List;
import java.util.Map;

public class Renderer {

    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private Matrix4f projectionMatrix;
    private StaticShader shader;

    // Constructor will now create a projectionMatrix upon initialization
    public Renderer(StaticShader shader) {
        this.shader = shader; // Set the shader

        // Enabling cull facing on the Back Face prevents rendering of faces on a model that are not in view
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);

        createProjectionMatrix(); // Create the projection matrix

        shader.start();
        shader.loadProjectionMatrix(projectionMatrix); // Load the projection matrix it into the shader code
        shader.stop();
    }

    /*
        This method will be responsible for rendering all entities very efficiently as it will bind/unbind
        the textures & VBOs and the activate shaders only once for a group of common entities. Previously, the
        renderer will bind & unbind the textures and VBOs when each entity would be rendered, entailing if 100
        stall objects were rendered, it would bind/unbind the textures, VBOs and shaders 100 times; But now it
        only for it once.
    */
    public void render(Map<TexturedModel, List<Entity>> entities) {
        // Iterate over all grouped entities
        for(TexturedModel model: entities.keySet()) {
            prepareTexturedModel(model); // For each entity, we bind the VBOs & activate its texture
            List<Entity> batch = entities.get(model); // Get the list of this entity

            // For every entity in this group
            for(Entity entity : batch) {
                prepareInstance(entity); // Create its transformation matrix
                // Draw the entity using the necessary information
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }

            unbindTexturedModel(); // When all entities of this group have been rendered, unbind the textures and VBOs
        }
    }

    public void prepareTexturedModel(TexturedModel model) {
        RawModel rawModel = model.getRawModel();
        // Bind the VBO
        GL30.glBindVertexArray(rawModel.getVaoId());
        GL20.glEnableVertexAttribArray(0); // Enable the vertices VBO

        // Enable the texture VBO
        GL20.glEnableVertexAttribArray(1);

        // Enable the normals VBO
        GL20.glEnableVertexAttribArray(2);

        // Load the damper and reflectivity variables into the shader code from the texture
        ModelTexture texture = model.getTexture();
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());

        // Activate and bind the texture
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
    }

    public void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0); // Disable the vertices VBO after it's rendered
        GL20.glDisableVertexAttribArray(1); // Disable the texture VBO after it's rendered
        GL20.glDisableVertexAttribArray(2); // Disable the normals VBO after it's rendered

        GL30.glBindVertexArray(0); // Unbind the vertices VBO
    }

    public void prepareInstance(Entity entity) {
        // Create a transformation matrix for this entity
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix); // Load the matrix into the shader
    }

    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Allow OpenGL to monitor overlapping vertices and render accordingly
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // Clear the screen from the previous frame & allow depth buffer testing
        GL11.glClearColor(1, 1, 1, 1); // Fill the window with a simple red color
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
