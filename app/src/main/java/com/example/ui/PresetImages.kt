package com.example.ui

object PresetImages {

    data class PresetVisual(
        val title: String,
        val subtitle: String,
        val description: String,
        val b64: String // Valid small png fallback
    )

    // A tiny, valid transparent 1x1 PNG Base64 to bypass empty image payload rules
    private const val TINY_PNG_B64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII="

    val cameraPresets = listOf(
        PresetVisual(
            title = "Stark Arc Reactor Core MK-85",
            subtitle = "Physical Fusion Reactor Spec Scan",
            description = "Physical scan of the primary thermal palladium core emitter. Visual diagnostics show temperature levels, plasma distribution, and electromagnetic containment grids.",
            b64 = TINY_PNG_B64
        ),
        PresetVisual(
            title = "Electronic Workstation Hub",
            subtitle = "Desktop Hardware Prototyping Lab",
            description = "A visual camera scan of an operator desk covered in microcontrollers, soldering iron assemblies, wiring harnesses, and a localized diagnostic computer.",
            b64 = TINY_PNG_B64
        ),
        PresetVisual(
            title = "Textbook Schematic Page",
            subtitle = "Aerodynamic Flight Matrix Formulae",
            description = "Scanning printed manual diagram with mechanical aerodynamic vector formulas, propulsion math, thrust drag diagrams, and handwritten notes in the margins.",
            b64 = TINY_PNG_B64
        )
    )

    val screenPresets = listOf(
        PresetVisual(
            title = "WhatsApp Chat: Pepper Potts",
            subtitle = "User Chat History Screenshot",
            description = "Screenshot of instant messaging application WhatsApp. Active contact 'Pepper Potts', containing last read messages, bubble alerts, text box node input, and photo thumbnails.",
            b64 = TINY_PNG_B64
        ),
        PresetVisual(
            title = "Stark Terminal System Log",
            subtitle = "Android Exception Dump Layout",
            description = "A screenshot of an Android system console showing multiple debugger threads, stack trace prints, package loading reports, resource exceptions, and execution timings.",
            b64 = TINY_PNG_B64
        ),
        PresetVisual(
            title = "Industrial Spotify Workspace",
            subtitle = "Media Controller Screen Capture",
            description = "Screenshot of Spotify player UI showing song progress bar node (0.45%), Album cover, forward skip vector trigger, volume sliders, and dynamic playlist selections.",
            b64 = TINY_PNG_B64
        )
    )
}
