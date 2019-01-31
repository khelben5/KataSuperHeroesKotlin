package com.karumi

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.espresso.matcher.ViewMatchers.Visibility.GONE
import android.support.test.runner.AndroidJUnit4
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.karumi.data.repository.SuperHeroRepository
import com.karumi.domain.model.SuperHero
import com.karumi.matchers.ToolbarMatcher
import com.karumi.ui.view.MainActivity
import com.karumi.ui.view.SuperHeroDetailActivity
import com.karumi.ui.view.adapter.SuperHeroViewHolder
import com.karumi.mothers.SuperHeroesMother
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.core.IsNot.not
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock

private const val EMPTY_STATE_TEXT = "¯\\_(ツ)_/¯"

@RunWith(AndroidJUnit4::class)
class MainActivityTest : AcceptanceTest<MainActivity>(MainActivity::class.java) {

    @Mock
    lateinit var repository: SuperHeroRepository

    @Test
    fun showsEmptyCaseIfThereAreNoSuperHeroes() {
        givenThereAreNoSuperHeroes()

        startActivity()

        onView(withText(EMPTY_STATE_TEXT)).check(matches(isDisplayed()))
    }

    @Test
    fun progressBarShouldBeHiddenWhenThereAreSomeSuperHeroes() {
        givenThereAreSomeSuperHeroes()

        startActivity()

        onView(withId(R.id.progress_bar)).check(matches(withEffectiveVisibility(GONE)))
    }

    @Test
    fun emptyCaseShouldBeHiddenWhenThereAreSomeSuperHeroes() {
        givenThereAreSomeSuperHeroes()

        startActivity()

        onView(withText(EMPTY_STATE_TEXT)).check(matches(not(isDisplayed())))
    }

    @Test
    fun screenTitleShouldBeShownCorrectly() {
        startActivity()

        ToolbarMatcher.onToolbarWithTitle("Kata Screenshot")
    }

    @Test
    fun superHeroShouldShowBadgeIfAvenger() {
        givenThereIsJustAnAvenger()

        startActivity()

        onView(withId(R.id.iv_avengers_badge)).check(matches(isDisplayed()))
    }

    @Test
    fun superHeroShouldNotShowBadgeIfRegularSuperHero() {
        givenThereIsJustARegularSuperHero()

        startActivity()

        onView(withId(R.id.iv_avengers_badge)).check(matches(withEffectiveVisibility(GONE)))
    }

    @Test
    fun shouldNavigateWhenHeroSelected() {
        val heroes = givenThereIsJustAnAvenger()

        startActivity()
        RecyclerViewActions.actionOnItemAtPosition<SuperHeroViewHolder>(0, click())

        intended(hasComponent(SuperHeroDetailActivity::class.java.canonicalName))
        intended(hasExtra("super_hero_name_key", heroes.first().name))
    }

    private fun givenThereAreNoSuperHeroes() {
        whenever(repository.getAllSuperHeroes()).thenReturn(emptyList())
    }

    private fun givenThereAreSomeSuperHeroes() {
        val heroesList = SuperHeroesMother.givenAnySuperHeroesList()
        whenever(repository.getAllSuperHeroes()).thenReturn(heroesList)
    }

    private fun givenThereIsJustAnAvenger(): List<SuperHero> {
        val heroes = listOf(SuperHeroesMother.givenAnySuperHero { isAvenger = true })
        givenSuperHeroList(heroes)
        return heroes
    }

    private fun givenThereIsJustARegularSuperHero() {
        givenSuperHeroList(
            listOf(SuperHeroesMother.givenAnySuperHero { isAvenger = false })
        )
    }

    private fun givenSuperHeroList(heroes: List<SuperHero>) {
        whenever(repository.getAllSuperHeroes()).thenReturn(heroes)
    }


    override val testDependencies = Kodein.Module(allowSilentOverride = true) {
        bind<SuperHeroRepository>() with instance(repository)
    }
}
