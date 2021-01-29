package Entities;

import RenderEngine.DisplayManager;
import Terrains.Terrain;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

    // Camera's position
    private static final float X = 100;
    private static final float Y = 5;
    private static final float Z = 500;

    private Vector3f position = new Vector3f(X, Y, Z);

    // Camera presets
    private float pitch = 5; // Angle of the camera tangent to the ground
    private float yaw = (-Display.getWidth() - (float) Mouse.getX() / 4); // Horizontal offsets of the camera
    private float roll; // Distance of the camera from another entity, for a future player model

    // Camera movement controllable variables
    private static final float CAMERA_SPEED = 30f;
    private static float HORIZONTAL_SENSITIVITY = 10f;
    private static float VERTICAL_SENSITIVITY = 10f;

    // "Environment" variables
    private static final float GRAVITY = -50f;
    private static final float JUMP_POWER = 25f;
    private static final float PLAYER_HEIGHT = 5;

    private float currentUpwardsSpeed = 0;
    private boolean didJump = false;

    public Camera() { }

    // Based on given keyboard inputs, move the camera
    public void move(Terrain terrain) {

        // Change in the mouse cursor position since the last frame
        float dx = Mouse.getDX() / HORIZONTAL_SENSITIVITY;
        float dy = -(Mouse.getDY() / VERTICAL_SENSITIVITY); // Negative due to the 2D pixel coordinate system
        yaw += dx;

        // If the pitch becomes obtuse, it will invert terrain and view matrices, thus a limit
        // is added allowing only positive and negative acute angles
        if(pitch + dy < 90 && pitch + dy > -90) {  pitch += dy;  }

        // Time since last frame which will be used to calculate the speed of the camera
        // This way if frames skip or frame times vary, the camera won't "teleport"
        final float delta = DisplayManager.getDelta();

        // Poll keyboard events
        checkInputs(delta);

        // Calculate the current upwards velocity since the last frame and change the camera's Y position
        // based on the frame time interval of that upwards speed
        currentUpwardsSpeed += GRAVITY * delta;
        position.y += currentUpwardsSpeed * delta;

        // Get the terrain height at the updated camera position
        float terrainHeight = terrain.getHeightOfTerrain(position.x, position.z) + PLAYER_HEIGHT;

        // Collision detection with the terrain at this position in the world
        if(position.y < terrainHeight) {
            position.y = terrainHeight;
            currentUpwardsSpeed = 0;
            didJump = false;
        }
    }

    private void checkInputs(float delta) {
        if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
            position.z += -(float)Math.cos(Math.toRadians(yaw)) * CAMERA_SPEED * delta;
            position.x += (float)Math.sin(Math.toRadians(yaw)) * CAMERA_SPEED * delta;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
            position.z -= (float)Math.sin(Math.toRadians(yaw)) * CAMERA_SPEED * delta;
            position.x -= (float)Math.cos(Math.toRadians(yaw)) * CAMERA_SPEED * delta;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
            position.z -= -(float)Math.cos(Math.toRadians(yaw)) * CAMERA_SPEED * delta;
            position.x -= (float)Math.sin(Math.toRadians(yaw)) * CAMERA_SPEED * delta;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
            position.z += (float)Math.sin(Math.toRadians(yaw)) * CAMERA_SPEED * delta;
            position.x += (float)Math.cos(Math.toRadians(yaw)) * CAMERA_SPEED * delta;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !didJump) {
            jump();
        }
    }

    private void jump() {
        this.currentUpwardsSpeed = JUMP_POWER;
        didJump = true;
    }

    public void invertPitch() {
        this.pitch = -pitch;
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

    public static float getPlayerHeight() { return PLAYER_HEIGHT; }
}
