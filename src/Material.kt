import org.lwjgl.assimp.AIColor4D
import org.lwjgl.assimp.AIMaterial
import org.lwjgl.assimp.Assimp
import org.lwjgl.assimp.Assimp.*

class Material(
    val material: AIMaterial,
    val ambientColor: AIColor4D = AIColor4D.create(),
    val diffuseColor: AIColor4D = AIColor4D.create(),
    val specularColor: AIColor4D = AIColor4D.create(),
) {
    init {
        if (aiGetMaterialColor(material, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, ambientColor) != 0) throw IllegalStateException(aiGetErrorString());
        if (aiGetMaterialColor(material, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, diffuseColor) != 0) throw IllegalStateException(aiGetErrorString());
        if (aiGetMaterialColor(material, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, specularColor) != 0) throw IllegalStateException(aiGetErrorString());
    }
}