package com.sagsaguz.appograph.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.sagsaguz.appograph.AllFriendsActivity;
import com.sagsaguz.appograph.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BackgroundService extends Service {

    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;
    private DatabaseHelper databaseHelper;
    private List<Friends> usersList = new ArrayList<>();
    private List<String> usersDOB = new ArrayList<>();
    private String formattedDate, dob, userSubDOB, currentSubDOB;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
    }

    private Runnable myTask = new Runnable() {
        public void run() {
            // Do something here
            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
            formattedDate = df.format(c.getTime());

            usersList.clear();
            databaseHelper = new DatabaseHelper(context);
            usersList = databaseHelper.getAllUsers();

            for (int i=0; i<usersList.size(); i++){
                usersDOB.add(usersList.get(i).getDob());
                dob = usersDOB.get(i);
                int endCharacter = dob.indexOf(",");
                if (endCharacter != -1){
                    userSubDOB = dob.substring(0,endCharacter);
                    currentSubDOB = formattedDate.substring(0, endCharacter);
                }
                if(userSubDOB.equals(currentSubDOB)){
                    sendNotification(i, usersList.get(i).getName());
                }
            }

            System.out.println("Bg is running "+formattedDate);
            stopSelf();
        }
    };

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }

    private void sendNotification(int notificationId, String name){

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, AllFriendsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String id = "appograph_notification";
        CharSequence appName = "Appograph";

        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_DEFAULT;
        }

        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(id, appName,importance);

            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);

            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder b = new NotificationCompat.Builder(context);

            b.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.appograph_launcher_icon)
                    .setTicker("Appograph")
                    .setContentTitle("Its "+name+"'s birthday")
                    .setContentText("Make this birthday more memorable for "+name)
                    .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND);

            if (isAppIsInBackground(context)){
                b.setContentIntent(pendingIntent);
            }

            Notification notification = b.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            if (mNotificationManager != null) {
                mNotificationManager.notify(notificationId, b.build());
            }

        } else {

            NotificationCompat.Builder b = new NotificationCompat.Builder(context);

            b.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.appograph_launcher_icon)
                    .setTicker("Appograph")
                    .setContentTitle("Its "+name+"'s birthday")
                    .setContentText("Make this birthday more memorable for "+name)
                    .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND);

            if (isAppIsInBackground(context)){
                b.setContentIntent(pendingIntent);
            }

            Notification notification = b.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            if (mNotificationManager != null) {
                mNotificationManager.notify(notificationId, b.build());
            }

        }

    }

    private static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = null;
        if (am != null) {
            runningProcesses = am.getRunningAppProcesses();
        }
        assert runningProcesses != null;
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        isInBackground = false;
                    }
                }
            }
        }

        return isInBackground;
    }

}
