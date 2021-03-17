//import org.joml.Matrix4f
//import org.joml.Vector3f
//import org.lwjgl.assimp.*
//import org.lwjgl.opengl.GL11.*
//import org.lwjgl.glfw.GLFW.*;
//import org.lwjgl.opengl.*
//
//var app_window: Long = 0;
//var app_width = 800;
//var app_height = 600;
//
//fun init_window() {
//    if (!glfwInit()) {
//        throw IllegalStateException("Unable to initialize GLFW");
//    }
//
//    glfwDefaultWindowHints();
//    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
//    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
//    app_window = glfwCreateWindow(app_width, app_height, "Wavefront obj model loading with Assimp demo", 0, 0)!!;
//
//    println("Move the mouse to look around");
//    println("Zoom in/out with mouse wheel");
////    glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
////        if (width > 0 && height > 0 && (fbWidth != width || fbHeight != height)) {
////            fbWidth = width;
////            fbHeight = height;
////        }
////    });
////    glfwSetWindowSizeCallback(window, (window, width, height) -> {
////        if (width > 0 && height > 0 && (this.width != width || this.height != height)) {
////            this.width = width;
////            this.height = height;
////        }
////    });
////    glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
////        if (action != GLFW_RELEASE) {
////            return;
////        }
////        if (key == GLFW_KEY_ESCAPE) {
////            glfwSetWindowShouldClose(window, true);
////        }
////    });
////    glfwSetCursorPosCallback(window, (window, x, y) -> {
////        rotation = ((float)x / width - 0.5f) * 2f * (float)Math.PI;
////    });
////    glfwSetScrollCallback(window, (window, xoffset, yoffset) -> {
////        if (yoffset < 0) {
////            fov *= 1.05f;
////        } else {
////            fov *= 1f / 1.05f;
////        }
////        if (fov < 10.0f) {
////            fov = 10.0f;
////        } else if (fov > 120.0f) {
////            fov = 120.0f;
////        }
////    });
//
//    val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())!!;
////    glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
//    glfwMakeContextCurrent(app_window);
//    glfwSwapInterval(0);
////    glfwSetCursorPos(window, width / 2, height / 2);
//
////    try (MemoryStack frame = MemoryStack.stackPush()) {
////        IntBuffer framebufferSize = frame.mallocInt(2);
////        nglfwGetFramebufferSize(window, memAddress(framebufferSize), memAddress(framebufferSize) + 4);
////        width = framebufferSize.get(0);
////        height = framebufferSize.get(1);
////    }
//
//    val caps = GL.createCapabilities();
//    if (!caps.GL_ARB_shader_objects) {
//        throw AssertionError("This demo requires the ARB_shader_objects extension.");
//    }
//    if (!caps.GL_ARB_vertex_shader) {
//        throw AssertionError("This demo requires the ARB_vertex_shader extension.");
//    }
//    if (!caps.GL_ARB_fragment_shader) {
//        throw AssertionError("This demo requires the ARB_fragment_shader extension.");
//    }
//    val debugProc = GLUtil.setupDebugMessageCallback();
//
//    glClearColor(0f, 0f, 0f, 1f);
//    glEnable(GL_DEPTH_TEST);
//
//    /* Create all needed GL resources */
////    loadModel();
////    createProgram();
//
//    /* Show window */
//    glfwShowWindow(app_window);
//}
//
//var modelMatrix = Matrix4f().rotateY(0.5f * Math.PI.toFloat()).scale(1.5f, 1.5f, 1.5f)
//var viewMatrix = Matrix4f()
//var projectionMatrix = Matrix4f()
//var viewProjectionMatrix = Matrix4f()
//var viewPosition = Vector3f()
//var lightPosition = Vector3f(-5f, 5f, 5f)
//var fov = 60f
//var rotation = 0f
//
//fun update() {
//    projectionMatrix.setPerspective(
//        Math.toRadians(fov.toDouble()).toFloat(), app_width as Float / app_height, 0.01f,
//        100.0f
//    )
//    viewPosition.set(
//        10f * Math.cos(rotation.toDouble()).toFloat(), 10f, 10f * Math.sin(rotation.toDouble())
//            .toFloat()
//    )
//    viewMatrix.setLookAt(
//        viewPosition.x, viewPosition.y, viewPosition.z, 0f, 0f, 0f, 0f, 1f,
//        0f
//    )
//    projectionMatrix.mul(viewMatrix, viewProjectionMatrix)
//}
//
//fun render() {
////    glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
////    ARBShaderObjects.glUseProgramObjectARB(program)
////    for (mesh in model.meshes) {
////        ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, mesh.vertexArrayBuffer)
////        ARBVertexShader.glVertexAttribPointerARB(vertexAttribute, 3, GL_FLOAT, false, 0, 0)
////        ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, mesh.normalArrayBuffer)
////        ARBVertexShader.glVertexAttribPointerARB(normalAttribute, 3, GL_FLOAT, false, 0, 0)
////        ARBShaderObjects.glUniformMatrix4fvARB(modelMatrixUniform, false, modelMatrix.get(modelMatrixBuffer))
////        ARBShaderObjects.glUniformMatrix4fvARB(
////            viewProjectionMatrixUniform, false,
////            viewProjectionMatrix.get(viewProjectionMatrixBuffer)
////        )
////        normalMatrix.set(modelMatrix).invert().transpose()
////        ARBShaderObjects.glUniformMatrix3fvARB(normalMatrixUniform, false, normalMatrix.get(normalMatrixBuffer))
////        ARBShaderObjects.glUniform3fvARB(lightPositionUniform, lightPosition.get(lightPositionBuffer))
////        ARBShaderObjects.glUniform3fvARB(viewPositionUniform, viewPosition.get(viewPositionBuffer))
////        val material: WavefrontObjDemo.Model.Material = model.materials.get(mesh.mesh.mMaterialIndex())
////        ARBShaderObjects.nglUniform3fvARB(ambientColorUniform, 1, material.mAmbientColor.address())
////        ARBShaderObjects.nglUniform3fvARB(diffuseColorUniform, 1, material.mDiffuseColor.address())
////        ARBShaderObjects.nglUniform3fvARB(specularColorUniform, 1, material.mSpecularColor.address())
////        ARBVertexBufferObject.glBindBufferARB(
////            ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB,
////            mesh.elementArrayBuffer
////        )
////        glDrawElements(GL_TRIANGLES, mesh.elementCount, GL_UNSIGNED_INT, 0)
////    }
//}
//
//fun loop() {
//    while (!glfwWindowShouldClose(app_window)) {
//        glfwPollEvents();
//        glViewport(0, 0, app_width, app_height);
////        update();
////        render();
//        glfwSwapBuffers(app_window);
//    }
//    GL.setCapabilities(null);
//}
//
//fun main(args: Array<String>) {
//    val aiScene = Assimp.aiImportFile("res/magnet.obj", Assimp.aiProcess_JoinIdenticalVertices or Assimp.aiProcess_Triangulate)!!
//    for (i in 0 until aiScene.mNumMaterials()) {
//        println(Material(AIMaterial.create(aiScene.mMaterials()?.get(i)!!)))
//    }
//    try {
//        init_window()
//        loop();
////        glfwFreeCallbacks(window);
////        glfwDestroyWindow(window);
//    } catch (t: Throwable) {
//        t.printStackTrace();
//    } finally {
//        glfwTerminate();
//    }
//}
