package com.example.groupstudyingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.Iterator;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;


@RunWith(AndroidJUnit4.class)
public class MainTest {

    @Rule
    public ActivityTestRule<OpeningScreenActivity> mainActivityRule
            = new ActivityTestRule<>(OpeningScreenActivity.class, true, true);

    @Before
    public void setUp() {
        onView(isRoot()).perform(waitFor(3000));
    }

    @Test
    public void testGoToQuestion() {
        onView(withId(R.id.rvCourses)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    @Test
    public void testShowSolution() {
        onView(withId(R.id.rvCourses)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.solutionButton)).perform(click());
    }

    @Test
    public void testGoToCourse() {
        onView(withId(R.id.rvCourses)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    @Test
    public void testAddAnswer() {
        onView(withId(R.id.rvCourses)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.addAnswerButton)).perform(click());
        onView(withId(R.id.userAnswerTitle)).perform(typeText("testAddAnswer"));
        AppData appData = getApplicationContext();
        FireStoreHandler fireStoreHandler = appData.fireStoreHandler;
        Uri newImageUri = Uri.parse("android.resource://com.example.groupstudyingapp/drawable/blue_gradient.jpg");
        String newAnswerImagePath = "answers/" + newImageUri.getLastPathSegment();
//        fireStoreHandler.uploadAnswerImage(newImageUri, newAnswerImagePath,
//                "testAddAnswer", getActivityInstance());
    }

    @Test
    public void testAddQuestion() {
        onView(withId(R.id.rvCourses)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.addQuestionButton)).perform(click());
        onView(withId(R.id.userTitle)).perform(typeText("testAddQuestion"));
//        onView(withId(R.id.cameraButton)).perform(click()); //todo continue
    }

    @Test
    public void testLikeAnswer() {
        onView(withId(R.id.rvCourses)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.solutionButton)).perform(click());
        String rateBefore = onView(withId(R.id.solutionRateText)).toString();
        onView( withId(R.id.solutionLikeButton)).perform( scrollTo(), click());
        String rateAfter = onView(withId(R.id.solutionRateText)).toString();
        assert (Integer.getInteger(rateBefore) + 1 == Integer.getInteger(rateAfter));
        onView(withId(R.id.solutionLikeButton)).perform(click());
        String rateAfterUnlike = onView(withId(R.id.solutionRateText)).toString();
        assert (Integer.getInteger(rateAfter) - 1 == Integer.getInteger(rateAfterUnlike));
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

    private Activity getActivityInstance(){
        final Activity[] currentActivity = {null};

        getInstrumentation().runOnMainSync(new Runnable(){
            public void run(){
                Collection<Activity> resumedActivity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                Iterator<Activity> it = resumedActivity.iterator();
                currentActivity[0] = it.next();
            }
        });

        return currentActivity[0];
    }

}
