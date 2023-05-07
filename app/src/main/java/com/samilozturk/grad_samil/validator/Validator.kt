package com.samilozturk.grad_samil.validator

interface Validator<T> {
    fun validate(args: T)
}