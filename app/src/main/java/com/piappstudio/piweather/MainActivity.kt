/*
 *
 *  * Copyright 2024 All rights are reserved by Pi App Studio
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 */

package com.piappstudio.piweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.piappstudio.pinavigation.ErrorManager
import com.piappstudio.pinavigation.NavManager
import com.piappstudio.piui.theme.PiWeatherTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navManager: NavManager

    @Inject
    lateinit var errorManager:ErrorManager


    lateinit var navController:NavHostController
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PiWeatherTheme {
                Scaffold {
                    Surface(modifier = Modifier
                        .padding(it)
                        .fillMaxSize()) {
                        SetUpAppNavGraph()
                    }
                }

            }
        }

        // Get the intent that started this activity
        // Get the intent that started this activity
        val intent = intent
        val data = intent.data
        if (data != null) {
            // Handle the deep link data here
            val scheme = data.scheme
            val host = data.host
            val path = data.path
            // Process the deep link data as needed
            Timber.d("Schema: $scheme")
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PiWeatherTheme {
        Greeting("Android")
    }
}