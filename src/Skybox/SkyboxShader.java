package Skybox;

import Entities.Camera;
import Shaders.ShaderProgram;
import Tools.MatrixMath;
import org.lwjgl.util.vector.Matrix4f;

public class SkyboxShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/Skybox/SkyboxVertexShader.txt";
    private static final String FRAGMENT_FILE = "src/Skybox/SkyboxFragmentShader.txt";

    private int location_projectionMatrix; // Location of the projection matrix in the shader code
    private int location_viewMatrix; // Location of the view matrix in the shader code

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

        super.loadMatrix(location_viewMatrix, matrix);
    }

    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
}
