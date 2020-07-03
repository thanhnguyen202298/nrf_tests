package no.nordicsemi.android.nrfmesh.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import no.nordicsemi.android.mesh.ApplicationKey
import no.nordicsemi.android.mesh.transport.Element
import no.nordicsemi.android.mesh.transport.GenericOnOffSet
import no.nordicsemi.android.mesh.transport.MeshModel
import no.nordicsemi.android.nrfmesh.node.GenericOnOffServerActivity

class ContractServer:Service() {

    override fun onCreate() {
        super.onCreate()

    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        while (true){
            var enpoin = 0
            var isnode = false
            for (node in GenericOnOffServerActivity.NODES) {
                enpoin++
                var element: Element? = null
                var model: MeshModel? = null
                for (dd in node.elements.keys) {
                    element = node.elements[dd]
                    if (element != null) for (cc in element.meshModels.keys) {
                        model = element.meshModels[cc]
                        if (model!!.getType() == 3) {
                            isnode = true
                            break
                        }
                    }
                    if (isnode) break
                }
                try {
                    Thread.sleep(100)
                } catch (es: Exception) {
                }
                isnode = false
                if (element != null) {
//                    if (model != null) {
//                        if (!model.boundAppKeyIndexes.isEmpty()) {
//                            val appKeyIndex = model.boundAppKeyIndexes[0]
//                            val appKey: ApplicationKey = mViewModel.getNetworkLiveData().getMeshNetwork().getAppKey(appKeyIndex)
//                            val address = element.elementAddress
//                            val genericOnOffSet = GenericOnOffSet(appKey, enpoin,
//                                node.sequenceNumber, count, type, optcode)
//                            sendMessage(address, genericOnOffSet)
//                        } else {
//                            mViewModel.displaySnackBar(this@GenericOnOffServerActivity, mContainer, getString(R.string.error_no_app_keys_bound), Snackbar.LENGTH_LONG)
//                        }
//                    }
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.e("SERVICE NODES","cancel serviec")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}