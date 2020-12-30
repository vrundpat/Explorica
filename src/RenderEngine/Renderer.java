package RenderEngine;

import Models.RawModel;
import Models.TexturedModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Renderer {

    public void prepare() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT); // Clear the screen from the previous frame
        GL11.glClearColor(1, 0, 0, 1); // Fill the window with a simple red color
    }

    public void render(TexturedModel texturedModel) {
        RawModel model = texturedModel.getRawModel();
        // Bind the VBO
        GL30.glBindVertexArray(model.getVaoId());
        GL20.glEnableVertexAttribArray(0); // Enable the vertices VBO

        // Enable the texture VBO & activate and bind the texture
        GL20.glEnableVertexAttribArray(1);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.getTexture().getTextureID());

        // Draw the object using triangles starting from the first to the last triangle in the mesh
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        GL20.glDisableVertexAttribArray(0); // Disable the vertices VBO after it's rendered
        GL20.glDisableVertexAttribArray(1); // Disable the texture VBO after it's rendered

        GL30.glBindVertexArray(0); // Unbind the vertices VBO
    }

}
