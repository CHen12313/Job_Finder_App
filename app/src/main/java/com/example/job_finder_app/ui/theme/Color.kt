package com.example.job_finder_app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Immutable

@Immutable
data class Palette(
    val bg: Color,
    val surf: Color,
    val surf2: Color,
    val ink: Color,
    val muted: Color,
    val faint: Color,
    val line: Color,
    val lineStrong: Color,
    val primary: Color,
    val primaryDeep: Color,
    val primaryInk: Color,
    val primarySoft: Color,
    val track: Color,
    val good: Color,
    val goodDeep: Color,
    val goodSoft: Color
)

val LightPalette = Palette(
    bg = Color(0xFFFAF4EE),
    surf = Color(0xFFFFFFFF),
    surf2 = Color(0xFFF4EBE1),
    ink = Color(0xFF2C231C),
    muted = Color(0xFF8A7B6E),
    faint = Color(0xFFA99B8E),
    line = Color(0xFFEBE0D4),
    lineStrong = Color(0xFFDACBBA),
    primary = Color(0xFFDD6347),
    primaryDeep = Color(0xFFC04E33),
    primaryInk = Color(0xFFFFFFFF),
    primarySoft = Color(0xFFFAE4DB),
    track = Color(0xFFEFE4D9),
    good = Color(0xFF5E9472),
    goodDeep = Color(0xFF3F7355),
    goodSoft = Color(0xFFDEEDE2)
)

val DarkPalette = Palette(
    bg = Color(0xFF211913),
    surf = Color(0xFF2B2119),
    surf2 = Color(0xFF352A20),
    ink = Color(0xFFF6ECE2),
    muted = Color(0xFFB7A595),
    faint = Color(0xFF8C7A6A),
    line = Color(0xFF3B2F24),
    lineStrong = Color(0xFF4A3B2D),
    primary = Color(0xFFF07A5C),
    primaryDeep = Color(0xFFF8997F),
    primaryInk = Color(0xFF23150F),
    primarySoft = Color(0x29F07A5C),
    track = Color(0xFF3B2F24),
    good = Color(0xFF74B28D),
    goodDeep = Color(0xFF93C7A8),
    goodSoft = Color(0x2974B28D)
)




