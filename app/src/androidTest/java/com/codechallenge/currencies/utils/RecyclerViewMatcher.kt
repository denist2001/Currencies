package com.codechallenge.currencies.utils

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.Surface
import android.view.View
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.awaitility.Awaitility
import org.awaitility.core.ConditionTimeoutException
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Assert
import java.util.concurrent.TimeUnit


val orientationDescriptions = mapOf(
    Pair(-1, "SCREEN_ORIENTATION_UNSPECIFIED"),
    Pair(0, "SCREEN_ORIENTATION_LANDSCAPE"),
    Pair(1, "SCREEN_ORIENTATION_PORTRAIT"),
    Pair(2, "SCREEN_ORIENTATION_USER"),
    Pair(3, "SCREEN_ORIENTATION_BEHIND"),
    Pair(4, "SCREEN_ORIENTATION_SENSOR"),
    Pair(5, "SCREEN_ORIENTATION_NOSENSOR"),
    Pair(6, "SCREEN_ORIENTATION_SENSOR_LANDSCAPE"),
    Pair(7, "SCREEN_ORIENTATION_SENSOR_PORTRAIT"),
    Pair(8, "SCREEN_ORIENTATION_REVERSE_LANDSCAPE"),
    Pair(9, "SCREEN_ORIENTATION_REVERSE_PORTRAIT"),
    Pair(10, "SCREEN_ORIENTATION_FULL_SENSOR")
)

fun recyclerItemAtPosition(position: Int, @NonNull itemMatcher: Matcher<View>): Matcher<View> {
    return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("has item at position $position: ")
            itemMatcher.describeTo(description)
        }

        override fun matchesSafely(view: RecyclerView): Boolean {
            val viewHolder = view.findViewHolderForAdapterPosition(position)
                ?: return false
            return itemMatcher.matches(viewHolder.itemView)
        }
    }
}

fun waitUntilView(
    viewId: Int,
    timeout: Long = 3,
    matcher: Matcher<View>
) {
    try {
        Awaitility.await().atMost(timeout, TimeUnit.SECONDS).untilAsserted {
            onView(withId(viewId)).check(matches(matcher))
        }
    } catch (e: ConditionTimeoutException) {
        Assert.fail("View with id: $viewId doesn't match $matcher in $timeout seconds")
    }
}

fun withTextColor(colorId: Int): Matcher<View> {
    return object : BoundedMatcher<View, TextView>(TextView::class.java) {
        override fun matchesSafely(textView: TextView): Boolean {
            return textView.currentTextColor == colorId
        }

        override fun describeTo(description: Description) {
            description.appendText("with text color: ")
            description.appendValue(colorId)
        }
    }
}

fun waitForOrientation(activity: Activity, orientation: Int, timeout: Long = 2) {
    try {
        Awaitility.await().atMost(timeout, TimeUnit.SECONDS).until {
            hasOrientation(activity, orientation)
        }
    } catch (e: ConditionTimeoutException) {
        Assert.fail("Activity haven't changed orientation to ${orientationDescriptions[orientation]} in $timeout seconds")
    }
}

internal fun hasOrientation(activity: Activity, orientation: Int): Boolean {
    return getScreenOrientation(activity) == orientation
}

private fun getScreenOrientation(activity: Activity): Int {
    val rotation = activity.windowManager.defaultDisplay.rotation
    val orientation = activity.resources.configuration.orientation
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        return if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_270) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
        }
    }
    return if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        }
    } else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
}