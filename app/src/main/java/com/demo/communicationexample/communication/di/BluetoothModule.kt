package com.demo.communicationexample.communication.di

import android.content.Context
import com.demo.communicationexample.communication.BluetoothManagerWrapper
import com.demo.communicationexample.communication.BluetoothManagerWrapperImpl
import com.demo.communicationexample.data.AndroidBluetoothController
import com.demo.communicationexample.domain.BluetoothController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {

    @Provides
    @Singleton
    fun provideBluetoothManager(@ApplicationContext context: Context): BluetoothManagerWrapper {
        return BluetoothManagerWrapperImpl(context)
    }

    @Provides
    @Singleton
    fun provideBluetoothController(@ApplicationContext context: Context): BluetoothController {
        return AndroidBluetoothController(context)
    }
}
