/*
 *
 *   *
 *   *  * Copyright 2024 All rights are reserved by Pi App Studio
 *   *  *
 *   *  * Unless required by applicable law or agreed to in writing, software
 *   *  * distributed under the License is distributed on an "AS IS" BASIS,
 *   *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   *  * See the License for the specific language governing permissions and
 *   *  * limitations under the License.
 *   *  *
 *   *
 *
 */

package com.piappstudio.piui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.piappstudio.piui.theme.Dimen

@Composable
fun PermissionBox(
    modifier: Modifier = Modifier,
    lottieResource: Int,
    permission: String,
    description: String? = null,
    contentAlignment: Alignment = Alignment.TopStart,
    onDismiss: () -> Unit,
    onGranted: () -> Unit,
) {
    PermissionBox(
        modifier,
        lottieResource,
        permissions = listOf(permission),
        requiredPermissions = listOf(permission),
        description,
        contentAlignment,
        onDismiss = {
            onDismiss()
        }
    ) { onGranted() }
}

/**
 * A variation of [PermissionBox] that takes a list of permissions and only calls [onGranted] when
 * all the [requiredPermissions] are granted.
 *
 * By default it assumes that all [permissions] are required.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionBox(
    modifier: Modifier = Modifier,
    lottieResource:Int,
    permissions: List<String>,
    requiredPermissions: List<String> = permissions,
    description: String? = null,
    contentAlignment: Alignment = Alignment.TopStart,
    onDismiss:()->Unit,
    onGranted: (List<String>) -> Unit,
) {
    Dialog(onDismissRequest = { onDismiss() }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        val context = LocalContext.current
        var errorText by remember {
            mutableStateOf("")
        }

        val requiredPiWeather = stringResource(R.string.required_for_the_piweather)
        val permissionState = rememberMultiplePermissionsState(permissions = permissions) { map ->
            val rejectedPermissions = map.filterValues { !it }.keys
            errorText = if (rejectedPermissions.none { it in requiredPermissions }) {
                ""
            } else {
                String.format(requiredPiWeather, rejectedPermissions.joinToString())
            }
        }
        val allRequiredPermissionsGranted =
            permissionState.revokedPermissions.none { it.permission in requiredPermissions }

        Box(
            modifier = Modifier
                .fillMaxSize().background(MaterialTheme.colorScheme.background)
                .then(modifier),
            contentAlignment = if (allRequiredPermissionsGranted) {
                contentAlignment
            } else {
                Alignment.Center
            },
        ) {
            if (allRequiredPermissionsGranted) {
                onGranted(
                    permissionState.permissions
                        .filter { it.status.isGranted }
                        .map { it.permission },
                )
            } else {
                PermissionScreen(
                    permissionState,
                    description,
                    lottieResource = lottieResource,
                    errorText,
                ) {
                    onDismiss()
                }

                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(Dimen.doubleSpace),
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            data = Uri.parse("package:${context.packageName}")
                        }
                        context.startActivity(intent)
                    },
                ) {
                    Icon(imageVector = Icons.Rounded.Settings, contentDescription = stringResource(R.string.app_settings))
                }
            }
        }
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionScreen(
    state: MultiplePermissionsState,
    description: String?,
    lottieResource: Int,
    errorText: String,
    onClickCancel:()->Unit
) {
    var showRationale by remember(state) {
        mutableStateOf(false)
    }

    val permissionString = stringResource(R.string.android_permission)

    val permissions = remember(state.revokedPermissions) {
        state.revokedPermissions.joinToString("\n") {
            " - " + it.permission.removePrefix(permissionString)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.piweather_requires_below_permission),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp),
        )
        PILottie(resourceId = lottieResource, modifier = Modifier.fillMaxSize(0.6f))
        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp),
            )
        }
        
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(Dimen.doubleSpace), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { onClickCancel() }) {
                Text(text = stringResource(id = R.string.dismiss))
            }
            Button(
                onClick = {
                    if (state.shouldShowRationale) {
                        showRationale = true
                    } else {
                        state.launchMultiplePermissionRequest()
                    }
                },
            ) {
                Text(text = stringResource(R.string.grant_permissions))
            }
        }
        
        if (errorText.isNotBlank()) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
    if (showRationale) {
        AlertDialog(
            onDismissRequest = {
                showRationale = false
            },
            title = {
                Text(text = stringResource(R.string.permissions_required_by_the_piweather))
            },
            text = {
                Text(text = stringResource(R.string.the_piweather_requires_the, permissions))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRationale = false
                        state.launchMultiplePermissionRequest()
                    },
                ) {
                    Text(stringResource(R.string.str_continue))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showRationale = false
                    },
                ) {
                    Text(stringResource(R.string.dismiss))
                }
            },
        )
    }
}