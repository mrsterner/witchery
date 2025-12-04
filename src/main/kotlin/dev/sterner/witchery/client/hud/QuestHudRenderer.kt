package dev.sterner.witchery.client.hud

import com.mojang.blaze3d.systems.RenderSystem
import dev.sterner.witchery.core.registry.WitcheryKeyMappings
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.features.affliction.AfflictionTypes
import dev.sterner.witchery.features.affliction.lich.LichdomLeveling
import dev.sterner.witchery.features.affliction.vampire.VampireLeveling
import dev.sterner.witchery.features.affliction.werewolf.WerewolfLeveling
import dev.sterner.witchery.features.blood.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.features.misc.HudPlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Player
import kotlin.math.sin

object QuestHudRenderer {

    var isVisible = false
    private var expandAnimation = 0f
    private val questCompletionAnimations = mutableMapOf<String, QuestAnimation>()
    private var lastCompletedQuests = setOf<String>()
    private val questStates = mutableMapOf<String, QuestState>()

    private const val ANIMATION_SPEED = 0.15f
    private const val TITLE_COLOR = 0xFFD700
    private const val QUEST_COLOR = 0xE0E0E0
    private const val COMPLETE_COLOR = 0x40FF40

    data class QuestAnimation(
        var progress: Float = 0f,
        var bounceOffset: Float = 0f,
        var alpha: Float = 1f
    )

    data class QuestState(
        val id: String,
        val description: String,
        val current: Int,
        val max: Int,
        val isComplete: Boolean
    )

    fun toggle() {
        isVisible = !isVisible

        if (isVisible) {
            Minecraft.getInstance().soundManager.play(
                SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.2f)
            )
        } else {
            Minecraft.getInstance().soundManager.play(
                SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 0.8f)
            )
        }
    }

    fun tick() {
        val minecraft = Minecraft.getInstance()
        val player = minecraft.player ?: return

        val data = AfflictionPlayerAttachment.getData(player)
        if ((data.getWerewolfLevel() == 0 && data.getVampireLevel() == 0 && data.getLichLevel() == 0) && !isVisible) {
            isVisible = false
        }

        val targetExpand = if (isVisible) 1f else 0f
        expandAnimation = Mth.lerp(ANIMATION_SPEED, expandAnimation, targetExpand)

        val currentQuests = getCurrentQuestIds(player)
        val newlyCompleted = currentQuests.filter { it.isComplete && !lastCompletedQuests.contains(it.id) }

        newlyCompleted.forEach { quest ->
            questCompletionAnimations[quest.id] = QuestAnimation()
            minecraft.soundManager.play(
                SimpleSoundInstance.forUI(SoundEvents.PLAYER_LEVELUP, 1.5f, 1.2f)
            )
        }

        lastCompletedQuests = currentQuests.filter { it.isComplete }.map { it.id }.toSet()

        questCompletionAnimations.entries.removeIf { (_, anim) ->
            anim.progress += 0.05f
            anim.bounceOffset = sin(anim.progress * Math.PI * 2).toFloat() * 3f * (1f - anim.progress)
            anim.alpha = 1f - (anim.progress * 0.3f)
            anim.progress >= 1f
        }

        questStates.clear()
        currentQuests.forEach { questStates[it.id] = it }
    }

    fun render(guiGraphics: GuiGraphics) {
        if (expandAnimation <= 0.01f) return

        val minecraft = Minecraft.getInstance()
        val player = minecraft.player ?: return
        val font = minecraft.font

        val positions = HudPlayerAttachment.getData(player).hudPositions
        val (baseX, baseY) = positions.getQuestHudPos(guiGraphics.guiWidth(),
            guiGraphics.guiHeight())

        val alpha = (expandAnimation * 255).toInt().coerceIn(0, 255)

        val poseStack = guiGraphics.pose()
        poseStack.pushPose()

        val scale = 0.25f + (expandAnimation * 0.5f)
        poseStack.translate(baseX.toFloat(), baseY.toFloat(), 0f)
        poseStack.scale(scale, scale, 1f)
        poseStack.translate(-baseX.toFloat(), -baseY.toFloat(), 0f)

        var yOffset = baseY

        val data = AfflictionPlayerAttachment.getData(player)

        if (data.getWerewolfLevel() > 0) {
            yOffset = renderAfflictionQuests(
                guiGraphics, font, baseX, yOffset,
                "Lycanthropy", AfflictionTypes.LYCANTHROPY,
                data.getWerewolfLevel(), alpha
            )
            yOffset += 10
        }

        if (data.getVampireLevel() > 0) {
            yOffset = renderAfflictionQuests(
                guiGraphics, font, baseX, yOffset,
                "Vampirism", AfflictionTypes.VAMPIRISM,
                data.getVampireLevel(), alpha
            )
            yOffset += 10
        }

        if (data.getLichLevel() > 0) {
            yOffset = renderAfflictionQuests(
                guiGraphics, font, baseX, yOffset,
                "Lichdom", AfflictionTypes.LICHDOM,
                data.getLichLevel(), alpha
            )
        }

        if (expandAnimation > 0.9f) {
            val hintText = "Press ${WitcheryKeyMappings.TOGGLE_QUEST_HUD.key.displayName.string} to hide"
            val hintWidth = font.width(hintText) / 2
            guiGraphics.drawString(
                font, hintText,
                baseX - hintWidth, yOffset,
                (QUEST_COLOR and 0xFFFFFF) or ((alpha / 2) shl 24),
                false
            )
        }

        poseStack.popPose()
    }

    private fun renderAfflictionQuests(
        guiGraphics: GuiGraphics,
        font: Font,
        x: Int,
        startY: Int,
        title: String,
        type: AfflictionTypes,
        level: Int,
        alpha: Int
    ): Int {
        var y = startY

        val titleComponent = Component.literal(title)
        val titleWidth = font.width(titleComponent) / 2
        guiGraphics.drawString(
            font, titleComponent,
            x - titleWidth, y,
            (TITLE_COLOR and 0xFFFFFF) or (alpha shl 24),
            false
        )
        y += 12

        val separatorWidth = 100
        RenderSystem.enableBlend()
        guiGraphics.fill(
            x - separatorWidth / 4, y,
            x + separatorWidth / 4, y + 1,
            (TITLE_COLOR and 0xFFFFFF) or (alpha shl 24)
        )
        y += 8

        val quests = getQuestsForAffliction(type, level)

        quests.forEach { quest ->
            val animation = questCompletionAnimations[quest.id]
            val questY = y + (animation?.bounceOffset?.toInt() ?: 0)
            val questAlpha = ((animation?.alpha ?: 1f) * alpha).toInt().coerceIn(0, 255)

            val bulletColor = if (quest.isComplete) COMPLETE_COLOR else QUEST_COLOR
            guiGraphics.drawString(
                font, "•",
                x - 50, questY,
                (bulletColor and 0xFFFFFF) or (questAlpha shl 24),
                false
            )

            val questText = buildQuestText(quest)
            val textColor = if (quest.isComplete) COMPLETE_COLOR else QUEST_COLOR

            guiGraphics.drawString(
                font, questText,
                x - 45, questY,
                (textColor and 0xFFFFFF) or (questAlpha shl 24),
                false
            )

            if (quest.isComplete) {
                val strikeProgress = animation?.progress ?: 1f
                val textWidth = font.width(questText)
                val strikeWidth = (textWidth * strikeProgress).toInt()

                guiGraphics.fill(
                    x - 45, questY + 4,
                    x - 45 + strikeWidth, questY + 5,
                    (COMPLETE_COLOR and 0xFFFFFF) or (questAlpha shl 24)
                )
            }

            y += 10
        }

        return y
    }

    private fun buildQuestText(quest: QuestState): Component {
        return if (quest.max > 0) {
            Component.literal("${quest.description} (${quest.current}/${quest.max})")
        } else {
            Component.literal(quest.description)
        }
    }

    private fun getQuestsForAffliction(type: AfflictionTypes, level: Int): List<QuestState> {
        val minecraft = Minecraft.getInstance()
        val player = minecraft.player ?: return emptyList()
        val data = AfflictionPlayerAttachment.getData(player)

        return when (type) {
            AfflictionTypes.LYCANTHROPY -> getWerewolfQuests(data, level)
            AfflictionTypes.VAMPIRISM -> getVampireQuests(player, data, level)
            AfflictionTypes.LICHDOM -> getLichQuests(data, level)
            else -> emptyList()
        }
    }

    private fun getWerewolfQuests(data: AfflictionPlayerAttachment.Data, level: Int): List<QuestState> {
        val level = data.getWerewolfLevel()
        val nextLevel = level + 1
        if (nextLevel > 10) return emptyList()

        val requirement = WerewolfLeveling.LEVEL_REQUIREMENTS[nextLevel] ?: return emptyList()
        val quests = mutableListOf<QuestState>()

        requirement.threeGold?.let {
            quests.add(QuestState(
                "gold_$nextLevel",
                "Give 3 gold ingots to altar",
                if (data.hasGivenGold()) 1 else 0,
                1,
                data.hasGivenGold()
            ))
        }

        requirement.killedSheep?.let { max ->
            quests.add(QuestState(
                "sheep_$nextLevel",
                "Kill sheep",
                data.getKilledSheep(),
                max,
                data.getKilledSheep() >= max
            ))
        }

        requirement.offeredMutton?.let {
            quests.add(QuestState(
                "mutton_$nextLevel",
                "Offer 30 mutton to altar",
                if (data.hasOfferedMutton()) 1 else 0,
                1,
                data.hasOfferedMutton()
            ))
        }

        requirement.killedWolves?.let { max ->
            quests.add(QuestState(
                "wolves_$nextLevel",
                "Kill wolves",
                data.getKilledWolves(),
                max,
                data.getKilledWolves() >= max
            ))
        }

        requirement.offeredTongues?.let {
            quests.add(QuestState(
                "tongues_$nextLevel",
                "Offer 10 tongues to altar",
                if (data.hasOfferedTongue()) 1 else 0,
                1,
                data.hasOfferedTongue()
            ))
        }

        requirement.killHornedOne?.let {
            quests.add(QuestState(
                "horned_$nextLevel",
                "Kill Horned Huntsman",
                if (data.hasKilledHornedOne()) 1 else 0,
                1,
                data.hasKilledHornedOne()
            ))
        }

        requirement.airSlayMonster?.let { max ->
            quests.add(QuestState(
                "air_slay_$nextLevel",
                "Kill monsters in air",
                data.getAirSlayMonster(),
                max,
                data.getAirSlayMonster() >= max
            ))
        }

        requirement.nightHowl?.let { max ->
            quests.add(QuestState(
                "howl_$nextLevel",
                "Howl at night",
                data.getNightHowl(),
                max,
                data.getNightHowl() >= max
            ))
        }

        requirement.wolfPack?.let { max ->
            quests.add(QuestState(
                "pack_$nextLevel",
                "Form wolf pack",
                data.getWolfPack(),
                max,
                data.getWolfPack() >= max
            ))
        }

        requirement.pigmenKilled?.let { max ->
            quests.add(QuestState(
                "pigmen_$nextLevel",
                "Kill piglins",
                data.getPigmenKilled(),
                max,
                data.getPigmenKilled() >= max
            ))
        }

        requirement.spreadLycanthropy?.let {
            quests.add(QuestState(
                "spread_$nextLevel",
                "Spread lycanthropy",
                if (data.hasSpreadLycanthropy()) 1 else 0,
                1,
                data.hasSpreadLycanthropy()
            ))
        }

        return quests
    }

    private fun getVampireQuests(
        player: Player,
        data: AfflictionPlayerAttachment.Data,
        level: Int
    ): List<QuestState> {
        val nextLevel = level + 1
        if (nextLevel > 10) return emptyList()

        val quests = mutableListOf<QuestState>()

        if (nextLevel == 2) {
            val bloodData = BloodPoolLivingEntityAttachment.getData(player)
            quests.add(QuestState(
                "fill_up_$nextLevel",
                "Fill your blood pool",
                bloodData.bloodPool,
                900,
                bloodData.bloodPool >= 900
            ))
            return quests
        }

        val requirement = VampireLeveling.LEVEL_REQUIREMENTS[nextLevel] ?: return emptyList()

        requirement.halfVillagers?.let { max ->
            quests.add(QuestState(
                "half_blood_$nextLevel",
                "Suck half-blood of villagers",
                data.getVillagersHalfBlood().size,
                max,
                data.getVillagersHalfBlood().size >= max
            ))
        }

        requirement.nightCounter?.let { max ->
            quests.add(QuestState(
                "night_$nextLevel",
                "Survive nights",
                data.getNightTicker(),
                max,
                data.getNightTicker() >= max
            ))
        }

        requirement.sunGrenades?.let { max ->
            quests.add(QuestState(
                "grenades_$nextLevel",
                "Use sun grenades",
                data.getUsedSunGrenades(),
                max,
                data.getUsedSunGrenades() >= max
            ))
        }

        requirement.blazesKilled?.let { max ->
            quests.add(QuestState(
                "blazes_$nextLevel",
                "Kill blazes",
                data.getKilledBlazes(),
                max,
                data.getKilledBlazes() >= max
            ))
        }

        requirement.villagesVisited?.let { max ->
            quests.add(QuestState(
                "villages_$nextLevel",
                "Visit villages as bat",
                data.getVisitedVillages().size,
                max,
                data.getVisitedVillages().size >= max
            ))
        }

        requirement.trappedVillagers?.let { max ->
            quests.add(QuestState(
                "trapped_$nextLevel",
                "Trap villagers",
                data.getTrappedVillagers().size,
                max,
                data.getTrappedVillagers().size >= max
            ))
        }

        return quests
    }

    private fun getLichQuests(data: AfflictionPlayerAttachment.Data, level: Int): List<QuestState> {
        val nextLevel = level + 1
        if (nextLevel > 10) return emptyList()

        val requirement = LichdomLeveling.LEVEL_REQUIREMENTS[nextLevel] ?: return emptyList()
        val quests = mutableListOf<QuestState>()

        requirement.boundSouls?.let { max ->
            quests.add(QuestState(
                "souls_$nextLevel",
                "Bind souls",
                data.getBoundSouls(),
                max,
                data.getBoundSouls() >= max
            ))
        }

        requirement.zombieKilledMob?.let {
            quests.add(QuestState(
                "zombie_kill_$nextLevel",
                "Zombie slave kills mob",
                if (data.hasZombieKilledMob()) 1 else 0,
                1,
                data.hasZombieKilledMob()
            ))
        }

        requirement.killedGolems?.let { max ->
            quests.add(QuestState(
                "golems_$nextLevel",
                "Kill golems",
                data.getKilledGolems(),
                max,
                data.getKilledGolems() >= max
            ))
        }

        requirement.drainedAnimals?.let { max ->
            quests.add(QuestState(
                "drained_$nextLevel",
                "Drain animals",
                data.getDrainedAnimals(),
                max,
                data.getDrainedAnimals() >= max
            ))
        }

        requirement.possessedKillVillager?.let {
            quests.add(QuestState(
                "possess_$nextLevel",
                "Possess and kill villager",
                if (data.hasPossessedKillVillager()) 1 else 0,
                1,
                data.hasPossessedKillVillager()
            ))
        }

        requirement.killedWither?.let {
            quests.add(QuestState(
                "wither_$nextLevel",
                "Kill wither",
                if (data.hasKilledWither()) 1 else 0,
                1,
                data.hasKilledWither()
            ))
        }

        requirement.phylacteryDeaths?.let { max ->
            quests.add(QuestState(
                "phylactery_$nextLevel",
                "Die with phylactery",
                data.getPhylacteryDeaths(),
                max,
                data.getPhylacteryDeaths() >= max
            ))
        }

        return quests
    }

    private fun getCurrentQuestIds(player: Player): List<QuestState> {
        val data = AfflictionPlayerAttachment.getData(player)
        val allQuests = mutableListOf<QuestState>()

        if (data.getWerewolfLevel() > 0) {
            allQuests.addAll(getWerewolfQuests(data, data.getWerewolfLevel()))
        }
        if (data.getVampireLevel() > 0) {
            allQuests.addAll(getVampireQuests(player, data, data.getVampireLevel()))
        }
        if (data.getLichLevel() > 0) {
            allQuests.addAll(getLichQuests(data, data.getLichLevel()))
        }

        return allQuests
    }
}