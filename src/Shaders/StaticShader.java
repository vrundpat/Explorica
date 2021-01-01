package Shaders;

import Entities.Camera;
import Entities.Light;
import Tools.Maths;
import org.lwjgl.util.vector.Matrix4f;

public class StaticShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/Shaders/VertexShader.txt";
    private static final String FRAGMENT_FILE = "src/Shaders/FragmentShader.txt";

    private int location_transformationMatrix; // Location of the transformation matrix variable in shader code
    private int location_projectionMatrix; // Location of project matrix variable in the shader code
    private int location_viewMatrix; // Location of tbe view matrix variable in the shader code
    private int location_lightPosition; // Location of the light position in the shader code
    private int location_lightColour; // Location of the light colour in the shader code


    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        // Bind the position attribute in the shader
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords"); // Bind the textureCoords attribute
        super.bindAttribute(2, "normal"); // Bind the normal attribute

    }

    @Override
    protected void getAllUniformLocations() {
        // Get locations of all three matrices which will be used when rendering
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");

        // Loading the necessities for the lighting
        location_lightPosition = super.getUniformLocation("lightPosition");
        location_lightColour = super.getUniformLocation("lightColour");
    }

    // Load transformation matrix in the shader code
    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    // Load projection matrix in the shader code
    public void loadProjectionMatrix(Matrix4f projection) {
        super.loadMatrix(location_projectionMatrix, projection);
    }

    // Load view matrix in the shader code
    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    // Load light position & colour into the shader code
    public void loadLight(Light light) {
        super.loadVector(location_lightPosition, light.getPosition());
        super.loadVector(location_lightColour, light.getColour());
    }
}
