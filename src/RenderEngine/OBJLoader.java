package RenderEngine;

import Models.RawModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/*
    An Object file parser to prevent hard coding of Models in the game. These object files have certain
    characteristics which make parsing a bit tedious and not very portable. Firstly, the lines on the .obj files
    are terminated with ASCII newline characters ('\n') and start with certain abbreviations that help identify
    a certain aspect of the end model.


    Note: Each of the following are preceded by an ASCII Space character and a series of floats
          which are also separated by ASCII Space characters

    v  = Vertex information  -> 3D Vector       ->     Eg. "v -100 -1.45 1.67"
    vt = Texture information -> 2D Vector       ->     Eg. "vt 100 200"
    vn = Normal information  -> 3D Vector       ->     Eg. "vn 0.22 0.44 0.66"
    f  = Face information    -> Hard to Explain ->     Eg. "f 100/75/23 200/75/23 300/75/33"

    The f (Face) is the most crucial piece of information in the file and requires a rather long process of parsing
    in order to do correctly. The Face information is followed by a series of numbers which are terminated by ASCII
    forward slash characters. Each letter in that sequence represents a vertex in the vertex information.
    Each set of numbers in that sequence represents a triangle in the model. The information in the form of:
    Vertex Index /Texture Index / Normal Index.

    Below is a lot of tedious and rather inefficient way of parsing an OBJ File, but as it only needs to be loaded
    once during the instantiation of the game, it has no impact on the performance.
 */
public class OBJLoader {

    public static RawModel loadObjModel(String filename, Loader loader) {
        FileReader fileReader = null;

        // Try to open the file
        try {
            fileReader = new FileReader(new File("src/Resources/" + filename + ".obj"));
        }
        catch (FileNotFoundException e) {
            System.err.println("An error occurred when loading Object File: " + filename);
            e.printStackTrace();
        }

        // Create a buffered reader for efficient reads from memory
        BufferedReader reader = new BufferedReader(fileReader);
        String line;

        // These lists will hold the vertices, textures coords, normals, and indices as they come from the OBJ File
        List<Vector3f> vertices  = new ArrayList<Vector3f>();
        List<Vector2f> textures  = new ArrayList<Vector2f>();
        List<Vector3f> normals   = new ArrayList<Vector3f>();
        List<Integer>  indices   = new ArrayList<Integer>();

        // These arrays will later be initialized to a fixed size and be used to create the final model
        float[] verticesArray = null;
        float[] texturesArray = null;
        float[] normalsArray  = null;
        int[] indicesArray    = null;

        // Using try/catch to help debug
        try {
            // Iterate until broken out of the loop
            while(true) {

                // Read the line and split it at the space
                line = reader.readLine();
                String[] currentLine = line.split(" ");

                // Vertex Position
                if(line.startsWith("v ")) {
                    // Pull vertex information
                    Vector3f vertex = new Vector3f( Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]),
                                                    Float.parseFloat(currentLine[3]));
                    vertices.add(vertex);
                }

                // Texture coordinate
                else if(line.startsWith("vt ")) {
                    // Pull texture coordinate information
                    Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]));
                    textures.add(texture);
                }

                // Normal
                else if(line.startsWith("vn ")) {
                    // Pull normal information
                    Vector3f normal = new Vector3f( Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]),
                                                    Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                }

                // Face
                // Once we reach the face (which are at the end of the file after the vertices, normals. and texture coords)
                // we can initialize our arrays and break out of the loop
                else if(line.startsWith("f ")) {
                    // When we reach here, we would've already gotten all of the vertices in the model
                    // so we use that information to initialize our arrays to a fixed size
                    texturesArray = new float[vertices.size() * 2]; // Texture arrays are 2D, so multiple by 2
                    normalsArray  = new float[vertices.size() * 3]; // Normals are 3D, so multiply by 3
                    break;
                }
            }

            // Now keep reading from the beginning of the face information
            while(line !=  null) {
                // If there is somehow a line that does not hold face information, skip to next iteration
                if(!line.startsWith("f ")) {
                    line = reader.readLine();
                    continue;
                }

                // Split "f 100/75/20" to ["f", "100/75/20", "200/65/20", "300/75/20"];
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/"); // Get first set of numbers
                String[] vertex2 = currentLine[2].split("/"); // 2nd set
                String[] vertex3 = currentLine[3].split("/"); // 3rd set

                // Process all 3 vertices
                processVertex(vertex1, indices, textures, normals, texturesArray, normalsArray);
                processVertex(vertex2, indices, textures, normals, texturesArray, normalsArray);
                processVertex(vertex3, indices, textures, normals, texturesArray, normalsArray);

                line = reader.readLine(); // Read the next line
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize our vertices and indices arrays
        verticesArray = new float[vertices.size() * 3]; // Vertices are 3D so multiply by 3
        indicesArray = new int[indices.size()];

        // Iterate over all vertices in the vertices list that we made and add each component
        // of that vertex to verticesArray
        int vertexPointer = 0;
        for(Vector3f vertex : vertices) {
            verticesArray[vertexPointer++] = vertex.x;
            verticesArray[vertexPointer++] = vertex.y;
            verticesArray[vertexPointer++] = vertex.z;
        }

        // Add all indices into indicesArray
        for(int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }

        // Now use the data from the OBJ File to load the model into a VAO for rendering
        return loader.loadToVAO(verticesArray, texturesArray, indicesArray);
    }


    private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures,
                                      List<Vector3f> normals, float[] texturesArray, float[] normalsArray) {

        // OBJ Files are indexed starting at 1, so we have to subtract 1
        int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPointer); // Add the index for the vertex data to our indicesArray

        // Parse texture information from the given vertexData
        Vector2f currentIndex = textures.get(Integer.parseInt(vertexData[1]) - 1);
        texturesArray[currentVertexPointer * 2]     = currentIndex.x;
        texturesArray[currentVertexPointer * 2 + 1] = 1 - currentIndex.y; // OBJ files start from bottom right, we start from top-left

        // Parse normal information from the given vertexData
        Vector3f currentNormal = normals.get(Integer.parseInt(vertexData[2]) - 1);
        normalsArray[currentVertexPointer * 3]     = currentNormal.x;
        normalsArray[currentVertexPointer * 3 + 1] = currentNormal.y;
        normalsArray[currentVertexPointer * 3 + 2] = currentNormal.z;
    }
}