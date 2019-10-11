package me.ctknight.topk

import org.junit.Test
import java.io.File
import java.lang.StringBuilder

class CorrectnessTest {
    @Test
    fun correctnessCheck() {
        val partitionOutputDir = File("./correct/dst")
        val inputDir = File("./correct/src/")
        val fileNum = 100
        generateTestFile("./correct/src", fileNum, 500_000, object : Function3<Int, Int, Long, String> {
            private val domain = "https://example.com"
            override fun invoke(fileId: Int, count: Int, cursor: Long): String {
                val urlBuilder = StringBuilder("$domain/")
                if (count < 100) {
                    urlBuilder.append("$count")
                } else {
//                make it random enough
                    repeat(6) {
                        urlBuilder.append(random.nextInt(0, 9))
                    }
                }
                return urlBuilder.toString()
            }
        })

        val partitioner = Partitioner(inputDir, partitionOutputDir, 500)
        partitioner.partition()
        val sorter = Sorter(partitionOutputDir, 8)
        val result = sorter.sort()
        File("./correct").deleteRecursively()
        println("Result is:")
        assert(result.size == 100) {"Wrong result size"}
        result.forEach {
            val value = it.first.split("/").last().toInt()
            assert(value in 0..99) {"Wrong URL result"}
            assert(it.second == fileNum) {"Wrong count result"}
        }
        println("Check succeeded!")
    }
}
