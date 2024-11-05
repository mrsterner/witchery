package dev.sterner.witchery.fabric.asm

import com.chocohead.mm.api.ClassTinkerers
import dev.sterner.witchery.Witchery
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.registries.BuiltInRegistries

object EarlyRiser : Runnable {
    override fun run() {
        val remapper = FabricLoader.getInstance().mappingResolver

        val boatType = remapper.mapClassName("intermediary", "net.minecraft.class_1690\$class_1692")
        val block = "L${remapper.mapClassName("intermediary", "net.minecraft.class_2248")};"
        ClassTinkerers.enumBuilder(boatType, block, "Ljava/lang/String;")
            .addEnum("WITCHERY_ROWAN") {
                arrayOf(BuiltInRegistries.BLOCK.get(Witchery.id("rowan_planks")), "rowan")
            }.addEnum("WITCHERY_ALDER") {
                arrayOf(BuiltInRegistries.BLOCK.get(Witchery.id("alder_planks")), "alder")
            }.addEnum("WITCHERY_HAWTHORN") {
                arrayOf(BuiltInRegistries.BLOCK.get(Witchery.id("hawthorn_planks")), "hawthorn")
            }.build()
    }
}