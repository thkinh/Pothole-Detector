package com.example.doan.feature;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.example.doan.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;


public class DetectEngine {

    private final LinkedList<Double> zValuesWindow;
    private final int windowSize;
    private SensorEventListener sensorEventListener;
    private double meanZ, sdZ;
    Model model;
    private int potholes = 0;

    public int GetPotholes(){
        return potholes;
    }

    public double getSdZ() {
        return sdZ;
    }

    public double getMeanZ() {
        return meanZ;
    }

    public interface DetectionCallback {
        void onPotholeDetected(int potholeCount);
        void onSafe();
    }


    private final DetectionCallback callback;

    public DetectEngine(Context context, int windowSize, DetectionCallback callback) throws IOException {
        this.zValuesWindow = new LinkedList<>();
        this.windowSize = windowSize;
        this.callback = callback;
        this.model = Model.newInstance(context);
    }

    public SensorEventListener getSensorEventListener() {
        if (sensorEventListener == null) { // Create the instance only once
            sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    double z = event.values[2];
                    updateRollingWindow(z);

                    if (zValuesWindow.size() == windowSize) {
                        meanZ = calculateMean();
                        double varianceZ = calculateVariance(meanZ);
                        sdZ = Math.sqrt(varianceZ);

                        try {
                            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(2 * 4);
                            byteBuffer.order(ByteOrder.nativeOrder());
                            byteBuffer.putFloat((float) meanZ);
                            byteBuffer.putFloat((float) sdZ);

                            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 2}, DataType.FLOAT32);
                            inputFeature0.loadBuffer(byteBuffer);

                            Model.Outputs outputs = model.process(inputFeature0);
                            float prediction = outputs.getOutputFeature0AsTensorBuffer().getFloatArray()[0];
                            if (prediction > 0.9055) {
                                potholes++;
                                callback.onPotholeDetected(potholes);
                            } else {
                                callback.onSafe();
                            }
                            zValuesWindow.clear();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    // No action needed
                }
            };
        }
        return sensorEventListener;
    }

    private void updateRollingWindow(double zValue) {
        if (zValuesWindow.size() >= windowSize) {
            zValuesWindow.poll();
        }
        zValuesWindow.add(zValue);
    }

    private double calculateMean() {
        double sum = 0.0;
        for (double value : zValuesWindow) {
            sum += value;
        }
        return sum / zValuesWindow.size();
    }

    private double calculateVariance(double mean) {
        double variance = 0.0;
        for (double value : zValuesWindow) {
            variance += (value - mean) * (value - mean);
        }
        return variance / zValuesWindow.size();
    }

    public void close() {
        if (model != null) {
            model.close();
            model = null;
        }
        sensorEventListener = null;
    }
}
