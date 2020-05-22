package com.example.udacityemojifyme.emojicat

sealed class Emojies {
    data class Smile(val name: String) : Emojies()
    data class Frown(val name: String) : Emojies()
    data class LeftWink(val name: String) : Emojies()
    data class RightWink(val name: String) : Emojies()
    data class LeftWinkFrown(val name: String) : Emojies()
    data class RightWinkFrown(val name: String) : Emojies()
    data class ClosedEyeSmile(val name: String) : Emojies()
    data class ClosedEyeFrown(val name: String) : Emojies()
}