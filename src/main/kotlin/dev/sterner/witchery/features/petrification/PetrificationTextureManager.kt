package dev.sterner.witchery.features.petrification

import com.mojang.blaze3d.platform.NativeImage
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.mixin.client.HttpTextureAccessor
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.texture.HttpTexture
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.entity.LivingEntity
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.ConcurrentHashMap

object PetrificationTextureManager {

    private val STONE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/block/stone.png")
    private val textureCache = ConcurrentHashMap<String, ResourceLocation>()
    private val dynamicTextures = ConcurrentHashMap<ResourceLocation, DynamicTexture>()

    @Volatile
    private var currentEntity: LivingEntity? = null

    fun setCurrentEntity(entity: LivingEntity?) {
        currentEntity = entity
    }

    fun clearCurrentEntity() {
        currentEntity = null
    }

    fun getPetrifiedTexture(originalTexture: ResourceLocation): ResourceLocation {
        val progressKey = 1
        val cacheKey = "${originalTexture.namespace}:${originalTexture.path}_petrified_$progressKey"

        textureCache[cacheKey]?.let { return it }

        val petrifiedTexture = generatePetrifiedTexture(originalTexture)
        textureCache[cacheKey] = petrifiedTexture
        return petrifiedTexture
    }

    private fun loadNativeImage(
        resourceManager: ResourceManager,
        location: ResourceLocation
    ): NativeImage? {
        val resource = resourceManager.getResource(location).orElse(null) ?: return null
        return resource.open().use { inputStream ->
            NativeImage.read(inputStream)
        }
    }

    fun clearCache() {
        dynamicTextures.values.forEach { it.close() }
        dynamicTextures.clear()
        textureCache.clear()
    }

    fun clearOldTextures(maxAge: Long = 300000) {
        val currentTime = System.currentTimeMillis()
        val toRemove = mutableListOf<ResourceLocation>()

        dynamicTextures.forEach { (location, _) ->
            val timestamp = location.path.substringAfter("dynamic/petrified/")
                .substringBefore("_")
                .toLongOrNull() ?: 0L

            if (currentTime - timestamp > maxAge) {
                toRemove.add(location)
            }
        }

        toRemove.forEach { location ->
            dynamicTextures.remove(location)?.close()
            textureCache.entries.removeIf { it.value == location }
        }
    }

    //PLAYER NONSENSE

    fun loadPlayerSkinAsNativeImage(skin: HttpTexture): NativeImage? {
        try {
            val cacheFile: File? = (skin as HttpTextureAccessor).file

            val nativeImage: NativeImage? = if (cacheFile != null && cacheFile.isFile) {
                NativeImage.read(FileInputStream(cacheFile))
            } else {
                null
            }

            return nativeImage
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun copyNativeImage(image: NativeImage): NativeImage {
        val copy = NativeImage(image.format(), image.width, image.height, false)
        copy.copyFrom(image)
        return copy
    }

    private fun extractNativeImageFromTextureManager(textureManager: TextureManager, resLoc: ResourceLocation): NativeImage? {
        val tex: AbstractTexture? = textureManager.getTexture(resLoc)

        return when (tex) {
            is DynamicTexture -> tex.pixels?.let { copyNativeImage(it) }
            is HttpTexture -> loadPlayerSkinAsNativeImage(tex)
            else -> null
        }
    }

    data class Size(var width: Int, var height: Int){

    }

    fun getTextureSize(originalTexture: ResourceLocation): Size {
        val minecraft = Minecraft.getInstance()
        val resourceManager = minecraft.resourceManager

        val originalImage: NativeImage? = loadNativeImage(resourceManager, originalTexture)
        return Size(originalImage!!.width, originalImage.height)
    }

    private fun generatePetrifiedTexture(originalTexture: ResourceLocation): ResourceLocation {
        val minecraft = Minecraft.getInstance()
        val textureManager = minecraft.textureManager
        val resourceManager = minecraft.resourceManager

        var originalImage: NativeImage? = loadNativeImage(resourceManager, originalTexture)

        if (originalImage == null) {
            originalImage = extractNativeImageFromTextureManager(textureManager, originalTexture)
        }

        if (originalImage == null) {
            return originalTexture
        }

        val stoneImage = loadNativeImage(resourceManager, STONE_TEXTURE)
            ?: run {
                originalImage.close()
                return originalTexture
            }

        val width = originalImage.width
        val height = originalImage.height
        val resultImage = NativeImage(width, height, true)

        try {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val originalPixel = originalImage.getPixelRGBA(x, y)
                    val alpha = (originalPixel shr 24) and 0xFF
                    if (alpha == 0) {
                        resultImage.setPixelRGBA(x, y, 0)
                        continue
                    }

                    val red = (originalPixel shr 16) and 0xFF
                    val green = (originalPixel shr 8) and 0xFF
                    val blue = originalPixel and 0xFF

                    val gray = (0.299f * red + 0.587f * green + 0.114f * blue).toInt()

                    val stoneX = x % stoneImage.width
                    val stoneY = y % stoneImage.height
                    val stonePixel = stoneImage.getPixelRGBA(stoneX, stoneY)

                    val stoneRed = (stonePixel shr 16) and 0xFF
                    val stoneGreen = (stonePixel shr 8) and 0xFF
                    val stoneBlue = stonePixel and 0xFF

                    val blendFactor = 0.5f

                    val grayProgress = 1f - blendFactor
                    val stoneProgress = blendFactor

                    val finalRed = ((gray * grayProgress + stoneRed * stoneProgress) / (grayProgress + stoneProgress)).toInt().coerceIn(0, 255)
                    val finalGreen = ((gray * grayProgress + stoneGreen * stoneProgress) / (grayProgress + stoneProgress)).toInt().coerceIn(0, 255)
                    val finalBlue = ((gray * grayProgress + stoneBlue * stoneProgress) / (grayProgress + stoneProgress)).toInt().coerceIn(0, 255)


                    val darkenFactor = 0.85f + (0.15f * (1.0f - 1f))
                    val finalR = (finalRed * darkenFactor).toInt().coerceIn(0, 255)
                    val finalG = (finalGreen * darkenFactor).toInt().coerceIn(0, 255)
                    val finalB = (finalBlue * darkenFactor).toInt().coerceIn(0, 255)

                    val finalPixel = (alpha shl 24) or (finalR shl 16) or (finalG shl 8) or finalB
                    resultImage.setPixelRGBA(x, y, finalPixel)
                }
            }

            val dynamicTexture = DynamicTexture(resultImage)
            val textureLocation = Witchery.id("dynamic/petrified/${System.currentTimeMillis()}_${originalTexture.path.hashCode()}")

            textureManager.register(textureLocation, dynamicTexture)
            dynamicTextures[textureLocation] = dynamicTexture

            return textureLocation

        } finally {
            originalImage.close()
            stoneImage.close()
        }
    }

}