package ru.mipt.npm.controls.server

import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.cio.websocket.Frame
import io.ktor.response.respondText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import ru.mipt.npm.controls.api.DeviceMessage
import ru.mipt.npm.controls.api.toMeta
import space.kscience.dataforge.io.*
import space.kscience.dataforge.meta.MetaSerializer


internal fun Frame.toEnvelope(): Envelope {
    return data.asBinary().readWith(TaggedEnvelopeFormat)
}

internal fun Envelope.toFrame(): Frame {
    val data = buildByteArray {
        writeWith(TaggedEnvelopeFormat, this@toFrame)
    }
    return Frame.Binary(false, data)
}

internal suspend fun ApplicationCall.respondJson(builder: JsonObjectBuilder.() -> Unit) {
    val json = buildJsonObject(builder)
    respondText(json.toString(), contentType = ContentType.Application.Json)
}

public suspend fun ApplicationCall.respondMessage(message: DeviceMessage) {
    respondText(Json.encodeToString(MetaSerializer, message.toMeta()), contentType = ContentType.Application.Json)
}