package ru.vtb.at.template.utils

class SoftAssertError : AssertionError {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}