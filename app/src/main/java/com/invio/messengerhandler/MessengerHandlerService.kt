package com.invio.messengerhandler

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*

class MessengerHandlerService : Service() {
    companion object {
        const val KEY_SET_MESSENGER = 0
        const val KEY_REQUEST = 1
        const val KEY_RESPONSE = 2
    }

    private lateinit var mMessenger: Messenger

    internal class IncomingHandler(
        context: Context,
        private val applicationContext: Context = context.applicationContext
    ) : Handler() {

        private var mMessengerReceived: Messenger? = null

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                KEY_SET_MESSENGER -> {
                    // Save the messenger to send data back.
                    mMessengerReceived = msg.replyTo
                }
                KEY_REQUEST -> {
                    // Read the data coming from :another process.
                    val randomNumberAtAnotherProcess =
                        msg.data.getInt(AnotherProcessActivity.KEY_RANDOM_INTEGER)

                    // Get the from the current process (main)
                    val rand = (applicationContext as MessengerHandlerApplication).randomNumber

                    val msgResponse: Message = Message.obtain(null, KEY_RESPONSE, 0, 0)
                    val bundle = Bundle()
                    bundle.putString(
                        AnotherProcessActivity.KEY_LINKED_RANDOM_INTEGERS,
                        "$rand-$randomNumberAtAnotherProcess"
                    )
                    msgResponse.data = bundle
                    try {
                        mMessengerReceived?.send(msgResponse)
                    } catch (e: RemoteException) {
                    }
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        mMessenger = Messenger(IncomingHandler(this))
        return mMessenger.binder
    }
}