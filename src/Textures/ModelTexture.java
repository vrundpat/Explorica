package Textures;

public class ModelTexture {

    private int textureID;

    private float shineDamper = 1;
    private float reflectivity = 0;

    private boolean hasTransparency = false;
    private boolean useFakeLighting = false;

    public ModelTexture(int id) {
        this.textureID = id;
    }

    public int getTextureID() {
        return this.textureID;
    }

    // Getters
    public float getReflectivity() {
        return reflectivity;
    }

    public float getShineDamper() {
        return shineDamper;
    }

    public boolean getHasTransparency() { return hasTransparency; }

    public boolean getUseFakeLighting() { return useFakeLighting; }

    // Setters
    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }

    public void setShineDamper(float shineDamper) {
        this.shineDamper = shineDamper;
    }

    public void setHasTransparency(boolean hasTransparency) { this.hasTransparency = hasTransparency; }

    public void setUseFakeLighting(boolean useFakeLighting) { this.useFakeLighting = useFakeLighting; }
}
