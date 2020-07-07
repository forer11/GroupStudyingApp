package com.example.groupstudyingapp;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Description:
 * 1. Start up the app and sign in
 * 2. Press profile button and assure that:
 *      a) "cancel" button works properly    (result = popup closes, obviously)
 *      b) "sign out" button works properly  (result = transferred to startup page without crushing)
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class SignInAndOutTest {

    @Rule
    public ActivityTestRule<OpeningScreenActivity> mActivityTestRule = new ActivityTestRule<>
            (OpeningScreenActivity.class);

    @Test
    public void signInOutTest() {

        // 1.

        try { // for test safety in case some previous test already opened the app
            waitTillActivityLoaded();   // will be removed in the next commit with idling resources
            // click sign-in button
            onView(allOf(withId(R.id.anonymous_sign_in_button), withText("Sign in Anonymously"),
                    isDisplayed())).perform(click());

        }
        catch (NoMatchingViewException e) {
            // anonymous_sign_in_button is not displayed, which means we are already signed in.
            // Thus, assume the courses list is displayed.
        }
        waitTillActivityLoaded();   // will be removed in the next commit with idling resources

        // 2.
        // click the profile image
        onView(allOf(withId(R.id.profile_image_layout), isDisplayed()))
                .perform(click());
        //  a)
        // click cancel to check that the cancel button works properly
        onView(allOf(withId(R.id.cancel_signout), withText("cancel"), isDisplayed()))
                .perform(click());

        // assert that the popup dialog disappeared
        onView(allOf(withId(R.id.cancel_signout), withText("cancel")))
                .check(doesNotExist());

        // click the profile image
        onView(allOf(withId(R.id.profile_image_layout), isDisplayed()))
                .perform(click());

        //  b)
        // click sign-out
        onView(allOf(withId(R.id.signout_button), withText("SignOut"), isDisplayed()))
                .perform(click());

        // assert sign-in page is now displayed to the user
        onView(allOf(withId(R.id.anonymous_sign_in_button), withText("Sign in Anonymously")))
                .check(matches(isDisplayed()));


        // we want to wait with no activity to make sure all processes on exit succeed and
        // the app doesn't crash after signing out
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Thread sleep is needed so that while the app transitions to another page, Espresso is idle.
     * Otherwise, Espresso will try to interact with a view which is yet to appear, this will cause
     * the test to fail.
     *
     * (Sleep is not optimal for testing. It will be removed with the addition of Espresso idling
     * resources in the next tests update)
     */
    private void waitTillActivityLoaded() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
