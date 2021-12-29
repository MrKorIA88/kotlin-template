package ru.vtb.at.template.browser

import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.WebDriverRunner
import com.codeborne.selenide.logevents.SelenideLogger
import io.qameta.allure.selenide.AllureSelenide
import org.openqa.selenium.WebDriver
import ru.vtb.at.template.properties.props
import ru.vtb.at.template.scripts.invoke

val browser by lazy { Browser() }

class Browser {

    var initialized = false
    val driver: WebDriver get() = WebDriverRunner.getWebDriver()

    init {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/webdrivers/chromedriver.exe")
        Configuration.baseUrl = props.url
        Configuration.startMaximized = true
        Configuration.timeout = props.timeout
        SelenideLogger.addListener(
            "AllureSelenide", AllureSelenide()
                .screenshots(true)
                .savePageSource(true)
        )
        initialized = true
    }

    fun open(url : String) {
        "Открыть страничку яндекса" {
            Selenide.open(url)
        }

    }

    fun close() {
        Selenide.closeWebDriver()
    }
}