package com.wasge.stockalyser.util;

import android.util.Log;

import java.util.ArrayList;

import static java.lang.Float.NaN;

public class ProcessData {

    String TAG = "ProcessData";

    public float[] setDatatoPercent(float[] data){
        if (data == null)
            return null;
        float pivot = data[0];
        float temp;
        for (int i = 0; i < data.length; i++) {
            temp = data[i];
            data[i] = ((data[i] - pivot) / pivot) * 100;
            pivot = temp;
        }
        return data;
    }

    public float[] strectchFloatArrayToLength(float[] original, int desiredLength){
        float[] targetArray = new float[desiredLength];
        int originalLength = original.length;
        int newNumbers = desiredLength - originalLength;
        float stepSize = ((float)originalLength) / newNumbers;
        targetArray[0] = original[0];
        int count = 1;
        int originalPos = 1;
        for(int i = 1; i < desiredLength && originalPos < originalLength; i++){
            targetArray[i] = original[originalPos];
            count++;
            if(count > (int) stepSize){
                count = 0;
            } else {
                originalPos++;
            }
        }
        Log.d(TAG,"array stretched");
        for(int i = desiredLength-1; ( targetArray[i] == 0.0f) && i > 0; i--){
            targetArray[i] = original[originalLength-1];
        }
        return targetArray;
    }

    public float[] compactData(ArrayList<float[]> data){
        if (data == null)
            return null;
        int maxLen = 0;
        for(float[] arr: data){
            if(arr == null || arr.length == 0) {
                Log.e(TAG,"couldnt calculate average, one of the datasets was null");
                return null;
            }
            if(arr.length > maxLen)
                maxLen = arr.length;
        }


        float[] output = new float[maxLen];
        for (float[] f : data) {
            if(f.length < maxLen) f = strectchFloatArrayToLength(f,maxLen);
            float[] temp = setDatatoPercent(f);
            for (int i = 0; i < temp.length; i++) {
                output[i] += temp[i];
            }
        }
        for (int i = 0; i < output.length; i++) {
            output[i] = output[i] / data.get(0).length;
        }
        return output;
    }
}
