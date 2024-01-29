/*
 *
 *  *
 *  *  * Copyright 2024 All rights are reserved by Pi App Studio
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *  *
 *  *
 *
 *
 */

package com.piappstudio.piui.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import com.piappstudio.piui.R

const val FOREGROUND_NOTIFICATION_ID = 400

/***
 * https://developer.android.com/guide/background/persistent/how-to/long-running
 * To update the foreground notification's content text by using it unique id i.e [FOREGROUND_NOTIFICATION_ID]
 */
fun Context.showPiNotification(message: String, notificationBuilder: Builder) {
    // Make a channel if necessary
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createChannel(this)
    }

    // Show the notification
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    NotificationManagerCompat.from(this).notify(FOREGROUND_NOTIFICATION_ID, notificationBuilder.setContentText(message).build())
}

fun CoroutineWorker.getNotificationBuilder(): NotificationCompat.Builder {
    // Build a notification using bytesRead and contentLength
    val context: Context = applicationContext
    val id = context.getString(R.string.location_channel_id)
    val title = context.getString(R.string.location_channel_title)
    val cancel = context.getString(R.string.location_cancel)
    val contextText = context.getString(R.string.default_location)
    // This PendingIntent can be used to cancel the worker
    val intent: PendingIntent = WorkManager.getInstance(context)
        .createCancelPendingIntent(getId())
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createChannel(context)
    }
    val builder = NotificationCompat.Builder(context, id)
        .setContentTitle(title)
        .setContentText(contextText)
        .setSmallIcon(android.R.drawable.ic_menu_mylocation)
        .setOngoing(true) // Add the cancel action to the notification which can
        // be used to cancel the worker
        .addAction(android.R.drawable.ic_delete, cancel, intent)

    return builder
}

/***
 * Create a foreground notification id
 */
fun CoroutineWorker.createForegroundInfo(notificationBuilder:NotificationCompat.Builder): ForegroundInfo {


    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ForegroundInfo(FOREGROUND_NOTIFICATION_ID, notificationBuilder.build(), FOREGROUND_SERVICE_TYPE_LOCATION )
    } else {
        ForegroundInfo(FOREGROUND_NOTIFICATION_ID, notificationBuilder.build())
    }
}

/***
 * Create a channcel based on [R.string.location_channel_id] id
 */
@RequiresApi(Build.VERSION_CODES.O)
fun createChannel(context: Context) {
    val id = context.getString(R.string.location_channel_id)
    val chan = NotificationChannel(
        id,
        context.getString(R.string.location_channel_title),
        NotificationManager.IMPORTANCE_LOW
    )
    chan.lightColor = Color.BLUE
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    manager?.createNotificationChannel(chan)

}

