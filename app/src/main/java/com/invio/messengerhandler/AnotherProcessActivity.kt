package com.invio.messengerhandler

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.invio.messengerhandler.databinding.ActivityAnotherProcessBinding
import java.lang.Exception

class AnotherProcessActivity : AppCompatActivity(), IncomingHandlerListener {
    companion object {
        const val KEY_RANDOM_INTEGER = "KEY_RANDOM_INTEGER"
        const val KEY_LINKED_RANDOM_INTEGERS = "KEY_LINKED_RANDOM_INTEGERS"
    }
    lateinit var binding: ActivityAnotherProcessBinding

    // Messenger part
    val mMessenger = Messenger(IncomingHandler(this))
    private var mService: Messenger? = null
    private var bound: Boolean = false
    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mService = Messenger(service)
            bound = true

            // Set messenger
            try {
                val msg: Message =
                    Message.obtain(null, MessengerHandlerService.KEY_SET_MESSENGER, 0, 0)
                msg.replyTo = mMessenger
                mService?.send(msg)
            } catch (e: Exception) {

            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            mService = null
            bound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_another_process)

        val randomNumber = (application as MessengerHandlerApplication).randomNumber
        binding.rand = randomNumber

        binding.btnTriggerCurrentProgressVar.setOnClickListener {
            val msg: Message = Message.obtain(null, MessengerHandlerService.KEY_REQUEST, 0, 0)
            val bundle = Bundle()
            bundle.putInt(KEY_RANDOM_INTEGER, randomNumber)
            msg.data = bundle
            try {
                mService?.send(msg)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, MessengerHandlerService::class.java).also { intent ->
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (bound) {
            unbindService(mConnection)
            bound = false
        }
    }

    override fun onDataReceived(data: String) {
        binding.randMerged = data
    }

    internal class IncomingHandler(private val listener: IncomingHandlerListener) : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MessengerHandlerService.KEY_RESPONSE -> {
                    val responseStr = msg.data.getString(AnotherProcessActivity.KEY_LINKED_RANDOM_INTEGERS, null)
                    if (responseStr != null) {
                        listener.onDataReceived(responseStr)
                    }
                }
                else -> {
                    super.handleMessage(msg)
                }
            }
        }
    }
}

interface IncomingHandlerListener {
    fun onDataReceived(data: String)
}