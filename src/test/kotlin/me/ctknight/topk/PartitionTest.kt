package me.ctknight.topk

import java.io.File

fun main() {
    testPartition()
}

fun testPartition() {
    val partitioner = Partitioner(File("./test/src/"), File("./test/dst"), 50)
    partitioner.partition()
}