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

public class EntityRenderer {

    private StaticShader shader;

    // Constructor will now create a projectionMatrix upon initialization
    public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
        this.shader = shader; // Set the shader

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
        RawModel rawModel = model.getRawModel(); // Fetch this textured models base mesh (RawModel)

        GL30.glBindVertexArray(rawModel.getVaoId()); // Bind the VAO

        GL20.glEnableVertexAttribArray(0); // Enable the vertices VBO
        GL20.glEnableVertexAttribArray(1); // Enable the texture VBO
        GL20.glEnableVertexAttribArray(2); // Enable the normals VBO

        // Load the damper and reflectivity variables into the shader code from the texture
        ModelTexture texture = model.getTexture();
        if(texture.getHasTransparency()) {
            MainRenderer.disableCulling();
        }
        shader.loadFakeLighting(texture.getUseFakeLighting());
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());

        // Activate and bind the texture
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
    }

    public void unbindTexturedModel() {
        MainRenderer.enableCulling();
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
}
