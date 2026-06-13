package com.app.sms.di

import android.content.Context
import com.app.sms.data.repo.SmsRepoImpl
import com.app.sms.domain.contract.SmsRepo
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SmsModule {

    @Provides
    @Singleton
    fun providesSingletonSmsRepo(
        @ApplicationContext context: Context
    ): SmsRepo{
        return SmsRepoImpl(context)
    }
}
