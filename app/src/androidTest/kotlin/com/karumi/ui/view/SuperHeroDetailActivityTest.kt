package com.karumi.ui.view

import android.os.Bundle
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.karumi.AcceptanceTest
import com.karumi.R
import com.karumi.data.repository.SuperHeroRepository
import com.karumi.domain.model.NetworkError
import com.karumi.domain.model.NotFound
import com.karumi.domain.model.Success
import com.karumi.domain.model.SuperHero
import com.karumi.mothers.SuperHeroesMother
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock

@RunWith(AndroidJUnit4::class)
class SuperHeroDetailActivityTest : AcceptanceTest<SuperHeroDetailActivity>(SuperHeroDetailActivity::class.java) {

    @Mock
    lateinit var repository: SuperHeroRepository

    override val testDependencies = Kodein.Module(allowSilentOverride = true) {
        bind<SuperHeroRepository>() with instance(repository)
    }

    @Test
    fun shouldShowName() {
        val hero = givenAHero()

        startActivity { putString("super_hero_name_key", hero.name) }

        onView(withId(R.id.tv_super_hero_name)).check(matches(withText(hero.name)))
    }

    @Test
    fun shouldShowBadgeIfAvenger() {
        val hero = givenAnAvenger()

        startActivity { putString("super_hero_name_key", hero.name) }

        onView(withId(R.id.iv_avengers_badge)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldShowNotFoundError() {
        givenAHeroIsNotFound()

        startActivity { putString("super_hero_name_key", "any name") }

        onView(withId(R.id.error))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.not_found)))
    }

    @Test
    fun shouldShowNetworkError() {
        givenThereIsANetworkError()

        startActivity { putString("super_hero_name_key", "any name") }

        onView(withId(R.id.error))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.not_found)))
    }

    private fun givenAHero(): SuperHero {
        val hero = SuperHeroesMother.givenAnySuperHero { }
        givenAHero(hero)
        return hero
    }

    private fun givenAnAvenger(): SuperHero {
        val hero = SuperHeroesMother.givenAnySuperHero { isAvenger = true }
        givenAHero(hero)
        return hero
    }

    private fun givenAHero(hero: SuperHero) {
        doReturn(Success(hero)).whenever(repository).getByName(any())
    }

    private fun givenAHeroIsNotFound() {
        doReturn(NotFound<SuperHero>()).whenever(repository).getByName(any())
    }

    private fun givenThereIsANetworkError() {
        doReturn(NetworkError<SuperHero>()).whenever(repository).getByName(any())
    }


    // de muestra el detalle
    // se muestra el badge
    // no se muestra el badge
    // se muestra el title
    // se oculta el progress bar

    private fun startActivity(block: Bundle.() -> Unit) {
        startActivity(Bundle().apply(block))
    }
}
