package com.powerhold.app.ui.theme

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Lightweight, zero-overhead icon definitions containing only the icons used by Powerhold.
 * Replaces the heavy `material-icons-extended` dependency (~20MB+ uncompressed DEX bloat).
 */
object AppIcons {

    val Power: ImageVector = materialIcon(name = "Power") {
        materialPath {
            moveTo(13.0f, 3.0f)
            horizontalLineToRelative(-2.0f)
            verticalLineToRelative(10.0f)
            horizontalLineToRelative(2.0f)
            verticalLineTo(3.0f)
            close()
            moveTo(17.83f, 4.64f)
            lineToRelative(-1.42f, 1.42f)
            curveTo(18.06f, 7.57f, 19.0f, 9.68f, 19.0f, 12.0f)
            curveToRelative(0.0f, 3.87f, -3.13f, 7.0f, -7.0f, 7.0f)
            reflectiveCurveToRelative(-7.0f, -3.13f, -7.0f, -7.0f)
            curveToRelative(0.0f, -2.32f, 0.94f, -4.43f, 2.58f, -5.93f)
            lineTo(6.17f, 4.64f)
            curveTo(4.22f, 6.48f, 3.0f, 9.1f, 3.0f, 12.0f)
            curveToRelative(0.0f, 4.97f, 4.03f, 9.0f, 9.0f, 9.0f)
            reflectiveCurveToRelative(9.0f, -4.03f, 9.0f, -9.0f)
            curveToRelative(0.0f, -2.9f, -1.22f, -5.52f, -3.17f, -7.36f)
            close()
        }
    }

    val Flashlight: ImageVector = materialIcon(name = "Flashlight") {
        materialPath {
            moveTo(6.0f, 2.0f)
            horizontalLineToRelative(12.0f)
            verticalLineToRelative(3.0f)
            lineToRelative(-4.0f, 4.0f)
            verticalLineToRelative(11.0f)
            curveToRelative(0.0f, 1.1f, -0.9f, 2.0f, -2.0f, 2.0f)
            reflectiveCurveToRelative(-2.0f, -0.9f, -2.0f, -2.0f)
            verticalLineTo(9.0f)
            lineTo(6.0f, 5.0f)
            verticalLineTo(2.0f)
            close()
            moveTo(12.0f, 13.0f)
            curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
            reflectiveCurveToRelative(0.45f, 1.0f, 1.0f, 1.0f)
            reflectiveCurveToRelative(1.0f, -0.45f, 1.0f, -1.0f)
            reflectiveCurveToRelative(-0.45f, -1.0f, -1.0f, -1.0f)
            close()
        }
    }

    val ScreenRotation: ImageVector = materialIcon(name = "ScreenRotation") {
        materialPath {
            moveTo(16.48f, 2.52f)
            curveToRelative(3.27f, 1.55f, 5.61f, 4.72f, 5.97f, 8.48f)
            horizontalLineToRelative(1.5f)
            curveTo(23.44f, 4.84f, 18.29f, 0.0f, 12.0f, 0.0f)
            lineToRelative(-0.66f, 0.03f)
            lineToRelative(3.81f, 3.81f)
            lineToRelative(1.33f, -1.32f)
            close()
            moveTo(7.52f, 21.48f)
            curveTo(4.25f, 19.94f, 1.91f, 16.76f, 1.55f, 13.0f)
            horizontalLineTo(0.05f)
            curveTo(0.56f, 19.16f, 5.71f, 24.0f, 12.0f, 24.0f)
            lineToRelative(0.66f, -0.03f)
            lineToRelative(-3.81f, -3.81f)
            lineToRelative(-1.33f, 1.32f)
            close()
            moveTo(10.23f, 1.75f)
            curveToRelative(-0.59f, -0.59f, -1.54f, -0.59f, -2.12f, 0.0f)
            lineTo(1.75f, 8.11f)
            curveToRelative(-0.59f, 0.59f, -0.59f, 1.54f, 0.0f, 2.12f)
            lineToRelative(12.02f, 12.02f)
            curveToRelative(0.59f, 0.59f, 1.54f, 0.59f, 2.12f, 0.0f)
            lineToRelative(6.36f, -6.36f)
            curveToRelative(0.59f, -0.59f, 0.59f, -1.54f, 0.0f, -2.12f)
            lineTo(10.23f, 1.75f)
            close()
        }
    }

    val Bedtime: ImageVector = materialIcon(name = "Bedtime") {
        materialPath {
            moveTo(12.3f, 2.0f)
            curveToRelative(0.43f, 0.0f, 0.85f, 0.03f, 1.28f, 0.08f)
            curveTo(7.88f, 3.42f, 3.5f, 8.52f, 3.5f, 14.65f)
            curveToRelative(0.0f, 6.78f, 5.42f, 12.28f, 12.18f, 12.35f)
            curveToRelative(1.08f, 0.01f, 2.13f, -0.15f, 3.12f, -0.47f)
            curveTo(16.74f, 29.87f, 10.9f, 30.5f, 5.8f, 26.5f)
            close()
            moveTo(9.27f, 4.49f)
            curveTo(5.2f, 7.02f, 2.5f, 11.63f, 2.5f, 16.5f)
            curveTo(2.5f, 23.95f, 8.55f, 30.0f, 16.0f, 30.0f)
            curveToRelative(3.54f, 0.0f, 6.76f, -1.37f, 9.17f, -3.61f)
            close()
        }
    }

    val VolumeUp: ImageVector = materialIcon(name = "VolumeUp") {
        materialPath {
            moveTo(3.0f, 9.0f)
            verticalLineToRelative(6.0f)
            horizontalLineToRelative(4.0f)
            lineToRelative(5.0f, 5.0f)
            verticalLineTo(4.0f)
            lineTo(7.0f, 9.0f)
            horizontalLineTo(3.0f)
            close()
            moveTo(16.5f, 12.0f)
            curveToRelative(0.0f, -1.77f, -1.02f, -3.29f, -2.5f, -4.03f)
            verticalLineToRelative(8.05f)
            curveToRelative(1.48f, -0.73f, 2.5f, -2.25f, 2.5f, -4.02f)
            close()
            moveTo(14.0f, 3.23f)
            verticalLineToRelative(2.06f)
            curveToRelative(2.89f, 0.86f, 5.0f, 3.54f, 5.0f, 6.71f)
            reflectiveCurveToRelative(-2.11f, 5.85f, -5.0f, 6.71f)
            verticalLineToRelative(2.06f)
            curveToRelative(4.01f, -0.91f, 7.0f, -4.49f, 7.0f, -8.77f)
            reflectiveCurveToRelative(-2.99f, -7.86f, -7.0f, -8.77f)
            close()
        }
    }

    val BatterySaver: ImageVector = materialIcon(name = "BatterySaver") {
        materialPath {
            moveTo(16.0f, 4.0f)
            horizontalLineToRelative(-1.0f)
            verticalLineTo(2.0f)
            horizontalLineToRelative(-6.0f)
            verticalLineToRelative(2.0f)
            horizontalLineTo(8.0f)
            curveTo(7.45f, 4.0f, 7.0f, 4.45f, 7.0f, 5.0f)
            verticalLineToRelative(16.0f)
            curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
            horizontalLineToRelative(8.0f)
            curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
            verticalLineTo(5.0f)
            curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
            close()
            moveTo(15.0f, 14.0f)
            horizontalLineToRelative(-2.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(-2.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineTo(9.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(2.0f)
            close()
        }
    }

    val InvertColors: ImageVector = materialIcon(name = "InvertColors") {
        materialPath {
            moveTo(17.66f, 7.93f)
            lineTo(12.0f, 2.27f)
            lineTo(6.34f, 7.93f)
            curveToRelative(-3.12f, 3.12f, -3.12f, 8.19f, 0.0f, 11.31f)
            curveTo(7.9f, 20.8f, 9.95f, 21.58f, 12.0f, 21.58f)
            reflectiveCurveToRelative(4.1f, -0.78f, 5.66f, -2.34f)
            curveToRelative(3.12f, -3.12f, 3.12f, -8.19f, 0.0f, -11.31f)
            close()
            moveTo(12.0f, 19.59f)
            curveToRelative(-1.6f, 0.0f, -3.11f, -0.62f, -4.24f, -1.76f)
            curveTo(6.62f, 16.69f, 6.0f, 15.19f, 6.0f, 13.59f)
            reflectiveCurveToRelative(0.62f, -3.11f, 1.76f, -4.24f)
            lineTo(12.0f, 5.1f)
            verticalLineToRelative(14.49f)
            close()
        }
    }

    val Nightlight: ImageVector = materialIcon(name = "Nightlight") {
        materialPath {
            moveTo(14.0f, 2.0f)
            curveToRelative(0.0f, 0.06f, 0.01f, 0.11f, 0.01f, 0.17f)
            curveToRelative(-0.1f, 5.44f, 4.15f, 9.92f, 9.58f, 10.22f)
            curveToRelative(0.14f, 0.01f, 0.27f, 0.01f, 0.41f, 0.01f)
            curveToRelative(0.0f, 5.52f, -4.48f, 10.0f, -10.0f, 10.0f)
            reflectiveCurveTo(4.0f, 17.52f, 4.0f, 12.0f)
            reflectiveCurveTo(8.48f, 2.0f, 14.0f, 2.0f)
            close()
        }
    }

    val Bluetooth: ImageVector = materialIcon(name = "Bluetooth") {
        materialPath {
            moveTo(17.71f, 7.71f)
            lineTo(12.0f, 2.0f)
            horizontalLineToRelative(-1.0f)
            verticalLineToRelative(7.59f)
            lineTo(6.41f, 5.0f)
            lineTo(5.0f, 6.41f)
            lineTo(10.59f, 12.0f)
            lineTo(5.0f, 17.59f)
            lineTo(6.41f, 19.0f)
            lineTo(11.0f, 14.41f)
            verticalLineTo(22.0f)
            horizontalLineToRelative(1.0f)
            lineToRelative(5.71f, -5.71f)
            lineToRelative(-4.3f, -4.29f)
            lineToRelative(4.3f, -4.29f)
            close()
            moveTo(13.0f, 5.83f)
            lineToRelative(1.88f, 1.88f)
            lineTo(13.0f, 9.59f)
            verticalLineTo(5.83f)
            close()
            moveTo(14.88f, 16.29f)
            lineTo(13.0f, 18.17f)
            verticalLineToRelative(-3.76f)
            lineToRelative(1.88f, 1.88f)
            close()
        }
    }

    val Wifi: ImageVector = materialIcon(name = "Wifi") {
        materialPath {
            moveTo(12.0f, 4.0f)
            curveTo(7.31f, 4.0f, 3.07f, 5.9f, 0.0f, 8.98f)
            lineTo(12.0f, 21.0f)
            lineTo(24.0f, 8.98f)
            curveTo(20.93f, 5.9f, 16.69f, 4.0f, 12.0f, 4.0f)
            close()
        }
    }

    val SignalCellular: ImageVector = materialIcon(name = "SignalCellular") {
        materialPath {
            moveTo(2.0f, 22.0f)
            horizontalLineToRelative(20.0f)
            verticalLineTo(2.0f)
            lineTo(2.0f, 22.0f)
            close()
        }
    }

    val AirplaneMode: ImageVector = materialIcon(name = "AirplaneMode") {
        materialPath {
            moveTo(21.0f, 16.0f)
            verticalLineToRelative(-2.0f)
            lineToRelative(-8.0f, -5.0f)
            verticalLineTo(3.5f)
            curveToRelative(0.0f, -0.83f, -0.67f, -1.5f, -1.5f, -1.5f)
            reflectiveCurveTo(10.0f, 2.67f, 10.0f, 3.5f)
            verticalLineTo(9.0f)
            lineToRelative(-8.0f, 5.0f)
            verticalLineToRelative(2.0f)
            lineToRelative(8.0f, -2.5f)
            verticalLineTo(19.0f)
            lineToRelative(-2.0f, 1.5f)
            verticalLineTo(22.0f)
            lineToRelative(3.5f, -1.0f)
            lineToRelative(3.5f, 1.0f)
            verticalLineToRelative(-1.5f)
            lineTo(13.0f, 19.0f)
            verticalLineToRelative(-5.5f)
            lineToRelative(8.0f, 2.5f)
            close()
        }
    }

    val DataSaver: ImageVector = materialIcon(name = "DataSaver") {
        materialPath {
            moveTo(13.0f, 2.05f)
            verticalLineToRelative(3.03f)
            curveToRelative(3.39f, 0.49f, 6.0f, 3.39f, 6.0f, 6.92f)
            curveToRelative(0.0f, 0.9f, -0.18f, 1.75f, -0.48f, 2.54f)
            lineToRelative(2.6f, 1.53f)
            curveTo(21.68f, 14.83f, 22.0f, 13.45f, 22.0f, 12.0f)
            curveToRelative(0.0f, -5.18f, -3.95f, -9.45f, -9.0f, -9.95f)
            close()
            moveTo(12.0f, 19.0f)
            curveToRelative(-3.87f, 0.0f, -7.0f, -3.13f, -7.0f, -7.0f)
            curveToRelative(0.0f, -3.53f, 2.61f, -6.43f, 6.0f, -6.92f)
            verticalLineTo(2.05f)
            curveTo(5.95f, 2.55f, 2.0f, 6.82f, 2.0f, 12.0f)
            curveToRelative(0.0f, 5.52f, 4.47f, 10.0f, 9.99f, 10.0f)
            curveToRelative(3.31f, 0.0f, 6.24f, -1.61f, 8.04f, -4.09f)
            lineToRelative(-2.54f, -1.46f)
            curveTo(16.14f, 17.92f, 14.21f, 19.0f, 12.0f, 19.0f)
            close()
            moveTo(11.0f, 8.0f)
            verticalLineToRelative(3.0f)
            horizontalLineTo(8.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(3.0f)
            verticalLineToRelative(3.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(-3.0f)
            horizontalLineToRelative(3.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineToRelative(-3.0f)
            verticalLineTo(8.0f)
            horizontalLineToRelative(-2.0f)
            close()
        }
    }

    val Location: ImageVector = materialIcon(name = "Location") {
        materialPath {
            moveTo(12.0f, 2.0f)
            curveTo(8.13f, 2.0f, 5.0f, 5.13f, 5.0f, 9.0f)
            curveToRelative(0.0f, 5.25f, 7.0f, 13.0f, 7.0f, 13.0f)
            reflectiveCurveToRelative(7.0f, -7.75f, 7.0f, -13.0f)
            curveToRelative(0.0f, -3.87f, -3.13f, -7.0f, -7.0f, -7.0f)
            close()
            moveTo(12.0f, 11.5f)
            curveToRelative(-1.38f, 0.0f, -2.5f, -1.12f, -2.5f, -2.5f)
            reflectiveCurveToRelative(1.12f, -2.5f, 2.5f, -2.5f)
            reflectiveCurveToRelative(2.5f, 1.12f, 2.5f, 2.5f)
            reflectiveCurveToRelative(-1.12f, 2.5f, -2.5f, 2.5f)
            close()
        }
    }

    val Videocam: ImageVector = materialIcon(name = "Videocam") {
        materialPath {
            moveTo(17.0f, 10.5f)
            verticalLineTo(7.0f)
            curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
            horizontalLineTo(4.0f)
            curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
            verticalLineToRelative(10.0f)
            curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
            horizontalLineToRelative(12.0f)
            curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
            verticalLineToRelative(-3.5f)
            lineToRelative(4.0f, 4.0f)
            verticalLineToRelative(-11.0f)
            lineToRelative(-4.0f, 4.0f)
            close()
        }
    }

    val Mic: ImageVector = materialIcon(name = "Mic") {
        materialPath {
            moveTo(12.0f, 14.0f)
            curveToRelative(1.66f, 0.0f, 3.0f, -1.34f, 3.0f, -3.0f)
            verticalLineTo(5.0f)
            curveToRelative(0.0f, -1.66f, -1.34f, -3.0f, -3.0f, -3.0f)
            reflectiveCurveTo(9.0f, 3.34f, 9.0f, 5.0f)
            verticalLineToRelative(6.0f)
            curveToRelative(0.0f, 1.66f, 1.34f, 3.0f, 3.0f, 3.0f)
            close()
            moveTo(17.3f, 11.0f)
            curveToRelative(0.0f, 3.0f, -2.54f, 5.1f, -5.3f, 5.1f)
            reflectiveCurveTo(6.7f, 14.0f, 6.7f, 11.0f)
            horizontalLineTo(5.0f)
            curveToRelative(0.0f, 3.41f, 2.72f, 6.23f, 6.0f, 6.72f)
            verticalLineTo(21.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(-3.28f)
            curveToRelative(3.28f, -0.48f, 6.0f, -3.3f, 6.0f, -6.72f)
            horizontalLineToRelative(-1.7f)
            close()
        }
    }

    val Widgets: ImageVector = materialIcon(name = "Widgets") {
        materialPath {
            moveTo(13.0f, 13.0f)
            verticalLineToRelative(8.0f)
            horizontalLineToRelative(8.0f)
            verticalLineToRelative(-8.0f)
            horizontalLineToRelative(-8.0f)
            close()
            moveTo(3.0f, 21.0f)
            horizontalLineToRelative(8.0f)
            verticalLineToRelative(-8.0f)
            horizontalLineTo(3.0f)
            verticalLineToRelative(8.0f)
            close()
            moveTo(3.0f, 3.0f)
            verticalLineToRelative(8.0f)
            horizontalLineToRelative(8.0f)
            verticalLineTo(3.0f)
            horizontalLineTo(3.0f)
            close()
            moveTo(16.66f, 1.69f)
            lineTo(11.0f, 7.34f)
            lineTo(16.66f, 13.0f)
            lineToRelative(5.66f, -5.66f)
            lineToRelative(-5.66f, -5.65f)
            close()
        }
    }

    val Terminal: ImageVector = materialIcon(name = "Terminal") {
        materialPath {
            moveTo(20.0f, 4.0f)
            horizontalLineTo(4.0f)
            curveTo(2.89f, 4.0f, 2.0f, 4.9f, 2.0f, 6.0f)
            verticalLineToRelative(12.0f)
            curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 2.0f, 2.0f)
            horizontalLineToRelative(16.0f)
            curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
            verticalLineTo(6.0f)
            curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
            close()
            moveTo(7.5f, 13.5f)
            lineToRelative(-2.0f, -2.0f)
            lineTo(7.0f, 10.0f)
            lineToRelative(3.5f, 3.5f)
            lineTo(7.0f, 17.0f)
            lineToRelative(-1.5f, -1.5f)
            lineToRelative(2.0f, -2.0f)
            close()
            moveTo(17.0f, 17.0f)
            horizontalLineToRelative(-5.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineToRelative(5.0f)
            verticalLineToRelative(2.0f)
            close()
        }
    }

    val ContentCopy: ImageVector = materialIcon(name = "ContentCopy") {
        materialPath {
            moveTo(16.0f, 1.0f)
            horizontalLineTo(4.0f)
            curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
            verticalLineToRelative(14.0f)
            horizontalLineToRelative(2.0f)
            verticalLineTo(3.0f)
            horizontalLineToRelative(12.0f)
            verticalLineTo(1.0f)
            close()
            moveTo(19.0f, 5.0f)
            horizontalLineTo(8.0f)
            curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
            verticalLineToRelative(14.0f)
            curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
            horizontalLineToRelative(11.0f)
            curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
            verticalLineTo(7.0f)
            curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
            close()
            moveTo(19.0f, 21.0f)
            horizontalLineTo(8.0f)
            verticalLineTo(7.0f)
            horizontalLineToRelative(11.0f)
            verticalLineToRelative(14.0f)
            close()
        }
    }

    val PlayArrow: ImageVector = materialIcon(name = "PlayArrow") {
        materialPath {
            moveTo(8.0f, 5.0f)
            verticalLineToRelative(14.0f)
            lineToRelative(11.0f, -7.0f)
            close()
        }
    }

    val Code: ImageVector = materialIcon(name = "Code") {
        materialPath {
            moveTo(9.4f, 16.6f)
            lineTo(4.8f, 12.0f)
            lineToRelative(4.6f, -4.6f)
            lineTo(8.0f, 6.0f)
            lineToRelative(-6.0f, 6.0f)
            lineToRelative(6.0f, 6.0f)
            lineToRelative(1.4f, -1.4f)
            close()
            moveTo(14.6f, 16.6f)
            lineToRelative(4.6f, -4.6f)
            lineToRelative(-4.6f, -4.6f)
            lineTo(16.0f, 6.0f)
            lineToRelative(6.0f, 6.0f)
            lineToRelative(-6.0f, 6.0f)
            lineToRelative(-1.4f, -1.4f)
            close()
        }
    }

    val Apps: ImageVector = materialIcon(name = "Apps") {
        materialPath {
            moveTo(4.0f, 8.0f)
            horizontalLineToRelative(4.0f)
            verticalLineTo(4.0f)
            horizontalLineTo(4.0f)
            verticalLineToRelative(4.0f)
            close()
            moveTo(10.0f, 20.0f)
            horizontalLineToRelative(4.0f)
            verticalLineToRelative(-4.0f)
            horizontalLineToRelative(-4.0f)
            verticalLineToRelative(4.0f)
            close()
            moveTo(4.0f, 20.0f)
            horizontalLineToRelative(4.0f)
            verticalLineToRelative(-4.0f)
            horizontalLineTo(4.0f)
            verticalLineToRelative(4.0f)
            close()
            moveTo(4.0f, 14.0f)
            horizontalLineToRelative(4.0f)
            verticalLineToRelative(-4.0f)
            horizontalLineTo(4.0f)
            verticalLineToRelative(4.0f)
            close()
            moveTo(10.0f, 14.0f)
            horizontalLineToRelative(4.0f)
            verticalLineToRelative(-4.0f)
            horizontalLineToRelative(-4.0f)
            verticalLineToRelative(4.0f)
            close()
            moveTo(16.0f, 4.0f)
            verticalLineToRelative(4.0f)
            horizontalLineToRelative(4.0f)
            verticalLineTo(4.0f)
            horizontalLineToRelative(-4.0f)
            close()
            moveTo(10.0f, 8.0f)
            horizontalLineToRelative(4.0f)
            verticalLineTo(4.0f)
            horizontalLineToRelative(-4.0f)
            verticalLineToRelative(4.0f)
            close()
            moveTo(16.0f, 14.0f)
            horizontalLineToRelative(4.0f)
            verticalLineToRelative(-4.0f)
            horizontalLineToRelative(-4.0f)
            verticalLineToRelative(4.0f)
            close()
            moveTo(16.0f, 20.0f)
            horizontalLineToRelative(4.0f)
            verticalLineToRelative(-4.0f)
            horizontalLineToRelative(-4.0f)
            verticalLineToRelative(4.0f)
            close()
        }
    }
}
