package net.blumia.pineapple.lockscreen.ui.about

import android.content.Context
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import net.blumia.pineapple.lockscreen.BuildConfig
import net.blumia.pineapple.lockscreen.R
import net.blumia.pineapple.lockscreen.ui.icons.Translate


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

fun getInstallerPackageDisplayName(context: Context): String {
    fun pkgDisplayName(pkgName: String?): String {
        return when (pkgName) {
            "com.android.vending" -> context.getString(R.string.google_play)
            "com.amazon.venezia" -> context.getString(R.string.amazon_appstore)
            null -> context.getString(R.string.sideloaded)
            else -> pkgName
        }
    }

    runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            return pkgDisplayName(context.packageManager.getInstallSourceInfo(context.packageName).installingPackageName)
        @Suppress("DEPRECATION")
        return pkgDisplayName(context.packageManager.getInstallerPackageName(context.packageName))
    }
    return "Unknown" // should be a bug
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AboutScreen(
    onBackBtnClicked: () -> Unit = {},
    onPrivacyPolicyBtnClicked: () -> Unit = {},
    onRateUsBtnClicked: () -> Unit = {},
    onShareBtnClicked: () -> Unit = {},
    onGetPlusVersionBtnClicked: () -> Unit = {},
    onContributeTranslationBtnClicked: () -> Unit = {},
    onSourceCodeBtnClicked: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackBtnClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = { Text(stringResource(id = R.string.about)) }
            )
        },
    ) { padding ->
        Column(Modifier.fillMaxWidth().padding(padding)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 0.dp),
            ) {
                Column() {
                    ListItem(trailing = {
                        MutedText(text = BuildConfig.VERSION_NAME)
                    }) {
                        Text(stringResource(id = R.string.current_version))
                    }

                    ListItem(trailing = {
                        MutedText(text =
                            if (LocalInspectionMode.current) "Preview"
                            else (getInstallerPackageDisplayName(LocalContext.current))
                        )
                    }) {
                        Text(stringResource(id = R.string.install_source))
                    }

                    ListItem(
                        modifier = Modifier.clickable { onShareBtnClicked() },
                        trailing = { MutedIcon(Icons.Default.Share) },
                    ) {
                        Text(stringResource(id = R.string.share))
                    }

                    ListItem(
                        modifier = Modifier.clickable { onContributeTranslationBtnClicked() },
                        trailing = { MutedIcon(Icons.Default.Translate) },
                    ) {
                        Text(stringResource(id = R.string.contribute_translation))
                    }

                    if (BuildConfig.PROMOTE_PLUS_VERSION) {
                        ListItem(
                            modifier = Modifier.clickable { onGetPlusVersionBtnClicked() },
                            trailing = { MutedIcon(Icons.Default.Star) },
                        ) {
                            Text(stringResource(id = R.string.get_plus_version))
                        }
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
                        trailing = { MutedIcon(Icons.AutoMirrored.Filled.KeyboardArrowRight) },
                    ) {
                        Text(stringResource(id = R.string.privacy_policy))
                    }

                    ListItem(
                        modifier = Modifier.clickable { onRateUsBtnClicked() },
                        trailing = { MutedIcon(Icons.AutoMirrored.Filled.KeyboardArrowRight) },
                    ) {
                        Text(stringResource(id = R.string.rate_us))
                    }

                    ListItem(
                        modifier = Modifier.clickable { onSourceCodeBtnClicked() },
                        trailing = { MutedIcon(Icons.AutoMirrored.Filled.KeyboardArrowRight) },
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