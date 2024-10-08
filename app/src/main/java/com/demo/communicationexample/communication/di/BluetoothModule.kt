package com.demo.communicationexample.communication.di

import android.content.Context
import com.demo.communicationexample.communication.BluetoothManagerWrapper
import com.demo.communicationexample.communication.BluetoothManagerWrapperImpl
import com.demo.communicationexample.data.BluetoothRepository
import com.demo.communicationexample.data.BluetoothRepositoryImpl
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
    fun provideBluetoothRepository(bluetoothManager: BluetoothManagerWrapper): BluetoothRepository {
        return BluetoothRepositoryImpl(bluetoothManager)
    }
}
