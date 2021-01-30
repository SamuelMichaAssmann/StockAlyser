package com.wasge.stockalyser.util;

import java.util.ArrayList;

public class ProcessData {

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

    public float[] compactData(ArrayList<float[]> data){
        if (data == null)
            return null;
        if (data.get(0) == null)
            return null;
        float[] output = new float[data.get(0).length];
        for (float[] f : data) {
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
