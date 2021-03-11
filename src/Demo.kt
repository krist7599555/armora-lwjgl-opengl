import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.*
import org.lwjgl.opengl.ARBShaderObjects.*
import org.lwjgl.opengl.ARBVertexShader.*
import org.lwjgl.opengl.ARBFragmentShader.*
import org.lwjgl.opengl.ARBVertexBufferObject.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.Callback
import org.lwjgl.system.MemoryStack
import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.assertNotEquals

class Demo {
    var width = 300
    var height = 300
    var window: Long = 0

    // float buffer - for set display screen size with right resolution
    var fbWidth = width // * scale
    var fbHeight = height // * scale
    var fov = 60f
    var rotation = 0f

    var program = 0

    var vertexAttribute: Int
    var normalAttribute: Int
    var modelMatrixUniform: Int
    var viewProjectionMatrixUniform: Int
    var normalMatrixUniform: Int
    var lightPositionUniform: Int
    var viewPositionUniform: Int
    var ambientColorUniform: Int
    var diffuseColorUniform: Int
    var specularColorUniform: Int

    var model: Model

    var modelMatrix = Matrix4f().rotateY(0.5f * Math.PI.toFloat()).scale(1.5f, 1.5f, 1.5f)!!
    var viewMatrix = Matrix4f()
    var projectionMatrix = Matrix4f()
    var viewProjectionMatrix = Matrix4f()
    var viewPosition = Vector3f()
    var lightPosition = Vector3f(-5f, 5f, 5f)

    private val modelMatrixBuffer = BufferUtils.createFloatBuffer(4 * 4)
    private val viewProjectionMatrixBuffer = BufferUtils.createFloatBuffer(4 * 4)
    private val normalMatrix = Matrix3f()
    private val normalMatrixBuffer = BufferUtils.createFloatBuffer(3 * 3)
    private val lightPositionBuffer = BufferUtils.createFloatBuffer(3)
    private val viewPositionBuffer = BufferUtils.createFloatBuffer(3)

    private var caps: GLCapabilities
    var debugProc: Callback? = null

    init {
        check(glfwInit()) { "Unable to initialize GLFW" }
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        /* Create Window */
        window = glfwCreateWindow(width, height, "ARMORA", 0, 0)

        /* Apply Callback */
        window.also {
            assertNotEquals(0, it,"Failed to create the GLFW window")
            // LISTEN EVENT - RESIZE WINDOW
            glfwSetFramebufferSizeCallback(it) { _, w, h ->
                println("frame buffer size callback [${w}, ${h}]")
                fbWidth = w
                fbHeight = h
            }
            glfwSetWindowSizeCallback(it) { _, w, h ->
                println("windo buffer size callback [${w}, ${h}]")
                width = w
                height = h
            }
            // LISTEN EVENT - KEY PRESS
            glfwSetKeyCallback(it) { _, key, _, action, _ ->
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(it, true)
                }
            }
            // LISTEN EVENT - MOUSE MOVE
            glfwSetCursorPosCallback(it) { _, x, y ->
                rotation = (x.toFloat() / width - 0.5f) * 2f * Math.PI.toFloat()
            }
            // LISTEN EVENT - MOUSE SCROLL
            glfwSetScrollCallback(it) { _, _, yoffset ->
                fov *= if (yoffset < 0) 1.05f else 1f / 1.05f
                fov = fov.coerceIn(10.0f, 120.0f)
            }
        }

        glfwMakeContextCurrent(window) // Make the OpenGL context current
        glfwSwapInterval(0) // Enable v-sync
        glfwSetCursorPos(window, (width / 2).toDouble(), (height / 2).toDouble()) // mouse position

        MemoryStack.stackPush().use { stack ->
            val screenMonitor = glfwGetVideoMode(glfwGetPrimaryMonitor())!! // Get the resolution of the primary monitor
            // Center the window
            glfwSetWindowPos(window, (screenMonitor.width() - width) / 2, (screenMonitor.height() - height) / 2)

            val scaleWidth = stack.mallocFloat(1) // float*
            val scaleHeight = stack.mallocFloat(1) // float*
            glfwGetWindowContentScale(window, scaleWidth, scaleHeight) // screen resolution

            fbWidth = (width * scaleWidth[0]).toInt()
            fbHeight = (width * scaleHeight[0]).toInt()
        } // the stack frame is popped automatically

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        caps = GL.createCapabilities().also {
            check(it.GL_ARB_shader_objects) { "This demo requires the ARB_shader_objects extension." }
            check(it.GL_ARB_vertex_shader) { "This demo requires the ARB_vertex_shader extension." }
            check(it.GL_ARB_fragment_shader) { "This demo requires the ARB_fragment_shader extension." }
        }
        debugProc = GLUtil.setupDebugMessageCallback()

        glClearColor(0f, 0f, 0f, 1f)
        glEnable(GL_DEPTH_TEST)

        /* Create all needed GL resources */
        model = Model.from_path("res/booster-arrow.obj")
//        model = Model.from_path("res/magnet.obj")

        /* Create Program */
        program = glCreateProgramObjectARB().also { prog ->
            glAttachObjectARB(prog, create_shader("res/magnet.vs.glsl", GL_VERTEX_SHADER_ARB))
            glAttachObjectARB(prog, create_shader("res/magnet.fs.glsl", GL_FRAGMENT_SHADER_ARB))
            glLinkProgramARB(prog)
            val linkStatus = glGetObjectParameteriARB(prog, GL_OBJECT_LINK_STATUS_ARB)
            val programLog = glGetInfoLogARB(prog)
            if (programLog.trim().isNotEmpty()) {
                System.err.println(programLog)
            }
            assertNotEquals(0, linkStatus, "Could not link program")
            glUseProgramObjectARB(prog)
        }

        /* Set Program Variable */
        vertexAttribute = glGetAttribLocationARB(program, "aVertex").also { glEnableVertexAttribArrayARB(it) }
        normalAttribute = glGetAttribLocationARB(program, "aNormal").also { glEnableVertexAttribArrayARB(it) }
        modelMatrixUniform   = glGetUniformLocationARB(program, "uModelMatrix")
        viewProjectionMatrixUniform = glGetUniformLocationARB(program, "uViewProjectionMatrix")
        normalMatrixUniform  = glGetUniformLocationARB(program, "uNormalMatrix")
        lightPositionUniform = glGetUniformLocationARB(program, "uLightPosition")
        viewPositionUniform  = glGetUniformLocationARB(program, "uViewPosition")
        ambientColorUniform  = glGetUniformLocationARB(program, "uAmbientColor")
        diffuseColorUniform  = glGetUniformLocationARB(program, "uDiffuseColor")
        specularColorUniform = glGetUniformLocationARB(program, "uSpecularColor")

        /* Show window */
        glfwShowWindow(window)
    }

    private fun update() {
        projectionMatrix.setPerspective(Math.toRadians(fov.toDouble()).toFloat(), width.toFloat() / height, 0.01f, 100.0f)
        viewPosition[10f * cos(rotation.toDouble()).toFloat(), 10f] = 10f * sin(rotation.toDouble()).toFloat()
        viewMatrix.setLookAt(viewPosition.x, viewPosition.y, viewPosition.z, 0f, 0f, 0f, 0f, 1f, 0f)
        projectionMatrix.mul(viewMatrix, viewProjectionMatrix)
    }

    private fun render() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glUseProgramObjectARB(program)
        for (mesh in model.meshes) {
            glBindBufferARB(GL_ARRAY_BUFFER_ARB, mesh.vertexArrayBuffer)
            glVertexAttribPointerARB(vertexAttribute, 3, GL_FLOAT, false, 0, 0)
            glBindBufferARB(GL_ARRAY_BUFFER_ARB, mesh.normalArrayBuffer)
            glVertexAttribPointerARB(normalAttribute, 3, GL_FLOAT, false, 0, 0)

            glUniformMatrix4fvARB(modelMatrixUniform, false, modelMatrix[modelMatrixBuffer])
            glUniformMatrix4fvARB(viewProjectionMatrixUniform, false, viewProjectionMatrix[viewProjectionMatrixBuffer])
            normalMatrix.set(modelMatrix).invert().transpose()
            glUniformMatrix3fvARB(normalMatrixUniform, false, normalMatrix[normalMatrixBuffer])
            glUniform3fvARB(lightPositionUniform, lightPosition[lightPositionBuffer])
            glUniform3fvARB(viewPositionUniform, viewPosition[viewPositionBuffer])

            model.materials[mesh.mesh.mMaterialIndex()].also { material ->
                nglUniform3fvARB(ambientColorUniform, 1, material.ambientColor.address())
                nglUniform3fvARB(diffuseColorUniform, 1, material.diffuseColor.address())
                nglUniform3fvARB(specularColorUniform, 1, material.specularColor.address())
            }

            glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, mesh.elementArrayBuffer)
            glDrawElements(GL_TRIANGLES, mesh.elementCount, GL_UNSIGNED_INT, 0)
        }
    }

    fun run() {
        try {
            while (!glfwWindowShouldClose(window)) {
                glfwPollEvents()
                glViewport(0, 0, fbWidth, fbHeight)
                update()
                render()
                glfwSwapBuffers(window)
            }
            GL.setCapabilities(null)
            model.free()
            debugProc?.free()
            Callbacks.glfwFreeCallbacks(window)
            glfwDestroyWindow(window)
        } catch (t: Throwable) {
            t.printStackTrace()
        } finally {
            glfwTerminate()
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Demo().run()
        }
    }
}