package com.example.ugurozmen.imagelist

import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ugurozmen.imagelist.ui.ItemViewHolder
import com.example.ugurozmen.imagelist.ui.MainActivity
import com.jakewharton.espresso.OkHttp3IdlingResource
import okhttp3.OkHttpClient
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SmokeTest {

    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)
    private var idlingResource: OkHttp3IdlingResource? = null

    @Before
    fun setUp() {
        var okHttpClient: OkHttpClient? = null
        activityTestRule.scenario.onActivity {
            okHttpClient = (it.application as App).okHttpClient
        }
        okHttpClient?.let {
            idlingResource = OkHttp3IdlingResource.create("okHttp", it)
            IdlingRegistry.getInstance().register(idlingResource)
        }
    }

    @Test
    fun smoke_test() {
        verify_list_is_visible()

        scroll_to_item(500)

        clickItem(6)

        verify_details_are_visible()

        Espresso.pressBack()

        verify_list_is_visible()
    }

    private fun verify_details_are_visible() {
        Espresso.onView(ViewMatchers.withId(R.id.listPanel))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
        Espresso.onView(ViewMatchers.withId(R.id.detailsPanel))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun clickItem(position: Int) {
        Espresso.onView(ViewMatchers.withId(R.id.listPanel)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ItemViewHolder>(
                position,
                ViewActions.click()
            )
        )
    }

    private fun scroll_to_item(position: Int) {
        Espresso.onView(ViewMatchers.withId(R.id.listPanel)).perform(
            RecyclerViewActions.scrollToPosition<ItemViewHolder>(
                position
            )
        )
    }

    private fun verify_list_is_visible() {
        Espresso.onView(ViewMatchers.withId(R.id.errorPanel))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
        Espresso.onView(ViewMatchers.withId(R.id.detailsPanel))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
        Espresso.onView(ViewMatchers.withId(R.id.listPanel))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @After
    fun tearDown() {
        idlingResource?.let {
            IdlingRegistry.getInstance().unregister(idlingResource)
        }
    }
}
