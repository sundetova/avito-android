package com.avito.runner.scheduler.listener

import com.avito.runner.millisecondsToHumanReadableTime
import com.avito.runner.service.listener.TestListener
import com.avito.runner.service.model.TestCase
import com.avito.runner.service.model.TestCaseRun
import com.avito.runner.service.worker.device.Device

class LogListener : TestListener {

    override fun started(
        device: Device,
        targetPackage: String,
        test: TestCase,
        executionNumber: Int
    ) {
        device.info("Test started: ${test.className}.${test.methodName}")
    }

    override fun finished(
        device: Device,
        test: TestCase,
        targetPackage: String,
        result: TestCaseRun.Result,
        durationMilliseconds: Long,
        executionNumber: Int
    ) {
        val status = when (result) {
            is TestCaseRun.Result.Passed -> "PASSED"
            is TestCaseRun.Result.Ignored -> "IGNORED"
            is TestCaseRun.Result.Failed.InRun -> "FAILED"
            is TestCaseRun.Result.Failed.InfrastructureError -> {
                device.warn(result.errorMessage, result.cause)
                "LOST"
            }
        }

        device.info(
            "Test $status in ${durationMilliseconds.millisecondsToHumanReadableTime()}: " +
                "${test.className}.${test.methodName}"
        )
    }
}
