package Water;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

/*
* This Frame Buffer Container class is responsible for storing a 2d texture of the 3D Game World above and
* below a body of water so they can be used to create reflection and refraction affects for the water body texture
*/
public class WaterFrameBuffers {

    // Height of the reflection frame buffer. These are just temporary values as the frame buffers will not
    // be used to render anything
    protected static final int REFLECTION_WIDTH = 320;
    private static final int REFLECTION_HEIGHT = 180;

    // Height of the refraction frame buffer. These are just temporary values as the frame buffers will not
    // be used to render anything
    protected static final int REFRACTION_WIDTH = 1280;
    private static final int REFRACTION_HEIGHT = 720;

    private int reflectionFrameBuffer;
    private int reflectionTexture;
    private int reflectionDepthBuffer;

    private int refractionFrameBuffer;
    private int refractionTexture;
    private int refractionDepthTexture;

    public WaterFrameBuffers() {
        // On initialization of a new WaterFrameBuffer, two frame buffer objects have to be
        // created that will be responsible for storing the scene above the water surface
        // and below the water surface
        initialiseReflectionFrameBuffer(); // Stores the texture of the scene above the water
        initialiseRefractionFrameBuffer(); // Stores the texture of the scene below the water
    }

    /* Called during game termination*/
    public void cleanUp() {
        GL30.glDeleteFramebuffers(reflectionFrameBuffer);
        GL11.glDeleteTextures(reflectionTexture);
        GL30.glDeleteRenderbuffers(reflectionDepthBuffer);
        GL30.glDeleteFramebuffers(refractionFrameBuffer);
        GL11.glDeleteTextures(refractionTexture);
        GL11.glDeleteTextures(refractionDepthTexture);
    }

    /* Following two functions bind Frame Buffers to their correct place in memory*/
    public void bindReflectionFrameBuffer() {
        bindFrameBuffer(reflectionFrameBuffer, REFLECTION_WIDTH,REFLECTION_HEIGHT);
    }

    public void bindRefractionFrameBuffer() {
        bindFrameBuffer(refractionFrameBuffer, REFRACTION_WIDTH,REFRACTION_HEIGHT);
    }

    /*
    * This is called when we want to revert the original frame buffer provided by OpenGL
    * as the original frame buffer is what updates the display. So after binding and using either
    * the reflection or the refraction frame buffer, this function has to be called to ensure the correct
    * display is used for the rendering of the game
    */
    public void unbindCurrentFrameBuffer() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
    }

    /* Initialize a reflection frame buffer with a Depth Buffer & a pixel texture attachment*/
    private void initialiseReflectionFrameBuffer() {
        reflectionFrameBuffer = createFrameBuffer();
        reflectionTexture = createTextureAttachment(REFLECTION_WIDTH,REFLECTION_HEIGHT);
        reflectionDepthBuffer = createDepthBufferAttachment(REFLECTION_WIDTH,REFLECTION_HEIGHT);
        unbindCurrentFrameBuffer();
    }

    /* Initialize a refraction frame buffer with a Depth Buffer & a pixel texture attachment*/
    private void initialiseRefractionFrameBuffer() {
        refractionFrameBuffer = createFrameBuffer();
        refractionTexture = createTextureAttachment(REFRACTION_WIDTH,REFRACTION_HEIGHT);
        refractionDepthTexture = createDepthTextureAttachment(REFRACTION_WIDTH,REFRACTION_HEIGHT);
        unbindCurrentFrameBuffer();
    }

    /* This will bind a frame buffer into memory so it can be used */
    private void bindFrameBuffer(int frameBuffer, int width, int height){
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);//To make sure the texture isn't bound
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
        GL11.glViewport(0, 0, width, height);
    }

    /* Initialize a frame buffer object with a corresponding FBO (Frame Buffer Object) Id */
    private int createFrameBuffer() {
        int frameBuffer = GL30.glGenFramebuffers(); // Generate a new frame buffer Id

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer); // Generate name for frame buffer
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0); // Create the framebuffer

        // Indicate that we will always render to color attachment 0
        return frameBuffer;
    }

    /* Create a texture attachment for a generic frame buffer */
    private int createTextureAttachment( int width, int height) {
        int texture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height,
                0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
                texture, 0);
        return texture;
    }

    /* Create a depth texture for a generic frame buffer so renders can determine pixels to render based on their position */
    private int createDepthTextureAttachment(int width, int height){
        int texture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT32, width, height,
                0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                texture, 0);
        return texture;
    }

    /* Creates a new depth buffer which cam be attached to a frame buffer */
    private int createDepthBufferAttachment(int width, int height) {
        int depthBuffer = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, width,
                height);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                GL30.GL_RENDERBUFFER, depthBuffer);
        return depthBuffer;
    }

    // Getters
    public int getReflectionTexture() {  return reflectionTexture;  }

    public int getRefractionTexture() {  return refractionTexture;  }

    public int getRefractionDepthTexture(){  return refractionDepthTexture;  }
}