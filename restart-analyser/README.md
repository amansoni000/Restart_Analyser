# Service Restart Detection and System Metrics Collection

This project provides a robust solution for detecting and logging service restarts, alongside collecting detailed system metrics such as CPU, memory, and thread usage. The solution is built using Spring Boot and integrates with a set of collectors that gather various system-level metrics during service restarts.

## Features

- **Service Restart Detection**: Automatically detects and logs service restarts with reasons, uptime, and system metrics.
- **System Metrics Collection**: Collects system metrics, including:
    - Memory usage
    - CPU usage
    - Thread activity
    - Garbage collection statistics
    - Top CPU-consuming threads
- **Customizable Metric Collectors**: Supports multiple metric collectors, with custom sorting for the order of collection.
- **Logging**: Logs all restart events in a JSON format for easy monitoring and analysis.
- **Spring Boot Integration**: Easily integrates with Spring Boot applications using `@Component`, `@Configuration`, and other Spring annotations.

## Architecture

- **ServiceRestartListener**: Listens for the application context refresh event and triggers the collection of system metrics upon restart.
- **SystemMetricCollector**: Interface implemented by various collectors to gather system metrics.
- **RestartReasonLogger**: Handles logging of service restart events and system metrics in JSON format.
- **SystemMetricContext**: Context class that provides access to system management beans (e.g., `OperatingSystemMXBean`, `ThreadMXBean`, etc.) for collecting metrics.

## Installation

To get started, you can clone this repository and add it as a dependency to your Spring Boot application.

1. **Clone the repository**:
    ```bash
    git clone https://github.com/your-username/service-restart-detection.git
    cd service-restart-detection
    ```

2. **Add dependencies to your Spring Boot application**:
    ```xml
    <dependency>
        <groupId>com.addverb</groupId>
        <artifactId>restart-analyser</artifactId>
        <version>1.0.0</version>
    </dependency>
    ```

3. **Configure the service restart listener**:
   Add the following properties to your `application.properties` or `application.yml`:
    ```properties
    spring :
        application :
            name : your-service-name
    restart :
        detection :
            enabled : true
    ```

## Usage

Once integrated, the system will automatically collect system metrics and log them when the service restarts. The log will include:

- Service name
- Restart reason
- Uptime in milliseconds
- Collected system metrics (memory, CPU, thread stats, etc.)

### Example Log Output:

```json
{
    "timestamp": "2025-04-28T07:26:44.317944202Z",
    "phase": "MANUAL_TRIGGER",
    "uptime": {
        "seconds": 3335,
        "milliseconds": 3335473,
        "formatted": "00h 55m 35s"
    },
    "memoryUsage": {
        "heap": {
            "maxMB": 247,
            "usedMB": 187,
            "usedPercentage": 75.7572890772964,
            "maxBytes": 259522560,
            "committedMB": 239,
            "usedBytes": 196607256,
            "committedBytes": 251019264
        },
        "nonHeap": {
            "maxMB": "Unknown",
            "usedMB": 165,
            "usedPercentage": 0.0,
            "maxBytes": "Unknown",
            "committedMB": 239,
            "usedBytes": 173417872,
            "committedBytes": 251019264
        }
    },
    "cpu": {
        "totalMemoryMB": 1024,
        "isOverloaded": false,
        "availableProcessors": 1,
        "jvmCpuLoad": 7.075392235037792E-4,
        "totalSwapMB": 0,
        "osArchitecture": "amd64",
        "osName": "Linux",
        "usedMemoryMB": 506,
        "freeSwapMB": 0,
        "systemLoadAverage": 0.38,
        "osVersion": "6.6.72+",
        "systemCpuLoad": 0.016900941292359944,
        "usedSwapMB": 0,
        "freeMemoryMB": 517,
        "committedVirtualMemoryMB": 5909,
        "loadPerCore": 0.38,
        "processCpuTimeNanos": 95240000000
    },
    "threads": {
        "deadlockedThreads": 0,
        "currentThreadCount": 74,
        "daemonThreadCount": 24,
        "highThreadUsageDetected": false,
        "totalStartedThreadCount": 76,
        "peakThreadCount": 74
    },
    "garbageCollectors": {
        "summary": {
            "excessiveGcDetected ( count or time )": true,
            "totalCollectionCount": 365,
            "totalCollectionTimeMs": 9637
        },
        "collectors": [
            {
                "collectionTimeMs": 8241,
                "name": "Copy",
                "collectionCount": 359
            },
            {
                "collectionTimeMs": 1396,
                "name": "MarkSweepCompact",
                "collectionCount": 6
            }
        ]
    },
    "topCpuThreads": [
        {
            "stackTrace": [],
            "threadId": 83,
            "threadName": "DestroyJavaVM",
            "cpuTimeMs": 31889,
            "threadState": "RUNNABLE"
        },
        {
            "stackTrace": [
                "java.base@17/java.lang.Thread.sleep(Native Method)",
                "io.netty.util.HashedWheelTimer$Worker.waitForNextTick(HashedWheelTimer.java:600)",
                "io.netty.util.HashedWheelTimer$Worker.run(HashedWheelTimer.java:496)",
                "io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)",
                "java.base@17/java.lang.Thread.run(Thread.java:833)"
            ],
            "threadId": 23,
            "threadName": "lettuce-timer-3-1",
            "cpuTimeMs": 2455,
            "threadState": "TIMED_WAITING"
        },
        {
            "stackTrace": [
                "java.base@17/jdk.internal.misc.Unsafe.park(Native Method)",
                "java.base@17/java.util.concurrent.locks.LockSupport.park(LockSupport.java:341)",
                "java.base@17/java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionNode.block(AbstractQueuedSynchronizer.java:506)",
                "java.base@17/java.util.concurrent.ForkJoinPool.unmanagedBlock(ForkJoinPool.java:3463)",
                "java.base@17/java.util.concurrent.ForkJoinPool.managedBlock(ForkJoinPool.java:3434)",
                "java.base@17/java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:1623)",
                "java.base@17/java.util.concurrent.ScheduledThreadPoolExecutor$DelayedWorkQueue.take(ScheduledThreadPoolExecutor.java:1177)",
                "java.base@17/java.util.concurrent.ScheduledThreadPoolExecutor$DelayedWorkQueue.take(ScheduledThreadPoolExecutor.java:899)",
                "java.base@17/java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1062)",
                "java.base@17/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1122)",
                "java.base@17/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)",
                "org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)",
                "java.base@17/java.lang.Thread.run(Thread.java:833)"
            ],
            "threadId": 16,
            "threadName": "Catalina-utility-2",
            "cpuTimeMs": 428,
            "threadState": "WAITING"
        },
        {
            "stackTrace": [
                "java.base@17/jdk.internal.misc.Unsafe.park(Native Method)",
                "java.base@17/java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:252)",
                "java.base@17/java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.awaitNanos(AbstractQueuedSynchronizer.java:1672)",
                "java.base@17/java.util.concurrent.ScheduledThreadPoolExecutor$DelayedWorkQueue.take(ScheduledThreadPoolExecutor.java:1182)",
                "java.base@17/java.util.concurrent.ScheduledThreadPoolExecutor$DelayedWorkQueue.take(ScheduledThreadPoolExecutor.java:899)",
                "java.base@17/java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1062)",
                "java.base@17/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1122)",
                "java.base@17/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)",
                "org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)",
                "java.base@17/java.lang.Thread.run(Thread.java:833)"
            ],
            "threadId": 15,
            "threadName": "Catalina-utility-1",
            "cpuTimeMs": 416,
            "threadState": "TIMED_WAITING"
        },
        {
            "stackTrace": [
                "java.base@17/java.lang.Thread.sleep(Native Method)",
                "com.mongodb.internal.connection.DefaultServerMonitor.waitForNext(DefaultServerMonitor.java:443)",
                "com.mongodb.internal.connection.DefaultServerMonitor.access$1500(DefaultServerMonitor.java:64)",
                "com.mongodb.internal.connection.DefaultServerMonitor$RoundTripTimeRunnable.run(DefaultServerMonitor.java:415)",
                "java.base@17/java.lang.Thread.run(Thread.java:833)"
            ],
            "threadId": 21,
            "threadName": "cluster-rtt-ClusterId{value='680f213fe0fe94590c6143e6', description='null'}-mongodb:27017",
            "cpuTimeMs": 377,
            "threadState": "TIMED_WAITING"
        }
    ],
    "abnormalities": [
        "Excessive GC Count",
        "High GC Time"
    ]
}
