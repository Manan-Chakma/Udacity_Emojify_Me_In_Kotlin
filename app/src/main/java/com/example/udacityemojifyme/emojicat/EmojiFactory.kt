package com.example.udacityemojifyme.emojicat

import java.util.*


object EmojiFactory {
    fun create(type: EmojiType): Emojies{
        val name = when(type){
            EmojiType.SMILE -> type.name.toUpperCase(Locale.ROOT)
            EmojiType.FROWN ->type.name.toUpperCase(Locale.ROOT)
            EmojiType.LEFT_WINK ->type.name.toUpperCase(Locale.ROOT)
            EmojiType.RIGHT_WINK ->type.name.toUpperCase(Locale.ROOT)
            EmojiType.LEFT_WINK_FROWN ->type.name.toUpperCase(Locale.ROOT)
            EmojiType.RIGHT_WINK_FROWN ->type.name.toUpperCase(Locale.ROOT)
            EmojiType.CLOSED_EYE_SMILE ->type.name.toUpperCase(Locale.ROOT)
            EmojiType.CLOSED_EYE_FROWN ->type.name.toUpperCase(Locale.ROOT)
        }
        return when(type){
            EmojiType.SMILE -> Emojies.Smile(name)
            EmojiType.FROWN -> Emojies.Frown(name)
            EmojiType.LEFT_WINK -> Emojies.LeftWink(name)
            EmojiType.RIGHT_WINK -> Emojies.RightWink(name)
            EmojiType.LEFT_WINK_FROWN -> Emojies.LeftWinkFrown(name)
            EmojiType.RIGHT_WINK_FROWN -> Emojies.RightWinkFrown(name)
            EmojiType.CLOSED_EYE_SMILE -> Emojies.ClosedEyeSmile(name)
            EmojiType.CLOSED_EYE_FROWN -> Emojies.ClosedEyeFrown(name)
        }
    }
}