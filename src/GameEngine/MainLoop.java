package GameEngine;

import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import Models.TexturedModel;
import RenderEngine.*;
import Models.RawModel;
import Terrains.Terrain;
import Terrains.TerrainTexture;
import Terrains.TerrainTexturePack;
import Textures.ModelTexture;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainLoop {

    public static void generateModel(TexturedModel model, List<Entity> entities, Terrain[][] terrains, float scale, int count, int numberOfCols) {
        Random random = new Random();
        for(int i = 0; i < count / 2; i++){
            float x = random.nextFloat() * 800;
            float z = random.nextFloat() * 800;
            float y = terrains[0][0].getHeightOfTerrain(x, z);

            entities.add(new Entity(model, random.nextInt(numberOfCols), new Vector3f(x, y, z),0,0,0, scale));
        }

        for(int i = 0; i < count / 2; i++){
            float x = random.nextFloat() * 800;
            float z = random.nextFloat() * 1600;
            float y = z > 800 ? terrains[0][1].getHeightOfTerrain(x, z) : terrains[0][0].getHeightOfTerrain(x, z);

            entities.add(new Entity(model, random.nextInt(numberOfCols), new Vector3f(x, y, z),0,0,0, scale));
        }
    }

    public static void generateLights(List<Light> lights, Terrain[][] terrains, List<Entity> entities, Loader loader, int count) {
        // The Sun/Moon; Light source that doesn't attenuate
        lights.add(new Light(new Vector3f(0, 1000, 0), new Vector3f(0.5f, 0.5f, 0.5f)));

        RawModel lampModel = OBJLoader.loadObjModel("lamp", loader);
        TexturedModel lamp = new TexturedModel(lampModel, new ModelTexture(loader.loadTexture("lamp")));

        lamp.getTexture().setUseFakeLighting(true);

        // Generate attenuating lights (limited by range)
        Random random = new Random();
        for(int i = 0; i < count; i++){
            float x = random.nextFloat() * 800;
            float z = random.nextFloat() * 1600;
            float y = z > 800 ? terrains[0][1].getHeightOfTerrain(x, z) : terrains[0][0].getHeightOfTerrain(x, z);

            lights.add(new Light(new Vector3f(x, y + 15, z), new Vector3f(0, 5, 0), new Vector3f(1, 0.1f, 0.002f)));
            entities.add(new Entity(lamp, new Vector3f(x, y, z), 0, 0, 0, 1));
        }
    }

    public static void generateTerrains(int rows, int cols, Terrain[][] terrains, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap, String heightMap) {
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                terrains[i][j] = new Terrain(0, 0, loader, texturePack, blendMap, heightMap);
            }
        }
    }

    public static void enableFakeLightingAndHasTransparency(TexturedModel model) {
        model.getTexture().setUseFakeLighting(true);
        model.getTexture().setHasTransparency(true);
    }

    public static void main(String[] args) {

        DisplayManager.createDisplay();

        // Takes care of loading in textures and 3D .obj models
        Loader loader = new Loader();

        // Load up the terrain texture pack
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        RawModel grassModel = OBJLoader.loadObjModel("grassModel", loader);
        RawModel fernModel = OBJLoader.loadObjModel("fern", loader);
        RawModel pineTreeModel = OBJLoader.loadObjModel("pine", loader);

        // Texture Atlases
        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern")); fernTextureAtlas.setNumberOfRows(2);

        TexturedModel grass = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("grassTexture")));
        TexturedModel fern = new TexturedModel(fernModel, new ModelTexture(loader.loadTexture("fern")));
        TexturedModel pinkFlower = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("flower")));
        TexturedModel pineTree = new TexturedModel(pineTreeModel, new ModelTexture(loader.loadTexture("pine")));

        // Enable fake lighting and transparency here for models that need it
        enableFakeLightingAndHasTransparency(grass);
        enableFakeLightingAndHasTransparency(fern);
        enableFakeLightingAndHasTransparency(pinkFlower);

        Camera camera = new Camera();

        final int TERRAIN_MATRIX_ROWS = 2;
        final int TERRAIN_MATRIX_COLS = 2;

        MainRenderer renderer = new MainRenderer(loader);

        List<Entity> entities = new ArrayList<>();
        List<Light> lights = new ArrayList<>();
        Terrain[][] terrains = new Terrain[TERRAIN_MATRIX_ROWS][TERRAIN_MATRIX_COLS];

        generateTerrains(TERRAIN_MATRIX_ROWS, TERRAIN_MATRIX_COLS, terrains, loader, texturePack, blendMap, "heightmap");
        generateLights(lights, terrains, entities, loader, 10);

        generateModel(pineTree, entities, terrains, 2, 500, 1);
        generateModel(grass, entities, terrains, 1, 500, 1);
        generateModel(fern, entities, terrains, 1, 500, 4);

        // Loop until the 'X' is clicked on the game window
        while(!Display.isCloseRequested()) {

            int gridX = (int) (camera.getPosition().x / Terrain.SIZE);
            int gridZ = (int) (camera.getPosition().z / Terrain.SIZE);

            // Collision detection with the terrains
            camera.move(terrains[gridX][gridZ]);

            for(int i = 0; i < TERRAIN_MATRIX_ROWS; i++) {
                for(int j = 0; j < TERRAIN_MATRIX_COLS; j++) {
                    renderer.processTerrain(terrains[i][j]);
                }
            }

            for(Entity object : entities) {
                renderer.processEntity(object);
            }

            renderer.render(lights, camera);

            // Step 2
            DisplayManager.updateDisplay();
        }

        renderer.cleanUp();
        loader.cleanUp();

        // When the game loop terminates, we want to do any clean up and close the window
        DisplayManager.closeDisplay();
    }
}
