package me.ctknight.topk

import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Partition URLs into files of partitionNum (hopefully evenly)
 */
class Partitioner(
  private val srcPath: File,
  private val dstPath: File,
  private val partitionNum: Int,
  private val concurrency: Int = Runtime.getRuntime().availableProcessors()
) {
  fun partition() {
    val executor = Executors.newFixedThreadPool(concurrency)
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
    while (!executor.awaitTermination(300, TimeUnit.SECONDS)){}
    writers.forEach { it.close() }
  }

  /**
   * Quick workaround for Kotlin rem
   * (A negative hash gives negative index if not doing so)
   */
  private fun assignBucket(url: String): Int {
    var initIndex = url.hashCode() % partitionNum
    if (initIndex < 0) {
      initIndex += partitionNum
    }
    return initIndex
  }
}