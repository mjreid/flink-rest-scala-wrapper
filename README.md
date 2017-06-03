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

Create an instance of the FlinkRestClient:

Use the FlinkRestClient to make a request, being sure to have an implicit ExecutionContext in scope:

Do what you want with the result!

# Handling failures

This wrapper uses "basic" Scala idioms (namely, `Future` and `Option`) to communicate failures. Any caught exceptions in
a failed `Future` will be wrapped in a `FlinkRestException`.

`FlinkRestTimeoutException`

# Execution contexts

This wrapper uses play-ws under the hood to do HTTP requests in a non-blocking fashion. As a result, all calls return
`Future` objects, which require an execution context in scope.

If you're making a small app, you can import the global execution context. However, as a best practice for larger apps,
we recommend using a dedicated executor service. This will isolate your thread pools so that your application doesn't
stall if the Flink REST endpoints take a long time to respond, for example.

# Logging

This wrapper uses slf4j to log.