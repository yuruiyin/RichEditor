package com.yuruiyin.richeditor.sample.utils

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorManager
import android.text.TextUtils


class DeviceUtil {

    companion object {

        /**
         * 判断是否存在光传感器来判断是否为模拟器
         * 部分真机也不存在温度和压力传感器。其余传感器模拟器也存在。
         * @return true 为模拟器
         */
        fun isEmulator(context: Context): Boolean {
            val sensorManager =
                context.getSystemService(SENSOR_SERVICE) as SensorManager
            val sensor8: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) //光
            return null == sensor8
        }
    }

}