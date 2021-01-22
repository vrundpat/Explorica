package Terrains;

import Models.RawModel;
import RenderEngine.Loader;
import Tools.MatrixMath;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Terrain {
    public static final float SIZE = 800;
    private static final float MAX_HEIGHT = 40; // # of vertices on each side of a terrain
    private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;

    private float x;
    private float z;
    private float[][] heights;

    private RawModel model;
    private TerrainTexturePack texturePack;
    private TerrainTexture blendMap;

    public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap, String heigthMap) {
        this.texturePack = texturePack;
        this.blendMap = blendMap;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.model = generateTerrain(loader, heigthMap);
    }

    // This is responsible for generating a flat terrain. Quite messy but it will bound to
    // change anyway, so this is just here for testing purposes
    private RawModel generateTerrain(Loader loader, String heightMap) {

        BufferedImage image = null;

        // Load in the heightMap (a form of perlin noise)
        try {
            image = ImageIO.read(new File("src/Resources/" + heightMap + ".png"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        int VERTEX_COUNT = image.getHeight();
        heights = new float[VERTEX_COUNT][VERTEX_COUNT];

        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
        int vertexPointer = 0;

        for (int i = 0; i < VERTEX_COUNT; i++) {
            for (int j = 0; j < VERTEX_COUNT; j++) {
                // Vertex calculations
                float height = getHeight(j, i, image);
                heights[j][i] = height;
                vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;
                vertices[vertexPointer * 3 + 1] = height;
                vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1) * SIZE;

                // Normal calculations
                Vector3f normal = calculateNormal(j, i, image);
                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;

                // Texture calculations
                textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
                textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
            for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
                int topLeft = (gz * VERTEX_COUNT) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }

    public float getHeightOfTerrain(float worldX, float worldZ) {
        float terrainX = worldX - this.x; // x index into tile into the terrain
        float terrainZ = worldZ - this.z; // z index into a tile in the terrain

        // Size of the terrain tile
        float gridSquareSize = SIZE / ((float) heights.length - 1);

        // Calculate the X and Z coordinates of the worldX and worldZ into the tile
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
        if(gridX < 0 || gridX >= heights.length - 1 || gridZ < 0 || gridZ >= heights.length - 1) {
            return 0;
        }

        // X and Z coordinates of the specific quad made from the triangles in this tile
        float xCoordinate = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoordinate = (terrainZ % gridSquareSize) / gridSquareSize;
        float height;

        // If the coordinates are a part of the left triangle in the quad
        if (xCoordinate <= (1- zCoordinate)) {
            height = MatrixMath.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
                            heights[gridX + 1][gridZ], 0), new Vector3f(0,
                            heights[gridX][gridZ + 1], 1), new Vector2f(xCoordinate, zCoordinate));
        }
        // Coordinates are a part of the right triangle in the quad
        else {
            height = MatrixMath.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
                            heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
                            heights[gridX][gridZ + 1], 1), new Vector2f(xCoordinate, zCoordinate));
        }
        return height;
    }

    // Calculate the normal of a vertex based on the heights of the surrounding heights in the height map
    private Vector3f calculateNormal(int x, int z, BufferedImage image) {
        float heightL = getHeight(x-1,    z  , image);
        float heightR = getHeight(x+1,    z  , image);
        float heightD = getHeight(   x  , z-1, image);
        float heightU = getHeight(   x  , z+1, image);

        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalise();
        return normal;
    }

    // Get the height of a vertex at one point in the height map
    private float getHeight(int x, int y, BufferedImage image) {
        if(x < 0 || x >= image.getHeight() || y < 0 || y >= image.getHeight()) {
            return 0;
        }
        float height = image.getRGB(x, y);
        height += MAX_PIXEL_COLOR / 2f;
        height /= MAX_PIXEL_COLOR / 2f;
        height *= MAX_HEIGHT;
        return height;
    }

    // Getters
    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public RawModel getModel() {
        return model;
    }

    public TerrainTexturePack getTexturePack() {
        return texturePack;
    }

    public TerrainTexture getBlendMap() { return blendMap; }
}
