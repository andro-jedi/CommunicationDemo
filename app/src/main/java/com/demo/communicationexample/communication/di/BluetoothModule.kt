package com.demo.communicationexample.communication.di

import com.demo.communicationexample.communication.BluetoothManager
import com.demo.communicationexample.data.BluetoothRepository
import com.demo.communicationexample.data.BluetoothRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {

    @Provides
    fun provideBluetoothManager(): BluetoothManager = BluetoothManager()

    @Provides
    fun provideBluetoothRepository(bluetoothManager: BluetoothManager): BluetoothRepository {
        return BluetoothRepositoryImpl(bluetoothManager)
    }
}
