package Skybox;

import Entities.Camera;
import RenderEngine.DisplayManager;
import Shaders.ShaderProgram;
import Tools.MatrixMath;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class SkyboxShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/Skybox/SkyboxVertexShader.txt";
    private static final String FRAGMENT_FILE = "src/Skybox/SkyboxFragmentShader.txt";

    private int location_projectionMatrix; // Location of the projection matrix in the shader code
    private int location_viewMatrix; // Location of the view matrix in the shader code
    private int location_fogColour; // Location of the fog colour in the shader code
    private int location_blendFactor; // Location of the blend factor in the shader code
    private int location_cubeMap1; // Location of the day time in the shader code
    private int location_cubeMap2; // Location of the night time factor in the shader code


    private static final float ROTATE_SPEED = 1f; // Speed at which the skybox will rotate
    private float current_rotation = 0;

    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f matrix = MatrixMath.createViewMatrix(camera);

        // Remove the translation in the view matrix so the skybox doesn't translate with respect to
        // the Camera's position
        matrix.m30 = 0;
        matrix.m31 = 0;
        matrix.m32 = 0;

        // Accumulate the rotation of the skybox & rotate the skybox
        current_rotation += ROTATE_SPEED * DisplayManager.getDelta();
        matrix.rotate((float) Math.toRadians(current_rotation), new Vector3f(0, 1, 0));

        super.loadMatrix(location_viewMatrix, matrix);
    }

    public void loadFogColour(float r, float g, float b) {
        super.load3DVector(location_fogColour, new Vector3f(r, g, b));
    }

    public void loadCubeMapTextures() {
        super.loadInt(location_cubeMap1, 0);
        super.loadInt(location_cubeMap2, 1);
    }

    public void loadBlendFactor(float blendFactor) {
        super.loadFloat(location_blendFactor, blendFactor);
    }

    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_fogColour = super.getUniformLocation("fogColour");

        location_blendFactor = super.getUniformLocation("blendFactor");
        location_cubeMap1 = super.getUniformLocation("cubeMap1");
        location_cubeMap2 = super.getUniformLocation("cubeMap2");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
}
