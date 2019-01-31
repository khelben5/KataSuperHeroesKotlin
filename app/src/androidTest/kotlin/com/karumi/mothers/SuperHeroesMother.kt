package com.karumi.mothers

import com.karumi.domain.model.SuperHero

class SuperHeroesMother {

    companion object {
        fun givenAnySuperHeroesList(): List<SuperHero> =
            (0..4).map { givenAnySuperHero { } }

        fun givenAnySuperHero(block: SuperHeroBuilder.() -> Unit) =
            SuperHeroBuilder().apply(block).build()
    }

    class SuperHeroBuilder {
        var name: String = "Name"
        var photo: String? = "Photo"
        var isAvenger: Boolean = false
        var description: String = "Description"

        fun build() = SuperHero(name, photo, isAvenger, description)
    }
}
