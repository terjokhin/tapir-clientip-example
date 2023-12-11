### Issue example

Run with `sbt run`

Request: `curl "localhost:8888/hello/here/some_id" -H X-Real-Ip:127.0.0.1`

Returns: `You asked for id some_id, yout security context is AuthInputData(None,Some(127.0.0.1))`

As Expected

## BUT

Request: `curl "localhost:8888/hello/here/some_id"`

Returns: `404 Not Found`

Expected: `You asked for id some_id, yout security context is AuthInputData(None,None)`

But it looks like `clientIp` is optional but only can be `Some(some_value)` and never `None`

