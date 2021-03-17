import org.lwjgl.assimp.AIColor4D
import org.lwjgl.assimp.AIMaterial
import org.lwjgl.assimp.Assimp
import org.lwjgl.assimp.Assimp.*

fun extract_color_from_material(mat: AIMaterial, color_type: String): AIColor4D {
    return when (color_type) {
        AI_MATKEY_COLOR_AMBIENT, 
        AI_MATKEY_COLOR_DIFFUSE, 
        AI_MATKEY_COLOR_SPECULAR -> AIColor4D.create().also {
            if (aiGetMaterialColor(mat, color_type, aiTextureType_NONE, 0, it) != 0) {
                throw IllegalStateException(aiGetErrorString())
            }
        }
        else -> throw Exception("color_type $color_type is not valid material color Ka Kd Ks")
    }
}

class Material(material: AIMaterial) {
    val ambientColor: AIColor4D = extract_color_from_material(material, AI_MATKEY_COLOR_AMBIENT)
    val diffuseColor: AIColor4D = extract_color_from_material(material, AI_MATKEY_COLOR_DIFFUSE)
    val specularColor: AIColor4D = extract_color_from_material(material, AI_MATKEY_COLOR_SPECULAR)
}