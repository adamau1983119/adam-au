package com.example.wtsaskingforsignature.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.wtsaskingforsignature.ui.theme.WtsDimens
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.BorderStroke
/* Use ButtonDefaults for outlined button colors on Material3 1.2.x */
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.wtsaskingforsignature.ui.theme.MdGradientBottom
import com.example.wtsaskingforsignature.ui.theme.MdGradientTop
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue

// 按鈕層級：Primary / Tonal / Outlined / Text，以一致高度與圓角
@Composable
fun WtsPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    large: Boolean = true,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(if (large) WtsDimens.ButtonHeightLarge else WtsDimens.ButtonHeightMedium),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) { Text(text, style = MaterialTheme.typography.titleMedium) }
}

@Composable
fun WtsTonalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    large: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(if (large) WtsDimens.ButtonHeightLarge else WtsDimens.ButtonHeightMedium),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.filledTonalButtonColors()
    ) { Text(text, style = MaterialTheme.typography.titleMedium) }
}

@Composable
fun WtsOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    large: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(if (large) WtsDimens.ButtonHeightLarge else WtsDimens.ButtonHeightMedium),
        shape = RoundedCornerShape(24.dp)
    ) { Text(text, style = MaterialTheme.typography.titleMedium) }
}

@Composable
fun WtsTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(onClick = onClick, modifier = modifier) { Text(text, style = MaterialTheme.typography.titleMedium) }
}

// 半透明白底按鈕：容器 50% 白、深色文字，用於粉霧背景上更清爽
@Composable
fun WtsWhiteButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    large: Boolean = true,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(if (large) WtsDimens.ButtonHeightLarge else WtsDimens.ButtonHeightMedium),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) { Text(text, style = MaterialTheme.typography.titleMedium) }
}

// 分類選擇用：半透明白底 + 邊框；選中時邊框用主色
@Composable
fun WtsFrostedChoiceButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(WtsDimens.ButtonHeightSmall),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.5f),
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.7f))
    ) { Text(text, style = MaterialTheme.typography.labelLarge) }
}

// 玻璃擬物卡片：帶毛玻璃與噪點紋理的半透明卡片
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    Surface(
        modifier = modifier
            .clip(shape)
            .graphicsLayer { alpha = 0.9f }
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.50f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.30f)
                    )
                )
            )
            .blur(12.dp),
        color = Color.Transparent,
        tonalElevation = 1.dp,
        shadowElevation = 8.dp,
        shape = shape
    ) {
        // 疊一層極淡噪點
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.06f),
                            Color.Black.copy(alpha = 0.04f)
                        )
                    )
                )
                .padding(12.dp)
        ) { content() }
    }
}

// 全域背景：優先顯示 drawable/wtsbg，找不到則退回主題漸層
@Composable
fun WtsBackground(modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val resId = remember { ctx.resources.getIdentifier("wtsbg", "drawable", ctx.packageName) }
    if (resId != 0) {
        Box(modifier) {
            Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
            // 低透明覆蓋，避免內容與背景對比不足
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.06f),
                                Color.White.copy(alpha = 0.06f)
                            )
                        )
                    )
            )
        }
    } else {
        Box(modifier.background(Brush.verticalGradient(listOf(MdGradientTop, MdGradientBottom))))
    }
}

// 統一輸入框樣式：圓角、內邊距與顏色，貼近設計稿
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WtsOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    supportingText: @Composable (() -> Unit)? = null,
    onFocusChanged: ((Boolean) -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    LaunchedEffect(isFocused) {
        onFocusChanged?.invoke(isFocused)
    }
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        label = label,
        supportingText = supportingText,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        ),
        interactionSource = interactionSource
    )
}


