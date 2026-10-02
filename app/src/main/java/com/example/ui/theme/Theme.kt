package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme = darkColorScheme(
  primary = EmeraldDarkPrimary,
  onPrimary = Color(0xFF00381E),
  primaryContainer = Color(0xFF0F5132),
  onPrimaryContainer = Color(0xFFA1F5C6),
  secondary = GoldDarkSecondary,
  onSecondary = Color(0xFF402E00),
  secondaryContainer = Color(0xFF5A4200),
  onSecondaryContainer = Color(0xFFFFDF8E),
  background = EmeraldDarkBackground,
  onBackground = Color(0xFFE2E3DF),
  surface = EmeraldDarkSurface,
  onSurface = Color(0xFFE2E3DF),
  surfaceVariant = Color(0xFF243029),
  onSurfaceVariant = Color(0xFFC3C8C2),
  outline = Color(0xFF3F4A42)
)

private val LightColorScheme = lightColorScheme(
  primary = EmeraldPrimary,
  onPrimary = EmeraldOnPrimary,
  primaryContainer = EmeraldContainer,
  onPrimaryContainer = EmeraldOnContainer,
  secondary = GoldSecondary,
  onSecondary = GoldOnSecondary,
  secondaryContainer = GoldContainer,
  onSecondaryContainer = GoldOnContainer,
  tertiary = BronzeTertiary,
  tertiaryContainer = BronzeContainer,
  background = WarmLightBackground,
  onBackground = Color(0xFF1E211E),
  surface = WarmLightSurface,
  onSurface = Color(0xFF1E211E),
  surfaceVariant = Color(0xFFF1F3EE),
  onSurfaceVariant = Color(0xFF454B44),
  outline = Color(0xFFD6DAD4)
)

@Composable
fun AhlulbaytPricingTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}
