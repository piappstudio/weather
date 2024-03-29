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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.Dialog

@Composable
fun PiProgressIndicator(isDialogIndicator:Boolean = true, modifier: Modifier= Modifier) {
    if (isDialogIndicator) {
        Dialog(onDismissRequest = { /*TODO*/ }) {
            PiProgressBar(modifier = modifier)
        }
    } else {
        PiProgressBar(modifier = modifier)
    }
}


@Composable
private fun PiProgressBar(modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize().testTag("PiProgressBar")) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}