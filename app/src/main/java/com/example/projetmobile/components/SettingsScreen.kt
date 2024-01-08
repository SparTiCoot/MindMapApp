package com.example.projetmobile.components

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.projetmobile.MainActivity
import com.example.projetmobile.R
import com.example.projetmobile.ui.RappelWorker
import com.example.projetmobile.ui.SettingsViewModel
import com.example.projetmobile.ui.theme.ButtonColor
import com.example.projetmobile.ui.theme.ColumnColor
import com.example.projetmobile.ui.theme.IconButtonColor
import com.example.projetmobile.ui.theme.Orange80
import com.example.projetmobile.ui.theme.Purple80
import java.util.concurrent.TimeUnit

const val CHANNEL_ID = "MY_CHANNEL_ID"

lateinit var workManager: WorkManager

@Composable
fun SettingsScreen(
    navController: NavHostController, viewModelSettings: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("permissions", "granted")
        } else {
            Log.d("permissions", "denied")
        }
    }

    val backgroundColor by viewModelSettings.backgroundColorFlow.collectAsState(initial = viewModelSettings.defaultBackgroundColor)
    val bodyFS by viewModelSettings.bodyFontSizeFlow.collectAsState(initial = viewModelSettings.defaultBodyFontSize)
    val titleFS by viewModelSettings.titleFontSizeFlow.collectAsState(initial = viewModelSettings.defaultTitleFontSize)

    val optionsTitleFS = listOf("16", "18", "22")
    val optionsBodyFS = listOf("12", "14", "16")
    val selectedOptionTitle = remember { mutableIntStateOf(-1) }
    val selectedOptionBody = remember { mutableIntStateOf(-1) }

    createChannel(context)
    workManager = WorkManager.getInstance(context)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(backgroundColor))
            .padding(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.size(60.dp)
                        .padding(start = 8.dp, top = 25.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = IconButtonColor
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(ColumnColor, RoundedCornerShape(16.dp)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Button(
                        onClick = {
                            if (!NotificationManagerCompat.from(context)
                                    .areNotificationsEnabled()
                            ) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                scheduleWork()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            ButtonColor, Color.White
                        ),
                    ) {
                        Text(
                            text = "Plannifier Notifications",
                            fontSize = bodyFS.sp,
                        )
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(ColumnColor, RoundedCornerShape(16.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_color_mode),
                        contentDescription = "",
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Couleur du Fond",
                        fontSize = titleFS.sp,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Black,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp)
                            .weight(1f)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                ) {
                    val buttonModifier = Modifier
                        .height(45.dp)
                        .weight(1f)
                    Button(
                        onClick = {
                            viewModelSettings.changeBackgroundColor(Purple80.toArgb())
                        }, colors = ButtonDefaults.buttonColors(
                            Purple80, Color.White
                        ), modifier = buttonModifier
                    ) {
                        Text(
                            text = "1", fontSize = bodyFS.sp
                        )
                    }

                    Button(
                        onClick = {
                            viewModelSettings.changeBackgroundColor(Color.LightGray.toArgb())
                        }, colors = ButtonDefaults.buttonColors(
                            Color.LightGray, Color.White
                        ), modifier = buttonModifier
                    ) {
                        Text(
                            text = "2", fontSize = bodyFS.sp
                        )
                    }

                    Button(
                        onClick = {
                            viewModelSettings.changeBackgroundColor(Orange80.toArgb())
                        }, colors = ButtonDefaults.buttonColors(
                            Orange80, Color.White
                        ), modifier = buttonModifier
                    ) {
                        Text(
                            text = "3", fontSize = bodyFS.sp
                        )
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(ColumnColor, RoundedCornerShape(16.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_font_mode),
                        contentDescription = "",
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Taille des Titres",
                        fontSize = titleFS.sp,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Black,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 16.dp)
                            .weight(1f)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    optionsTitleFS.forEachIndexed { index, option ->
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                        ) {
                            val isSelected = index == selectedOptionTitle.intValue
                            RadioButton(
                                selected = isSelected, onClick = {
                                    selectedOptionTitle.intValue = index
                                    val fontSize = when (option) {
                                        "16" -> 16
                                        "18" -> 18
                                        "22" -> 22
                                        else -> 16
                                    }
                                    viewModelSettings.changeTitleFontSize(fontSize)
                                }, modifier = Modifier.size(30.dp)
                            )
                            Text(
                                text = option,
                                fontSize = bodyFS.sp,
                                color = Color.Black,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(ColumnColor, RoundedCornerShape(16.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_font_mode),
                        contentDescription = "",
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Taille de la Police",
                        fontSize = titleFS.sp,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Black,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 16.dp)
                            .weight(1f)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    optionsBodyFS.forEachIndexed { index, option ->
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                        ) {
                            val isSelected = index == selectedOptionBody.intValue
                            RadioButton(
                                selected = isSelected, onClick = {
                                    selectedOptionBody.intValue = index
                                    val fontSize = when (option) {
                                        "12" -> 12
                                        "14" -> 14
                                        "16" -> 16
                                        else -> 14
                                    }
                                    viewModelSettings.changeBodyFontSize(fontSize)
                                }, modifier = Modifier.size(30.dp)
                            )
                            Text(
                                text = option,
                                fontSize = bodyFS.sp,
                                color = Color.Black,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

fun createChannel(c: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "MY_CHANNEL"
        val descriptionText = "Notification Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        val notificationManager =
            c.getSystemService(ComponentActivity.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
}

fun createNotif(c: Context) {
    val intent1 = Intent(c, MainActivity::class.java)
    val pending1 = PendingIntent.getActivity(c, 1, intent1, PendingIntent.FLAG_IMMUTABLE)
    val builder = NotificationCompat.Builder(c, CHANNEL_ID).setSmallIcon(R.drawable.small)
        .setContentTitle("Il est l'heure du savoir !").setContentText("Viens jouer !")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true)
        .setContentIntent(pending1).setCategory(Notification.CATEGORY_REMINDER)
    val myNotif = builder.build()
    val notificationManager =
        c.getSystemService(ComponentActivity.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(44, myNotif)
}

fun scheduleWork(): PeriodicWorkRequest {
    val initialDelay = 24L
    val workRequest = PeriodicWorkRequest.Builder(
        RappelWorker::class.java, 24, TimeUnit.HOURS
    ).setInitialDelay(initialDelay, TimeUnit.HOURS).build()

    workManager.enqueue(workRequest)
    return workRequest
}