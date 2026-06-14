package com.app.data.di

import android.content.Context
import com.app.data.sharedpref.SharedPrefsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataAppModule {

    @Provides
    @Singleton
    fun providesSingletonSharedPrefsManager(@ApplicationContext context: Context): SharedPrefsManager{
        return SharedPrefsManager(context)
    }
}