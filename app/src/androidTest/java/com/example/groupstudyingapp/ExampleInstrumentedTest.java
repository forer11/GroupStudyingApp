package com.example.groupstudyingapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.contrib.RecyclerViewActions;


import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;
import androidx.test.rule.ActivityTestRule;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private Context appContext;

    @Rule
    public ActivityTestRule<LoginActivity> loginActivityRule
            = new ActivityTestRule<>(LoginActivity.class, true, true);

    @Before
    public void useAppContext() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.groupstudyingapp", appContext.getPackageName());
    }

    @Test
    public void testSignIn() {
        onView(withId(R.id.anonymous_sign_in_button)).perform(click());
    }

    @Test
    public void testSignOut() {
        onView(withId(R.id.anonymous_sign_in_button)).perform(click());
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.profile_image_layout)).perform(click());
        onView(withId(R.id.signout_button)).perform(click());
    }

    public static ViewAction waitFor(final long delay) {
        return new ViewAction() {
            @Override public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override public String getDescription() {
                return "wait for " + delay + "milliseconds";
            }

            @Override public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(delay);
            }
        };
    }

}
