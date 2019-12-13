package org.bheaver.ngl4.aa

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.PathMatcher
import akka.stream.ActorMaterializer
import org.bheaver.ngl4.aa.conf.AAServiceRegistry
import org.bheaver.ngl4.aa.protocol.authentication.JWTService
import org.bheaver.ngl4.aa.protocol.model.{DecodeRequest, JWTRenewTokenResponse}
//import org.bheaver.ngl4.aa.authentication.datastore.PatronDSImpl
import org.bheaver.ngl4.aa.authentication.exceptions.AuthenticationFailureException
import org.bheaver.ngl4.aa.authentication.{AuthenticationRequest, AuthenticationService, /*AuthenticationServiceImpl,*/ AuthenticationSuccessResponse}
import org.bheaver.ngl4.aa.conf.NGLConfig
//import org.bheaver.ngl4.aa.protocol.authentication.JWTServiceImpl
import org.bheaver.ngl4.util.db.DBConnection
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future


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
  val pathMatcherAuth: PathMatcher[Unit] = "aa" / "v1" / "authenticate"
  val routeAuth = path(pathMatcherAuth){
    get{
      entity(as[AuthenticationRequest]){ request =>
        val eventualResponse: Future[AuthenticationSuccessResponse] = authenticationService.authenticate(request)
        onSuccess(eventualResponse) {
          case item => complete(item)
        }
        completeOrRecoverWith(eventualResponse) {
          case e: AuthenticationFailureException => complete(StatusCodes.Unauthorized)
          case _ => complete(StatusCodes.InternalServerError)
        }
      }
    }
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
  val bindingFuture = Http().bindAndHandle(concat(routeAuth,routeJWT),"localhost")
}
