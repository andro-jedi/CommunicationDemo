# Bluetooth Communication Demo

This is a quick first version of the app. It will be improved in the nearest future.
- Modularization
- Clear API separation
- More robust error and edge case handling
- Improved UI
- Unit tests

This is a demo Android application demonstrating wireless connectivity between two mobile devices using Bluetooth. The app allows devices to:

1. Discovery and pair to devices
2. Connect using the Bluetooth protocol.
3. Exchange simple messages between two devices (text or byte data).
4. Display sent and received messages on the UI.

The project follows **Clean Architecture** principles and is written using **Kotlin** with **Jetpack Compose** for the UI. Coroutines are used for managing asynchronous operations.

## Features

- Discover nearby Bluetooth devices.
- Connect to a selected device.
- Exchange bi-directional messages between connected devices.
- Display a list of discovered and paired devices.
- Handle Bluetooth permissions and state changes.

## Architecture

The app is structured using the Clean Architecture pattern, divided into several modules:

- **Domain**: Contains the business logic and entity models.
- **Data**: Manages communication with Bluetooth APIs and handles the data layer.
- **Presentation**: UI layer that includes the Jetpack Compose UI screens and ViewModels.

### Layers

1. **Data Layer**: Handles Bluetooth connections and communication. It uses Android's Bluetooth APIs to manage device discovery and socket connections.
2. **Domain Layer**: Contains the core business logic, such as initiating connections and sending/receiving messages.
3. **Presentation Layer**: Jetpack Compose-based UI that displays available devices, connection status, and messages sent/received.

## Technologies Used

- **Kotlin**: Primary programming language.
- **Jetpack Compose**: For building the UI.
- **Coroutines**: For asynchronous operations.
- **Hilt**: For dependency injection.
- **StateFlow**: To manage and observe UI state.
- **Bluetooth APIs**: For device discovery and communication.

## Getting Started

### To start a communication:

1. Start app on both devices (Emulator can be used)
2. Make one of them server by pressing corresponding button
3. Search and pair target device
4. Connect to it and start messaging

### Prerequisites

To build and run the app, you need:

- Android Studio Electric Eel or higher.
- Minimum SDK version: 29 (Android 10).
- Target SDK version: 33 (Android 13).
- Bluetooth-capable Android devices (for testing).

