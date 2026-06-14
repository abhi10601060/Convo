package com.app.call_log.di

import android.content.Context
import com.app.call_log.data.repo.CallLogRepoImpl
import com.app.call_log.domain.contract.CallLogRepo
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CallLogModule {

    @Provides
    @Singleton
    fun providesSingletonCallLogRepo(
        @ApplicationContext context: Context
    ): CallLogRepo{
        return CallLogRepoImpl(context)
    }
}
