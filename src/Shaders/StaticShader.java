package Shaders;

import org.lwjgl.util.vector.Matrix4f;

public class StaticShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/Shaders/VertexShader.txt";
    private static final String FRAGMENT_FILE = "src/Shaders/FragmentShader.txt";

    private int location_transformationMatrix;

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        // Bind the position attribute in the shader
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords"); // Bind the textureCoords attribute
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }
}
