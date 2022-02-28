package net.blumia.pineapple.lockscreen.ui.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.blumia.pineapple.lockscreen.R


@Composable
fun LineItem(
    onClicked: () -> Unit = {},
    startText: String,
    endText: String,
) {
    Row(
        Modifier
            .heightIn(min = 48.dp)
            .clickable { onClicked() }
            .padding(horizontal = 15.dp)
    ) {
        ProvideTextStyle(MaterialTheme.typography.subtitle1) {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = startText,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        MutedText(Modifier.align(Alignment.CenterVertically), endText)
    }
}

@Composable
fun MutedText(
    modifier: Modifier = Modifier,
    text: String
) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        ProvideTextStyle(MaterialTheme.typography.subtitle1) {
            Text(
                modifier = modifier,
                text = text,
            )
        }
    }
}

@Composable
fun MutedIcon(
    imageVector: ImageVector
) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Icon(imageVector, null)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AboutScreen(
    onBackBtnClicked: () -> Unit = {},
    onPrivacyPolicyBtnClicked: () -> Unit = {},
    onRateUsBtnClicked: () -> Unit = {},
    onShareBtnClicked: () -> Unit = {},
    onSourceCodeBtnClicked: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackBtnClicked) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = { Text(stringResource(id = R.string.about)) }
            )
        },
    ) {
        Column(Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 0.dp),
            ) {
                Column() {
                    ListItem(trailing = {
                        MutedText(text = net.blumia.pineapple.lockscreen.BuildConfig.VERSION_NAME)
                    }) {
                        Text(stringResource(id = R.string.current_version))
                    }

                    ListItem(
                        modifier = Modifier.clickable { onShareBtnClicked() },
                        trailing = { MutedIcon(Icons.Default.Share) },
                    ) {
                        Text(stringResource(id = R.string.share))
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
            ) {
                Column() {
//                    ListItem(
//                        modifier = Modifier.clickable {  },
//                        trailing = { MutedIcon(Icons.Default.KeyboardArrowRight) },
//                    ) {
//                        Text(stringResource(id = R.string.open_source_licenses))
//                    }
                    ListItem(
                        modifier = Modifier.clickable { onPrivacyPolicyBtnClicked() },
                        trailing = { MutedIcon(Icons.Default.KeyboardArrowRight) },
                    ) {
                        Text(stringResource(id = R.string.privacy_policy))
                    }

                    ListItem(
                        modifier = Modifier.clickable { onRateUsBtnClicked() },
                        trailing = { MutedIcon(Icons.Default.KeyboardArrowRight) },
                    ) {
                        Text(stringResource(id = R.string.rate_us))
                    }

                    ListItem(
                        modifier = Modifier.clickable { onSourceCodeBtnClicked() },
                        trailing = { MutedIcon(Icons.Default.KeyboardArrowRight) },
                    ) {
                        Text(stringResource(id = R.string.source_code))
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AboutScreenPreview() {
    AboutScreen()
}