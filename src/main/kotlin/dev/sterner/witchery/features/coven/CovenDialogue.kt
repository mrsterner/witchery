package dev.sterner.witchery.features.coven

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.util.RandomSource

object CovenDialogue {

    private val FIRST_NAMES = listOf(
        "Agatha", "Circe", "Morgana", "Hecate", "Bellatrix",
        "Minerva", "Cassandra", "Medea", "Tituba", "Bridget",
        "Alice", "Agnes", "Ursula", "Sybil", "Elphaba",
        "Glinda", "Willow", "Rowena", "Hermione", "Belinda",
        "Tabitha", "Endora", "Samantha", "Sabrina", "Maleficent",
        "Evanora", "Theodora", "Glinda", "Zelena", "Regina",
        "Nimue", "Morgause", "Vivienne", "Selene", "Seraphina",
        "Lilith", "Bathsheba", "Hester", "Goody", "Mercy",
        "Prudence", "Temperance", "Constance", "Fortuna", "Luna"
    )

    private val LAST_NAMES = listOf(
        "Harkness", "Blackwood", "Nightshade", "Ravencroft", "Darkmore",
        "Shadowend", "Moonshadow", "Starweaver", "Thornheart", "Grimwood",
        "Ashborne", "Crowley", "Vesper", "Midnight", "Stormcrow",
        "Winterborn", "Bloodmoon", "Ravenwood", "Darkholme", "Spellman",
        "Hawthorne", "Bishop", "Good", "Nurse", "Proctor",
        "Corey", "Osborne", "Martin", "Easty", "Wardwell",
        "Pendle", "Device", "Redferne", "Demdike", "Chattox",
        "Southeil", "Shipton", "Morgan", "Le Fay", "Pendragon"
    )

    private val BINDING_RESPONSES = listOf(
        "Fine, I'll join your coven.",
        "Very well... I accept your offer.",
        "I suppose I could be of use to you.",
        "Hmm... Your coven has potential.",
        "A wise choice. I shall serve you well."
    )

    private val ALREADY_BOUND_RESPONSES = listOf(
        "I already serve another master!",
        "My loyalty is already pledged elsewhere.",
        "You're too late, I'm already bound to a coven.",
        "Another has claimed my service.",
        "I cannot serve two masters!"
    )

    private val NEEDS_HEART_RESPONSES = listOf(
        "A Demon Heart... bring me one and we shall talk.",
        "I require a Demon Heart before I pledge myself.",
        "My loyalty comes at a price - a Demon Heart.",
        "Without a Demon Heart, I cannot be bound.",
        "You expect me to serve without a Demon Heart? Foolish!"
    )

    private val COVEN_FULL_RESPONSES = listOf(
        "Your coven is already full. I cannot join.",
        "You have no room for me in your coven.",
        "Too many witches already serve you!",
        "I sense your coven is at its limit.",
        "You cannot bind more witches to your service."
    )

    fun generateName(random: RandomSource): MutableComponent {
        val firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.size)]
        val lastName = LAST_NAMES[random.nextInt(LAST_NAMES.size)]
        return Component.literal("$firstName $lastName")
    }

    fun getBindingResponse(witchName: Component, random: RandomSource): MutableComponent {
        val response = BINDING_RESPONSES[random.nextInt(BINDING_RESPONSES.size)]
        return Component.literal("<${witchName.string}> $response")
    }

    fun getAlreadyBoundResponse(witchName: Component, random: RandomSource): MutableComponent {
        val response = ALREADY_BOUND_RESPONSES[random.nextInt(ALREADY_BOUND_RESPONSES.size)]
        return Component.literal("<${witchName.string}> $response")
    }

    fun getNeedsHeartResponse(witchName: Component, random: RandomSource): MutableComponent {
        val response = NEEDS_HEART_RESPONSES[random.nextInt(NEEDS_HEART_RESPONSES.size)]
        return Component.literal("<${witchName.string}> $response")
    }

    fun getCovenFullResponse(witchName: Component, random: RandomSource): MutableComponent {
        val response = COVEN_FULL_RESPONSES[random.nextInt(COVEN_FULL_RESPONSES.size)]
        return Component.literal("<${witchName.string}> $response")
    }
}