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

### Test result

Env:  3.20GHz 4C8T i7, nvme SSD, Windows 10, no max heap constraint, Test files size sum: 10Gb,

- Test 1(partitionNum: 500, partitioner concurrency: 8, sorter concurrency: 8)

  ```
  Partition STARTED
  Partition DONE
  Partition time: 30300 ms
  Sort STARTED
  Sort DONE
  Sort time: 13658 ms
  Result is:
  Url:https://google.com count: 66938
  Url:https://example.com count: 66533
  Url:https://abc.com count: 66529
  Url:https://google.com/97 count: 3459
  ...
  ```
  
- Test 2 (partitionNum: 500, partitioner concurrency: 16, sorter concurrency: 16)
  
  ```
  Partition STARTED
  Partition DONE
  Partition time: 44786 ms
  Sort STARTED
  Sort DONE
  Sort time: 13618 ms
  Result is:
  Url:https://google.com count: 66938
  Url:https://example.com count: 66533
  Url:https://abc.com count: 66529
  Url:https://google.com/97 count: 3459
  ...
  ```
  
- Test 3 (partitionNum: 500, partitioner concurrency: 32, sorter concurrency: 32)
  
  ```
  Partition STARTED
  Partition DONE
  Partition time: 61793 ms
  Sort STARTED
  Sort DONE
  Sort time: 15055 ms
  Result is:
  Url:https://google.com count: 66938
  Url:https://example.com count: 66533
  Url:https://abc.com count: 66529
  Url:https://google.com/97 count: 3459
  ...
  ```
  
- Test 4 (partitionNum: 500, partitioner concurrency: 8, sorter concurrency: 8)

  ```
  Partition STARTED
  Partition DONE
  Partition time: 31837 ms
  Sort STARTED
  Sort DONE
  Sort time: 12963 ms
  Result is:
  Url:https://google.com count: 66938
  Url:https://example.com count: 66533
  Url:https://abc.com count: 66529
  Url:https://google.com/97 count: 3459
  ...
  ```
- Test 5 (partitionNum: 500, partitioner concurrency: 4, sorter concurrency: 4)

  ```
  Partition STARTED
  Partition DONE
  Partition time: 42026 ms
  Sort STARTED
  Sort DONE
  Sort time: 18809 ms
  Result is:
  Url:https://google.com count: 66938
  Url:https://example.com count: 66533
  Url:https://abc.com count: 66529
  Url:https://google.com/97 count: 3459
  ...
  ```

- Test 6 (partitionNum: 500, partitioner concurrency: 2, sorter concurrency: 2)

  ```
  Partition STARTED
  Partition DONE
  Partition time: 64077 ms
  Sort STARTED
  Sort DONE
  Sort time: 31883 ms
  Result is:
  Url:https://google.com count: 66938
  Url:https://example.com count: 66533
  Url:https://abc.com count: 66529
  Url:https://google.com/97 count: 3459
  ...
  ```

- Test 7 (partitionNum: 500, partitioner concurrency: 8, sorter concurrency: 8, maxHeapSize: 1G)

  ```
  Partition STARTED
  Partition DONE
  Partition time: 36124 ms
  Sort STARTED
  Sort DONE
  Sort time: 16128 ms
  Result is:
  Url:https://google.com count: 66938
  Url:https://example.com count: 66533
  Url:https://abc.com count: 66529
  Url:https://google.com/97 count: 3459
  ...
  ```

  

## Clarification

### Assumptions

- The files containing URLs are mostly uniformed.

- One URL per line in each file.

### Memory use

The size of runtime memory use is mainly determined by concurrency. Let `C_p` = partitioner concurrency, `C_s` = sorter concurrency, `N_p` = partition Num, `K` = 100 

`Mem = C_p * (stack_size + read_buffer) + N_p * write_buffer + C_s * (stack_size + local_heap + read_buffer) + global_heap`

`local_heap = global_heap = O(K) = O(1) (in this case)`

From the test results, it implies the most efficient concurrency is `C_p` = `C_s` `Runtime.getRuntime().availableProcessors()`

Given the `C_p` and `C_s` can be viewed as a constant at runtime which is typically 8 in my computer so 

`Mem = O(C_p + N_p + C_s) = O(N_p)`

So as long set the `N_p` in a reasonable range, it should run under constant memory use, and the 10G data tests works under `maxHeapSize = "1G"` setting .

Typical mem use: ~750Mb commited to 