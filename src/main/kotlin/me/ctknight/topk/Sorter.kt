package me.ctknight.topk

import java.io.File
import java.util.PriorityQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.Comparator

/**
 * An Nlog(k) / concurrency Sorter
 */
class Sorter(
  private val srcPath: File,
  private val concurrency: Int,
  private val k: Int = 100
) {
  fun sort(): List<Pair<String, Int>> {
    val result = mutableListOf<Pair<String, Int>>()
    require(!(!srcPath.exists() || !srcPath.isDirectory)) { "Input srcPath is not valid dir" }
    val executor = Executors.newFixedThreadPool(concurrency)
//    final result
    val globalMinHeap = makeHeap(k)

    srcPath.listFiles()?.filter { it.isFile }?.forEach { file ->
      executor.submit {
        //        local result per file
        val localMinHeap = makeHeap(k)
        val hashMap = hashMapOf<String, Int>()
        try {
          file.forEachLine {
            val count = hashMap.getOrDefault(it, 0)
            hashMap[it] = count + 1
          }
          hashMap.forEach { (url, cnt) ->
            //            ensuring top-k in local heap
            if (localMinHeap.size >= k) {
              if (localMinHeap.peek().second < cnt) {
                localMinHeap.poll()
                localMinHeap.offer(Pair(url, cnt))
              }
            } else {
              localMinHeap.offer(Pair(url, cnt))
            }
          }
          assert(localMinHeap.size <= k)
//          merge into global heap
          synchronized(globalMinHeap) {
            localMinHeap.forEach {
              if (globalMinHeap.size < k) {
                globalMinHeap.offer(it)
              } else {
//                replace if the new url is more frequent
                if (globalMinHeap.peek().second < it.second) {
                  globalMinHeap.poll()
                  globalMinHeap.offer(it)
                }
              }
            }
            assert(globalMinHeap.size <= k) { "heap size > k" }
          }
        } catch (e: Exception) {
          e.printStackTrace()
          throw e
        }
      }
    }
    executor.shutdown()
    while (!executor.awaitTermination(300, TimeUnit.SECONDS)) {
      Thread.yield()
    }
    assert(globalMinHeap.size <= k) { "heap size > k" }
    while (!globalMinHeap.isEmpty()) {
      result.add(globalMinHeap.poll())
    }
    result.reverse()
    return result
  }

  companion object {
    /**
     * A helper for creating min-heap
     */
    private fun makeHeap(k: Int): PriorityQueue<Pair<String, Int>> =
      PriorityQueue(k, Comparator { lhs, rhs ->
        return@Comparator if (lhs.second != rhs.second) {
          lhs.second - rhs.second
        } else {
          lhs.first.compareTo(rhs.first, true)
        }
      })
  }
}