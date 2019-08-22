package org.bheaver.ngl4.aa.controllers

import java.util.concurrent.{CompletableFuture, CompletionStage}

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.{GetMapping, RequestMapping, RestController}

import scala.compat.java8.FutureConverters
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@RestController
@RequestMapping(Array("/aa"))
class AAController {

  @GetMapping(value = Array("/hello"))
  def authenticate():CompletionStage[String] = {
    FutureConverters.toJava(Future("Hello"))
  }
}