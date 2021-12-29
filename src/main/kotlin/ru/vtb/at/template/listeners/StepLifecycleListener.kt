package ru.vtb.at.template.listeners

import io.qameta.allure.model.Status
import io.qameta.allure.model.StepResult

class StepLifecycleListener : io.qameta.allure.listener.StepLifecycleListener {

    override fun afterStepStop(result: StepResult) {
        if (result.status == Status.PASSED) {
            if (result.steps.size != 0) {
                result.steps.filter { it.status != Status.PASSED }.run {
                    find { it.status == Status.BROKEN }?.also { result.status = Status.BROKEN }
                    find { it.status == Status.FAILED }?.also { result.status = Status.FAILED }
                }
            }
            if (result.description != null) {
                result.status = Status.valueOf(result.description)
                result.description = null
            }
        }
    }
}