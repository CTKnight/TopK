package me.ctknight.topk

import org.junit.Before
import org.junit.Test
import java.io.File

class SorterTest {
  private val workingDir = File("../test/")
  private val srcPath = File(workingDir, "src/")
  @Before
  fun ensureFile() {

  }

  @Test
  fun testSorter() {
    val patitionOutput = File(workingDir, "dst/")

    val partitioner = Partitioner(srcPath, patitionOutput, 50)
    partitioner.partition()
    val sorter = Sorter(patitionOutput, 8)
    val result = sorter.sort()
    println("Result is:")
    result.forEach {
      println("Url:${it.first} count: ${it.second}")
    }
  }
}
