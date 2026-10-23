package ernest.ascrcpy.ui.main

import ernest.ascrcpy.scrcpy.VirtualDisplay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VirtualDisplaySpecTest {
    @Test fun blankFieldsMeanDeviceDefaultSize() {
        assertEquals(VirtualDisplay(), parseVirtualDisplay("", "", ""))
    }

    @Test fun parsesNumericFields() {
        assertEquals(VirtualDisplay(1920, 1080), parseVirtualDisplay("1920", "1080", ""))
        assertEquals(VirtualDisplay(1920, 1080, 240), parseVirtualDisplay("1920", "1080", "240"))
    }

    @Test fun widthAndHeightAreRequiredTogether() {
        assertNull(parseVirtualDisplay("1920", "", ""))
        assertNull(parseVirtualDisplay("", "1080", ""))
        assertNull(parseVirtualDisplay("1920", "", "240"))
    }

    @Test fun rejectsZeroAndNonPositiveDpi() {
        assertNull(parseVirtualDisplay("0", "1080", ""))
        assertNull(parseVirtualDisplay("1920", "0", ""))
        assertNull(parseVirtualDisplay("1920", "1080", "0"))
    }

    @Test fun maxSizeUsesTheShortestEdgeAndTheAspectRatio() {
        assertEquals(0, maxSizeForShortEdge(0, 20f / 9f))
        assertEquals(1280, maxSizeForShortEdge(720, 16f / 9f))
        assertEquals(1600, maxSizeForShortEdge(720, 20f / 9f))
        assertEquals(540, maxSizeForShortEdge(540, 1f))
    }
}
