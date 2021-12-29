package ru.vtb.at.template.utils

import io.qameta.allure.Allure
import io.qameta.allure.AllureLifecycle
import io.qameta.allure.internal.AllureStorage

fun getStorageAsJavaStyle(): AllureStorage {
    val allureLifecycle = Allure.getLifecycle()
    val allureStorageField = AllureLifecycle::class.java.getDeclaredField("storage")
    allureStorageField.isAccessible = true
    return allureStorageField.get(allureLifecycle) as AllureStorage
}

fun getStorage() = Allure.getLifecycle().let { allure ->
    AllureLifecycle::class.java.getDeclaredField("storage").apply {
        isAccessible = true
    }.run { get(allure) as AllureStorage }
}

//функции области видимости
//apply - this - context
//also - it - context
//run - this - lambda
//with - this - lambda
//let - it - lambda
