# TopK (URL)

Sort URLs in parallelly and extract Top K occurrence

## How to Use
Please check `Topk.kt`
Input: a dir of files, each of which contains a URL per line

```kotlin
val partitioner = Partitioner(inputDir, partitionOutputDir, 500)
partitioner.partition()
val sorter = Sorter(partitionOutputDir, 8)
result = sorter.sort()
```

## Test

`gradlew test`

