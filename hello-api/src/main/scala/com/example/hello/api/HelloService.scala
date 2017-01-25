package com.example.hello.api

import akka.stream.scaladsl.Source
import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}

/**
  * The Hello service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the HelloService.
  */
trait HelloService extends Service {

  /**
    * Example: curl http://localhost:9000/api/hello/Alice
    */
  def hello(id: String): ServiceCall[NotUsed, String]

  def hellos(): ServiceCall[NotUsed, Source[Foo, NotUsed]]

  /**
    * Example: curl -H "Content-Type: application/json" -X POST -d '{"message":
    * "Hi"}' http://localhost:9000/api/hello/Alice
    */
  def useGreeting(id: String): ServiceCall[GreetingMessage, Done]

  override final def descriptor = {
    import Service._
    // @formatter:off
    named("hello").withCalls(
      pathCall("/api/hello/:id", hello _),
      pathCall("/api/hellos", hellos _),
      pathCall("/api/hello/:id", useGreeting _)
    ).withAutoAcl(true)
    // @formatter:on
  }
}

case class Foo(message: String)
object Foo {
  implicit val format: Format[Foo] = Json.format[Foo]
}
case class GreetingMessage(message: String)
object GreetingMessage {
  implicit val format: Format[GreetingMessage] = Json.format[GreetingMessage]
}
