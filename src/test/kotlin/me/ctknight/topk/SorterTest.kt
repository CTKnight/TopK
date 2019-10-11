package me.ctknight.topk

import org.junit.Before
import org.junit.Test
import java.io.File

class SorterTest {
  private val workingDir = File("../test/")
  private val testSrcPath = File(workingDir, "src/")
  @Before
  fun ensureFile() {
    if (workingDir.exists()) {
      return
    }
    generateTestFile(testSrcPath.path, 10_000, 1_000_000, genUrl())
  }

  @Test
  fun testSorter() {
    val partitionOutput = File(workingDir, "dst/")

    val partitioner = Partitioner(testSrcPath, partitionOutput, 500)
    partitioner.partition()
    val sorter = Sorter(partitionOutput, 8)
    val result = sorter.sort()
    println("Result is:")
    result.forEach {
      val value = it.first.split("/").last().toInt()
      assert(value in 0..99) {"Wrong URL result"}
      println("Url:${it.first} count: ${it.second}")
    }
    println("Test done")
  }
}
