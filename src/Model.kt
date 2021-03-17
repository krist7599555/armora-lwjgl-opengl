import org.lwjgl.PointerBuffer
import org.lwjgl.assimp.*
import org.lwjgl.opengl.ARBShaderObjects
import org.lwjgl.opengl.ARBVertexBufferObject
import org.lwjgl.opengl.ARBVertexShader
import org.lwjgl.opengl.GL11

fun ptrbuffer_to_address(arr: PointerBuffer, size: Int): List<Long> {
    return 0.until(size).map { idx -> arr.get(idx) }
}

class Model(private val scene: AIScene) {

    var materials: List<Material> = ptrbuffer_to_address(scene.mMaterials()!!, scene.mNumMaterials())
        .map { addr -> Material(AIMaterial.create(addr)) }
        .toList()

    var meshes: List<Mesh>  = ptrbuffer_to_address(scene.mMeshes()!!, scene.mNumMeshes())
        .map { addr -> Mesh(AIMesh.create(addr)) }
        .toList()

    fun free() {
        Assimp.aiReleaseImport(scene)
    }

    companion object {
        fun from_path(path: String): Model {
            val scene = Assimp.aiImportFile(path, Assimp.aiProcess_JoinIdenticalVertices or Assimp.aiProcess_Triangulate)
                ?: throw IllegalStateException(Assimp.aiGetErrorString())
            return Model(scene)
        }
    }
}