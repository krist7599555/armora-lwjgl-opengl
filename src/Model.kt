import org.lwjgl.assimp.*
import java.util.ArrayList

class Model(var scene: AIScene) {

    var materials: MutableList<Material> = scene.mMaterials()!!.let { materialsBuffer ->
        0.until(scene.mNumMaterials())
            .map { AIMaterial.create(materialsBuffer[it]) }
            .map { Material(it) }
            .toMutableList()
    }

    var meshes: MutableList<Mesh> = scene.mMeshes()!!.let { meshesBuffer ->
        0.until(scene.mNumMeshes())
            .map { AIMesh.create(meshesBuffer[it]) }
            .map { Mesh(it) }
            .toMutableList()
    }

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