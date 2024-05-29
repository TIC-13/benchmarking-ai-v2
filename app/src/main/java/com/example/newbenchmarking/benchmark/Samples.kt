package com.example.newbenchmarking.benchmark

import android.util.Log
import kotlin.math.abs
import kotlin.math.sqrt

class Samples<T> {

    private var creationTime = 0L
    private var samples = mutableListOf<T>()

    init {
        creationTime = System.currentTimeMillis()
    }

    fun addSample(sample: T) {
        samples.add(sample)
    }

    fun getSamples(): List<T> {
        synchronized(samples) {
            return samples.toList()
        }
    }

    fun getCreationTime(): Long {
        return creationTime
    }
}

data class Energy(
    val power: Double,
    val joule: Double
)

fun getEnergyConsumption(voltages: Samples<Float>, currents: Samples<Float>): Energy {
    val timePassed = (System.currentTimeMillis() - voltages.getCreationTime()) / 1000
    val avgVoltages = voltages.getSamples().average()
    val avgCurrents = currents.getSamples().average()
    val power = avgVoltages * avgCurrents
    return Energy(abs(power), (abs(power) * timePassed))
}

fun timeFromEnergyConsumption(energy: Energy): Double {
    return energy.joule / energy.power
}

fun standardDeviation(values: List<Float>): Double {
    val mean = values.average()
    val variance = values.map { (it - mean) * (it - mean) }.average()
    return sqrt(variance)
}

