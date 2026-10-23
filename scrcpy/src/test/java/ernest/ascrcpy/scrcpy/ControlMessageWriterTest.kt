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

    @Test fun serializesStartAppAsTypeLengthAndUtf8Name(): Unit = runBlocking {
        val out = ByteArrayOutputStream()
        val writer = ControlMessageWriter(capturing(out))
        writer.startApp("com.miui.home")
        val written = out.toByteArray()
        assertArrayEquals(byteArrayOf(16, 12), written.copyOfRange(0, 2))
        assertArrayEquals("com.miui.home".toByteArray(Charsets.UTF_8), written.copyOfRange(2, written.size))
    }

    private fun capturing(out: ByteArrayOutputStream) = object : AdbChannel {
        override suspend fun read(buffer: ByteArray, offset: Int, length: Int): Int = -1
        override suspend fun write(buffer: ByteArray, offset: Int, length: Int) {
            out.write(buffer, offset, length)
        }
        override fun close() = Unit
    }
}
