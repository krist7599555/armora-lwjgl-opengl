import armora.IOUtil
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.ARBShaderObjects
import org.lwjgl.opengl.ARBShaderObjects.*
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

fun read_file(path: String): String {
    val path = Paths.get("res/magnet.obj")
    var file = Files
        .readAllLines(path)
        .joinToString("\n")
    return file
}

fun read_file_to_byte_buffer(resource_path: String): ByteBuffer {
    val path = Paths.get(resource_path)
    val fc = Files.newByteChannel(path)
    var buffer = BufferUtils.createByteBuffer(fc.size().toInt() + 1)
    while (fc.read(buffer) != -1) {
        println("read != -1")
    }
    buffer.flip()
    return buffer
}


fun create_shader(resource: String, type: Int): Int {
    val shader = glCreateShaderObjectARB(type)
    val source = IOUtil.ioResourceToByteBuffer(resource, 1024)
    val strings = BufferUtils.createPointerBuffer(1);
    strings.put(0, source)
    val lengths = BufferUtils.createIntBuffer(1)
    lengths.put(0, source.remaining())
    glShaderSourceARB(shader, strings, lengths)
    glCompileShaderARB(shader)
    val compiled = glGetObjectParameteriARB(shader, GL_OBJECT_COMPILE_STATUS_ARB)
    val shaderLog = glGetInfoLogARB(shader)
    if (shaderLog.trim().isNotEmpty()) {
        System.err.println(shaderLog)
    }
    assertNotEquals(0, compiled, "Could not compile shader");
    return shader
}