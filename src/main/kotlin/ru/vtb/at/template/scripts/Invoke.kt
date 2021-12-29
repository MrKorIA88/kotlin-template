package ru.vtb.at.template.scripts

import io.qameta.allure.Allure
import io.qameta.allure.model.StatusDetails
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import ru.vtb.at.template.browser.browser
import ru.vtb.at.template.scripts.Invoke.Status.*
import java.nio.charset.StandardCharsets

private val invocationsThreadLocal by lazy { ThreadLocal.withInitial { mutableListOf<Invoke<*, *>>() } }

val invocations by lazy { invocationsThreadLocal.get() }

private val parentInvocation get() = invocations.findLast { it.status == RUNNING }

class Invoke<T : Any, R>(
    private val isSoft: Boolean,
    val target: T,
    val script: T.() -> R
) {

    private val title = target.toString()

    var output: R? = null
        private set

    var error: Throwable? = null
        private set

    var status = UNDEFINED
        private set

    operator fun invoke(): Invoke<T, R> {
        val parent = parentInvocation
        return if (parent != null && parent.target === target) {
            execute()
        } else if (target is String || target is Step) {
            step()
        } else {
            execute()
        }
    }

    private fun step(): Invoke<T, R> {
        return if (title == target.toString()) step(title) else step(title, target)
    }

    @io.qameta.allure.Step("{#step invoke#}")
    private fun step(`#step invoke#`: Any, target: T) = execute(true)

    @io.qameta.allure.Step("{#step invoke#}")
    private fun step(`#step invoke#`: Any) = execute(true)

    private fun execute(isStep: Boolean = false): Invoke<T, R> {
        val parent = parentInvocation
        invocations.add(this)
        try {
            status = RUNNING
            output = script.invoke(target)
            status = when (status) {
                RUNNING -> PASSED
                else -> throw if (status == BROKEN) ScriptException(this) else ScriptError(this)
            }
        } catch (error: Throwable) {
            status = if (error is AssertionError) FAILED else BROKEN
            this.error = error
            if (parent != null && parent.status != FAILED) parent.status = status
            if (isStep) updateStep(error, status)
            if (!isSoft) throw error
        } finally {
            invocations.remove(this)
        }
        return this
    }

    private fun updateStep(ex: Throwable, status: Status) {
        val allure = Allure.getLifecycle()
        allure.updateStep {
            it.setStatusDetails(
                StatusDetails()
                    .withTrace(ex.stackTrace.toList().joinToString("\n"))
                    .withMessage(ex.message ?: ex::class.simpleName)
            )
                .withStatus(
                    when (status) {
                        FAILED -> io.qameta.allure.model.Status.FAILED
                        BROKEN -> io.qameta.allure.model.Status.BROKEN
                        PASSED -> io.qameta.allure.model.Status.PASSED
                        else -> io.qameta.allure.model.Status.SKIPPED
                    }
                ).withDescription(status.toString())
        }

        allure.addAttachment(
            "StackTrace",
            "text/plain",
            "log",
            ex.stackTrace.toList().joinToString("\n").toByteArray(StandardCharsets.UTF_8)
        )
        if (browser.initialized) allure.addAttachment(
            allure.currentTestCaseOrStep.get(),
            "image/png",
            "png",
            (browser.driver as TakesScreenshot).getScreenshotAs(OutputType.BYTES)
        )
    }

    infix operator fun <NR> invoke(script: T.() -> NR) = target invoke script

    infix fun <NR> soft(script: T.() -> NR) = target soft script

    operator fun <NR> invoke(isSoft: Boolean = false, script: T.() -> NR) = target.invoke(isSoft, script)

    override fun toString() = title.trim()

    enum class Status {
        UNDEFINED, RUNNING, PASSED, FAILED, BROKEN
    }
}

private class ScriptError(invoke: Invoke<*, *>) : AssertionError("${invoke.status} : $invoke", invoke.error)

private class ScriptException(invoke: Invoke<*, *>) : RuntimeException("${invoke.status} : $invoke", invoke.error)

infix operator fun <T : Any, R> T.invoke(script: T.() -> R) = invoke(false, script)

infix fun <T : Any, R> T.soft(script: T.() -> R) = invoke(true, script)

fun <T : Any, R> T.invoke(isSoft: Boolean, script: T.() -> R): Invoke<T, R> = Invoke(isSoft, this, script).invoke()

interface Step
