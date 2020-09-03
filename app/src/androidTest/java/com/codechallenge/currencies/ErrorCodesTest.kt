package com.codechallenge.currencies

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import com.codechallenge.currencies.di.NetworkModule
import com.codechallenge.currencies.utils.getStringFrom
import com.codechallenge.currencies.utils.waitUntilView
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Matchers
import org.junit.*
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
@UninstallModules(NetworkModule::class)
@HiltAndroidTest
class ErrorCodesTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    private lateinit var mockServer: MockWebServer

    @Before
    fun setUp() {
        hiltRule.inject()
        mockServer = MockWebServer()
        mockServer.start(8080)
    }

    @Ignore("needs to investigate correct format of error response for Retrofit")
    @Test
    fun checkIfServerSentError100_shouldShowError() {
        setDispatcher("full_json_1.json", 100)
        activityTestRule.launchActivity(null)

        waitUntilView(R.id.currencies_rv, 3, ViewMatchers.isDisplayed())
        assertEquals(1, mockServer.requestCount)
        checkToast("Continue")
    }

    @Ignore("needs to investigate correct format of error response for Retrofit")
    @Test
    fun checkIfServerSentError105_shouldShowError() {
        setDispatcher("error102.json", 102)
        activityTestRule.launchActivity(null)

        waitUntilView(R.id.currencies_rv, 3, ViewMatchers.isDisplayed())
        assertEquals(1, mockServer.requestCount)
        checkToast("Processing (WebDAV)")
    }

    @Ignore("needs to investigate correct format of error response for Retrofit")
    @Test
    fun checkIfServerSentError300_shouldShowError() {
        setDispatcher("error300.json", 300)
        activityTestRule.launchActivity(null)

        waitUntilView(R.id.currencies_rv, 3, ViewMatchers.isDisplayed())
        assertEquals(1, mockServer.requestCount)
        checkToast("Multiple Choices")
    }

    @Ignore("needs to investigate correct format of error response for Retrofit")
    @Test
    fun checkIfServerSentError400_shouldShowError() {
        setDispatcher("error400.json", 400)
        activityTestRule.launchActivity(null)

        waitUntilView(R.id.currencies_rv, 3, ViewMatchers.isDisplayed())
        assertEquals(1, mockServer.requestCount)
        checkToast("Bad Request")
    }

    fun setDispatcher(fileName: String, responseCode: Int) {
        mockServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setResponseCode(responseCode)
                    .setBody(getStringFrom(fileName))
            }
        }
    }

    private fun checkToast(message: String) {
        Espresso.onView(ViewMatchers.withText(message))
            .inRoot(
                RootMatchers.withDecorView(
                    Matchers.not(
                        Matchers.`is`(
                            activityTestRule.activity.window.decorView
                        )
                    )
                )
            )
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @After
    fun tearDown() {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).setOrientationNatural()
        mockServer.shutdown()
    }

}