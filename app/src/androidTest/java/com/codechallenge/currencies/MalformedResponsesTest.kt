package com.codechallenge.currencies

import android.graphics.Color
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import com.codechallenge.currencies.di.NetworkModule
import com.codechallenge.currencies.utils.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.awaitility.Awaitility
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
@UninstallModules(NetworkModule::class)
@HiltAndroidTest
class MalformedResponsesTest {

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

    @Test
    fun checkIfEmptyJSONComes_shouldShowError() {
        setDispatcher("empty_json.json", 200)
        activityTestRule.launchActivity(null)

        waitUntilView(R.id.currencies_rv, 3, isDisplayed())
        assertEquals(1, mockServer.requestCount)
        checkToast("Empty response")
    }

    @Test
    fun checkIfEmptyRatesComes_shouldShowError() {
        setDispatcher("empty_rates.json", 200)
        activityTestRule.launchActivity(null)

        waitUntilView(R.id.currencies_rv, 3, isDisplayed())
        assertEquals(1, mockServer.requestCount)
        checkToast("Empty response")
    }

    @Test
    fun checkIfStartLoading_shouldAppearsSpinnerUntilDataBeRecieved() {
        setDispatcher("full_json_1.json", 200)
        activityTestRule.launchActivity(null)
        waitUntilProgressBarAppears(R.id.progressBar, 1000)
        assertEquals(1, mockServer.requestCount)
        waitUntilView(R.id.currencies_rv, 3, isDisplayed())
        waitUntilProgressBarDisappears(R.id.progressBar, 1000)
    }

    @Test
    fun checkIfMalformedPriceComes_shouldShowItem() {
        setDispatcher("malformed_price.json", 200)
        activityTestRule.launchActivity(null)

        waitUntilView(R.id.currencies_rv, 3, isDisplayed())
        assertEquals(1, mockServer.requestCount)
        onView(withId(R.id.currency_value_tv)).check(matches(withText("")))
        onView(withId(R.id.first_currency_iv)).check(matches(isDisplayed()))
        onView(withId(R.id.second_currency_iv)).check(matches(isDisplayed()))
    }

    @Test
    fun checkIfMalformedSymbolComes_shouldShowItem() {
        setDispatcher("malformed_symbol.json", 200)
        activityTestRule.launchActivity(null)

        waitUntilView(R.id.currencies_rv, 3, isDisplayed())
        assertEquals(1, mockServer.requestCount)
        onView(withId(R.id.currency_value_tv)).check(matches(withText("1.1513")))
        onView(withId(R.id.first_currency_iv)).check(matches(isDisplayed()))
        onView(withId(R.id.second_currency_iv)).check(matches(isDisplayed()))
    }

    @Test
    fun checkIfNormalJSONComes_shouldShowItems() {
        setDispatcher("full_json_1.json", 200)
        activityTestRule.launchActivity(null)

        waitUntilView(R.id.currencies_rv, 3, isDisplayed())
        assertEquals(1, mockServer.requestCount)

        onView(withId(R.id.currencies_rv)).check(
            matches(
                recyclerItemAtPosition(
                    0,
                    hasDescendant(withText("1.2228"))
                )
            )
        )
        onView(withId(R.id.currencies_rv)).check(
            matches(
                recyclerItemAtPosition(
                    1,
                    hasDescendant(withText("1.3042"))
                )
            )
        )
        onView(withId(R.id.currencies_rv)).check(
            matches(
                recyclerItemAtPosition(
                    2,
                    hasDescendant(withText("0.9511"))
                )
            )
        )
        onView(withId(R.id.currencies_rv)).check(
            matches(
                recyclerItemAtPosition(
                    3,
                    hasDescendant(withText("1.1241"))
                )
            )
        )
    }

    @Test
    fun checkIfNewJSONComes_shouldChangeColorsOfCurrencies() {
        setDispatcher("full_json_1.json", 200)
        activityTestRule.launchActivity(null)

        waitUntilView(R.id.currencies_rv, 3, isDisplayed())
        assertEquals(1, mockServer.requestCount)
        setDispatcher("full_json_2.json", 200)
        //TODO here is better to manipulate with system timer. Next time.
        sleep(11_000)
        Awaitility.await().atMost(11, TimeUnit.SECONDS).untilAsserted {
            onView(withText("1.1513")).check(matches(isDisplayed()))
        }

        onView(withId(R.id.currencies_rv)).check(
            matches(
                recyclerItemAtPosition(
                    0,
                    hasDescendant(withTextColor(Color.RED))
                )
            )
        )
        onView(withId(R.id.currencies_rv)).check(
            matches(
                recyclerItemAtPosition(
                    1,
                    hasDescendant(withTextColor(Color.GREEN))
                )
            )
        )
        onView(withId(R.id.currencies_rv)).check(
            matches(
                recyclerItemAtPosition(
                    2,
                    hasDescendant(withTextColor(Color.RED))
                )
            )
        )
        onView(withId(R.id.currencies_rv)).check(
            matches(
                recyclerItemAtPosition(
                    3,
                    hasDescendant(withTextColor(Color.GREEN))
                )
            )
        )
    }

    @Test
    fun checkIfValueSavedAfterRotation() {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).unfreezeRotation()
        setDispatcher("full_json_1.json", 200)
        activityTestRule.launchActivity(null)

        waitUntilView(R.id.currencies_rv, 3, isDisplayed())
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).setOrientationRight()
        assertEquals(1, mockServer.requestCount)
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).setOrientationLeft()
        assertEquals(1, mockServer.requestCount)

        onView(withId(R.id.currencies_rv)).check(
            matches(
                recyclerItemAtPosition(
                    0,
                    hasDescendant(withText("1.2228"))
                )
            )
        )
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
        onView(withText(message))
            .inRoot(
                RootMatchers.withDecorView(
                    not(
                        `is`(
                            activityTestRule.activity.window.decorView
                        )
                    )
                )
            )
            .check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).setOrientationNatural()
        mockServer.shutdown()
    }
}