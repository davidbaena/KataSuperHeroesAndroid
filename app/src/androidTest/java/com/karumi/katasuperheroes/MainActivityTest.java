/*
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.karumi.katasuperheroes;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.karumi.katasuperheroes.di.MainComponent;
import com.karumi.katasuperheroes.di.MainModule;
import com.karumi.katasuperheroes.matchers.RecyclerViewItemsCountMatcher;
import com.karumi.katasuperheroes.model.SuperHero;
import com.karumi.katasuperheroes.model.SuperHeroesRepository;
import com.karumi.katasuperheroes.ui.view.MainActivity;
import com.karumi.katasuperheroes.ui.view.SuperHeroDetailActivity;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.cosenonjaviste.daggermock.DaggerMockRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class) @LargeTest public class MainActivityTest {

  public static final String SUPER_HERO_NAME = "SuperHero ";
  public static final String SUPER_HERO_PHOTO = "http://www.example.com/ ";
  @Rule public DaggerMockRule<MainComponent> daggerRule =
      new DaggerMockRule<>(MainComponent.class, new MainModule()).set(
          new DaggerMockRule.ComponentSetter<MainComponent>() {
            @Override public void setComponent(MainComponent component) {
              SuperHeroesApplication app =
                  (SuperHeroesApplication) InstrumentationRegistry.getInstrumentation()
                      .getTargetContext()
                      .getApplicationContext();
              app.setComponent(component);
            }
          });

  @Rule public IntentsTestRule<MainActivity> activityRule =
      new IntentsTestRule<>(MainActivity.class, true, false);

  @Mock SuperHeroesRepository repository;

  @Test public void showsEmptyCaseIfThereAreNoSuperHeroes() {
    givenThereAreNoSuperHeroes();


    startActivity();

    onView(withText("¯\\_(ツ)_/¯")).check(matches(isDisplayed()));
  }

  @Test public void doesNotShowsEmptyCaseifThereAreSuperHeroes(){
    givenThereAreSuperHeroes(3,false);

    startActivity();

    onView(withText("¯\\_(ツ)_/¯")).check(matches(not(isDisplayed())));
  }

  @Test public void numberOfSuperHeros(){
    givenThereAreSuperHeroes(10,false);

    startActivity();

    onView(withId(R.id.recycler_view)).check(matches(RecyclerViewItemsCountMatcher.recyclerViewHasItemCount(10)));
  }


  private void givenThereAreNoSuperHeroes() {
    when(repository.getAll()).thenReturn(Collections.<SuperHero>emptyList());
  }

  private List<SuperHero> givenThereAreSuperHeroes(int nuOfSuperHeroes, boolean averanges){
    List<SuperHero> heroes = new ArrayList<>();

    for (int i = 0; i < nuOfSuperHeroes; i++) {

      String name = SUPER_HERO_NAME + i;
      String photo = SUPER_HERO_PHOTO + i;
      boolean isAvenger = true;
      String description = "Description";
      SuperHero hero = new SuperHero(name,photo,isAvenger,description);
      heroes.add(hero);
      when(repository.getByName(name)).thenReturn(hero);
    }
    when(repository.getAll()).thenReturn(heroes);
    return heroes;
  }



  @Ignore
  @Test
  public void testshowSuperHeroesName() throws Exception {

    int nuOfSuperHeroes = 200;
    List<SuperHero> superHeros= givenThereAreSuperHeroes(nuOfSuperHeroes,true);
    startActivity();

    for (int i = 0; i < nuOfSuperHeroes; i++) {
      onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.scrollToPosition(i));
      onView(withText("SuperHero "+i)).check(matches(isDisplayed()));
      if(superHeros.get(i).isAvenger()){
//        onView(hasSibling(withText("SuperHero "+i))).check(matches(isDisplayed()));

//        onView(withId(R.id.iv_avengers_badge)).check(matches(isDisplayed()));
      }
    }
  }

  @Test
  public void testTitleMatchesWithHeroDetails() throws Exception {

    List<SuperHero> superHeros =  givenThereAreSuperHeroes(1,true);

    startActivity();

    onView(withId(R.id.recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));

    intended(hasComponent(hasClassName(SuperHeroDetailActivity.class.getCanonicalName())));
    intended(hasExtra(SuperHeroDetailActivity.SUPER_HERO_NAME_KEY,superHeros.get(0).getName()));
  }

  private MainActivity startActivity() {
    return activityRule.launchActivity(null);
  }
}