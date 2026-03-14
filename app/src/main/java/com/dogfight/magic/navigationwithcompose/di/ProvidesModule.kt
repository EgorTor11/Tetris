package com.dogfight.magic.navigationwithcompose.di

import android.content.Context
import com.dogfight.magic.game_ui.radar.upravlenie.repository.ControlRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ProvidesModule {
    @Provides
    @Singleton
    fun provideControlRepository(
        @ApplicationContext context: Context
    ): ControlRepository = ControlRepository(context)
}