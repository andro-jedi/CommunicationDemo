package com.demo.communicationexample.domain.di

import com.demo.communicationexample.data.BluetoothRepository
import com.demo.communicationexample.domain.BluetoothConnectionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    fun provideBluetoothConnectionUseCase(
        bluetoothRepository: BluetoothRepository
    ): BluetoothConnectionUseCase {
        return BluetoothConnectionUseCase(bluetoothRepository)
    }
}
