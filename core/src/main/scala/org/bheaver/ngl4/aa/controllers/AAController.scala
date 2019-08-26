package org.bheaver.ngl4.aa.controllers

import java.util.concurrent.{CompletableFuture, CompletionStage}

import com.typesafe.scalalogging.Logger
import org.bheaver.ngl4.aa.authentication.{AuthenticationRequest, AuthenticationService, AuthenticationServiceImpl, AuthenticationSuccessResponse}
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.{GetMapping, RequestBody, RequestMapping, RestController}
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

import scala.compat.java8.FutureConverters
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@RestController
@RequestMapping(Array("/aa"))
class AAController {
  implicit val formats = DefaultFormats
  val logger = Logger(classOf[AAController])
  private val authenticationService:AuthenticationService = new AuthenticationServiceImpl
  @GetMapping(value = Array("/authenticate"),produces = Array(MediaType.APPLICATION_JSON_VALUE))
  def authenticate(@RequestBody authenticationRequest: AuthenticationRequest):CompletionStage[String] = {
    FutureConverters.toJava(authenticationService.authenticate(authenticationRequest).map(response => {
      logger.info(write(response))
      write(response)
    }))
  }
}