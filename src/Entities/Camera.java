package Entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

    // Camera's position
    private Vector3f position = new Vector3f(0, 3, 0);
    private float pitch = 5; // Angle tangent to ground
    private float yaw = (-Display.getWidth() - (float) Mouse.getX() / 4) ; // Height above ground
    private float roll; // Undefined term for future use

    private static final float CAMERA_SPEED = 0.2f;
    private static float HORIZONTAL_SENSITIVITY = 10f;
    private static float VERTICAL_SENSITIVITY = 10f;

    public Camera() { }

    // Based on given keyboard inputs, move the camera
    public void move() {

        // Change in the
        float x = Mouse.getDX() / HORIZONTAL_SENSITIVITY;
        float y = -(Mouse.getDY() / VERTICAL_SENSITIVITY);
        yaw += x;
        pitch += y;

        if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
            position.z += -(float)Math.cos(Math.toRadians(yaw)) * CAMERA_SPEED;
            position.x += (float)Math.sin(Math.toRadians(yaw)) * CAMERA_SPEED;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
            position.z -= (float)Math.sin(Math.toRadians(yaw)) * CAMERA_SPEED;
            position.x -= (float)Math.cos(Math.toRadians(yaw)) * CAMERA_SPEED;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
            position.z -= -(float)Math.cos(Math.toRadians(yaw)) * CAMERA_SPEED;
            position.x -= (float)Math.sin(Math.toRadians(yaw)) * CAMERA_SPEED;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
            position.z += (float)Math.sin(Math.toRadians(yaw)) * CAMERA_SPEED;
            position.x += (float)Math.cos(Math.toRadians(yaw)) * CAMERA_SPEED;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_Q)) {
            position.y += CAMERA_SPEED;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_E)) {
            position.y -= CAMERA_SPEED;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            Mouse.setGrabbed(!Mouse.isGrabbed());
        }
    }

    public void updateHorizontalSensitivity(float delta) {
        HORIZONTAL_SENSITIVITY += delta;
    }
    public void updateVerticalSensitivity(float delta) {
        VERTICAL_SENSITIVITY += delta;
    }

    // Getters
    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }
}
