package com.ahmadshahwaiz.ghoststrings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import com.ghoststrings.sdk.GhostLanguage
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import com.ghoststrings.sdk.GhostStrings
import com.ghoststrings.sdk.GhostStringsContextWrapper
import com.ghoststrings.sdk.GhostStringsProvider

// ─── Professional Typography (System-based SF mimic) ────────────────────────
private val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp),
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, lineHeight = 24.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium)
)

// ─── Palette (matches ghoststrings.com) ─────────────────────────────────────
private val BG         = Color(0xFFF8FAFC)
private val Surface    = Color(0xFFFFFFFF)
private val SurfaceAlt = Color(0xFFE2E8F0)
private val Accent     = Color(0xFF2563EB)
private val Muted      = Color(0xFF64748B)
private val TextMain   = Color(0xFF0F172A)
private val Green      = Color(0xFF16A34A)

/**
 * The demo activity showcasing GhostStrings integration.
 *
 * Notice how:
 * 1. The code calls standard [stringResource] and [Context.getString].
 *    No wrapper code or custom lookups are needed in individual views.
 * 
 * 2. You only add ONE wrapper at the root of your app.
 */
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: android.content.Context) {
        super.attachBaseContext(GhostStringsContextWrapper(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            // This wrapper enables stringResource() interception for ALL Composables
            GhostStringsProvider {
                val strings by GhostStrings.strings.collectAsState()
                key(strings) {
                    MaterialTheme {
                        DemoScreen()
                    }
                }
            }
        }
    }
}

// ─── Root screen ──────────────────────────────────────────────────────────────
@Composable
fun DemoScreen() {
    // We observe the StateFlow to trigger recomposition when new OTA values arrive
    Box(modifier = Modifier.fillMaxSize().background(BG)) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Accent.copy(alpha = 0.08f), Color.Transparent),
                        center = Offset(size.width / 2, 0f),
                        radius = size.width * 0.85f
                    )
                )
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))
            LiveBadge()
            Spacer(Modifier.height(28.dp))
            HeroSection()
            Spacer(Modifier.height(40.dp))
            StatsRow()
            Spacer(Modifier.height(40.dp))
            FeaturesSection()
            Spacer(Modifier.height(40.dp))
            PromoBanner()
            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
fun <T> GhostAnimatedContent(
    targetState: T,
    label: String,
    transitionSpec: AnimatedContentTransitionScope<T>.() -> ContentTransform = { fadeIn() togetherWith fadeOut() },
    content: @Composable (T) -> Unit
) {
    if (GhostStrings.isAnimationEnabled) {
        AnimatedContent(targetState = targetState, label = label, transitionSpec = transitionSpec) {
            content(it)
        }
    } else {
        content(targetState)
    }
}

@Composable
fun LiveBadge() {
    val text = stringResource(R.string.hero_badge)

    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "pulseAlpha"
    )
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFEFF6FF))
            .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(50))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(Modifier.size(7.dp).clip(CircleShape).background(Green.copy(alpha = pulse)))
        GhostAnimatedContent(targetState = text, label = "badge") {
            Text(it, color = Accent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun HeroSection() {
    val context = LocalContext.current
    
    val heroTitle    = stringResource(R.string.hero_title)
    val heroSubtitle = stringResource(R.string.hero_subtitle)
    val ctaPrimary   = stringResource(R.string.cta_primary)
    val ctaSecondary = stringResource(R.string.cta_secondary)

    GhostAnimatedContent(
        heroTitle, label = "title",
        transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) }
    ) {
        Text(
            text = it,
            color = TextMain,
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            lineHeight = 42.sp,
            letterSpacing = (-1).sp
        )
    }
    Spacer(Modifier.height(16.dp))
    GhostAnimatedContent(
        heroSubtitle, label = "subtitle",
        transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) }
    ) {
        Text(it, color = Muted, fontSize = 15.sp, textAlign = TextAlign.Center, lineHeight = 22.sp)
    }
    Spacer(Modifier.height(32.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = {
                context.startActivity(
                    android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse("https://ghoststrings.ai")
                    )
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(48.dp)
        ) {
            GhostAnimatedContent(ctaPrimary, label = "cta1") {
                Text(it, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
        OutlinedButton(
            onClick = {
                context.startActivity(
                    android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse("https://ghoststrings.ai/docs.html")
                    )
                )
            },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SurfaceAlt),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Muted),
            modifier = Modifier.height(48.dp)
        ) {
            GhostAnimatedContent(ctaSecondary, label = "cta2") {
                Text(it, color = Muted, fontSize = 14.sp)
            }
        }
    }
    
    Spacer(Modifier.height(16.dp))
    
    val coroutineScope = rememberCoroutineScope()
    var isSyncing by remember { mutableStateOf(false) }
    var supportedLanguages by remember { mutableStateOf<List<GhostLanguage>>(emptyList()) }
    var activeLang by remember { mutableStateOf(GhostStrings.getLanguage() ?: "en") }

    LaunchedEffect(Unit) {
        try {
            android.util.Log.d("GhostStrings", "Fetching supported languages on startup...")
            supportedLanguages = GhostStrings.getSupportedLanguages()
            android.util.Log.d("GhostStrings", "Supported languages list: $supportedLanguages")
        } catch (e: Exception) {
            android.util.Log.e("GhostStrings", "Error loading supported languages", e)
        }
    }
    
    OutlinedButton(
        onClick = { 
            isSyncing = true
            android.util.Log.d("GhostStrings", "Forcing string sync...")
            GhostStrings.sync(force = true) { success ->
                isSyncing = false
                android.util.Log.d("GhostStrings", "Sync success: $success")
                coroutineScope.launch {
                    try {
                        supportedLanguages = GhostStrings.getSupportedLanguages(force = true)
                        android.util.Log.d("GhostStrings", "Supported languages list after sync: $supportedLanguages")
                    } catch (e: Exception) {
                        android.util.Log.e("GhostStrings", "Error loading languages after sync", e)
                    }
                }
            }
        },
        enabled = !isSyncing,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Accent.copy(alpha = 0.3f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Accent),
        modifier = Modifier.fillMaxWidth().height(48.dp)
    ) {
        Text(if (isSyncing) "Checking for updates..." else "Check for Updates",
            fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }

    // Always show the language selector — seeded with English by default,
    // populates with additional languages once the API response arrives.
    Spacer(Modifier.height(16.dp))
    Text("Active Language", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(8.dp))

    var expanded by remember { mutableStateOf(false) }
    // English is always present; extra languages merge in from the API
    val allLangs = remember(supportedLanguages, activeLang) {
        val list = mutableListOf(GhostLanguage("en", "English"))
        supportedLanguages.forEach { lang ->
            if (lang.localeId != "en") {
                list.add(lang)
            }
        }
        val activeLocaleId = activeLang ?: "en"
        if (list.none { it.localeId == activeLocaleId }) {
            val locale = java.util.Locale(activeLocaleId)
            val name = locale.getDisplayLanguage(locale).replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() }
            list.add(GhostLanguage(activeLocaleId, name))
        }
        list
    }
    val currentLangObj = allLangs.find { it.localeId == (activeLang ?: "en") } ?: allLangs.first()


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Color.LightGray.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp))
            .border(BorderStroke(1.dp, Accent.copy(alpha = 0.2f)), shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable { expanded = true }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${currentLangObj.label} (${currentLangObj.localeId.uppercase()})",
                color = TextMain,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "▼",
                color = TextMain,
                fontSize = 11.sp
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            allLangs.forEach { lang ->
                DropdownMenuItem(
                    text = { Text("${lang.label} (${lang.localeId.uppercase()})") },
                    onClick = {
                        activeLang = lang.localeId
                        GhostStrings.setLanguage(if (lang.localeId == "en") null else lang.localeId)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun StatsRow() {
    val context = LocalContext.current
    val stat1Value = context.getString(R.string.stat_1_value)
    val stat1Label = context.getString(R.string.stat_1_label)
    val stat2Value = context.getString(R.string.stat_2_value)
    val stat2Label = context.getString(R.string.stat_2_label)
    val stat3Value = context.getString(R.string.stat_3_value)
    val stat3Label = context.getString(R.string.stat_3_label)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Surface)
            .border(1.dp, SurfaceAlt, RoundedCornerShape(20.dp))
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(stat1Value, stat1Label)
        HorizontalDivider(modifier = Modifier.width(1.dp).height(48.dp), color = SurfaceAlt)
        StatItem(stat2Value, stat2Label)
        HorizontalDivider(modifier = Modifier.width(1.dp).height(48.dp), color = SurfaceAlt)
        StatItem(stat3Value, stat3Label)
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedContent(
            value, label = "statVal",
            transitionSpec = { slideInVertically { -it } + fadeIn() togetherWith slideOutVertically { it } + fadeOut() }
        ) {
            Text(it, color = Accent, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(4.dp))
        Text(label, color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun FeaturesSection() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        FeatureCard(
            icon  = stringResource(R.string.feature_1_icon),
            title = stringResource(R.string.feature_1_title),
            desc  = stringResource(R.string.feature_1_desc),
            tint  = Accent.copy(alpha = 0.12f)
        )
        FeatureCard(
            icon  = stringResource(R.string.feature_2_icon),
            title = stringResource(R.string.feature_2_title),
            desc  = stringResource(R.string.feature_2_desc),
            tint  = Color(0xFF0EA5E9).copy(alpha = 0.1f)
        )
        FeatureCard(
            icon  = stringResource(R.string.feature_3_icon),
            title = stringResource(R.string.feature_3_title),
            desc  = stringResource(R.string.feature_3_desc),
            tint  = Color(0xFF10B981).copy(alpha = 0.1f)
        )
    }
}

@Composable
fun FeatureCard(icon: String, title: String, desc: String, tint: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .border(1.dp, SurfaceAlt, RoundedCornerShape(16.dp))
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(tint),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(icon, label = "icon", transitionSpec = { fadeIn() togetherWith fadeOut() }) {
                Text(it, fontSize = 22.sp)
            }
        }
        Column {
            AnimatedContent(title, label = "ftitle", transitionSpec = { fadeIn() togetherWith fadeOut() }) {
                Text(it, color = TextMain, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(Modifier.height(3.dp))
            AnimatedContent(desc, label = "fdesc", transitionSpec = { fadeIn() togetherWith fadeOut() }) {
                Text(it, color = Muted, fontSize = 13.sp, lineHeight = 18.sp)
            }
        }
    }
}

@Composable
fun PromoBanner() {
    val text = stringResource(R.string.promo_text)
    AnimatedContent(
        text, label = "promo",
        transitionSpec = { slideInVertically { it } + fadeIn() togetherWith slideOutVertically { -it } + fadeOut() }
    ) { t ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFEFF6FF))
                .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(14.dp))
                .padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(t, color = Accent, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        }
    }
}
