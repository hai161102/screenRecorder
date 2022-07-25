package com.example.screenrecorderv2.utils.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.IdRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.screenrecorderv2.R;
import com.example.screenrecorderv2.base.rx.RxBusHelper;
import com.example.screenrecorderv2.service.MyService;
import com.example.screenrecorderv2.utils.Config;
import com.example.screenrecorderv2.utils.Toolbox;

public class ServiceNotificationManager {

    public static final int ID_NOTIFICATION_SERVICE = 1001;

    public static final int ID_NOTIFICATION_SCREEN_RECORD_SUCCESS = 808;
    public static final int ID_NOTIFICATION_SCREENSHOT_SUCCESS = 809;
    public static final int ID_NOTIFICATION_SHAKE = 810;

    public static final String ACTION_NOTIFICATION_VIEW_CLICK = "action_view_clicked_in_notification";
    public static final String EXTRA_VIEW_CLICKED = "extra_id_view_clicked";


    private static ServiceNotificationManager instance;

    public static ServiceNotificationManager getInstance(Context context) {
        if (instance == null)
            instance = new ServiceNotificationManager(context);
        return instance;
    }

    private final Context context;
    private NotificationManager notificationManager;

    private ServiceNotificationManager(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void showMainNotification(Service service) {
        RemoteViews notificationLayout;
        if (context.getResources().getConfiguration().getLayoutDirection() == 1){
            notificationLayout = new RemoteViews(context.getPackageName(), R.layout.layout_notification_main_rtl);
        }else {
            notificationLayout = new RemoteViews(context.getPackageName(), R.layout.layout_notification_main);
        }
        IntentFilter intentFilterControl = new IntentFilter();
        intentFilterControl.addAction(ACTION_NOTIFICATION_VIEW_CLICK);
        try {
            context.unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        context.registerReceiver(receiver, intentFilterControl);
        notificationLayout.setTextViewText(R.id.tv_record,context.getResources().getString(R.string.record));
        notificationLayout.setTextViewText(R.id.tv_screenshot,context.getResources().getString(R.string.screenshots));
        notificationLayout.setTextViewText(R.id.tv_home,context.getResources().getString(R.string.home));
        notificationLayout.setTextViewText(R.id.tv_tools,context.getResources().getString(R.string.tools));
        notificationLayout.setTextViewText(R.id.tv_close,context.getResources().getString(R.string.close));
        notificationLayout.setOnClickPendingIntent(R.id.noti_record, onButtonNotificationClick(context, R.id.noti_record));
        notificationLayout.setOnClickPendingIntent(R.id.noti_screen_shot, onButtonNotificationClick(context, R.id.noti_screen_shot));
        notificationLayout.setOnClickPendingIntent(R.id.noti_tools, onButtonNotificationClick(context, R.id.noti_tools));
        notificationLayout.setOnClickPendingIntent(R.id.noti_home, onButtonNotificationClick(context, R.id.noti_home));
        notificationLayout.setOnClickPendingIntent(R.id.noti_exit, onButtonNotificationClick(context, R.id.noti_exit));
        createChannel(ID_NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context, String.valueOf(ID_NOTIFICATION_SERVICE))
                .setSmallIcon(R.drawable.ic_home_app)
                .setCustomContentView(notificationLayout)
                .setOngoing(true)
                .build();
        startFogroundNotificationService(service, ID_NOTIFICATION_SERVICE, notification);
    }

    public void showRecordingNotification() {
        RemoteViews notificationLayout;
        if (context.getResources().getConfiguration().getLayoutDirection() == 1){
            notificationLayout = new RemoteViews(context.getPackageName(), R.layout.layout_notification_recording_rtl);
        }else {
            notificationLayout = new RemoteViews(context.getPackageName(), R.layout.layout_notification_recording);
        }
        IntentFilter intentFilterControl = new IntentFilter();
        intentFilterControl.addAction(ACTION_NOTIFICATION_VIEW_CLICK);
        try {
            context.unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        context.registerReceiver(receiver, intentFilterControl);
        notificationLayout.setTextViewText(R.id.tv_pause,context.getResources().getString(R.string.pause));
        notificationLayout.setTextViewText(R.id.tv_resume,context.getResources().getString(R.string.resume));
        notificationLayout.setTextViewText(R.id.tv_stop,context.getResources().getString(R.string.stop));
        notificationLayout.setTextViewText(R.id.tv_tools,context.getResources().getString(R.string.tools));
        notificationLayout.setTextViewText(R.id.tv_close,context.getResources().getString(R.string.close));
        notificationLayout.setViewVisibility(R.id.noti_play, View.GONE);
        notificationLayout.setViewVisibility(R.id.noti_pause, View.VISIBLE);
        notificationLayout.setOnClickPendingIntent(R.id.noti_pause, onButtonNotificationClick(context, R.id.noti_pause));
        notificationLayout.setOnClickPendingIntent(R.id.noti_stop, onButtonNotificationClick(context, R.id.noti_stop));
        notificationLayout.setOnClickPendingIntent(R.id.noti_play, onButtonNotificationClick(context, R.id.noti_play));
        notificationLayout.setOnClickPendingIntent(R.id.noti_tools, onButtonNotificationClick(context, R.id.noti_tools));
        notificationLayout.setOnClickPendingIntent(R.id.noti_close, onButtonNotificationClick(context, R.id.noti_close));
        createChannel(ID_NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context, String.valueOf(ID_NOTIFICATION_SERVICE))
                .setSmallIcon(R.drawable.ic_home_app)
                .setCustomContentView(notificationLayout)
                .setOngoing(true)
                .build();
        updateNotification(ID_NOTIFICATION_SERVICE, notification);
    }

    public void showPausedNotification() {
        RemoteViews notificationLayout;
        if (context.getResources().getConfiguration().getLayoutDirection() == 1){
            notificationLayout = new RemoteViews(context.getPackageName(), R.layout.layout_notification_recording_rtl);
        }else {
            notificationLayout = new RemoteViews(context.getPackageName(), R.layout.layout_notification_recording);
        }
        IntentFilter intentFilterControl = new IntentFilter();
        intentFilterControl.addAction(ACTION_NOTIFICATION_VIEW_CLICK);
        try {
            context.unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        context.registerReceiver(receiver, intentFilterControl);
        notificationLayout.setTextViewText(R.id.tv_pause,context.getResources().getString(R.string.pause));
        notificationLayout.setTextViewText(R.id.tv_resume,context.getResources().getString(R.string.resume));
        notificationLayout.setTextViewText(R.id.tv_stop,context.getResources().getString(R.string.stop));
        notificationLayout.setTextViewText(R.id.tv_tools,context.getResources().getString(R.string.tools));
        notificationLayout.setTextViewText(R.id.tv_close,context.getResources().getString(R.string.close));
        notificationLayout.setViewVisibility(R.id.noti_pause, View.GONE);
        notificationLayout.setViewVisibility(R.id.noti_play, View.VISIBLE);
        notificationLayout.setOnClickPendingIntent(R.id.noti_pause, onButtonNotificationClick(context, R.id.noti_pause));
        notificationLayout.setOnClickPendingIntent(R.id.noti_stop, onButtonNotificationClick(context, R.id.noti_stop));
        notificationLayout.setOnClickPendingIntent(R.id.noti_play, onButtonNotificationClick(context, R.id.noti_play));
        notificationLayout.setOnClickPendingIntent(R.id.noti_tools, onButtonNotificationClick(context, R.id.noti_tools));
        notificationLayout.setOnClickPendingIntent(R.id.noti_close, onButtonNotificationClick(context, R.id.noti_close));
        createChannel(ID_NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context, String.valueOf(ID_NOTIFICATION_SERVICE))
                .setSmallIcon(R.drawable.ic_home_app)
                .setCustomContentView(notificationLayout)
                .setOngoing(true)
                .build();
        updateNotification(ID_NOTIFICATION_SERVICE, notification);
    }

    public void hideNotificationScreenRecording() {
        hidenNotification(ID_NOTIFICATION_SERVICE);
    }

    public void showScreenRecordSuccessNotification(String path) {
        Intent openVideoIntent = Toolbox.getIntentActionView(context, path);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                openVideoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        createChannel(ID_NOTIFICATION_SCREEN_RECORD_SUCCESS, NotificationManager.IMPORTANCE_DEFAULT);
        Notification notification = new NotificationCompat.Builder(context, String.valueOf(ID_NOTIFICATION_SCREEN_RECORD_SUCCESS))
                .setContentTitle(context.getString(R.string.screen_recorder))
                .setContentText("Open Video")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_video)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .build();
        updateNotification(ID_NOTIFICATION_SCREEN_RECORD_SUCCESS, notification);
    }

    public void hideScreenRecordSuccessNotification() {
        hidenNotification(ID_NOTIFICATION_SCREEN_RECORD_SUCCESS);
    }

    public void showScreenshotSuccessNotification(String path) {
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_crop);
        Intent openVideoIntent = Toolbox.getIntentActionView(context, path);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                openVideoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        createChannel(ID_NOTIFICATION_SCREENSHOT_SUCCESS, NotificationManager.IMPORTANCE_MAX);
        Notification notification = new NotificationCompat.Builder(context, String.valueOf(ID_NOTIFICATION_SCREENSHOT_SUCCESS))
                .setContentTitle(context.getString(R.string.screen_recorder))
                .setContentText("Open Image")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_camera)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .build();
        updateNotification(ID_NOTIFICATION_SCREENSHOT_SUCCESS, notification);
    }

    public void hideScreenshotSuccessNotification() {
        hidenNotification(ID_NOTIFICATION_SCREENSHOT_SUCCESS);
    }

    public void showShakeNotification() {
        Intent destroyShakeRecord = new Intent(context, MyService.class);
        destroyShakeRecord.setAction(Config.ACTION_STOP_SHAKE);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, destroyShakeRecord, 0);

        createChannel(ID_NOTIFICATION_SHAKE, NotificationManager.IMPORTANCE_DEFAULT);
        Notification notification = new NotificationCompat.Builder(context, String.valueOf(ID_NOTIFICATION_SHAKE))
                .setContentTitle(context.getString(R.string.screen_recorder))
                .setContentText(context.getString(R.string.content_notification_shake))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_record)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        updateNotification(ID_NOTIFICATION_SHAKE, notification);
    }

    public void hideShakeNotification() {
        hidenNotification(ID_NOTIFICATION_SHAKE);
    }

    public void startFogroundNotificationService(Service service, int id, Notification notification) {
        service.startForeground(id, notification);
    }

    public void createChannel(int id) {
        createChannel(id, NotificationManager.IMPORTANCE_LOW);
    }

    public void createChannel(int id, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            NotificationChannel channel = new NotificationChannel(String.valueOf(id), name, importance);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void updateNotification(int id, Notification notification) {
        notificationManager.notify(id, notification);
    }

    public PendingIntent onButtonNotificationClick(Context context, @IdRes int id) {
        Intent intent = new Intent(ACTION_NOTIFICATION_VIEW_CLICK);
        intent.putExtra(EXTRA_VIEW_CLICKED, id);
        return PendingIntent.getBroadcast(context, id, intent, 0);
    }

    public void hidenNotification(int id) {
        NotificationManagerCompat.from(context).cancel(id);
    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int id = intent.getIntExtra(EXTRA_VIEW_CLICKED, -1);
            switch (id) {
                case R.id.noti_record:
                    RxBusHelper.sendClickNotificationScreenRecord();
                    break;
                case R.id.noti_screen_shot:
                    RxBusHelper.sendClickNotificationScreenShot();
                    break;
                case R.id.noti_tools:
                    RxBusHelper.sendClickNotificationTools();
                    break;
                case R.id.noti_home:
                    RxBusHelper.sendClickNotificationHome();
                    break;
                case R.id.noti_exit:
                case R.id.noti_close:
                    RxBusHelper.sendClickNotificationExit();
                    break;
                case R.id.noti_pause:
                    RxBusHelper.sendClickNotificationPause();
                    break;
                case R.id.noti_play:
                    RxBusHelper.sendClickNotificationResume();
                    break;
                case R.id.noti_stop:
                    RxBusHelper.sendClickNotificationStop();
                    break;
            }
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        }
    };
}
