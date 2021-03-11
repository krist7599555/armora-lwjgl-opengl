import org.lwjgl.BufferUtils
import org.lwjgl.assimp.AIMesh
import org.lwjgl.assimp.AIVector3D
import org.lwjgl.opengl.ARBVertexBufferObject.*

class Mesh(
    val mesh: AIMesh,
    val vertexArrayBuffer: Int = glGenBuffersARB(),
    val normalArrayBuffer: Int = glGenBuffersARB(),
    val elementArrayBuffer: Int = glGenBuffersARB()
) {
    val elementCount: Int

    init {
        glBindBufferARB(GL_ARRAY_BUFFER_ARB, vertexArrayBuffer)
        mesh.mVertices()!!.also {
            nglBufferDataARB(GL_ARRAY_BUFFER_ARB, (AIVector3D.SIZEOF * it.remaining()).toLong(), it.address(), GL_STATIC_DRAW_ARB)
        }

        glBindBufferARB(GL_ARRAY_BUFFER_ARB, normalArrayBuffer)
        mesh.mNormals()!!.also {
            nglBufferDataARB(GL_ARRAY_BUFFER_ARB, (AIVector3D.SIZEOF * it.remaining()).toLong(), it.address(), GL_STATIC_DRAW_ARB)
        }

        elementCount = mesh.mNumFaces() * 3
        val elementArrayBufferData = BufferUtils
            .createIntBuffer(elementCount)
            .apply {
                0.until(mesh.mNumFaces())
                    .map { mesh.mFaces().get(it) }
                    .forEach { face ->
                        check(face.mNumIndices() == 3) { "AIFace.mNumIndices() != 3" }
                        this.put(face.mIndices())
                    }
                this.flip()
            }
        glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, elementArrayBuffer)
        glBufferDataARB(GL_ELEMENT_ARRAY_BUFFER_ARB, elementArrayBufferData, GL_STATIC_DRAW_ARB)
    }
}