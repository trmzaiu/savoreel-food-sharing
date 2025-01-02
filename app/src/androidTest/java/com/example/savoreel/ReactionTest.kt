package com.example.savoreel

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.savoreel.ui.home.EmojiAnimation
import com.example.savoreel.ui.home.FloatingEmoji
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EmojiAnimationTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testEmojiAnimation() {
        val emoji = FloatingEmoji("😀", 100f, 1200f)
        var animationEnded = false
        composeTestRule.setContent {
            EmojiAnimation(
                emoji = emoji,
                onAnimationEnd = { animationEnded = true }
            )
        }
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            animationEnded
        }
        assertTrue(animationEnded)
    }

}

