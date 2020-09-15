package hep.dataforge.control.controllers

import hep.dataforge.control.api.Device
import hep.dataforge.control.api.DeviceListener
import hep.dataforge.meta.MetaItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
public suspend fun Device.flowValues(): Flow<Pair<String, MetaItem<*>>> = callbackFlow {
    val listener = object : DeviceListener {
        override fun propertyChanged(propertyName: String, value: MetaItem<*>?) {
            if (value != null) {
                launch {
                    send(propertyName to value)
                }
            }
        }
    }
    registerListener(listener)
    awaitClose {
        removeListeners(listener)
    }
}