package com.servicesinaction.services;

import android.app.job.JobParameters;
import android.app.job.JobService;

/**
 * Created by dankoni on 10/31/17.
 */

public class JobServiceExample extends JobService{

    @Override
    public boolean onStartJob(JobParameters params) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
