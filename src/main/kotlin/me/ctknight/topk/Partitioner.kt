package me.ctknight.topk

import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Partitioner(
  private val srcPath: File,
  private val dstPath: File,
  private val partitionNum: Int
) {
  fun partition() {
    val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    while (!dstPath.mkdirs()) {
      dstPath.deleteRecursively()
    }

    val desFiles = IntRange(0, partitionNum).map {
      File("$dstPath/$it.txt").also { file ->
        while (!file.createNewFile()) {
          file.delete()
        }
      }
    }
    val writers = desFiles.map { it.bufferedWriter() }
    srcPath.listFiles()?.forEach {
      executor.submit {
        it.forEachLine { url ->
          val index = assignBucket(url)
          val writer = writers[index]
//          already thread-safe
          writer.append(url + "\n")
        }
      }
    }
    executor.shutdown()
    executor.awaitTermination(5, TimeUnit.MINUTES)
    writers.forEach { it.close() }
  }

  private fun assignBucket(url: String): Int = url.hashCode() % partitionNum
}