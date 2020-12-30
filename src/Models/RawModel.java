package Models;

public class RawModel {
    private int vaoId;
    private int vertexCount;

    public RawModel(int vaoId, int vertexCount) {
        this.vaoId = vaoId; // Id into the VAO Array
        this.vertexCount = vertexCount; // Number of vertices in its mesh
    }

    // Getters
    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
