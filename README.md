[![Build Status](https://travis-ci.org/mjreid/flink-rest-scala-wrapper.png?branch=master)](https://travis-ci.org/mjreid/flink-rest-scala-wrapper)
[<img src="https://img.shields.io/maven-central/v/com.github.mjreid/flink-wrapper_2.11.svg"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22flink-wrapper_2.11%22)

# Flink REST Api Scala Wrapper 

Scala wrapper around Flink's REST API endpoints. This API is listed in the Flink documentation as the
"Monitoring REST API", but it includes support for uploading JARs and managing jobs, so it's more fully-featured
than the name would imply.

This project is meant to be an easy way for Scala developers to interact with Flink's REST API. It contains wrapper
methods for API calls and returns responses in case classes for easy access.

This wrapper does *not* reference Flink as a library. This makes it for ideal for applications which need to invoke
Flink jobs or report on Flink job statuses, but don't want to be tied to Flink at compile time.

This library was developed and tested against **Flink 1.3.0**. Compatibility with older or newer versions is not
guaranteed.

# Getting started

Add an SBT reference:

```scala
libraryDependencies += "com.github.mjreid" %% "flink-wrapper" % "0.0.2"
```

Create an instance of the FlinkRestClient:

```scala
val flinkClient = FlinkRestClient("http://flink-server.example.com:8081/")
```

Use the FlinkRestClient to make a request, being sure to have an implicit ExecutionContext in scope:

```scala
import scala.concurrent.ExecutionContext.Implicits.global
val result = flinkClient.getJobsOverview()
```

Do something with the result.

```scala
result.map { jobsOverview => println(jobsOverview) }
```

# Handling failures

This wrapper uses "basic" Scala idioms (namely, `Future` and `Option`) to communicate failures. Any caught exceptions in
a failed `Future` will be wrapped in a `FlinkWrapperException` or one of its subclasses.

The possible exceptions are `FlinkWrapperInvalidJsonException` when an unexpected JSON value is returned, and
`FlinkWrapperUnexpectedStatusCodeException` when an unexpected HTTP status code is returned.

# Execution contexts

This wrapper uses play-ws under the hood to do HTTP requests in a non-blocking fashion. As a result, all calls return
`Future` objects, which require an execution context in scope.

If you're making a small app, you can import the global execution context. However, as a best practice for larger apps,
we recommend using a dedicated executor service. This will isolate your thread pools so that your application doesn't
stall if the Flink REST endpoints take a long time to respond, for example.

# Full API list

The follow APIs are available in this wrapper. See the Scaladoc for more details.

## Job Management

* `runProgram` starts a Flink program with the specified parameters. **Important note:** The `jarId` argument is *not*
what appears in the Flink UI -- a hidden GUID is appended. It's recommended to use the `uploadJar` method (see below),
which will return the full filename that can be used as part of `runProgram`.
* `cancelJob` cancels a job immediately.
* `cancelJobWithSavepoint` cancels a job with a savepoint. This method returns a `CancelJobAccepted` object; use the
`getCancellationStatus` method to query the status. Cancellations can fail if, for example, the savepoint files cannot
be written.
* `uploadJar` uploads a JAR that can be executed via `runProgram`.

## Job Status

* `getJobsList` returns all jobs in Flink, separated by state.
* `getJobOverview` returns all running and finished jobs in Flink with additional execution details.
* `getJobDetails` gets the details of a specific Flink job.
* `getJobPlan` gets the plan of a specific Flink job. (The plan data is also included in `getJobDetails`.)
* `getJobExceptions` gets any exceptions that occurred in a job.

## Informational

* `getConfig` gets the system-level Flink configuration.
* `getClusterOverview` gets the Flink cluster information.
* `getJobManagerConfig` gets all the JobManager configurations.
* `getClusterTaskManagers` gets the information about all the TaskManagers inside the Flink cluster.

# Credits

- Michael Reid (mjreid @github)
- Your name here? Pull requests welcome!