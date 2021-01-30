package com.wasge.stockalyser.util;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public abstract class ToastyAsyncTask<P,Q,R> extends AsyncTask<P,Q,R> {

    public ToastyAsyncTask(Context context){
        this.context = context;
    }

    @Override
    protected void onPostExecute(R r) {
        if(errorOccured){
            Toast.makeText(context, message, duration).show();
        }
    }

    private final Context context;
    protected String message = "Error occured in " + this.toString();
    protected boolean errorOccured = false;
    protected int duration = Toast.LENGTH_SHORT;

    protected void errorOccured(){
        errorOccured = true;
    }


}
