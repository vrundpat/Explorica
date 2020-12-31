package Entities;

import Models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;

public class Entity {

    // Properties of an entity
    private TexturedModel model; // A texture for this entity
    private Vector3f position; // Position in the world
    private float rotX, rotY, rotZ; // Rotation variables
    private float scale; // Scale is how large this entity will be

    public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
    }

    // Based on given params, increase the position vector
    public void increasePosition(float dx, float dy, float dz) {
        this.position.x += dx;
        this.position.y += dy;
        this.position.z += dz;
    }

    // Based on the given params, increase the rotation properties of this entity
    public void increaseRotation(float rx, float ry, float rz) {
        this.rotX += rx;
        this.rotY += ry;
        this.rotZ += rz;
    }

    // Getters
    public TexturedModel getModel() {
        return model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getRotX() {
        return rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    public float getScale() {
        return scale;
    }


    // Setters

    public void setModel(TexturedModel model) {
        this.model = model;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setRotX(float rotX) {
        this.rotX = rotX;
    }

    public void setRotY(float rotY) {
        this.rotY = rotY;
    }

    public void setRotZ(float rotZ) {
        this.rotZ = rotZ;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
