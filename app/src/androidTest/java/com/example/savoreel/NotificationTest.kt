package com.example.savoreel

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.savoreel.ui.home.NotificationScreen
import org.junit.Rule
import org.junit.Test

class NotificationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun notificationScreen_showsEmptyState() {
        composeTestRule.setContent {
            NotificationScreen(
                notifications = emptyList(),
                unreadCount = 0
            )
        }

        composeTestRule
            .onNodeWithText("No notifications available.")
            .assertExists()
    }


    @Test
    fun notificationScreen_showsUnreadCountBadge() {
        composeTestRule.setContent {
            NotificationScreen(
                notifications = emptyList(),
                unreadCount = 5
            )
        }

        composeTestRule
            .onNodeWithText("5")
            .assertExists()
    }
}
