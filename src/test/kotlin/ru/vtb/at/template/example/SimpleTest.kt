package ru.vtb.at.template.example

import com.codeborne.selenide.Condition.value
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import ru.vtb.at.template.browser.browser
import ru.vtb.at.template.pages.search
import ru.vtb.at.template.properties.props
import ru.vtb.at.template.scripts.invoke
import ru.vtb.at.template.scripts.soft

class SimpleTest {

    private lateinit var expectedText: String

    @Test
    @Tag("N1")
    @DisplayName("Позитивный тест")
    fun test1() {
        "Открыть страничку яндекса" {
            browser.open(props.url)
        }
        "Ввести текст" {
            "Получить текст для проверки" {
                expectedText = props.expected
                "Ввести в поле значение '$expectedText'" {
                    search.value = expectedText
                }
            }
        }
        "Проверить введенный текст" {
            search.shouldHave(value(expectedText))
        }
    }

    @Test
    @Tag("N2")
    @DisplayName("Негативный тест")
    fun test2() {
        "Первый шаг успешный самый" {
            assert(true)
        }
        "Тута что-то упадет!" {
            throw Exception("BROKEN : Бабах!!!")
        }
    }

    @Test
    @Tag("N3")
    @DisplayName("Хитрый тест")
    fun test3() {
        val tel = "+70078760010"
        "Открыть страничку яндекса" {
            browser.open(props.url)
        }
        "Ввести телефон для проверки" {
            search.value = tel
        }
        "Проверяем телефон" soft {
            "Это телефон призедента?" soft {
                search.shouldHave(value("призедента"))
            }
            "Внезапный ексепшен" soft {
                throw Exception("BROKEN : Внезапный ексепшен!!!")
            }
            "Марию Ивановну хочу!" soft {
                search.shouldHave(value("Мария"))
            }
        }
        "Тута что-то упадет!" soft {
            throw Exception("BROKEN : Бабах!!!")
        }
        "Проверяем телефон не смотря ни на что" {
            search.shouldHave(value(tel))
        }
    }
}