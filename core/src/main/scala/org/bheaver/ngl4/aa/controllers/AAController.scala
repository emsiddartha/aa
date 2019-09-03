package org.bheaver.ngl4.aa.controllers

import java.util.concurrent.CompletionStage

import com.typesafe.scalalogging.Logger
import javax.servlet.http.HttpServletResponse
import org.bheaver.ngl4.aa.authentication.{AuthenticationRequest, AuthenticationService}
import org.bheaver.ngl4.util.exceptions.HTTPException
import org.bheaver.ngl4.util.json.ExceptionJSONGenerator.JSONGenerator
import org.json4s._
import org.json4s.jackson.Serialization.write
import org.springframework.beans.factory.annotation.{Autowired, Qualifier}
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.{GetMapping, RequestBody, RequestMapping, RestController}

import scala.compat.java8.FutureConverters
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

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
    FutureConverters.toJava(
      Try(authenticationService.authenticate(authenticationRequest)) match {
        case Failure(e: HTTPException) => Future(JSONGenerator.toJSON(e,httpServletResponse))
        case Success(value) => {
          value.map(response => {
            logger.debug(write(response))
            write(response)
          }).recover {
            case e: HTTPException => JSONGenerator.toJSON(e, httpServletResponse)
          }
        }
      }
    )
  }
}