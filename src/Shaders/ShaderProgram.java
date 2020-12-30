package Shaders;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

public abstract class ShaderProgram {

    private int programID;
    private int vertexShaderID;
    private int fragmentShaderID;

    // 4 by 4 transformation matrix
    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public ShaderProgram(String vertexFilePath, String fragmentFilePath) {
        // Load the vertex & program shaders
        vertexShaderID = loadShader(vertexFilePath, GL20.GL_VERTEX_SHADER);
        fragmentShaderID = loadShader(fragmentFilePath, GL20.GL_FRAGMENT_SHADER);
        programID = GL20.glCreateProgram(); // Create a shader program

        // Attach this generated program to both shaders & link + validate
        GL20.glAttachShader(programID, vertexShaderID);
        GL20.glAttachShader(programID, fragmentShaderID);

        bindAttributes(); // Bind all attributes before linking and validating the shader

        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);

        // Get the locations of all uniform variables in the shader code
        getAllUniformLocations();
    }

    protected abstract void getAllUniformLocations();

    protected int getUniformLocation(String uniformName) {
        return GL20.glGetUniformLocation(programID, uniformName);
    }

    public void start() {
        GL20.glUseProgram(programID);
    }

    public void stop() {
        GL20.glUseProgram(0);
    }

    public void cleanUp() {
        stop(); // Stop the program

        // Detach both shaders from the program
        GL20.glDetachShader(programID, vertexShaderID);
        GL20.glDetachShader(programID, fragmentShaderID);

        // Delete the shaders & the program
        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);
        GL20.glDeleteProgram(programID);
    }

    protected void bindAttribute(int attribute, String variableName) {
        // @param attribute: Index into the VAO
        // @param variableName: variable name in the shader text file
        GL20.glBindAttribLocation(programID, attribute, variableName);
    }

    protected void loadFloat(int location, float value) {
        GL20.glUniform1f(location, value); // Load a uniform float variable
    }

    protected void loadVector(int location, Vector3f vector) {
        // Load a uniform vector variable
        GL20.glUniform3f(location, vector.x, vector.y, vector.z);
    }

    protected void loadBoolean(int location, boolean value) {
        float toLoad = 0;
        if(value) {
            toLoad = 1;
        }
        GL20.glUniform1f(location, toLoad); // Load a uniform boolean variable
    }

    protected void loadMatrix(int location, Matrix4f matrix) {
        // Store the matrixBuffer into this Matrix4f matrix, flip it so it can be read
        matrix.store(matrixBuffer);
        matrixBuffer.flip();
        // Load a uniform Matrix4f variable
        GL20.glUniformMatrix4(location, false, matrixBuffer);
    }

    // Each shader will then bind its attributes (variable names in the text file) to
    // this shader and allow the GPU to manipulate them
    protected abstract void bindAttributes();

    private static int loadShader(String filePath, int type) {

        // Some File IO that reads the appends the contents of the text GSDL shader to a string
        StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("//\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        int shaderID = GL20.glCreateShader(type); // Create a shader
        GL20.glShaderSource(shaderID, shaderSource); // Give it the shader source (text file that was just read)
        GL20.glCompileShader(shaderID); // Compile the shader

        // Ensure no error occurred during compilation
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader!");
            System.exit(-1);
        }
        return shaderID;
    }
}
