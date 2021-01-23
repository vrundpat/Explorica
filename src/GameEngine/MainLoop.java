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

    public static void main(String[] args) {

        // Create the Display
        DisplayManager.createDisplay();

        // Instantiate a loader
        Loader loader = new Loader();

        // Load up the terrain texture pack
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        RawModel treeModel = OBJLoader.loadObjModel("tree", loader);
        RawModel grassModel = OBJLoader.loadObjModel("grassModel", loader);
        RawModel fernModel = OBJLoader.loadObjModel("fern", loader);
        RawModel pineTreeModel = OBJLoader.loadObjModel("pine", loader);

        // Texture Atlases
        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern")); fernTextureAtlas.setNumberOfRows(2);

        TexturedModel tree = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("tree")));
        TexturedModel grass = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("grassTexture")));
        TexturedModel fern = new TexturedModel(fernModel, new ModelTexture(loader.loadTexture("fern")));
        TexturedModel pinkFlower = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("flower")));
        TexturedModel pineTree = new TexturedModel(pineTreeModel, new ModelTexture(loader.loadTexture("pine")));

        grass.getTexture().setHasTransparency(true);
        fern.getTexture().setHasTransparency(true);
        pinkFlower.getTexture().setHasTransparency(true);

        grass.getTexture().setUseFakeLighting(true);
        fern.getTexture().setUseFakeLighting(true);
        pinkFlower.getTexture().setUseFakeLighting(true);

        Light light = new Light(new Vector3f(200, 500, 100), new Vector3f(1, 1, 1));
        Camera camera = new Camera();

        Terrain terrain = new Terrain(0, 0, loader, texturePack, blendMap, "heightmap");
        Terrain terrain2 = new Terrain(0, 1, loader, texturePack, blendMap, "heightmap");

        MainRenderer renderer = new MainRenderer();

        List<Entity> entities = new ArrayList<>();
        Terrain[][] terrains = new Terrain[1][2];

        terrains[0][0] = terrain;
        terrains[0][1] = terrain2;

        generateModel(pineTree, entities, terrains, 3, 500, 1);
        generateModel(tree, entities, terrains, 3, 500, 1);
        generateModel(grass, entities, terrains, 1, 500, 1);
        generateModel(fern, entities, terrains, 1, 500, 4);

        // Loop until the 'X' is clicked on the game window
        while(!Display.isCloseRequested()) {

            int gridX = (int) (camera.getPosition().x / Terrain.SIZE);
            int gridZ = (int) (camera.getPosition().z / Terrain.SIZE);
            // This Main Game Loop has 3 vital tasks:
            //  1. Impose Game Logic
            //  2. Update Game Entities
            //  3. Render updated entities
            camera.move(terrains[gridX][gridZ]);

            renderer.processTerrain(terrains[0][0]);
            renderer.processTerrain(terrains[0][1]);

            for(Entity object : entities) {
                renderer.processEntity(object);
            }

            renderer.render(light, camera);

            // Step 2
            DisplayManager.updateDisplay();
        }

        renderer.cleanUp();
        loader.cleanUp();

        // When the game loop terminates, we want to do any clean up and close the window
        DisplayManager.closeDisplay();
    }
}
