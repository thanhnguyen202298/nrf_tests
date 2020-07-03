package no.nordicsemi.android.nrfmesh.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log

class AutoBackupService() : Service() {

    val context = this
    override fun onCreate() {
        super.onCreate()
    }

    lateinit var notifyFactory: NotifyFactory
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        notifyFactory = NotifyFactory(this, 100)


        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.e("<<TAH_AUTO_BACKUP ", "Destroy in service auto backup")
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

}