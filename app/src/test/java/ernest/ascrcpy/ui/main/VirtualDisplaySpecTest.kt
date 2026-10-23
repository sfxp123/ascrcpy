package ernest.ascrcpy.ui.main

import ernest.ascrcpy.scrcpy.VirtualDisplay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VirtualDisplaySpecTest {
    @Test fun blankSpecMeansDeviceDefaultSize() {
        assertEquals(VirtualDisplay(), parseVirtualDisplaySpec(""))
        assertEquals(VirtualDisplay(), parseVirtualDisplaySpec("   "))
    }

    @Test fun parsesSizeAndOptionalDpi() {
        assertEquals(VirtualDisplay(1920, 1080), parseVirtualDisplaySpec("1920x1080"))
        assertEquals(VirtualDisplay(1920, 1080, 240), parseVirtualDisplaySpec("1920x1080/240"))
        assertEquals(VirtualDisplay(1280, 720), parseVirtualDisplaySpec(" 1280x720 "))
    }

    @Test fun rejectsMalformedSpecs() {
        listOf("1920", "x1080", "1920*1080", "1920x1080/", "1920x1080/dpi", "1920x1080/0",
            "0x1080", "abc", "1920x1080/240/1").forEach { spec ->
            assertNull("spec '$spec' must be rejected", parseVirtualDisplaySpec(spec))
        }
    }
}
