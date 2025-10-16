package dev.sterner.witchery.client.arcana

data class TarotCardObject(
    val cardNumber: Int,
    var screenX: Float = 0f,
    var screenY: Float = 0f,
    var targetX: Float = 0f,
    var targetY: Float = 0f,
    var rotationX: Float = 0f,
    var rotationY: Float = 0f,
    var rotationZ: Float = 0f,
    var isFlipped: Boolean = false,
    var isReversed: Boolean = false,
    var animationProgress: Float = 0f
) {
    companion object {
        const val CARD_WIDTH = 46f
        const val CARD_HEIGHT = 81f

        val ARCANA_NAMES = mapOf(
            1 to "The Fool",
            2 to "The Magician",
            3 to "The High Priestess",
            4 to "The Empress",
            5 to "The Emperor",
            6 to "The Hierophant",
            7 to "The Lovers",
            8 to "The Chariot",
            9 to "Strength",
            10 to "The Hermit",
            11 to "Wheel of Fortune",
            12 to "Justice",
            13 to "The Hanged Man",
            14 to "Death",
            15 to "Temperance",
            16 to "The Devil",
            17 to "The Tower",
            18 to "The Star",
            19 to "The Moon",
            20 to "The Sun",
            21 to "Judgement",
            22 to "The World"
        )

        fun getArcanaName(cardNumber: Int, isReversed: Boolean): String {
            val baseName = ARCANA_NAMES[cardNumber] ?: "Unknown"
            return if (isReversed) "$baseName (Reversed)" else baseName
        }
    }
}