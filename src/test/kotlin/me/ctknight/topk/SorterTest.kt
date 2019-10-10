package me.ctknight.topk

import java.io.File

fun main() {
  testSorter()
}

fun testSorter() {
  val srcPath = File("./test/dst")
  val sorter = Sorter(srcPath, 8)
  val result = sorter.sort()
  println("Result is:")
  result.forEach {
    println("Url:${it.first} count: ${it.second}")
  }
}