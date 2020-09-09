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
import org.awaitility.Awaitility.await
import org.hamcrest.Matchers
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
@UninstallModules(NetworkModule::class)
@HiltAndroidTest
class StatusCodesTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    private lateinit var mockServer: MockWebServer
    private var numberOfRequests: AtomicInteger = AtomicInteger(0)

    @Before
    fun setUp() {
        hiltRule.inject()
        mockServer = MockWebServer()
        numberOfRequests = AtomicInteger(0)
        mockServer.start(8080)
    }

    @Test
    fun checkIfServerSentError300_shouldShowError() {
        setDispatcher("error300.xml", 300)
        activityTestRule.launchActivity(null)

        await().untilAtomic(numberOfRequests, greaterThanOrEqualTo(1))
        waitUntilView(R.id.currencies_rv, 3, ViewMatchers.isDisplayed())
        checkToast("Can not fetch data from server")
    }

    @Test
    fun checkIfServerSentError400_shouldShowError() {
        setDispatcher("error400.xml", 400)
        activityTestRule.launchActivity(null)

        await().untilAtomic(numberOfRequests, greaterThanOrEqualTo(1))
        waitUntilView(R.id.currencies_rv, 3, ViewMatchers.isDisplayed())
        checkToast("Can not fetch data from server")
    }

    @Test
    fun checkIfServerSentError404_shouldShowError() {
        setDispatcher("error404.xml", 404)
        activityTestRule.launchActivity(null)

        await().untilAtomic(numberOfRequests, greaterThanOrEqualTo(1))
        waitUntilView(R.id.currencies_rv, 3, ViewMatchers.isDisplayed())
        checkToast("Can not fetch data from server")
    }

    @Test
    fun checkIfServerSentError500_shouldShowError() {
        setDispatcher("error500.xml", 500)
        activityTestRule.launchActivity(null)

        await().untilAtomic(numberOfRequests, greaterThanOrEqualTo(1))
        waitUntilView(R.id.currencies_rv, 3, ViewMatchers.isDisplayed())
        checkToast("Can not fetch data from server")
    }

    fun setDispatcher(fileName: String, responseCode: Int) {
        mockServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                numberOfRequests.incrementAndGet()
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