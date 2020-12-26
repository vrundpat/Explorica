package RenderEngine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Renderer {

    public void prepare() {
        // Clear the window with a simple red color
        GL11.glClearColor(1, 0, 0, 1);

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT); // Clear the screen of previous RGBA residues
    }

    public void render(RawModel model) {
        // Bind the VBO
        GL30.glBindVertexArray(model.getVaoId());
        GL20.glEnableVertexAttribArray(0); // Enable the current VBO

        // Draw the object using triangles starting from the first to the last triangle in the mesh
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());

        GL20.glDisableVertexAttribArray(0); // Disable the VBO after it's rendered
        GL30.glBindVertexArray(0); // Unbind the VBO
    }

}
