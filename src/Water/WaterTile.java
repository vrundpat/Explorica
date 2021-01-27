package Water;

public class WaterTile {

    public static final float TILE_SIZE = 360;

    private float height;
    private float x;
    private float z;

    public WaterTile(float centerX, float centerZ, float height){
        this.x = centerX;
        this.z = centerZ;
        this.height = height;
    }

    public float getHeight() {
        return height;
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }



}