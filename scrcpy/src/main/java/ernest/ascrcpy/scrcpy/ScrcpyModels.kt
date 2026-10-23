package ernest.ascrcpy.scrcpy

data class ScrcpyConfig(
    val maxSize: Int = 1920,
    val videoBitRate: Int = 8_000_000,
    val maxFps: Int = 60,
    val virtualDisplay: VirtualDisplay? = null,
    val turnScreenOff: Boolean = false,
) {
    init {
        require(maxSize >= 0)
        require(videoBitRate > 0)
        require(maxFps > 0)
    }
}

/**
 * scrcpy `new_display` value: an empty [width]/[height] pair means the device default size,
 * and a zero [dpi] lets the device pick one. Mirrors `NewDisplay` parsing in the server.
 */
data class VirtualDisplay(val width: Int = 0, val height: Int = 0, val dpi: Int = 0) {
    init {
        require((width == 0 && height == 0) || (width > 0 && height > 0))
        require(dpi >= 0)
    }

    fun toServerValue(): String = buildString {
        if (width > 0) append("${width}x${height}")
        if (dpi > 0) append("/$dpi")
    }
}

data class VideoSize(val width: Int, val height: Int)

sealed interface ScrcpyState {
    data object Idle : ScrcpyState
    data object InstallingServer : ScrcpyState
    data object StartingServer : ScrcpyState
    data object ConnectingStreams : ScrcpyState
    data class Streaming(val videoSize: VideoSize) : ScrcpyState
    data class Failed(val cause: Throwable) : ScrcpyState
}
