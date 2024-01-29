package com.crud.userswebfluxcoroutine

import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.Row
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.DialectResolver
import java.nio.ByteBuffer
import java.util.UUID

@Configuration
class R2dbcConfiguration {

    @Bean
    fun customConversions(connectionFactory: ConnectionFactory): R2dbcCustomConversions {
        val converters = listOf(
            ListStringReadConverter(),
            ListStringWriteConverter(),
            UUIDReadConverter(),
            UUIDWriteConverter(),
            ByteBufferToUUIDConverter()
        )

        return R2dbcCustomConversions.of(DialectResolver.getDialect(connectionFactory), converters)
    }

}

@ReadingConverter
class ListStringReadConverter : Converter<Row, List<String>> {
    override fun convert(source: Row): List<String> {
        val text = source.get("stack", String::class.java)
        return StringListConverter.convertToEntityAttribute(text)
    }
}

@WritingConverter
class ListStringWriteConverter : Converter<List<String>, String> {
    override fun convert(source: List<String>): String {
        return StringListConverter.convertToDatabaseColumn(source)
    }
}

@ReadingConverter
class UUIDReadConverter : Converter<Row, UUID> {
    override fun convert(row: Row): UUID {
        val bb = ByteBuffer.wrap(row.get("id", ByteArray::class.java))
        return UUID(bb.getLong(), bb.getLong())
    }
}

@ReadingConverter
class ByteBufferToUUIDConverter : Converter<ByteBuffer, UUID> {
    override fun convert(source: ByteBuffer): UUID {
        val buffer = source.duplicate()
        val mostSigBits = buffer.long
        val leastSigBits = buffer.long
        return UUID(mostSigBits, leastSigBits)
    }
}

@WritingConverter
class UUIDWriteConverter : Converter<UUID, ByteArray> {
    override fun convert(uuid: UUID): ByteArray {
        val bb = ByteBuffer.wrap(ByteArray(16))
        bb.putLong(uuid.mostSignificantBits)
        bb.putLong(uuid.leastSignificantBits)
        return bb.array()
    }
}

fun UUID.toByteArray(): ByteArray {
    return UUIDWriteConverter().convert(this)
}