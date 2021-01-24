package Shaders;

import Entities.Camera;
import Entities.Light;
import Tools.MatrixMath;
import org.lwjgl.util.vector.Matrix4f;

import java.util.List;

public class TerrainShader extends ShaderProgram {

    private static final int NUM_LIGHTS = 10;

    private static final String VERTEX_FILE = "src/Shaders/TerrainVertexShader.txt";
    private static final String FRAGMENT_FILE = "src/Shaders/TerrainFragmentShader.txt";

    private int location_transformationMatrix; // Location of the transformation matrix variable in shader code
    private int location_projectionMatrix; // Location of project matrix variable in the shader code
    private int location_viewMatrix; // Location of tbe view matrix variable in the shader code

    private int[] location_lightPosition; // Location of the light position in the shader code
    private int[] location_lightColour; // Location of the light colour in the shader code
    private int[] location_attenuation;
    private int location_shineDamper; // Location of shine damper
    private int location_reflectivity; // Location of reflectivity
    private int location_numberOfLights;

    // Texture pack and blend map locations
    private int location_backgroundTexture;
    private int location_rTexture;
    private int location_gTexture;
    private int location_bTexture;
    private int location_blendMap;



    public TerrainShader() {
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
        location_numberOfLights = super.getUniformLocation("numberOfLights");

        location_lightPosition = new int[NUM_LIGHTS];
        location_lightColour = new int[NUM_LIGHTS];
        location_attenuation = new int[NUM_LIGHTS];

        for(int i = 0; i < NUM_LIGHTS; i++) {
            location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
            location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
            location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
        }

        // Texture Pack
        location_backgroundTexture = super.getUniformLocation("backgroundTexture");
        location_rTexture = super.getUniformLocation("rTexture");
        location_gTexture = super.getUniformLocation("gTexture");
        location_bTexture = super.getUniformLocation("bTexture");
        location_blendMap = super.getUniformLocation("blendMap");
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
                super.load3DVector(location_attenuation[i], lights.get(i).getAttenuation());
            }
        }
    }

    // Load the damper and reflectivity variables in the shader code
    public void loadShineVariables(float damper, float reflectivity) {
        super.loadFloat(location_shineDamper, damper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    public void connectTextureUnits() {
        super.loadInt(location_backgroundTexture, 0);
        super.loadInt(location_rTexture, 1);
        super.loadInt(location_gTexture, 2);
        super.loadInt(location_bTexture, 3);
        super.loadInt(location_blendMap, 4);
    }
}
