package com.dogfight.magic.navigationwithcompose.di

import com.dogfight.magic.navigationwithcompose.data.ItemsRepositoryImpl
import com.dogfight.magic.navigationwithcompose.domain.ItemsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BindsModule {

    @Binds
    abstract fun bindItemsRepository(
        itemsRepositoryImpl: ItemsRepositoryImpl
    ): ItemsRepository
}