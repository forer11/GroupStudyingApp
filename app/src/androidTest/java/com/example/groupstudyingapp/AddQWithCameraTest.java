package com.example.groupstudyingapp;


import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.intent.Intents;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.example.groupstudyingapp.ImageViewMatcher.hasDrawable;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

/**
 * This instrumented test validates question addition functionality.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class AddQWithCameraTest {

    private String date         = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
    private String questionName = "testQ" + date;

    // Test setup
    @Rule
    public ActivityTestRule<OpeningScreenActivity> mActivityTestRule = new ActivityTestRule<>(OpeningScreenActivity.class);

    @Before
    public void setBefore() {
        Intents.init();
        Instrumentation.ActivityResult result = createImageCaptureStub();
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(result);
    }
    @After
    public void setAfter() {
        Intents.release();
    }

    // Test implementation
    @Test
    public void addQWithCameraTest_A() {

        try {   // for test safety in case some previous test already opened the app
            // click sign-in button
            waitTillActivityLoaded();   // will be removed in the next commit with idling resources
            onView(allOf(withId(R.id.anonymous_sign_in_button), withText("Sign in Anonymously"),
                    isDisplayed())).perform(click());
        }
        catch (NoMatchingViewException e) {
            // anonymous_sign_in_button is not displayed, which means we are already signed in.
            // Thus, assume the courses list is displayed.
        }
        waitTillActivityLoaded();   // will be removed in the next commit with idling resources

        // click on first course in the list
        onView(withId(R.id.rvCourses))
                .perform(actionOnItemAtPosition(0, click()));

        waitTillActivityLoaded();   // will be removed in the next commit with idling resources

        // click add question
        onView(withId(R.id.addQuestionButton))
                .perform(click());

        // enter question name
        onView(withId(R.id.userTitle))
                .perform(scrollTo(), replaceText(questionName), closeSoftKeyboard());

        // assert that the image view is empty
        onView(withId(R.id.questionImage)).check(matches(not(hasDrawable())));

        // click the camera option button
        onView(allOf(withId(R.id.cameraButton), withText("From Camera")))
                .perform(scrollTo(), click());

        // todo: Using espresso intent we know exactly what image we "receive" from the camera.
        //  But, if we want to assert something about the image, need to fix next
        // assert that an image was added
        //onView(withId(R.id.questionImage)).check(matches(hasDrawable()));

        // click save
        onView(withId(R.id.saveButton)).
                perform(scrollTo(), click());

        // wait for the image to upload
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // todo: To test, add adapter to R.id.rvCourses, issue #190
        //onView(withId(R.id.recyclerView))
        //        .check(matches(hasDescendant(withText(questionName))));
    }

    private Instrumentation.ActivityResult createImageCaptureStub() {
        // Put the drawable in bundle
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", BitmapFactory.decodeResource(
                getInstrumentation().getTargetContext().getResources(), R.mipmap.logo));


        // Create the Intent that will include the bundle
        Intent resultData = new Intent();
        resultData.putExtras(bundle);

        // Create the ActivityResult with the Intent
        return new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
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
            Thread.sleep(2000);
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
