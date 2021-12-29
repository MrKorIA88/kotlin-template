package ru.vtb.at.template.properties

import org.aeonbits.owner.ConfigFactory

val props: Props by lazy { ConfigFactory.create(Props::class.java) }