package me.ctknight.topk

import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.system.measureTimeMillis

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
    val partitionTime = measureTimeMillis {
      println("Partition STARTED")
      partitioner.partition()
      println("Partition DONE")
    }
    println("Partition time: $partitionTime ms")

    val sorter = Sorter(partitionOutput, 8)
    var result = listOf<Pair<String, Int>>()

    val sortTime = measureTimeMillis {
      println("Sort STARTED")
      result = sorter.sort()
      println("Sort DONE")
    }
    println("Sort time: $sortTime ms")
    println("Result is:")
    result.forEachIndexed { index, pair ->
      println("Url:${pair.first} count: ${pair.second}")
      if (index <= 2) {
        return@forEachIndexed
      }
      val value = pair.first.split("/").last().toInt()
      assert(value in 0..99) {"Wrong URL result"}
    }
    println("Test done")
  }
}
