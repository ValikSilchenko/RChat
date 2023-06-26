package com.rchat.server.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.io.IOException
import java.nio.charset.StandardCharsets


class BytesToStringSerializer : StdSerializer<ByteArray> {
    constructor() : super(ByteArray::class.java)
    constructor(t: Class<ByteArray?>?) : super(t)

    @Throws(IOException::class)
    override fun serialize(value: ByteArray, gen: JsonGenerator, provider: SerializerProvider) {
        val yourReadableString = String(value, StandardCharsets.UTF_8)
        gen.writeString(yourReadableString)
    }
}