package Entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

    // Camera's position
    private Vector3f position = new Vector3f(0, 0, 0);
    private float pitch; // Angle tangent to ground
    private float yaw; // Height above ground
    private float roll; // Undefined term for future use

    public Camera() { }

    // Based on given keyboard inputs, move the camera
    public void move() {
        if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
            position.z -= 0.02f;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
            position.x += 0.02f;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
            position.x -= 0.02f;
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
