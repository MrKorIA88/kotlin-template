package ru.vtb.at.template.listeners

import io.qameta.allure.Allure
import io.qameta.allure.model.Status
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import ru.vtb.at.template.browser.browser
import ru.vtb.at.template.utils.SoftAssertError
import ru.vtb.at.template.utils.getStorage

class JUnitListener : AfterAllCallback, AfterEachCallback {

    override fun afterAll(context: ExtensionContext?) {
        browser.close()
    }

    override fun afterEach(context: ExtensionContext) {
        val message = StringBuffer()
        val trace = StringBuffer()
        var i = 0
        getStorage()
            .getTestResult(Allure.getLifecycle().currentTestCase.get()).get()
            .steps.filter { it.status != Status.PASSED }.forEach {
                message.append("${++i}. ").append(it.statusDetails.message).append("\n")
                trace.append(it.statusDetails.trace)
            }
        if (message.isNotEmpty()) throw SoftAssertError(message.toString(), Throwable(trace.toString()))
    }
}