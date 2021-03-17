import org.lwjgl.BufferUtils
import org.lwjgl.assimp.AIMesh
import org.lwjgl.assimp.AIVector3D
import org.lwjgl.opengl.ARBVertexBufferObject.*

fun to_arb_array_buffer(buffer: AIVector3D.Buffer): Int {
    return glGenBuffersARB().also {
        glBindBufferARB(GL_ARRAY_BUFFER_ARB, it)
        nglBufferDataARB(GL_ARRAY_BUFFER_ARB, (AIVector3D.SIZEOF * buffer.remaining()).toLong(), buffer.address(), GL_STATIC_DRAW_ARB)
    }
}

class Mesh(private val mesh: AIMesh) {
    val material_index = mesh.mMaterialIndex()
    val vertexArrayBuffer: Int = to_arb_array_buffer(mesh.mVertices()!!)
    val normalArrayBuffer: Int = to_arb_array_buffer(mesh.mNormals()!!)
    val elementArrayBuffer: Int = glGenBuffersARB()
    val elementCount: Int = mesh.mNumFaces() * 3

    init {
//        glBindBufferARB(GL_ARRAY_BUFFER_ARB, vertexArrayBuffer)
//        mesh.mVertices()!!.also {
//            nglBufferDataARB(GL_ARRAY_BUFFER_ARB, (AIVector3D.SIZEOF * it.remaining()).toLong(), it.address(), GL_STATIC_DRAW_ARB)
//        }
//
//        glBindBufferARB(GL_ARRAY_BUFFER_ARB, normalArrayBuffer)
//        mesh.mNormals()!!.also {
//            nglBufferDataARB(GL_ARRAY_BUFFER_ARB, (AIVector3D.SIZEOF * it.remaining()).toLong(), it.address(), GL_STATIC_DRAW_ARB)
//        }

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