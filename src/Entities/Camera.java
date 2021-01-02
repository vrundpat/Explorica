package Entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

    // Camera's position
    private Vector3f position = new Vector3f(0, 0, 0);
    private float pitch; // Angle tangent to ground
    private float yaw; // Height above ground
    private float roll; // Undefined term for future use

    private static final float CAMERA_SPEED = 0.1f;

    public Camera() { }

    // Based on given keyboard inputs, move the camera
    public void move() {
        if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
            position.z -= CAMERA_SPEED;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
            position.x -= CAMERA_SPEED;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
            position.z += CAMERA_SPEED;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
            position.x += CAMERA_SPEED;
        }
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
