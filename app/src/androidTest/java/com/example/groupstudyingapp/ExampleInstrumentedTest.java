package com.example.groupstudyingapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.contrib.RecyclerViewActions;



import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.*;
import androidx.test.rule.ActivityTestRule;

import java.util.regex.Pattern;


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
        // Context of the app under test.
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.example.groupstudyingapp", appContext.getPackageName());
        onView(withId(R.id.addCourseButton));
    }

    @Test
    public void testLogin() {
        onView(withId(R.id.anonymous_sign_in_button)).perform(click());
    }

    @Test
    public void testGoToCourse() {
        onView(withId(R.id.anonymous_sign_in_button)).perform(click());
        onView(withId(R.id.rvCourses)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    @Test
    public void testGoToQuestion() {
        onView(withId(R.id.anonymous_sign_in_button)).perform(click());
        onView(withId(R.id.rvCourses)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    @Test
    public void testShowSolution() {
        onView(withId(R.id.anonymous_sign_in_button)).perform(click());
        onView(withId(R.id.rvCourses)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.solutionButton)).perform(click());
    }

    @Test
    public void testAddQuestion() {
        onView(withId(R.id.anonymous_sign_in_button)).perform(click());
        onView(withId(R.id.rvCourses)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.addQuestionButton)).perform(click());
        onView(withId(R.id.userTitle)).perform(typeText("testAddQuestion"));
//        onView(withId(R.id.cameraButton)).perform(click()); //todo continue
    }

}
