package Shaders;

import Entities.Camera;
import Entities.Light;
import Tools.MatrixMath;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class StaticShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/Shaders/VertexShader.txt";
    private static final String FRAGMENT_FILE = "src/Shaders/FragmentShader.txt";

    private static final int NUM_LIGHTS = 1;

    private int location_transformationMatrix; // Location of the transformation matrix variable in shader code
    private int location_projectionMatrix; // Location of project matrix variable in the shader code
    private int location_viewMatrix; // Location of tbe view matrix variable in the shader code
    private int[] location_lightPosition; // Location of the light position in the shader code
    private int[] location_lightColour; // Location of the light colour in the shader code
    private int location_shineDamper; // Location of shine damper
    private int location_reflectivity; // Location of reflectivity
    private int location_useFakeLighting; // Location of useFakeLighting
    private int location_skyColour; // Location of skyColour
    private int location_numberOfRows;
    private int location_offset;
    private int location_numberOfLights;


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
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_useFakeLighting = super.getUniformLocation("useFakeLighting");
        location_numberOfLights = super.getUniformLocation("numberOfLights");

        // Get the position of all light position and colors at each index
        location_lightPosition = new int[NUM_LIGHTS];
        location_lightColour = new int[NUM_LIGHTS];

        for(int i = 0; i < NUM_LIGHTS; i++) {
            location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
            location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
        }

        // Load fog necessities
        location_skyColour = super.getUniformLocation("skyColour");

        // Texture atlas necessities
        location_numberOfRows = super.getUniformLocation("numberOfRows");
        location_offset = super.getUniformLocation("offset");
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
        Matrix4f viewMatrix = MatrixMath.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    // Load light position & colour into the shader code
    public void loadLights(List<Light> lights) {

        // Load in the number of lights into the shader code
        super.loadInt(location_numberOfLights, NUM_LIGHTS);

        for(int i = 0; i < NUM_LIGHTS; i++) {
            if(i < lights.size()) {
                super.load3DVector(location_lightPosition[i], lights.get(i).getPosition());
                super.load3DVector(location_lightColour[i], lights.get(i).getColour());
            }
        }
    }

    // Load the damper and reflectivity variables in the shader code
    public void loadShineVariables(float damper, float reflectivity) {
        super.loadFloat(location_shineDamper, damper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    public void loadFakeLighting(boolean useFakeLighting) {
        super.loadBoolean(location_useFakeLighting, useFakeLighting);
    }

    public void loadSkyColour(float r, float g, float b) {
        super.load3DVector(location_skyColour, new Vector3f(r, g, b));
    }

    // Load the texture atlas variables into the shader code
    public void loadNumberOfRows(int numberOfRows) {
        super.loadFloat(location_numberOfRows, numberOfRows);
    }

    public void loadOffset(float x, float y) {
        super.load2DVector(location_offset, new Vector2f(x, y));
    }
}
