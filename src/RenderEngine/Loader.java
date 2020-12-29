package RenderEngine;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Loader {

    private List<Integer> VAOS = new ArrayList<Integer>();
    private List<Integer> VBOS = new ArrayList<Integer>();


    public RawModel loadToVAO(float[] positions, int[] indices) {
        int vaoId = createVAO(); // Create a VAO
        bindIndicesBuffer(indices); // Bind the indices buffer to tis VAO
        storeDataInAttributeList(0, positions); // Store the vertex data into the VAO
        unbindVAO(); // Unbind VAO after creation
        return new RawModel(vaoId, indices.length); // Return its raw model
    }

    private int createVAO() {
        int vaoId = GL30.glGenVertexArrays(); // Generate a new VAO; glGenVertexArray() returns its id
        VAOS.add(vaoId); // Store this as an initialized VAO into our tracker list
        GL30.glBindVertexArray(vaoId); // Bind the VAO
        return vaoId; // return the id of the VAO
    }

    private void storeDataInAttributeList(int attributeNumber, float[] positions) {
        int vboId = GL15.glGenBuffers(); // Generate a new buffer to store into the VAO
        VBOS.add(vboId); // Store the Vbo id in our tracker list of initialized VBOs
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId); // Bind the VBO buffer

        // Covert the float array data into a float buffer
        FloatBuffer convertedData = storeDataInFloatBuffer(positions);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, convertedData, GL15.GL_STATIC_DRAW); // Store the buffered data

        // Map the VBO into memory
        GL20.glVertexAttribPointer(attributeNumber, 3, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // Now unbind it
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        // Create a new float buffer
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data); // Load the data in
        buffer.flip(); // Flip it to ensure its ready to be read
        return buffer;
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboId = GL15.glGenBuffers(); // Generate a new VBO buffer
        VBOS.add(vboId); // Store for clean up when terminating
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId); // Bind the buffer
        IntBuffer convertedData = storeDataInIntBuffer(indices); // Convert int[] to IntBuffer and store the data
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, convertedData, GL15.GL_STATIC_DRAW); // Finish the VBO generation
    }

    private IntBuffer storeDataInIntBuffer(int[] indices) {
        IntBuffer buffer = BufferUtils.createIntBuffer(indices.length); // Create new IntBuffer
        buffer.put(indices); // Put the data
        buffer.flip(); // Flip to signify stoppage of writes
        return buffer;
    }

    private void unbindVAO() {
        GL30.glBindVertexArray(0); // Unbinds the currently bound VAO

    }

    public void cleanUp() {
        // Clear all VAOs
        for(int vaoId: VAOS) {
            GL30.glDeleteVertexArrays(vaoId);
        }

        // Clear all VAOs
        for(int vboId: VBOS) {
            GL15.glDeleteBuffers(vboId);
        }
    }
}
