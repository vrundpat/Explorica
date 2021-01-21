package Entities;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

    // Camera's position
    private static final float X = 0;
    private static final float Y = 3;
    private static final float Z = 0;

    private Vector3f position = new Vector3f(X, Y, Z);
    private float pitch = 5; // Angle tangent to ground
    private float yaw = (-Display.getWidth() - (float) Mouse.getX() / 4); // Horizontal offsets of the camera
    private float roll; // Distance of the camera from another entity, for a future player model

    private static final float CAMERA_SPEED = 0.2f;
    private static float HORIZONTAL_SENSITIVITY = 10f;
    private static float VERTICAL_SENSITIVITY = 10f;

    private static final float TERRAIN_HEIGHT = Y;

    public Camera() { }

    // Based on given keyboard inputs, move the camera
    public void move() {

        // Change in the
        float x = Mouse.getDX() / HORIZONTAL_SENSITIVITY;
        float y = -(Mouse.getDY() / VERTICAL_SENSITIVITY);
        yaw += x;

        // If the pitch becomes obtuse, it will invert terrain and view matrices, thus a limit
        // is added allowing only positive and negative acute angles
        if(pitch + y < 90 && pitch + y > -90) {
            pitch += y;
        }

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
        if(Keyboard.isKeyDown(Keyboard.KEY_E)) {
            position.y -= CAMERA_SPEED;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_Q)) {
            position.y += CAMERA_SPEED;
        }

        // Collision detection with the flat terrain
        if(position.y < TERRAIN_HEIGHT) {
            position.y = TERRAIN_HEIGHT;
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

    public float getRoll() {  return roll; }
}
