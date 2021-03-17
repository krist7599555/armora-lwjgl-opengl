import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.ARBShaderObjects.*
import java.nio.FloatBuffer

class GlParam<T: Any> (val program: Int, val type: GlParam.Type, val name: String, var data: T) {
    enum class Type {
        UNIFORM,
        VERTEX_ATTRIBUTE
    }
    val address: Int
    private val capacity: Int = when (data) {
        is Matrix4f -> 16
        is Matrix3f -> 9
        is Vector3f -> 3
        else -> throw Exception("no matching type")
    }
    private val buffer: FloatBuffer
    init {
        address = when(type) {
            Type.UNIFORM -> glGetUniformLocationARB(program, name)
//            Type.VERTEX_ATTRIBUTE -> glGetAttribLocationARB(program, name).also { glEnableVertexAttribArrayARB(it) }
            else -> throw IllegalArgumentException("type $type not support")
        }
        buffer = BufferUtils.createFloatBuffer(capacity)
    }
    fun update() {
        when (data) {
            is Matrix4f -> (data as Matrix4f).get(buffer)
            is Matrix3f -> (data as Matrix3f).get(buffer)
            is Vector3f -> (data as Vector3f).get(buffer)
            else -> throw Exception("no matching type")
        }
    }
    fun render() {
        this.update()
        if (type == Type.UNIFORM) {
            when (data) {
                is Matrix4f -> glUniformMatrix4fvARB(address, false, buffer)
                is Matrix3f -> glUniformMatrix3fvARB(address, false, buffer)
                is Vector3f -> glUniform3fvARB(address, buffer)
                else -> throw Exception("no matching type")
            }
        } else {
            throw IllegalArgumentException("type $type not support")
        }
    }
}



fun main() {
    val program = 0
    val param = GlParam(program, GlParam.Type.UNIFORM, "uVertex", Matrix4f())
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