package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import com.example.data.database.JarvisNote
import com.example.data.database.JarvisReminder
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JarvisMainUi(viewModel: JarvisViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val activeVoice by viewModel.selectedVoice.collectAsState()
    
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                // Elegant Dark deep background
                drawRect(StarkDeepBlack)
                // Central Ambient Glow (Cyan border gradient style with blur of 72.dp size equivalent)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x1C06B6D4), Color.Transparent),
                        center = Offset(size.width / 2f, size.height / 2f),
                        radius = size.width * 0.9f
                    ),
                    radius = size.width * 0.9f,
                    center = Offset(size.width / 2f, size.height / 2f)
                )
            },
        containerColor = Color.Transparent,
        bottomBar = {
            JarvisBottomBar(
                currentScreen = currentScreen,
                onScreenSelected = { viewModel.setScreen(it) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // High Tech HUD Top Banner
            HudBannerHeader(
                isListening = isListening,
                isSpeaking = isSpeaking,
                voiceName = activeVoice
            )

            // Dynamic Screen Presentation
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                when (currentScreen) {
                    "home" -> HomeScreen(viewModel = viewModel)
                    "chat" -> ChatScreen(viewModel = viewModel)
                    "automation" -> AutomationScreen(viewModel = viewModel)
                    "vision" -> VisionScreen(viewModel = viewModel)
                    "notes" -> NotesScreen(viewModel = viewModel)
                    "settings" -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}

// Custom Glassmorphic container with neon borders
@Composable
fun GlassmorphicPanel(
    modifier: Modifier = Modifier,
    borderColor: Color = GlassPanelBorder,
    backgroundColor: Color = GlassSpaceBackground,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

// --- TOP HUD BAR ---
@Composable
fun HudBannerHeader(isListening: Boolean, isSpeaking: Boolean, voiceName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "SYSTEM STATUS",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = StarkBrightCyan.copy(alpha = 0.6f),
                letterSpacing = 2.sp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "JARVIS ",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light,
                    color = TextPrimaryWhite,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "V5.5",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = StarkBrightCyan
                )
            }
        }

        // Diagnostics metrics simulation using beautiful Elegant Dark capsules
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Capsule 1
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x0DFFFFFF))
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(
                            if (isListening) StarkBrightCyan
                            else if (isSpeaking) StarkHoloTeal
                            else StarkCyberBlue.copy(alpha = 0.5f)
                        )
                )
                Text(
                    text = "TEMP: 42°C",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = TextPrimaryWhite
                )
            }

            // Capsule 2
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x0DFFFFFF))
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val nameUpper = voiceName.uppercase()
                val voiceShortName = if (nameUpper.length > 8) nameUpper.take(6) + ".." else nameUpper
                Text(
                    text = "VOICE: $voiceShortName",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = StarkBrightCyan
                )
            }
        }
    }
}

// --- INTERACTIVE NEON WEB ORB ---
@Composable
fun ScientificJarvisOrb(
    isListening: Boolean,
    isSpeaking: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    
    // Rotating orbits
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Breathing inner core size
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    // Speaking sonic wave expanding factor
    val speakWaveFactor by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "speaking"
    )

    Canvas(
        modifier = modifier
            .size(200.dp)
            .padding(16.dp)
    ) {
        val width = size.width
        val height = size.height
        val center = Offset(width / 2, height / 2)
        val limitRadius = width.coerceAtMost(height) / 2

        // Determine core glowing colors depending on state
        val coreColor = when {
            isListening -> StarkBrightCyan
            isSpeaking -> StarkHoloTeal
            else -> StarkCyberBlue
        }

        // Draw animated voice waves if Jarvis is speaking
        if (isSpeaking) {
            for (i in 1..3) {
                val cycleAlpha = (1f - (speakWaveFactor / 65f)).coerceIn(0f, 1f)
                val dynamicRadius = limitRadius * 0.45f + (speakWaveFactor * (i * 0.35f))
                drawCircle(
                    color = StarkHoloTeal.copy(alpha = cycleAlpha * 0.49f),
                    radius = dynamicRadius,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }

        // Animated expansion bands when listening (sound detection)
        if (isListening) {
            drawCircle(
                color = StarkBrightCyan.copy(alpha = 0.25f),
                radius = limitRadius * breatheScale,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Outer Rotating Matrix Ring (Rotates clockwise)
        rotate(rotationAngle, center) {
            // Draw a dashed sci-fi orbit tracker
            val dashRadius = limitRadius * 0.88f
            drawCircle(
                color = coreColor.copy(alpha = 0.35f),
                radius = dashRadius,
                center = center,
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                        floatArrayOf(20f, 10f, 40f, 10f), 0f
                    )
                )
            )

            // Custom tech accent ticks around the rotation
            for (angle in 0 until 360 step 45) {
                val radian = Math.toRadians(angle.toDouble())
                val cos = Math.cos(radian).toFloat()
                val sin = Math.sin(radian).toFloat()
                
                val startPoint = Offset(center.x + (dashRadius - 10f) * cos, center.y + (dashRadius - 10f) * sin)
                val endPoint = Offset(center.x + (dashRadius + 10f) * cos, center.y + (dashRadius + 10f) * sin)
                
                drawLine(
                    color = coreColor.copy(alpha = 0.7f),
                    start = startPoint,
                    end = endPoint,
                    strokeWidth = 2.dp.toPx()
                )
            }
        }

        // Inner reverse counter-rotating indicator
        rotate(-rotationAngle * 1.5f, center) {
            drawCircle(
                color = coreColor.copy(alpha = 0.5f),
                radius = limitRadius * 0.65f,
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(45f, 45f))
                )
            )
        }

        // Glowing Solid Spherical Core with dynamic breeding scale
        val coreRadius = limitRadius * 0.35f * (if (isSpeaking) 1.22f else breatheScale)
        
        // Solid central matrix core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(coreColor, coreColor.copy(alpha = 0.12f)),
                center = center,
                radius = coreRadius
            ),
            radius = coreRadius,
            center = center
        )

        // Ultra intense high-energy center node
        drawCircle(
            color = Color.White,
            radius = coreRadius * 0.25f,
            center = center
        )
    }
}

// --- SCREEN 1: HOME ---
@Composable
fun HomeScreen(viewModel: JarvisViewModel) {
    val isListening by viewModel.isListening.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val textState by viewModel.userInputText.collectAsState()
    val callsUserSir by viewModel.callsUserSir.collectAsState()

    val sirTitle = if (callsUserSir) "Sir" else ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "ALWAYS LISTENING",
            fontFamily = FontFamily.SansSerif,
            fontSize = 13.sp,
            color = StarkBrightCyan,
            fontWeight = FontWeight.Medium,
            letterSpacing = 2.sp,
            modifier = Modifier.testTag("locator_id")
        )

        // Reactive Scientific Orb Widget
        ScientificJarvisOrb(
            isListening = isListening,
            isSpeaking = isSpeaking,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Wake word descriptor panel with Elegant Dark styling
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Wake Phrase: \"Hey Jarvis\" or \"Jarvis\"",
                fontFamily = FontFamily.Monospace,
                color = TextHoloMuted,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "\"Hey Jarvis, send a message to Rahul...\"",
                fontFamily = FontFamily.SansSerif,
                color = TextPrimaryWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Light,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        // Immersive quick-trigger task buttons
        GlassmorphicPanel(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "COMMUNICATION FLOW DEPLOYMENT",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = StarkBrightCyan,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Task 1
                Button(
                    onClick = { viewModel.runInstagramAutomation() },
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .testTag("insta_command_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x1500E5FF)),
                    border = BorderStroke(1.dp, GlassPanelBorder),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Bolt,
                            contentDescription = "Insta icon",
                            tint = StarkBrightCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Insta Post\nCaption",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryWhite,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Task 2
                Button(
                    onClick = { viewModel.runWhatsAppAutomation() },
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .testTag("whatsapp_command_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x1500E5FF)),
                    border = BorderStroke(1.dp, GlassPanelBorder),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Message,
                            contentDescription = "Message icon",
                            tint = StarkBrightCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Send message\nto Rahul",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryWhite,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Task 3
                Button(
                    onClick = { viewModel.runSpotifyAutomation() },
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .testTag("spotify_command_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x1500E5FF)),
                    border = BorderStroke(1.dp, GlassPanelBorder),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Spotify icon",
                            tint = StarkBrightCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Play Workout\nMusic",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryWhite,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Tap to voice command portal with Elegant Dark glass styling
        GlassmorphicPanel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isListening) "SPEECH SPECTRUM COUPLING..." else "PRESS TO ENGAGE RECOGNITION VECTORS",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = if (isListening) StarkBrightCyan else TextPrimaryWhite,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Large Voice activation Circle button
                FloatingActionButton(
                    onClick = { viewModel.startVoiceListening() },
                    modifier = Modifier
                        .size(68.dp)
                        .testTag("voice_talk_button"),
                    containerColor = if (isListening) StarkBrightCyan else StarkSlateDark,
                    contentColor = if (isListening) StarkDeepBlack else StarkBrightCyan,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = "Voice key triggers",
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Optional keyboard textbox entry underneath
                OutlinedTextField(
                    value = textState,
                    onValueChange = { viewModel.updateUserInput(it) },
                    placeholder = { Text("Command keyboard override...", color = Color(0x5500E5FF), fontSize = 12.sp) },
                    textStyle = TextStyle(color = TextPrimaryWhite, fontSize = 13.sp, fontFamily = FontFamily.Monospace),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hud_input_text")
                        .padding(horizontal = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (textState.isNotBlank()) {
                                    viewModel.processUserCommand(textState)
                                    viewModel.updateUserInput("")
                                }
                            }
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = "submit text core", tint = StarkBrightCyan)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StarkBrightCyan,
                        unfocusedBorderColor = GlassPanelBorder,
                        focusedContainerColor = Color(0x1A03070E),
                        unfocusedContainerColor = Color(0x0A03070E)
                    )
                )
            }
        }
    }
}

// --- SCREEN 2: CHAT TERMINAL ---
@Composable
fun ChatScreen(viewModel: JarvisViewModel) {
    val messages by viewModel.messageHistory.collectAsState()
    val currentStyle by viewModel.characterStyle.collectAsState()
    val textState by viewModel.userInputText.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()

    val stylesList = listOf("Professional", "Friendly", "Casual", "Funny", "Gen-Z", "Hinglish")
    var expandedStyles by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Holographic style configuration selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GlassPanelBorder, RoundedCornerShape(8.dp))
                .background(Color(0x3300E5FF), RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "STYLE ENGINE: $currentStyle",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = StarkBrightCyan
            )

            Box {
                Button(
                    onClick = { expandedStyles = !expandedStyles },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("style_dropdown_trigger"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x2200B0FF)),
                    border = BorderStroke(1.dp, StarkCyberBlue),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("MODIFY", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = TextPrimaryWhite)
                }

                DropdownMenu(
                    expanded = expandedStyles,
                    onDismissRequest = { expandedStyles = false },
                    modifier = Modifier
                        .background(StarkSlateDark)
                        .border(1.dp, GlassPanelBorder)
                ) {
                    stylesList.forEach { style ->
                        DropdownMenuItem(
                            text = { Text(style, fontFamily = FontFamily.Monospace, color = TextPrimaryWhite, fontSize = 12.sp) },
                            onClick = {
                                viewModel.setCharacterStyle(style)
                                expandedStyles = false
                            }
                        )
                    }
                }
            }
        }

        // Active chat message queue
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(1.dp, Color(0x1500E5FF), RoundedCornerShape(12.dp))
                .background(Color(0x9903070E), RoundedCornerShape(12.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                val isJarvis = msg.sender == "jarvis"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isJarvis) Alignment.Start else Alignment.End
                ) {
                    // Chat speech bubble glass panel
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .border(
                                width = 1.dp,
                                color = if (isJarvis) Color(0x2500E5FF) else Color(0x20ECEFF1),
                                shape = RoundedCornerShape(
                                    topStart = 12.dp,
                                    topEnd = 12.dp,
                                    bottomStart = if (isJarvis) 0.dp else 12.dp,
                                    bottomEnd = if (isJarvis) 12.dp else 0.dp
                                )
                            )
                            .background(
                                color = if (isJarvis) Color(0x1800E5FF) else Color(0x0AECEFF1),
                                shape = RoundedCornerShape(
                                    topStart = 12.dp,
                                    topEnd = 12.dp,
                                    bottomStart = if (isJarvis) 0.dp else 12.dp,
                                    bottomEnd = if (isJarvis) 12.dp else 0.dp
                                )
                            )
                            .padding(12.dp)
                    ) {
                        Column {
                            // Header metric
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isJarvis) "⚡ JARVIS AI" else "👤 OPERATOR SIR",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isJarvis) StarkBrightCyan else Color.White
                                )
                                Text(
                                    text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(msg.timestamp)),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 7.sp,
                                    color = TextHoloMuted
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = msg.text,
                                fontSize = 13.sp,
                                color = TextPrimaryWhite,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // Input row with quick action and audio stop controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stop speech dynamic key
            if (isSpeaking) {
                IconButton(
                    onClick = { viewModel.stopSpeaking() },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(StarkBurnOrange.copy(alpha = 0.2f))
                        .border(1.dp, StarkBurnOrange, CircleShape)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = "stop speeches", tint = StarkBurnOrange)
                }
            }

            OutlinedTextField(
                value = textState,
                onValueChange = { viewModel.updateUserInput(it) },
                placeholder = { Text("Compile speech block details...", color = Color(0x5500E5FF), fontSize = 12.sp) },
                textStyle = TextStyle(color = TextPrimaryWhite, fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_text"),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (textState.isNotBlank()) {
                                viewModel.processUserCommand(textState)
                                viewModel.updateUserInput("")
                            }
                        }
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = "dispatch node", tint = StarkBrightCyan)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = StarkBrightCyan,
                    unfocusedBorderColor = GlassPanelBorder,
                    focusedContainerColor = Color(0x1F03070E),
                    unfocusedContainerColor = Color(0x0F03070E)
                )
            )

            // Direct keyboard deletion button
            IconButton(
                onClick = { viewModel.clearChat() },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0x1F03070E))
                    .border(1.dp, GlassPanelBorder, CircleShape)
                    .testTag("clear_chat_button")
            ) {
                Icon(Icons.Default.Delete, contentDescription = "wipe feeds", tint = SparkRed)
            }
        }
    }
}

// Spark colors alias
val SparkRed = Color(0xFFFF5722)

// --- SCREEN 3: AUTOMATION AND PROCESS CONTROL ---
@Composable
fun AutomationScreen(viewModel: JarvisViewModel) {
    val steps by viewModel.activeAutomationSteps.collectAsState()
    val isRunning by viewModel.isAutomationRunning.collectAsState()
    val logs by viewModel.automationProgressLog.collectAsState()
    val historyLog by viewModel.allAutomations.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlassmorphicPanel(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ACCESSIBILITY OPERATION SIMULATOR",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarkBrightCyan,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Text(
                    text = "Trigger multi-step auto-navigation routines bypassing touch inputs dynamically.",
                    fontSize = 10.sp,
                    color = TextHoloMuted,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Activation keys
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.runInstagramAutomation() },
                        enabled = !isRunning,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("trigger_insta"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x2200E5FF)),
                        border = BorderStroke(1.dp, StarkBrightCyan)
                    ) {
                        Text("INSTAGRAM", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = TextPrimaryWhite)
                    }

                    Button(
                        onClick = { viewModel.runWhatsAppAutomation() },
                        enabled = !isRunning,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("trigger_wa"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x2200E5FF)),
                        border = BorderStroke(1.dp, StarkBrightCyan)
                    ) {
                        Text("WHATSAPP", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = TextPrimaryWhite)
                    }

                    Button(
                        onClick = { viewModel.runSpotifyAutomation() },
                        enabled = !isRunning,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("trigger_spotify"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x2200E5FF)),
                        border = BorderStroke(1.dp, StarkBrightCyan)
                    ) {
                        Text("SPOTIFY", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = TextPrimaryWhite)
                    }
                }
            }
        }

        // Active processes steps trackers
        if (steps.isNotEmpty()) {
            item {
                GlassmorphicPanel(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ACTIVE PIPELINE SEQUENCES",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StarkBrightCyan
                        )
                        if (isRunning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = StarkBrightCyan,
                                strokeWidth = 2.dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    steps.forEachIndexed { idx, st ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = when (st.status) {
                                    "Success" -> Icons.Default.CheckCircle
                                    "Running" -> Icons.Default.Bolt
                                    else -> Icons.Default.Info
                                },
                                contentDescription = "Step indicators",
                                tint = when (st.status) {
                                    "Success" -> StarkHoloTeal
                                    "Running" -> StarkBrightCyan
                                    else -> Color.Gray
                                },
                                modifier = Modifier.size(16.dp)
                            )

                            Column {
                                Text(
                                    text = st.title,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = if (st.status == "Running") StarkBrightCyan else TextPrimaryWhite
                                )
                                Text(
                                    text = st.description,
                                    fontSize = 10.sp,
                                    color = if (st.status == "Running") StarkCyberBlue.copy(alpha = 0.8f) else Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Real-time logs panel
            item {
                GlassmorphicPanel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    Text(
                        "SECURITY CONSOLE LOG DUMPS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = StarkBrightCyan,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF02050B))
                            .border(1.dp, Color(0x3300B0FF), RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(logs) { log ->
                                Text(
                                    text = log,
                                    color = StarkHoloTeal,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Database historical traces
        item {
            Text(
                text = "HISTORIC ACCESS DIAGNOSTIC LOGS",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = StarkCyberBlue,
                modifier = Modifier.padding(start = 4.dp, top = 6.dp, bottom = 4.dp)
            )
        }

        if (historyLog.isEmpty()) {
            item {
                Text(
                    text = "No historical logs saved in local Room Database.",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        } else {
            items(historyLog) { hist ->
                GlassmorphicPanel(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = Color(0x1500E5FF)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = hist.command,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryWhite
                            )
                            Text(
                                text = SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.getDefault()).format(Date(hist.timestamp)),
                                fontSize = 9.sp,
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    if (hist.status == "Success") StarkHoloTeal.copy(alpha = 0.15f)
                                    else StarkBurnOrange.copy(alpha = 0.15f),
                                    RoundedCornerShape(4.dp)
                                )
                                .border(
                                    1.dp,
                                    if (hist.status == "Success") StarkHoloTeal else StarkBurnOrange,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = hist.status.uppercase(),
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (hist.status == "Success") StarkHoloTeal else StarkBurnOrange
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN 4: SENSOR VISION (CAMERA/SCREEN SCANNING) ---
@Composable
fun VisionScreen(viewModel: JarvisViewModel) {
    val cameraStatus by viewModel.cameraStatusText.collectAsState()
    val isCameraProcessing by viewModel.isCameraProcessing.collectAsState()
    val activeCameraPreset by viewModel.selectedPresetImageIndex.collectAsState()

    val screenStatus by viewModel.screenStatusText.collectAsState()
    val isScreenProcessing by viewModel.isScreenProcessing.collectAsState()
    val activeScreenPreset by viewModel.selectedPresetScreenIndex.collectAsState()

    var activeTab by remember { mutableStateOf("lens") } // "lens" or "screen"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Toggle tab header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x1F03070E), RoundedCornerShape(12.dp))
                .border(1.dp, GlassPanelBorder, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Button(
                onClick = { activeTab = "lens" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeTab == "lens") StarkBrightCyan.copy(alpha = 0.2f) else Color.Transparent
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Camera, contentDescription = "camera", tint = StarkBrightCyan)
                    Text("CAMERA LENS", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextPrimaryWhite)
                }
            }

            Button(
                onClick = { activeTab = "screen" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeTab == "screen") StarkBrightCyan.copy(alpha = 0.2f) else Color.Transparent
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Code, contentDescription = "screen", tint = StarkBrightCyan)
                    Text("SCREEN INTEL", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextPrimaryWhite)
                }
            }
        }

        if (activeTab == "lens") {
            // Camera vision panel
            GlassmorphicPanel(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "STARK INDUSTRIES SMART EYE LENSES",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarkBrightCyan
                )
                Text(
                    "Simulate target capture utilizing integrated neural spectacles. Picks a diagnostic image preset below:",
                    fontSize = 10.sp,
                    color = TextHoloMuted,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Select image preset card horizontal strip
                PresetImages.cameraPresets.forEachIndexed { index, preset ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(
                                1.dp,
                                if (activeCameraPreset == index) StarkBrightCyan else Color(0x1100E5FF),
                                RoundedCornerShape(8.dp)
                            )
                            .background(
                                if (activeCameraPreset == index) Color(0x1F00E5FF) else Color(0x3003070E),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                viewModel.selectPresetImage(index)
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(preset.title, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = TextPrimaryWhite)
                            Text(preset.subtitle, fontSize = 9.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                        }
                        if (activeCameraPreset == index) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "active", tint = StarkBrightCyan, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action execution
                Button(
                    onClick = {
                        val active = PresetImages.cameraPresets[activeCameraPreset]
                        viewModel.analyzeCameraFeed(activeCameraPreset, active.title, active.b64)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("scan_camera_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x1100E5FF)),
                    border = BorderStroke(1.dp, StarkBrightCyan)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (isCameraProcessing) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = StarkBrightCyan, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Bolt, contentDescription = "lens laser info")
                        }
                        Text("DECRYPT FOCUS TARGET SPECTRUM", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextPrimaryWhite)
                    }
                }
            }

            // Report dump panel
            GlassmorphicPanel(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "DECRYPTED SPECTRAL REPORT",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarkHoloTeal,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0x3300E676), RoundedCornerShape(8.dp))
                        .background(Color(0xFF02050B))
                        .padding(12.dp)
                ) {
                    Text(
                        text = cameraStatus,
                        color = StarkHoloTeal,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp,
                        textAlign = TextAlign.Start
                    )
                }
            }
        } else {
            // Screen Intelligence page
            GlassmorphicPanel(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "ON-DEVICE ACCESSIBILITY SCREEN UNDERSTANDING",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarkBrightCyan
                )
                Text(
                    "Simulate binary analysis of on-device node layouts based on an app screens snapshot. Pick a diagnostic preset:",
                    fontSize = 10.sp,
                    color = TextHoloMuted,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Select image preset card horizontal strip
                PresetImages.screenPresets.forEachIndexed { index, preset ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(
                                1.dp,
                                if (activeScreenPreset == index) StarkBrightCyan else Color(0x1100E5FF),
                                RoundedCornerShape(8.dp)
                            )
                            .background(
                                if (activeScreenPreset == index) Color(0x1F00E5FF) else Color(0x3003070E),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                viewModel.selectPresetScreen(index)
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(preset.title, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = TextPrimaryWhite)
                            Text(preset.subtitle, fontSize = 9.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                        }
                        if (activeScreenPreset == index) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "active", tint = StarkBrightCyan, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action execution
                Button(
                    onClick = {
                        val active = PresetImages.screenPresets[activeScreenPreset]
                        viewModel.analyzeScreenFeed(activeScreenPreset, active.title, active.b64)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("scan_screen_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x1100E5FF)),
                    border = BorderStroke(1.dp, StarkBrightCyan)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (isScreenProcessing) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = StarkBrightCyan, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Bolt, contentDescription = "screen layout compiler")
                        }
                        Text("RESOLVE NODE HIERARCHY DIRECTIVES", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextPrimaryWhite)
                    }
                }
            }

            // Screen layout report pane
            GlassmorphicPanel(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "DIGITIZED SCREEN MAP METRICS",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarkBrightCyan,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GlassPanelBorder, RoundedCornerShape(8.dp))
                        .background(Color(0xFF02050B))
                        .padding(12.dp)
                ) {
                    Text(
                        text = screenStatus,
                        color = StarkBrightCyan,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

// --- SCREEN 5: NOTES & REMINDERS DATABASE INDEX ---
@Composable
fun NotesScreen(viewModel: JarvisViewModel) {
    val notes by viewModel.allNotes.collectAsState()
    val reminders by viewModel.activeReminders.collectAsState()

    var activeViewTab by remember { mutableStateOf("notes") } // "notes" or "reminders"

    var inputTitle by remember { mutableStateOf("") }
    var inputBody by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Toggle header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x1F03070E), RoundedCornerShape(12.dp))
                .border(1.dp, GlassPanelBorder, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Button(
                onClick = { activeViewTab = "notes" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeViewTab == "notes") StarkBrightCyan.copy(alpha = 0.2f) else Color.Transparent
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("KNOWLEDGE (ROOM DB)", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextPrimaryWhite)
            }

            Button(
                onClick = { activeViewTab = "reminders" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeViewTab == "reminders") StarkBrightCyan.copy(alpha = 0.2f) else Color.Transparent
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("REMINDER MATRIX", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextPrimaryWhite)
            }
        }

        if (activeViewTab == "notes") {
            // Notes submission box
            GlassmorphicPanel(modifier = Modifier.fillMaxWidth()) {
                Text("RECORD TO KNOWLEDGE INDEX", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = StarkBrightCyan)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = inputTitle,
                    onValueChange = { inputTitle = it },
                    placeholder = { Text("Title index...", color = Color(0x5500E5FF), fontSize = 12.sp) },
                    textStyle = TextStyle(color = TextPrimaryWhite, fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("note_title_input"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StarkBrightCyan,
                        unfocusedBorderColor = GlassPanelBorder
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = inputBody,
                    onValueChange = { inputBody = it },
                    placeholder = { Text("Record detailed memory content here...", color = Color(0x5500E5FF), fontSize = 12.sp) },
                    textStyle = TextStyle(color = TextPrimaryWhite, fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .testTag("note_body_input"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StarkBrightCyan,
                        unfocusedBorderColor = GlassPanelBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (inputTitle.isNotBlank() && inputBody.isNotBlank()) {
                            scope.launch {
                                viewModel.repository.insertNote(
                                    JarvisNote(title = inputTitle, content = inputBody, category = "Knowledge")
                                )
                                inputTitle = ""
                                inputBody = ""
                                viewModel.speak("Indexed to SQLite core successfully, Sir.")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_note_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x2200E5FF)),
                    border = BorderStroke(1.dp, StarkBrightCyan)
                ) {
                    Text("SYNCHRONIZE TO LOCAL ROOM DB", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = TextPrimaryWhite)
                }
            }

            // Grid displaying existing database items
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (notes.isEmpty()) {
                    item {
                        Text(
                            "Database empty, Sir. Instruct me to remember data to populate this index.",
                            color = Color.Gray,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        )
                    }
                } else {
                    items(notes) { note ->
                        GlassmorphicPanel(
                            borderColor = Color(0x1F00E5FF)
                        ) {
                            Text(
                                text = note.title,
                                color = StarkBrightCyan,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                            Text(
                                text = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault()).format(Date(note.timestamp)),
                                color = TextHoloMuted,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            Text(
                                text = note.content,
                                color = TextPrimaryWhite,
                                fontSize = 11.sp,
                                maxLines = 3,
                                modifier = Modifier.weight(1f)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = { viewModel.deleteNote(note) },
                                    modifier = Modifier.size(24.dp).testTag("delete_note_${note.id}")
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Trash",
                                        tint = StarkBurnOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Reminders schedule mapping list
            GlassmorphicPanel(modifier = Modifier.fillMaxWidth()) {
                Text("INITIALIZED ALERT MATRIX", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = StarkBrightCyan)
                Text("Chronologically sorted background alarm vectors.", fontSize = 9.sp, color = TextHoloMuted, modifier = Modifier.padding(bottom = 12.dp))

                var localRemText by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = localRemText,
                    onValueChange = { localRemText = it },
                    placeholder = { Text("Command schedule, e.g. clean thruster plates...", color = Color(0x5500E5FF), fontSize = 12.sp) },
                    textStyle = TextStyle(color = TextPrimaryWhite, fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reminder_text_input"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StarkBrightCyan,
                        unfocusedBorderColor = GlassPanelBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (localRemText.isNotBlank()) {
                            scope.launch {
                                viewModel.repository.insertReminder(
                                    JarvisReminder(
                                        text = localRemText,
                                        dateTime = System.currentTimeMillis() + (1000 * 60 * 60 * 4) // 4 hours out
                                    )
                                )
                                localRemText = ""
                                viewModel.speak("Scheduled alarm, Sir.")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_reminder_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x2200E5FF)),
                    border = BorderStroke(1.dp, StarkBrightCyan)
                ) {
                    Text("ADD VECTOR ALERT MATRIX", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = TextPrimaryWhite)
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (reminders.isEmpty()) {
                    item {
                        Text(
                            "No active alerting vectors scheduled.",
                            color = Color.Gray,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                } else {
                    items(reminders) { rem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, GlassPanelBorder, RoundedCornerShape(12.dp))
                                .background(Color(0x77080D1D), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = rem.text,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryWhite
                                )
                                Text(
                                    text = "EXECUTION DETECTOR: " + SimpleDateFormat("hh:mm a, MMM dd", Locale.getDefault()).format(Date(rem.dateTime)),
                                    fontSize = 9.sp,
                                    color = TextHoloMuted,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            IconButton(
                                onClick = { viewModel.completeReminder(rem) },
                                modifier = Modifier.testTag("complete_rem_${rem.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "mark completed action",
                                    tint = StarkHoloTeal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN 6: SETTINGS PANEL ---
@Composable
fun SettingsScreen(viewModel: JarvisViewModel) {
    val wakeText by viewModel.wakeWord.collectAsState()
    val sirState by viewModel.callsUserSir.collectAsState()
    val dynamicWittyFactor by viewModel.wittyFactor.collectAsState()
    val activeVoiceName by viewModel.selectedVoice.collectAsState()

    val voicesAvailable = listOf("Male Professional", "Male Futuristic", "Female Professional", "Female Futuristic")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GlassmorphicPanel(modifier = Modifier.fillMaxWidth()) {
            Text(
                "JARVIS DECODER DIRECTIVES",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = StarkBrightCyan,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Custom Wake Word
            Text("WAKE DIRECTIVITY WORD", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = TextHoloMuted)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = wakeText,
                onValueChange = { viewModel.setWakeWord(it) },
                textStyle = TextStyle(color = TextPrimaryWhite, fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wake_word_input"),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = StarkBrightCyan,
                    unfocusedBorderColor = GlassPanelBorder
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Sir calling option
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("PROTOCOL: STARK COUPLING ('SIR')", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextPrimaryWhite)
                    Text("Address the user as 'Sir' for Iron Man atmosphere", fontSize = 9.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                }
                Switch(
                    checked = sirState,
                    onCheckedChange = { viewModel.setCallsUserSir(it) },
                    modifier = Modifier.testTag("sir_switch")
                )
            }
        }

        GlassmorphicPanel(modifier = Modifier.fillMaxWidth()) {
            Text(
                "SPEECH ENGINE TUNER",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = StarkBrightCyan,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Select active voice pitch configurations
            Text("COGNITIVE VOCAL PRESET", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = TextHoloMuted, modifier = Modifier.padding(bottom = 6.dp))
            voicesAvailable.forEach { name ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(
                            1.dp,
                            if (activeVoiceName == name) StarkBrightCyan else Color(0x2200E5FF),
                            RoundedCornerShape(8.dp)
                        )
                        .background(
                            if (activeVoiceName == name) Color(0x3300E5FF) else Color(0x1F03070E),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.selectVoice(name) }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = name,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = if (activeVoiceName == name) FontWeight.Bold else FontWeight.Normal,
                        color = TextPrimaryWhite
                    )
                    if (activeVoiceName == name) {
                        Icon(Icons.Default.Star, contentDescription = "active settings", tint = StarkBrightCyan, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        GlassmorphicPanel(modifier = Modifier.fillMaxWidth()) {
            Text(
                "SYSTEM NEURAL DYNAMICS",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = StarkBrightCyan,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("WITTY FACTOR (REST COGNITION)", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextPrimaryWhite)
                Text(
                    text = String.format("%.2f", dynamicWittyFactor),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = StarkBrightCyan
                )
            }
            Slider(
                value = dynamicWittyFactor,
                onValueChange = { viewModel.setWittyFactor(it) },
                valueRange = 0.2f..1.1f,
                modifier = Modifier.testTag("witty_slider")
            )
        }
    }
}

// --- HUD BOTTOM NAVIGATION BAR ---
@Composable
fun JarvisBottomBar(
    currentScreen: String,
    onScreenSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp, top = 4.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0x0EFFFFFF)) // elegant translucent dark overlay (white/5%)
                .border(BorderStroke(1.dp, Color(0x1BFFFFFF)), RoundedCornerShape(32.dp))
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val navItems = listOf(
                Triple("home", "HUD", Icons.Default.Home),
                Triple("chat", "CHAT", Icons.Default.Chat),
                Triple("automation", "OPS", Icons.Default.Bolt),
                Triple("vision", "SENSORS", Icons.Default.Sensors),
                Triple("notes", "MEMORY", Icons.Default.Memory),
                Triple("settings", "CORE", Icons.Default.Settings)
            )

            navItems.forEach { (route, label, icon) ->
                val isSelected = currentScreen == route
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Color(0x1A22D3EE) else Color.Transparent) // Active translucent cyan-400 pill bg (cyan-500/10%)
                        .clickable { onScreenSelected(route) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            modifier = Modifier.size(20.dp),
                            tint = if (isSelected) StarkBrightCyan else Color.Gray
                        )
                        Text(
                            text = label,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) StarkBrightCyan else Color.Gray,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}
