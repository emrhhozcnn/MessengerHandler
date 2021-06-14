package com.invio.messengerhandler

import android.app.Application

class MessengerHandlerApplication : Application() {
    val randomNumber: Int = (0..10000).random()
}