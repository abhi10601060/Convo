package com.app.convo.di

import com.app.convo.data.model.TestDependency
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun providesTestDependency() : TestDependency{
        return TestDependency(name = "This is injected for test...")
    }
}