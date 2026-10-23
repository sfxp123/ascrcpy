package ernest.ascrcpy.scrcpy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ServerCommandTest {
    @Test fun pinsTheBundledServerVersionAndPath() {
        val command = buildServerCommand(ScrcpyConfig(), 0x1a2b3c4d)
        assertTrue(command.startsWith(
            "CLASSPATH=/data/local/tmp/ascrcpy-server.jar app_process / com.genymobile.scrcpy.Server 5.0.1 "))
        assertTrue(command.contains("scid=1a2b3c4d tunnel_forward=true"))
    }

    @Test fun keepsTheOptionsTheClientParses() {
        val command = buildServerCommand(ScrcpyConfig(), 1)
        assertTrue(command.contains("audio=false video_codec=h264"))
        assertTrue(command.endsWith("send_device_meta=false send_dummy_byte=false cleanup=false"))
        // turnScreenOff is a control message, not a server option.
        assertFalse(command.contains("turn_screen_off"))
        assertFalse(command.contains("new_display"))
    }

    @Test fun carriesResolutionBitrateAndFps() {
        val command = buildServerCommand(ScrcpyConfig(maxSize = 1280, videoBitRate = 4_000_000, maxFps = 30), 1)
        assertTrue(command.contains("max_size=1280"))
        assertTrue(command.contains("video_bit_rate=4000000"))
        assertTrue(command.contains("max_fps=30"))
    }

    @Test fun encodesVirtualDisplayVariants() {
        assertTrue(buildServerCommand(ScrcpyConfig(virtualDisplay = VirtualDisplay()), 1)
            .contains("new_display= "))
        assertTrue(buildServerCommand(ScrcpyConfig(virtualDisplay = VirtualDisplay(1920, 1080)), 1)
            .contains("new_display=1920x1080 "))
        assertTrue(buildServerCommand(ScrcpyConfig(virtualDisplay = VirtualDisplay(1920, 1080, 240)), 1)
            .contains("new_display=1920x1080/240 "))
        assertTrue(buildServerCommand(ScrcpyConfig(virtualDisplay = VirtualDisplay(dpi = 240)), 1)
            .contains("new_display=/240 "))
    }

    @Test fun rejectsInvalidVirtualDisplayDimensions() {
        val invalid = listOf(
            { VirtualDisplay(1920, 0) },
            { VirtualDisplay(0, 1080) },
            { VirtualDisplay(-1, -1) },
            { VirtualDisplay(0, 0, -1) },
        )
        invalid.forEach { build ->
            var thrown = false
            try { build() } catch (error: IllegalArgumentException) { thrown = true }
            assertTrue(thrown)
        }
    }

    @Test fun unlimitedResolutionIsEncodedAsZero() {
        assertTrue(buildServerCommand(ScrcpyConfig(maxSize = 0), 1).contains("max_size=0"))
    }

    @Test fun turnScreenOffDoesNotAlterTheServerCommand() {
        assertEquals(
            buildServerCommand(ScrcpyConfig(), 7),
            buildServerCommand(ScrcpyConfig(turnScreenOff = true), 7),
        )
    }
}
