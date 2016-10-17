package com.fyber.challenge.activity;

import android.support.design.widget.TextInputLayout;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.fyber.challenge.FyberChallenge;
import com.fyber.challenge.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by Nauman Zubair on 15/10/2016.
 */

@RunWith(AndroidJUnit4.class)
public class ChallengeActivityTest {
    //Sample data
    private static final String APP_ID = "2070";
    private static final String UID = "spiderman";
    private static final String PUB = "compaign2";

    @Rule
    public ActivityTestRule<ChallengeActivity> activityRule = new ActivityTestRule<>(ChallengeActivity.class);

    private static Matcher<? super View> hasError() {
        return new EditTextErrorMatcher();
    }



    @Test
    public void checkOffersButtonEnabledAndClickable() {
        onView(withId(R.id.btnGetOffers)).check(matches(isDisplayed()))
                .check(matches(isClickable()))
                .check(matches(isEnabled()));
    }


    @Test
    public void getOffers() {
        putDataInEditText(R.id.etAppId, APP_ID);
        putDataInEditText(R.id.etUid, UID);
        putDataInEditText(R.id.etPub, PUB);
        putDataInEditText(R.id.etApiKey, FyberChallenge.API_KEY);
        onView(withId(R.id.btnGetOffers)).perform(click());
    }

    private void putDataInEditText(int id, String text) {
        onView(withId(id))
                .perform(clearText())
                .perform(typeText(text))
                .check(matches(withText(text)));
    }

    @Test
    public void checkMandatoryFieldsAreNotEmpty() {
        onView(withText(R.id.btnGetOffers)).perform(click());
        onView(withId(R.id.etApiKey)).check(matches(not(withText(""))));
        onView(withId(R.id.etUid)).check(matches(not(withText(""))));
        onView(withId(R.id.etAppId)).check(matches(not(withText(""))));
    }

    @Test
    public void checkMandatoryFieldsHasNoError() {
        onView(withText(R.id.btnGetOffers)).perform(click());

        onView(withId(R.id.etApiKey)).check(matches(not(hasError())));
        onView(withId(R.id.etUid)).check(matches(not(hasError())));
        onView(withId(R.id.etAppId)).check(matches(not(hasError())));
    }

    private static class EditTextErrorMatcher extends TypeSafeMatcher<View> {
        private EditTextErrorMatcher() {
        }

        @Override
        public boolean matchesSafely(View view) {
            if (!(view instanceof TextInputLayout)) {
                return false;
            }
            TextInputLayout til = (TextInputLayout) view;
            return (til.getError() != null) && !til.getError().toString().isEmpty();
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("no error");
        }
    }
}