package org.bheaver.ngl4.aa

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.{ExceptionHandler, PathMatcher, RejectionHandler}
import akka.stream.ActorMaterializer
import org.bheaver.ngl4.aa.conf.AAServiceRegistry
import org.bheaver.ngl4.aa.protocol.authentication.JWTService
import org.bheaver.ngl4.aa.protocol.model.{DecodeRequest, JWTRenewTokenResponse}
import org.bheaver.ngl4.aa.authentication.exceptions.AuthenticationFailureException
import org.bheaver.ngl4.aa.authentication.{AuthenticationRequest, AuthenticationService, AuthenticationSuccessResponse}
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

case class CORSResponse(message: String)

object AAAkkaApp extends App {

  //val authenticationService:AuthenticationService = new AuthenticationServiceImpl(new JWTServiceImpl,new PatronDSImpl(DBConnection(pureconfig.loadConfig[NGLConfig].right.get.dbSettings)))
  val authenticationService:AuthenticationService = AAServiceRegistry.authenticateService
  val jwtService:JWTService = AAServiceRegistry.jwtService
  implicit val system = ActorSystem("AASystem")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  implicit val itemFormat = jsonFormat10(AuthenticationSuccessResponse)
  implicit val itemFormat1 = jsonFormat4(AuthenticationRequest)
  implicit  val decodeRequestFormat = jsonFormat5(DecodeRequest)
  implicit  val decodeResponseFormat = jsonFormat2(JWTRenewTokenResponse)
  implicit val CORSResponseFormat = jsonFormat1(CORSResponse)

  val pathMatcherAuth: PathMatcher[Unit] = "aa" / "v1" / "authenticate"
  val directive = entity(as[AuthenticationRequest]){ request =>
    val eventualResponse: Future[AuthenticationSuccessResponse] = authenticationService.authenticate(request)
    onSuccess(eventualResponse) {
      case item => complete(item)
    }
    completeOrRecoverWith(eventualResponse) {
      case e: AuthenticationFailureException => complete(StatusCodes.Unauthorized)
      case _ => complete(StatusCodes.InternalServerError)
    }
  }
  val routeAuth = path(pathMatcherAuth){
    get
      directive
    post
      directive
  }
  val pathMatcherJWT:PathMatcher[Unit] = "aa" / "v1" / "renewToken"
  val routeJWT = path(pathMatcherJWT) {
    get{
      entity(as[DecodeRequest]){ request =>
        val eventualResponse = jwtService.renewToken(request)
        complete(eventualResponse)
      }
    }
  }
  /*val routeCors = cors() {
    complete(CORSResponse("SUCCESS CORS"))
  }*/
  val bindingFuture = Http().bindAndHandle(concat(routeAuth,routeJWT,routesCors),"localhost")
  protected def routesCors = {
    import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

    // Your CORS settings are loaded from `application.conf`

    // Your rejection handler
    val rejectionHandler = corsRejectionHandler.withFallback(RejectionHandler.default)

    // Your exception handler
    val exceptionHandler = ExceptionHandler {
      case e: NoSuchElementException => complete(StatusCodes.NotFound -> e.getMessage)
    }

    // Combining the two handlers only for convenience
    val handleErrors = handleRejections(rejectionHandler) & handleExceptions(exceptionHandler)

    // Note how rejections and exceptions are handled *before* the CORS directive (in the inner route).
    // This is required to have the correct CORS headers in the response even when an error occurs.
    // format: off
    handleErrors {
      cors() {
        handleErrors {
          path("ping") {
            complete("pong")
          } ~
            path("pong") {
              failWith(new NoSuchElementException("pong not found, try with ping"))
            }
        }
      }
    }
    // format: on
  }
}
