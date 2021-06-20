package ru.mipt.npm.magix.server

import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.util.KtorExperimentalAPI
import io.rsocket.kotlin.core.RSocketServer
import io.rsocket.kotlin.transport.ktor.serverTransport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.mipt.npm.magix.api.MagixEndpoint.Companion.DEFAULT_MAGIX_RAW_PORT
import ru.mipt.npm.magix.api.MagixEndpoint.Companion.DEFAULT_MAGIX_WS_PORT

@OptIn(KtorExperimentalAPI::class)
public fun CoroutineScope.startMagixServer(
    port: Int = DEFAULT_MAGIX_WS_PORT,
    rawSocketPort: Int = DEFAULT_MAGIX_RAW_PORT,
    buffer: Int = 100,
): ApplicationEngine {

    val magixFlow = MutableSharedFlow<GenericMagixMessage>(
        buffer,
        extraBufferCapacity = buffer
    )

    val tcpTransport = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().serverTransport(port = rawSocketPort)
    RSocketServer().bind(tcpTransport, magixAcceptor(magixFlow))

    return embeddedServer(CIO, port = port) {
        magixModule(magixFlow)
    }
}