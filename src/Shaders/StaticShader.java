package Shaders;

import Entities.Camera;
import Tools.Maths;
import org.lwjgl.util.vector.Matrix4f;

public class StaticShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/Shaders/VertexShader.txt";
    private static final String FRAGMENT_FILE = "src/Shaders/FragmentShader.txt";

    private int location_transformationMatrix; // Location of the transformation matrix variable in shader code
    private int location_projectionMatrix; // Location of project matrix variable in the shader code
    private int location_viewMatrix; // Location of tbe view matrix variable in the shader code

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
        // Get locations of all three matrices which will be used when rendering
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
    }

    // Get location of transformation matrix in the shader code
    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    // Get location of projection matrix in the shader code
    public void loadProjectionMatrix(Matrix4f projection) {
        super.loadMatrix(location_projectionMatrix, projection);
    }

    // Get location of view matrix in the shader code
    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }
}
