package me.ctknight.topk

import java.io.File
import java.lang.StringBuilder
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random

fun main() {
  generateTestFile("./test/src/", 1000, 1_000_000)
}
