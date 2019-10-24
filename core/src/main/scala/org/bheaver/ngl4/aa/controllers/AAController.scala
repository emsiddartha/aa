package org.bheaver.ngl4.aa.controllers

import java.util.concurrent.CompletionStage

import com.typesafe.scalalogging.Logger
import javax.servlet.http.HttpServletResponse
import org.bheaver.ngl4.aa.authentication.{AuthenticationRequest, AuthenticationService}
import org.bheaver.ngl4.util.exceptions.HTTPException
import org.bheaver.ngl4.util.json.ExceptionJSONGenerator.JSONGenerator
import org.bheaver.ngl4.util.json.NewJSONGenerator
import org.json4s._
import org.json4s.jackson.Serialization.write
import org.springframework.beans.factory.annotation.{Autowired, Qualifier}
import org.springframework.http.{HttpMethod, HttpStatus, MediaType}
import org.springframework.web.bind.annotation.{CrossOrigin, GetMapping, RequestBody, RequestMapping, RequestMethod, RestController}

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

  @RequestMapping(value = Array("/v1/authenticate"), produces = Array(MediaType.APPLICATION_JSON_VALUE), method = Array(RequestMethod.GET,RequestMethod.POST))
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