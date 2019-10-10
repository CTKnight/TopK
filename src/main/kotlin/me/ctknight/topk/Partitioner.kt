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

    val desFiles = IntRange(0, partitionNum - 1).map {
      File("$dstPath/$it.txt").also { file ->
        while (!file.createNewFile()) {
          file.delete()
        }
      }
    }

    val writers = desFiles.map { it.bufferedWriter() }
    srcPath.listFiles()?.filter { it.isFile }?.forEach { file ->
      executor.submit {
        try {
          file.forEachLine { url ->
            val index = assignBucket(url)
            val writer = writers[index]
//          already thread-safe
            writer.append(url + "\n")
          }
        } catch (e: Exception) {
          e.printStackTrace()
          throw e
        }
      }
    }
    executor.shutdown()
    executor.awaitTermination(5, TimeUnit.MINUTES)
    writers.forEach { it.close() }
  }

  private fun assignBucket(url: String): Int {
    var initIndex = url.hashCode() % partitionNum
    if (initIndex < 0) {
      initIndex += partitionNum
    }
    return initIndex
  }
}