package GameEngine;

import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import Models.TexturedModel;
import RenderEngine.*;
import Models.RawModel;
import Terrains.Terrain;
import Textures.ModelTexture;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainLoop {
    public static void main(String[] args) {

        // Create the Display
        DisplayManager.createDisplay();

        // Instantiate a loader
        Loader loader = new Loader();

        RawModel treeModel = OBJLoader.loadObjModel("tree", loader);
        RawModel grassModel = OBJLoader.loadObjModel("grassModel", loader);
        RawModel fernModel = OBJLoader.loadObjModel("fern", loader);


        TexturedModel tree = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("tree")));
        TexturedModel grass = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("grassTexture")));
        TexturedModel fern = new TexturedModel(fernModel, new ModelTexture(loader.loadTexture("fern")));

        grass.getTexture().setHasTransparency(true);
        fern.getTexture().setHasTransparency(true);

        grass.getTexture().setUseFakeLighting(true);
        fern.getTexture().setUseFakeLighting(true);

        Light light = new Light(new Vector3f(200, 200, 100), new Vector3f(1, 1, 1));
        Camera camera = new Camera();

        Terrain terrain = new Terrain(0, -1, loader, new ModelTexture(loader.loadTexture("grass")));
        Terrain terrain2 = new Terrain(-1, -1, loader, new ModelTexture(loader.loadTexture("grass")));

        MainRenderer renderer = new MainRenderer();

        List<Entity> entities = new ArrayList<>();
        List<Terrain> terrains = new ArrayList<>();

        Random random = new Random();
        for(int i=0;i<500;i++){
            entities.add(new Entity(tree, new Vector3f(random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,3));
            entities.add(new Entity(grass, new Vector3f(random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,1));
            entities.add(new Entity(fern, new Vector3f(random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,0.6f));
        }

        terrains.add(terrain);
        terrains.add(terrain2);

        // Loop until the 'X' is clicked on the game window
        while(!Display.isCloseRequested()) {

            // This Main Game Loop has 3 vital tasks:
            //  1. Impose Game Logic
            //  2. Update Game Entities
            //  3. Render updated entities
            camera.move();

            for(Entity object : entities) {
                renderer.processEntity(object);
            }

            for(Terrain floor : terrains) {
                renderer.processTerrain(floor);
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
