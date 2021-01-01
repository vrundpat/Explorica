package RenderEngine;

import Models.RawModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Loader {

    private List<Integer> VAOS = new ArrayList<Integer>();
    private List<Integer> VBOS = new ArrayList<Integer>();
    private List<Integer> TEXTURES = new ArrayList<Integer>();


    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
        int vaoID = createVAO(); // Create a VAO
        bindIndicesBuffer(indices); // Bind the indices buffer to tis VAO
        storeDataInAttributeList(0, 3, positions); // Store the vertex data into the VAO
        storeDataInAttributeList(1, 2, textureCoords); // Store the texture data into the VAO
        storeDataInAttributeList(2, 3, normals); // Store the normal data into the VAO

        unbindVAO(); // Unbind VAO after creation
        return new RawModel(vaoID, indices.length); // Return its raw model
    }

    public int loadTexture(String filename) {
        Texture texture = null;
        try {
            texture = TextureLoader.getTexture("PNG", new FileInputStream("src/Resources/" + filename + ".png"));
        }
        catch (IOException e) {
            System.err.println("An error occured when loading in texture: " + filename);
            e.printStackTrace();
        }

        int textureID = texture.getTextureID();
        TEXTURES.add(textureID);
        return textureID;
    }

    private int createVAO() {
        int vaoID = GL30.glGenVertexArrays(); // Generate a new VAO; glGenVertexArray() returns its id
        VAOS.add(vaoID); // Store this as an initialized VAO into our tracker list
        GL30.glBindVertexArray(vaoID); // Bind the VAO
        return vaoID; // return the id of the VAO
    }

    private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] positions) {
        int vboID = GL15.glGenBuffers(); // Generate a new buffer to store into the VAO
        VBOS.add(vboID); // Store the Vbo id in our tracker list of initialized VBOs
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID); // Bind the VBO buffer

        // Covert the float array data into a float buffer
        FloatBuffer convertedData = storeDataInFloatBuffer(positions);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, convertedData, GL15.GL_STATIC_DRAW); // Store the buffered data

        // Map the VBO into memory
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
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
        int vboID = GL15.glGenBuffers(); // Generate a new VBO buffer
        VBOS.add(vboID); // Store for clean up when terminating
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID); // Bind the buffer
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
        for(int vaoID: VAOS) {
            GL30.glDeleteVertexArrays(vaoID);
        }

        // Clear all VAOs
        for(int vboID: VBOS) {
            GL15.glDeleteBuffers(vboID);
        }

        // Clear all textures
        for(int textureID : TEXTURES) {
            GL11.glDeleteTextures(textureID);
        }
    }
}
