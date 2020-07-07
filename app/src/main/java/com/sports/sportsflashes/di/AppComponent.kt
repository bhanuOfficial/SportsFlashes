package com.sports.sportsflashes.di

import com.sports.sportsflashes.repository.factory.ApiFactory
import com.sports.sportsflashes.repository.factory.RepositoryFactory
import com.sports.sportsflashes.view.activites.MainActivity
import com.sports.sportsflashes.view.adapters.EventsAdapter
import com.sports.sportsflashes.view.fragments.HomeFragment
import com.sports.sportsflashes.view.fragments.ShowViewFragment
import com.sports.sportsflashes.view.fragments.ScheduleFragment
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
    fun inject(scheduleFragment: ScheduleFragment)
    fun inject(playableShowFragment: ShowViewFragment)
    fun inject(eventsAdapter: EventsAdapter)
}