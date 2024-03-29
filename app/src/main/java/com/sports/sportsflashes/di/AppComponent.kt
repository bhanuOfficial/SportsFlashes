package com.sports.sportsflashes.di

import com.sports.sportsflashes.repository.ApiFactory
import com.sports.sportsflashes.repository.RepositoryFactory
import com.sports.sportsflashes.view.activites.MainActivity
import com.sports.sportsflashes.view.fragments.HomeFragment
import com.sports.sportsflashes.viewmodel.MainActivityViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApiModule::class, AppModule::class, ValueModule::class])
interface AppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(mainActivityViewModel: MainActivityViewModel)
    fun inject(apiFactory: ApiFactory)
    fun inject(repositoryFactory: RepositoryFactory)
    fun inject(homeFragment: HomeFragment)
}