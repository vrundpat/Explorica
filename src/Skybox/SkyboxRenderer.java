package Skybox;

import Entities.Camera;
import Models.RawModel;
import RenderEngine.DisplayManager;
import RenderEngine.Loader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class SkyboxRenderer {

    // Size of a single face of the cube map
    private static final float SIZE = 500f;

    /* Hard coded static vertices of all faces of the Skybox Cube */
    private static final float[] VERTICES = {
            -SIZE,  SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            -SIZE,  SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE,  SIZE
    };

    private static String[] CUBE_MAP_1_TEXTURE_FILES = { "right", "left", "top", "bottom", "back", "front" };
    private static String[] CUBE_MAP_2_TEXTURE_FILES = { "nightRight", "nightLeft", "nightTop", "nightBottom", "nightBack", "nightFront" };

    private final RawModel cube;
    private final SkyboxShader shader;

    private final int cube_map_1_textureID;
    private final int cube_map_2_textureID;

    public static final Vector3f SKY_COLOUR = new Vector3f(0.5f, 0.6f, 0.6f);

    private float current_time = 0;
    private int first_cube_map;
    private int second_cube_map;

    public SkyboxRenderer(Loader loader, Matrix4f projectionMatrix) {

        // With disabling the depth mask and ranges, the Skybox won't clip the terrain when it's rendered
        GL11.glDepthMask(false);
        GL11.glDepthRange(1f, 1f);

        cube = loader.loadToVAO(VERTICES, 3);
        cube_map_1_textureID = loader.loadCubeMap(CUBE_MAP_1_TEXTURE_FILES);
        cube_map_2_textureID = loader.loadCubeMap(CUBE_MAP_2_TEXTURE_FILES);

        shader = new SkyboxShader();
        shader.start();
        shader.loadCubeMapTextures();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();

        // Re-enable the depth mask and reset the depth range after the skybox is rendered
        GL11.glDepthRange(0, 1f);
        GL11.glDepthMask(true);
    }

    public void render(Camera camera, Vector3f skyColour) {

        shader.start();
        shader.loadViewMatrix(camera);
        shader.loadFogColour(skyColour.x, skyColour.y, skyColour.z);

        GL30.glBindVertexArray(cube.getVaoId());
        GL20.glEnableVertexAttribArray(0);

        bindCubeMapTextures();

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

        shader.stop();
    }

    private void bindCubeMapTextures() {

        float delta = DisplayManager.getDelta(); // Time since last frame
        current_time += delta * 1000;

        // Calculate the blend factor and textures to use depending the current "time" in game
        float blendFactor = calculateBlendFactorAndTextures(delta);

        // Load in a blend factor
        shader.loadBlendFactor(blendFactor);

        // Bind the first cube map
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, first_cube_map);

        // Bind the second cube map
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, second_cube_map);
    }

    private float calculateBlendFactorAndTextures(float delta) {

        // Hit midnight again, reset to 0
        if(current_time >= 24000) {
            current_time = 0;
        }

        first_cube_map = cube_map_1_textureID;
        second_cube_map = cube_map_1_textureID;

        return 0;
    }

}
