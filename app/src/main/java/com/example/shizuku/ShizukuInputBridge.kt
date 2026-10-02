package com.example.shizuku

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteException
import android.util.Log
import rikka.shizuku.Shizuku

class ShizukuInputBridge {

    companion object {
        private const val TAG = "FlexiPadShizuku"

        private const val DESCRIPTOR =
            "com.example.shizuku.IInputUserService"

        private const val TRANSACTION_EXECUTE =
            IBinder.FIRST_CALL_TRANSACTION

        private const val TRANSACTION_PING =
            IBinder.FIRST_CALL_TRANSACTION + 1

        private const val TRANSACTION_TOUCH =
            IBinder.FIRST_CALL_TRANSACTION + 2

        const val TOUCH_DOWN = 0
        const val TOUCH_MOVE = 1
        const val TOUCH_UP = 2
    }

    @Volatile
    private var remote: IBinder? = null

    @Volatile
    var connected: Boolean = false
        private set

    private val serviceArgs =
        Shizuku.UserServiceArgs(
            ComponentName(
                "com.aistudio.flexipad.wtov",
                InputUserService::class.java.name
            )
        )
            .daemon(true)
            .processNameSuffix("input")
            .version(2)
            .tag("flexipad-input")

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) {
            remote = service
            connected = service != null

            Log.i(
                TAG,
                "Shizuku UserService bağlandı: $connected"
            )
        }

        override fun onServiceDisconnected(
            name: ComponentName?
        ) {
            remote = null
            connected = false

            Log.w(
                TAG,
                "Shizuku UserService bağlantısı kesildi"
            )
        }
    }

    fun start(): Boolean {
        return try {

            if (!Shizuku.pingBinder()) {
                Log.w(TAG, "Shizuku çalışmıyor")
                return false
            }

            if (
                Shizuku.checkSelfPermission() !=
                android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                Log.w(TAG, "Shizuku izni yok")
                return false
            }

            Shizuku.bindUserService(
                serviceArgs,
                connection
            )

            true

        } catch (e: Throwable) {

            Log.e(
                TAG,
                "UserService başlatılamadı",
                e
            )

            false
        }
    }

    fun stop() {

        try {

            Shizuku.unbindUserService(
                serviceArgs,
                connection,
                true
            )

        } catch (e: Throwable) {

            Log.w(
                TAG,
                "UserService durdurulurken hata",
                e
            )
        }

        remote = null
        connected = false
    }

    fun ping(): Boolean {

        val binder = remote ?: return false

        return try {

            val data = Parcel.obtain()
            val reply = Parcel.obtain()

            try {

                data.writeInterfaceToken(
                    DESCRIPTOR
                )

                binder.transact(
                    TRANSACTION_PING,
                    data,
                    reply,
                    0
                )

                reply.readException()

                reply.readInt() == 1

            } finally {

                data.recycle()
                reply.recycle()
            }

        } catch (e: RemoteException) {

            Log.e(
                TAG,
                "Ping başarısız",
                e
            )

            false
        }
    }

    fun execute(command: String): Int {

        val binder = remote ?: return -100

        return try {

            val data = Parcel.obtain()
            val reply = Parcel.obtain()

            try {

                data.writeInterfaceToken(
                    DESCRIPTOR
                )

                data.writeString(command)

                binder.transact(
                    TRANSACTION_EXECUTE,
                    data,
                    reply,
                    0
                )

                reply.readException()

                reply.readInt()

            } finally {

                data.recycle()
                reply.recycle()
            }

        } catch (e: Throwable) {

            Log.e(
                TAG,
                "Shizuku komutu çalıştırılamadı",
                e
            )

            -101
        }
    }

    fun sendTouch(
        pointerId: Int,
        action: Int,
        x: Float,
        y: Float
    ): Boolean {

        val binder = remote ?: return false

        return try {

            val data = Parcel.obtain()
            val reply = Parcel.obtain()

            try {

                data.writeInterfaceToken(
                    DESCRIPTOR
                )

                data.writeInt(pointerId)
                data.writeInt(action)
                data.writeFloat(x)
                data.writeFloat(y)

                binder.transact(
                    TRANSACTION_TOUCH,
                    data,
                    reply,
                    0
                )

                reply.readException()

                reply.readInt() == 1

            } finally {

                data.recycle()
                reply.recycle()
            }

        } catch (e: Throwable) {

            Log.e(
                TAG,
                "Touch gönderilemedi",
                e
            )

            false
        }
    }
}
