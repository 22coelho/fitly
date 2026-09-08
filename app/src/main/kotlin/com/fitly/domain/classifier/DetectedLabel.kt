package com.fitly.domain.classifier

/** One label an on-device image classifier reported for a photo, with its confidence (0f-1f). */
data class DetectedLabel(val text: String, val confidence: Float)
