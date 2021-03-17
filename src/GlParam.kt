import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.ARBShaderObjects.*
import org.lwjgl.opengl.ARBVertexBufferObject.*
import org.lwjgl.opengl.ARBVertexShader.*
import org.lwjgl.opengl.GL11
import java.nio.FloatBuffer

class GlParam<T: Matrix4f> (val program: Int, val type: GlParam.Type, val name: String, var data: T, val size: Int) {
    enum class Type {
        UNIFORM,
        VERTEX_ATTRIBUTE
    }
    private val address: Int
    private val buffer: FloatBuffer
    init {
        address = when(type) {
            Type.UNIFORM -> glGetUniformLocationARB(program, name)
//            Type.VERTEX_ATTRIBUTE -> glGetAttribLocationARB(program, name).also { glEnableVertexAttribArrayARB(it) }
            else -> throw IllegalArgumentException("type $type not support")
        }
        buffer = BufferUtils.createFloatBuffer(size)
    }
    fun update() {
        data.get(buffer);
        when(type) {
            Type.UNIFORM -> glUniformMatrix4fvARB(address, false, buffer)
            else -> throw IllegalArgumentException("type $type not support")
//            Type.VERTEX_ATTRIBUTE -> {
//                glBindBufferARB(GL_ARRAY_BUFFER_ARB, buffer.address)
//                glVertexAttribPointerARB(address, 3, GL11.GL_FLOAT, false, 0, 0)
//            }
        }

    }
}


fun main() {
    val program = 0
    val param = GlParam(program, GlParam.Type.UNIFORM, "uVertex", Matrix4f(), 16)
//    Vector3f()
//    ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, mesh.vertexArrayBuffer)
//    ARBVertexShader.glVertexAttribPointerARB(engine.aVertex, 3, GL11.GL_FLOAT, false, 0, 0)
//    // mesh.normalArrayBuffer -> aNormal
//    ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, mesh.normalArrayBuffer)
//    ARBVertexShader.glVertexAttribPointerARB(engine.aNormal, 3, GL11.GL_FLOAT, false, 0, 0)
//
//    // engine.modelMatrixBuffer -> uModelMatrix
//    ARBShaderObjects.glUniformMatrix4fvARB(engine.uModelMatrix, false, engine.modelMatrixBuffer)
//
//    // engine.viewProjectionMatrixBuffer -> uViewProjectionMatrix
//    ARBShaderObjects.glUniformMatrix4fvARB(engine.uViewProjectionMatrix, false, engine.viewProjectionMatrixBuffer)
//
//    ARBShaderObjects.glUniformMatrix3fvARB(engine.uNormalMatrix, false, engine.normalMatrixBuffer)
//    ARBShaderObjects.glUniform3fvARB(engine.uLightPosition, engine.lightPositionBuffer)
//    ARBShaderObjects.glUniform3fvARB(engine.uViewPosition, engine.viewPositionBuffer)
}