package com.example.savoreel

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.example.savoreel.ui.home.SearchScreen
import org.junit.Rule
import org.junit.Test

class SearchScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSearchInputField() {
        composeTestRule.setContent {
            SearchScreen(
                initialQuery = "",
                searchResult = {},
                onUserClick = {}
            )
        }
        composeTestRule.onNodeWithText("Search")
            .performTextInput("coffee")

        composeTestRule.onNodeWithText("coffee")
            .assertIsDisplayed()
    }
}