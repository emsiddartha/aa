package org.bheaver.ngl4.aa.controllers

import java.util.concurrent.CompletionStage

import javax.servlet.http.HttpServletResponse
import org.bheaver.ngl4.aa.protocol.authentication.JWTService
import org.bheaver.ngl4.aa.protocol.model.DecodeRequest
import org.bheaver.ngl4.util.exceptions.HTTPException
import org.springframework.beans.factory.annotation.{Autowired, Qualifier}
import org.springframework.web.bind.annotation.{GetMapping, RequestBody, RequestMapping, RestController}
import org.bheaver.ngl4.util.json.ExceptionJSONGenerator.JSONGenerator
import org.json4s.DefaultFormats
import org.springframework.http.MediaType
import org.json4s.jackson.Serialization.write

import scala.compat.java8.FutureConverters
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@RestController
@RequestMapping(Array("/aa"))
class JWTController {

  @Autowired
  @Qualifier("JWTService")
  var jwtService: JWTService = null

  @GetMapping(value = Array("/v1/renewToken"), produces = Array(MediaType.APPLICATION_JSON_VALUE))
  def renewJWTToken(@RequestBody decodeRequest: DecodeRequest,
                    httpServletResponse: HttpServletResponse): CompletionStage[String] = {
    implicit val formats = DefaultFormats
    FutureConverters.toJava(Future {
      write(jwtService.renewToken(decodeRequest))
    }.recover{
      case e: HTTPException => JSONGenerator.toJSON(e,httpServletResponse)
    })
  }
}
