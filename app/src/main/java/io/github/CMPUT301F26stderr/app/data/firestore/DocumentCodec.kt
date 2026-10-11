package io.github.CMPUT301F26stderr.app.data.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import io.github.CMPUT301F26stderr.app.domain.shared.Coordinates
import java.time.Instant
import java.time.LocalDate

class DocumentDecodingException(
    val documentPath: String,
    detail: String,
    cause: Throwable? = null,
) : Exception("$documentPath: $detail", cause)

/** Explicit codecs keep SDK types and storage field names out of application models. */
abstract class DocumentCodec<T> {
    abstract fun encode(value: T): Map<String, Any?>

    protected abstract fun read(fields: Fields): T

    fun decode(
        path: String,
        data: Map<String, Any?>,
    ): T =
        try {
            read(Fields(path, data))
        } catch (failure: DocumentDecodingException) {
            throw failure
        } catch (failure: IllegalArgumentException) {
            throw DocumentDecodingException(path, "Invalid field value", failure)
        } catch (failure: java.time.DateTimeException) {
            throw DocumentDecodingException(path, "Invalid date/time", failure)
        }

    fun decode(document: DocumentSnapshot): T =
        decode(
            document.reference.path,
            document.data
                ?: throw DocumentDecodingException(document.reference.path, "Missing document"),
        )
}

class Fields(
    val path: String,
    private val data: Map<String, Any?>,
) {
    val id: String
        get() = path.substringAfterLast('/')

    val parentId: String
        get() = path.substringBeforeLast('/').substringBeforeLast('/').substringAfterLast('/')

    fun malformed(name: String): Nothing =
        throw DocumentDecodingException(path, "Missing or invalid '$name'")

    fun string(name: String): String =
        data[name] as? String ?: malformed(name)

    fun optionalString(name: String): String? =
        if (data[name] == null) null else string(name)

    fun long(name: String): Long =
        data[name] as? Long ?: malformed(name)

    fun boolean(name: String): Boolean =
        data[name] as? Boolean ?: malformed(name)

    fun double(name: String): Double =
        (data[name] as? Number)?.toDouble()?.takeIf { it.isFinite() } ?: malformed(name)

    fun instant(name: String): Instant =
        (data[name] as? Timestamp)?.let {
            Instant.ofEpochSecond(it.seconds, it.nanoseconds.toLong())
        } ?: malformed(name)

    fun optionalInstant(name: String): Instant? =
        if (data[name] == null) null else instant(name)

    fun optionalLong(name: String): Long? =
        if (data[name] == null) null else long(name)

    fun date(name: String): LocalDate =
        parseDate(string(name))

    fun point(name: String): Coordinates =
        (data[name] as? GeoPoint)?.let { Coordinates(it.latitude, it.longitude) } ?: malformed(name)

    fun strings(name: String): List<String> =
        (data[name] as? List<*>)?.map { it as? String ?: malformed(name) } ?: malformed(name)

    fun stringSet(name: String): Set<String> =
        strings(name).let {
            if (it.distinct().size != it.size) malformed(name)
            it.toSet()
        }

    inline fun <reified E : Enum<E>> enum(name: String): E =
        enumValueOf(string(name))

    inline fun <reified E : Enum<E>> enumSet(name: String): Set<E> =
        stringSet(name).map { enumValueOf<E>(it) }.toSet()

    fun nested(name: String): Fields {
        val map = data[name] as? Map<*, *> ?: malformed(name)
        return Fields(
            "$path.$name",
            map.entries.associate { (it.key as? String ?: malformed(name)) to it.value },
        )
    }

    fun optionalNested(name: String): Fields? =
        if (data[name] == null) null else nested(name)
}

fun parseDate(value: String): LocalDate =
    LocalDate.parse(value).also { require(it.toString() == value && value.length == 10) }

fun Instant.stored(): Timestamp =
    Timestamp(epochSecond, nano)

fun Coordinates.stored(): GeoPoint =
    GeoPoint(latitude, longitude)
