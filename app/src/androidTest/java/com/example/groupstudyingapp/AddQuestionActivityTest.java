package com.example.groupstudyingapp;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Text;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AddQuestionActivityTest {

    @Rule
    public ActivityTestRule<OpeningScreenActivity> mActivityTestRule = new ActivityTestRule<>(OpeningScreenActivity.class);

    @Test
    public void addQuestionActivityTest() {
        try { // for test safety in case some previous test already opened the app
            waitTillActivityLoaded(4000);
            // click sign-in button
            onView(allOf(withId(R.id.anonymous_sign_in_button), withText("Sign in Anonymously"),
                    isDisplayed())).perform(click());

        } catch (NoMatchingViewException e) {
            // anonymous_sign_in_button is not displayed, which means we are already signed in.
            // Thus, assume the courses list is displayed.
        }
        waitTillActivityLoaded(4000);
        onView(allOf(withId(R.id.rvCourses))).perform(actionOnItemAtPosition(1, click()));
        waitTillActivityLoaded(2000);

        onView(allOf(withId(R.id.addQuestionButton),
                isDisplayed())).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.toolbar_goto_sort), isDisplayed())).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.search_bar), withContentDescription("Search"),
                isDisplayed())).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.toolbar_profile_image),
                isDisplayed())).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.addQuestionButton),
                isDisplayed())).perform(click());

        waitTillActivityLoaded(2000);

        onView(allOf(withId(R.id.title),isDisplayed())).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.userTitle), isDisplayed())).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.galleryButton),
                isDisplayed())).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.cameraButtonText), withText("From Camera"),
                isDisplayed())).check(matches(withText("From Camera")));

        onView(allOf(withId(R.id.userTitle))).perform(scrollTo(), replaceText("mtitle"), closeSoftKeyboard());
    }

    private void waitTillActivityLoaded(int timeToSleep) {
        try {
            Thread.sleep(timeToSleep);
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
