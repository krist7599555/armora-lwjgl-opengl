import org.joml.Matrix3f
import org.lwjgl.opengl.ARBShaderObjects
import org.lwjgl.opengl.ARBVertexBufferObject
import org.lwjgl.opengl.ARBVertexShader
import org.lwjgl.opengl.GL11
import java.nio.FloatBuffer

class ModelViewer(private var model: Model, private var engine: Demo) {

    fun render() {
        // setter buffer
//        engine.modelMatrix.get(engine.modelMatrixBuffer)
        engine.uModelMatrix.render()
//        engine.viewProjectionMatrix.get(engine.viewProjectionMatrixBuffer)
        engine.uViewProjectionMatrix.render()
//        engine.normalMatrix.set(engine.modelMatrix).invert().transpose()
        engine.uNormalMatrix.data.set(engine.uModelMatrix.data).invert().transpose()
        engine.uNormalMatrix.render()

//        engine.normalMatrix.get(engine.normalMatrixBuffer)

//        engine.lightPosition.get(engine.lightPositionBuffer)
        engine.uLightPosition.render()
//        engine.viewPosition.get(engine.viewPositionBuffer)
        engine.uViewPosition.render()

        for (mesh in model.meshes) {
            // mesh.vertexArrayBuffer -> aVertex
            ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, mesh.vertexArrayBuffer)
            ARBVertexShader.glVertexAttribPointerARB(engine.aVertex, 3, GL11.GL_FLOAT, false, 0, 0)
            // mesh.normalArrayBuffer -> aNormal
            ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, mesh.normalArrayBuffer)
            ARBVertexShader.glVertexAttribPointerARB(engine.aNormal, 3, GL11.GL_FLOAT, false, 0, 0)

            // engine.modelMatrixBuffer -> uModelMatrix
//            ARBShaderObjects.glUniformMatrix4fvARB(engine.uModelMatrix, false, engine.modelMatrixBuffer)
            engine.uModelMatrix.render()

            // engine.viewProjectionMatrixBuffer -> uViewProjectionMatrix
//            ARBShaderObjects.glUniformMatrix4fvARB(engine.uViewProjectionMatrix, false, engine.viewProjectionMatrixBuffer)
            engine.uViewProjectionMatrix.render()

//            ARBShaderObjects.glUniformMatrix3fvARB(engine.uNormalMatrix, false, engine.normalMatrixBuffer)
            engine.uNormalMatrix.render()
//            ARBShaderObjects.glUniform3fvARB(engine.uLightPosition, engine.lightPositionBuffer)
            engine.uLightPosition.render()
//            ARBShaderObjects.glUniform3fvARB(engine.uViewPosition, engine.viewPositionBuffer)
            engine.uViewPosition.render()

            model.materials[mesh.material_index].also { material ->
                ARBShaderObjects.nglUniform3fvARB(engine.uAmbientColor.address, 1, material.ambientColor.address())
                ARBShaderObjects.nglUniform3fvARB(engine.uDiffuseColor.address, 1, material.diffuseColor.address())
                ARBShaderObjects.nglUniform3fvARB(engine.uSpecularColor.address, 1, material.specularColor.address())
            }

            ARBVertexBufferObject.glBindBufferARB(
                ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB,
                mesh.elementArrayBuffer
            )
            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.elementCount, GL11.GL_UNSIGNED_INT, 0)
        }
    }
}