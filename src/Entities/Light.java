package Entities;

import org.lwjgl.util.vector.Vector3f;

public class Light {

    private Vector3f position;
    private Vector3f colour;

    public Light(Vector3f position, Vector3f colour) {
        this.position = position;
        this.colour = colour;
    }

    // Getters
    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getColour() {
        return colour;
    }

    // Setters
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }
}
