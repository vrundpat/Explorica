package Tools;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Maths {

    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        Matrix4f matrix = new Matrix4f(); // Create a new 4x4 Matrix
        matrix.setIdentity(); // Setting its identity allows to it set certain properties via the singleton
        Matrix4f.translate(translation, matrix, matrix); // Translate the matrix based on the given transformation

        // Rotate the matrix based on the given rotation variables for all axes
        Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);

        // Scale the matrix based on the given scale
        Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
        return matrix;
    }
}
