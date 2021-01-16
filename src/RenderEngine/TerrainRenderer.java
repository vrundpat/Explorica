package RenderEngine;

import Entities.Entity;
import Models.RawModel;
import Models.TexturedModel;
import Shaders.TerrainShader;
import Terrains.Terrain;
import Terrains.TerrainTexturePack;
import Textures.ModelTexture;
import Tools.Maths;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class TerrainRenderer {

    private TerrainShader shader;

    public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.connectTextureUnits();
        shader.stop();
    }

    public void render(List<Terrain> terrains) {
        for(Terrain terrain : terrains) {
            prepareTexture(terrain);
            loadModelMatrix(terrain);

            // Draw the entity using the necessary information
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

            unbindTexturedModel();
        }
    }

    public void prepareTexture(Terrain terrain) {
        RawModel rawModel = terrain.getModel(); // Fetch this textured models base mesh (RawModel)

        GL30.glBindVertexArray(rawModel.getVaoId()); // Bind the VAO

        GL20.glEnableVertexAttribArray(0); // Enable the vertices VBO
        GL20.glEnableVertexAttribArray(1); // Enable the texture VBO
        GL20.glEnableVertexAttribArray(2); // Enable the normals VBO

        // Load the damper and reflectivity variables into the shader code from the texture
        shader.loadShineVariables(1, 0);

        // Activate and bind the texture pack and the blend map
        bindTextures(terrain);
    }

    // Bind the entirety of the texture pack & the blend map
    private void bindTextures(Terrain terrain) {
        TerrainTexturePack texturePack = terrain.getTexturePack();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());

        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getrTexture().getTextureID());

        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getgTexture().getTextureID());

        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getbTexture().getTextureID());

        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());



    }

    public void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0); // Disable the vertices VBO after it's rendered
        GL20.glDisableVertexAttribArray(1); // Disable the texture VBO after it's rendered
        GL20.glDisableVertexAttribArray(2); // Disable the normals VBO after it's rendered

        GL30.glBindVertexArray(0); // Unbind the vertices VBO
    }

    public void loadModelMatrix(Terrain terrain) {
        // Create a transformation matrix for this entity
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
        shader.loadTransformationMatrix(transformationMatrix); // Load the matrix into the shader
    }
}
