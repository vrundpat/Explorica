package RenderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;

public class DisplayManager {

    private static final int WIN_WIDTH = 1280; // Width of the window
    private static final int WIN_HEIGHT = 720; // Height of the window
    private static final int FPS = 120; // FPS Cap/Limit

    private static long lastFrameTime;
    private static float delta;

    public static void createDisplay() {

        //  New context attributes
        ContextAttribs attribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);

        attribs.withForwardCompatible(true); // Allow forward propagation
        attribs.withProfileCore(true);  // Allow use of the Profile Core

        // Set the Display attributes
        try {
            Display.setDisplayMode(new DisplayMode(WIN_WIDTH, WIN_HEIGHT));
            Display.create(new PixelFormat(), attribs);
            Display.setTitle("Wander");
        }
        catch (LWJGLException error) {
            error.printStackTrace();
        }

        // Set the viewport of the display (top-left & bottom-right)
         GL11.glViewport(0,0, WIN_WIDTH, WIN_HEIGHT);

        lastFrameTime = getCurrentTime(); // Initially, the last frame should be the time the window is created

        // Create the cursor and set it to grabbed so it gets detected only when in the game window
        try {
            Mouse.create();
            Mouse.setGrabbed(true);
        }
        catch (LWJGLException error) {
            error.printStackTrace();
        }
    }

    public static void updateDisplay() {
        // Sync at 120 Hz
        Display.sync(FPS);
        Display.update();

        long currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime) / 1000f;
        lastFrameTime = currentFrameTime;
    }

    public static float getDelta() {
        return delta;
    }

    public static void closeDisplay() {
        Display.destroy();
        Mouse.destroy();
    }

    private static long getCurrentTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }


}
