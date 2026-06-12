package com.app.contacts.di

import android.content.Context
import com.app.contacts.data.repo.ContactRepoImpl
import com.app.contacts.domain.contract.ContactsRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class ContactsAppModule {

    @Provides
    @Singleton
    fun providesSingletonContactsRepo(@ApplicationContext context: Context) : ContactsRepo{
        return ContactRepoImpl(context)
    }
}