package com.sports.sportsflashes.di

import com.sports.sportsflashes.repository.factory.ApiFactory
import com.sports.sportsflashes.repository.factory.RepositoryFactory
import com.sports.sportsflashes.view.activites.MainActivity
import com.sports.sportsflashes.view.adapters.*
import com.sports.sportsflashes.view.fragments.*
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
    fun inject(viewPagerAdapter: ScheduleViewPagerAdapter)
    fun inject(scheduleRecyclerFragment: ScheduleRecyclerFragment)
    fun inject(scheduleShowsAdapter: ScheduleShowsAdapter)
    fun inject(liveShowsAdapter: LiveShowAdapter)
    fun inject(liveShowPagerFragment: LiveShowPagerFragment)
    fun inject(categoryShowAdapter: CategoryShowAdapter)
    fun inject(reminderFragment: ReminderFragment)
}