package RenderEngine;

import Models.RawModel;
import Textures.TextureData;
import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Loader {

    private List<Integer> VAOS = new ArrayList<>();
    private List<Integer> VBOS = new ArrayList<>();
    private List<Integer> TEXTURES = new ArrayList<>();


    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
        int vaoID = createVAO(); // Create a VAO
        bindIndicesBuffer(indices); // Bind the indices buffer to tis VAO
        storeDataInAttributeList(0, 3, positions); // Store the vertex data into the VAO
        storeDataInAttributeList(1, 2, textureCoords); // Store the texture data into the VAO
        storeDataInAttributeList(2, 3, normals); // Store the normal data into the VAO

        unbindVAO(); // Unbind VAO after creation
        return new RawModel(vaoID, indices.length); // Return its raw model
    }

    // For possible GUI renders
    public RawModel loadToVAO(float[] positions, int dimensions) {
        int vaoID = createVAO();
        this.storeDataInAttributeList(0, dimensions, positions);
        unbindVAO();
        return new RawModel(vaoID, positions.length / dimensions);
    }

    public int loadTexture(String filename) {
        Texture texture = null;
        try {
            texture = TextureLoader.getTexture("PNG", new FileInputStream("src/Resources/" + filename + ".png"));

            // Improve rendering performance by enabling mip-mapping to textures further away
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);
        }
        catch (IOException e) {
            System.err.println("An error occured when loading in texture: " + filename);
            e.printStackTrace();
        }

        assert texture != null;
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

    /*
        GL Cube Map Apparatus:
            GL_TEXTURE_CUBE_MAP_POSITIVE_X = Right Face
            GL_TEXTURE_CUBE_MAP_NEGATIVE_X = Left Face
            GL_TEXTURE_CUBE_MAP_POSITIVE_Y = Top Face
            GL_TEXTURE_CUBE_MAP_NEGATIVE_Y = Bottom Face
            GL_TEXTURE_CUBE_MAP_POSITIVE_Z = Back Face
            GL_TEXTURE_CUBE_MAP_NEGATIVE_Z = Front Face

            The difference starting from the RIGHT face to the FRONT face is 1 each, so it can be
            manipulated using a loop to generating an entire cube map with regards to
            GL_TEXTURE_CUBE_MAP_POSITIVE_X
     */
    public int loadCubeMap(String[] textureFiles) {
        int textureID = GL11.glGenTextures();
        GL13.glActiveTexture(textureID);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureID);

        for(int i = 0; i < textureFiles.length; i++) {
            TextureData data = decodeTextureFile("src/Resources/SkyboxImages/" + textureFiles[i] + ".png");
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
        }

        // Identify texture parameters using linear vectors for the Cube Map
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

        TEXTURES.add(textureID); // Store the Cube Map TextureID so it can be safely removed on clean up

        return textureID;
    }

    // Decodes a texture file using byte buffers for textures compatible with the 3D coordinate system
    private TextureData decodeTextureFile(String fileName) {
        int width = 0;
        int height = 0;

        ByteBuffer buffer = null;
        try {
            FileInputStream in = new FileInputStream(fileName);
            PNGDecoder decoder = new PNGDecoder(in);

            width = decoder.getWidth();
            height = decoder.getHeight();
            buffer = ByteBuffer.allocateDirect(4 * width * height);
            decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
            buffer.flip(); // Finish writing, and prep for reading
            in.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading Texture: " + fileName + "using ByteBuffers");
            System.exit(-1);
        }

        return new TextureData(buffer, width, height);
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
