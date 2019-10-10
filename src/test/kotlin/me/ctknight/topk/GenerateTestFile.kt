package me.ctknight.topk

import java.io.File
import java.lang.Exception
import java.lang.StringBuilder
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random

fun main() {
  generateTestFile("./test/src/", 100, 10_000_000)
}

fun generateTestFile(path: String, num: Int, maxFileSize: Long) {
  val dir = File(path)
  while (!dir.mkdirs()) {
    dir.deleteRecursively()
  }
//    do it concurrently in a thread pool
  val threadPoolExecutor = Executors.newFixedThreadPool(8)
  repeat(num) { fileId ->
    threadPoolExecutor.submit {
      try {
        val file = File("$path/$fileId.txt")
        println("Creating file: ${file.name}")
        while (!file.createNewFile()) {
          file.delete()
        }
        file.bufferedWriter().use { out ->
          var size = 0L
          var count = 0
          while (size < maxFileSize) {
            val url = genUrl(fileId, count, size)
            val length = url.length

            out.append(url)
            out.newLine()

            size += length
            count += 1
          }
        }
        println("${file.name} done")
      } catch (e: Exception) {
        println(e.printStackTrace())
      }
    }
  }
//    await all tasks
  threadPoolExecutor.shutdown()
  threadPoolExecutor.awaitTermination(300, TimeUnit.SECONDS)
}

val domainList = listOf("https://example.com", "https://abc.com", "https://google.com")
val random = Random(42)
val tlSb = ThreadLocal.withInitial { StringBuilder() }
fun genUrl(fileId: Int, count: Int, cursor: Long, randomLength: Int = 5): String {
  val randomDomain = domainList[random.nextInt(0, domainList.size)]
  if (count < 100) {
    return "$randomDomain/$count"
  }
  if (count < 120) {
    return randomDomain
  }
//  random here
  val urlBuilder = tlSb.get()
  urlBuilder.setLength(0)
  urlBuilder.append(randomDomain)
  urlBuilder.append('/')
  repeat(randomLength) {
    urlBuilder.append(random.nextInt(0, 9))
  }
  return urlBuilder.toString()
}