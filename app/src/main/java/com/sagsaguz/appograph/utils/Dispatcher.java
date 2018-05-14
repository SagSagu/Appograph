package com.sagsaguz.appograph.utils;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class Dispatcher extends JobService {

    //RunTask runTask;

    /*@SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        runTask = new RunTask(){
            @Override
            protected void onPostExecute(String s) {
                Toast.makeText(getApplicationContext(), "Working here...", Toast.LENGTH_SHORT).show();
                Log.d("Sagardispatcher", "Working");
                *//*try {
                    startService(new Intent(getApplicationContext(), BackgroundService.class));
                    jobFinished(jobParameters, false);
                } catch (IllegalStateException e){
                    Log.d("OREO", "Illegal state exception");
                }*//*
            }
        };
        runTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return true;
    }*/

    private static final int JOB_ID = 1;
    private static final int ONE_MIN = 60 * 1000;

    public static void schedule(Context context) {
        @SuppressLint("JobSchedulerService")
        ComponentName component = new ComponentName(context, BackgroundService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, component)
                // schedule it to run any time between 1 - 5 minutes
                .setMinimumLatency(ONE_MIN)
                .setOverrideDeadline(5 * ONE_MIN);

        //JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        assert jobScheduler != null;
        jobScheduler.schedule(builder.build());
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Toast.makeText(getApplicationContext(), "Working here...", Toast.LENGTH_SHORT).show();
        Log.d("Sagardispatcher", "Working");
        schedule(getApplicationContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return true;
    }

    public  static class RunTask extends AsyncTask<Void,Void,String>
    {
        @Override
        protected String doInBackground(Void... voids) {
            return "true";
        }
    }
}
