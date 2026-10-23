package ernest.ascrcpy.scrcpy

import ernest.ascrcpy.adb.AdbChannel
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class ControlMessageWriterTest {
    @Test fun serializesSetDisplayPowerAsTypeAndFlag(): Unit = runBlocking {
        val out = ByteArrayOutputStream()
        val writer = ControlMessageWriter(capturing(out))
        writer.setDisplayPower(false)
        writer.setDisplayPower(true)
        assertArrayEquals(byteArrayOf(10, 0, 10, 1), out.toByteArray())
    }

    private fun capturing(out: ByteArrayOutputStream) = object : AdbChannel {
        override suspend fun read(buffer: ByteArray, offset: Int, length: Int): Int = -1
        override suspend fun write(buffer: ByteArray, offset: Int, length: Int) {
            out.write(buffer, offset, length)
        }
        override fun close() = Unit
    }
}
