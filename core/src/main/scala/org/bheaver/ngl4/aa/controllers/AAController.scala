package org.bheaver.ngl4.aa.controllers

import java.util.concurrent.{CompletableFuture, CompletionStage}

import com.typesafe.scalalogging.Logger
import javax.servlet.http.HttpServletResponse
import org.bheaver.ngl4.aa.authentication.exceptions.AuthenticationFailureException
import org.bheaver.ngl4.aa.authentication.{AuthenticationRequest, AuthenticationService, AuthenticationServiceImpl, AuthenticationSuccessResponse}
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.{GetMapping, RequestBody, RequestMapping, RestController}
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import org.springframework.beans.factory.annotation.{Autowired, Qualifier}

import scala.compat.java8.FutureConverters
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@RestController
@RequestMapping(Array("/aa"))
class AAController {
  implicit val formats = DefaultFormats
  val logger = Logger(classOf[AAController])

  @Autowired
  @Qualifier("AuthenticationService")
  var authenticationService: AuthenticationService = null

  @GetMapping(value = Array("/authenticate"), produces = Array(MediaType.APPLICATION_JSON_VALUE))
  def authenticate(@RequestBody authenticationRequest: AuthenticationRequest,
                   httpServletResponse: HttpServletResponse): CompletionStage[String] = {
    FutureConverters.toJava(authenticationService.authenticate(authenticationRequest).map(response => {
      logger.debug(write(response))
      write(response)
    }).recover {
      case e: AuthenticationFailureException => {
        httpServletResponse.setStatus(403)
        "Auth exp"
      }
    }
    )
  }
}