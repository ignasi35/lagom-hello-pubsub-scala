package com.example.hello.impl

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.example.hello.api.{Foo, HelloService}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Implementation of the HelloService.
  */
class HelloServiceImpl(persistentEntityRegistry: PersistentEntityRegistry, pubSub: PubSubRegistry) extends HelloService {

  private val topicId = TopicId[Foo]("hardcoded-qualifier")

  override def hello(id: String) = ServiceCall { _ =>
    // Look up the Hello entity for the given ID.
    val ref = persistentEntityRegistry.refFor[HelloEntity](id)

    // Ask the entity the Hello command.
    ref.ask(Hello(id, None)).map {
      msg =>
        pubSub.refFor(topicId).publish(Foo(msg))
        msg
    }
  }

  override def useGreeting(id: String) = ServiceCall { request =>
    // Look up the Hello entity for the given ID.
    val ref = persistentEntityRegistry.refFor[HelloEntity](id)
    // Tell the entity to use the greeting message specified.
    ref.ask(UseGreetingMessage(request.message))
  }

  override def hellos(): ServiceCall[NotUsed, Source[Foo, NotUsed]] = ServiceCall { _ =>
    Future.successful {
      pubSub.refFor(topicId).subscriber
    }
  }
}
