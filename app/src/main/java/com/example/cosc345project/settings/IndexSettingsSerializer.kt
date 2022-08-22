package com.example.cosc345project.settings

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.example.cosc345project.proto.IndexSettings
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

/**
 * Index Settings Serializer fix.
 *
 * ???
 */
// Suppress warnings for coroutine stuff. Bug with the interactions between protoc and the datastore library.
@Suppress("BlockingMethodInNonBlockingContext")
object IndexSettingsSerializer : Serializer<IndexSettings> {
    override val defaultValue: IndexSettings
        get() = IndexSettings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): IndexSettings {
        try {
            return IndexSettings.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto", exception)
        }
    }

    override suspend fun writeTo(t: IndexSettings, output: OutputStream) = t.writeTo(output)
}

val Context.indexSettingsDataStore: DataStore<IndexSettings> by dataStore(
    fileName = "indexSettings.pb",
    serializer = IndexSettingsSerializer
)