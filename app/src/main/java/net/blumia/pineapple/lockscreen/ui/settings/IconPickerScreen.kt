package net.blumia.pineapple.lockscreen.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.blumia.pineapple.lockscreen.R
import net.blumia.pineapple.lockscreen.preferences.IconColor
import net.blumia.pineapple.lockscreen.preferences.IconStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconPickerScreen(
    onBackBtnClicked: () -> Unit = {},
    selectedColor: IconColor = IconColor.GREEN,
    selectedStyle: IconStyle = IconStyle.LOCK,
    onConfirmClicked: (IconColor, IconStyle) -> Unit = { _, _ -> },
) {
    var previewColor by remember(selectedColor) { mutableStateOf(selectedColor) }
    var previewStyle by remember(selectedStyle) { mutableStateOf(selectedStyle) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackBtnClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = { Text(stringResource(id = R.string.app_icon)) }
            )
        },
        bottomBar = {
            Button(
                onClick = { onConfirmClicked(previewColor, previewStyle) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(stringResource(id = R.string.accept))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(id = R.string.icon_style),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                IconStyle.entries.forEach { style ->
                    FilterChip(
                        selected = previewStyle == style,
                        onClick = { previewStyle = style },
                        label = {
                            Text(
                                text = when (style) {
                                    IconStyle.LOCK -> stringResource(id = R.string.icon_style_lock)
                                    IconStyle.POWER -> stringResource(id = R.string.icon_style_power)
                                }
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = style.displayIconResId),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.icon_color),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconColor.entries.chunked(3).forEach { rowColors ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowColors.forEach { color ->
                            Box(modifier = Modifier.weight(1f)) {
                                IconColorItem(
                                    color = color,
                                    displayIconResId = previewStyle.displayIconResId,
                                    isSelected = previewColor == color,
                                    onClick = { previewColor = color }
                                )
                            }
                        }
                        repeat(3 - rowColors.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = stringResource(id = R.string.icon_restart_required),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun IconColorItem(
    color: IconColor,
    displayIconResId: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor = when (color) {
        IconColor.GREEN -> Color(0xFF00D2AB)
        IconColor.BLUE -> Color(0xFF0099D2)
        IconColor.RED -> Color(0xFFE53935)
        IconColor.ORANGE -> Color(0xFFFF6D00)
        IconColor.PURPLE -> Color(0xFFAA00FF)
        IconColor.PINK -> Color(0xFFFF4081)
        IconColor.CYAN -> Color(0xFF00E5FF)
        IconColor.INDIGO -> Color(0xFF536DFE)
        IconColor.YELLOW -> Color(0xFFFFEA00)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .then(
                    if (isSelected) {
                        Modifier.border(
                            width = 3.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    painter = painterResource(id = displayIconResId),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(id = color.labelResId),
            style = MaterialTheme.typography.bodySmall
        )
    }
}
