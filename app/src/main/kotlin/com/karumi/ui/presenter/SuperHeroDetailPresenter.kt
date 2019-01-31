package com.karumi.ui.presenter

import android.arch.lifecycle.Lifecycle.Event.ON_DESTROY
import android.arch.lifecycle.Lifecycle.Event.ON_RESUME
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import com.karumi.common.async
import com.karumi.common.weak
import com.karumi.domain.model.NetworkError
import com.karumi.domain.model.NotFound
import com.karumi.domain.model.Success
import com.karumi.domain.model.SuperHero
import com.karumi.domain.usecase.GetSuperHeroByName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SuperHeroDetailPresenter(
    view: View,
    private val getSuperHeroByName: GetSuperHeroByName
) : LifecycleObserver, CoroutineScope by MainScope() {

    private val view: View? by weak(view)

    private lateinit var name: String

    fun preparePresenter(name: String?) {
        if (name != null) {
            this.name = name
        } else {
            view?.close()
        }
    }

    @OnLifecycleEvent(ON_RESUME)
    fun update() {
        view?.showLoading()
        refreshSuperHeroes()
    }

    @OnLifecycleEvent(ON_DESTROY)
    fun destroy() {
        cancel()
    }

    private fun refreshSuperHeroes() = launch {
        val result = async { getSuperHeroByName(name) }
        view?.hideLoading()
        when(result){
            is Success -> view?.showSuperHero(result.value)
            is NotFound -> view?.showNotFoundError()
            is NetworkError -> view?.showNetworkError()
        }
    }

    interface View {
        fun close()
        fun showLoading()
        fun hideLoading()
        fun showSuperHero(superHero: SuperHero)
        fun showNotFoundError()
        fun showNetworkError()
    }
}
