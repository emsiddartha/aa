package org.bheaver.ngl4.aa.protocol.httpclient

import com.softwaremill.sttp._
import com.softwaremill.sttp.asynchttpclient.future.AsyncHttpClientFutureBackend
import org.bheaver.ngl4.aa.protocol.model.{DecodeRequest, JWTRenewTokenResponse}
import org.bheaver.ngl4.util.conf.AASettings
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.write
import org.json4s.jackson.JsonMethods._
import org.json4s._
import scala.concurrent.{ExecutionContext, Future}

trait Authorize {
  def authorizeRequest(decodeRequest: DecodeRequest): Future[JWTRenewTokenResponse]
}

class AuthorizeImpl(aaSettings: AASettings) extends Authorize {
  override def authorizeRequest(decodeRequest: DecodeRequest): Future[JWTRenewTokenResponse] = {
    implicit val sttpBackend = AsyncHttpClientFutureBackend()
    implicit val format = DefaultFormats
    implicit val global = ExecutionContext.global
    val request = sttp
      .body(write(decodeRequest))
      .get(Uri(aaSettings.aaurl))
      .send()
      .map(response => response.body)
      .map(response => response match {
        case Left(value) => null
        case Right(value) =>  parse(value).extract[JWTRenewTokenResponse]
      })
    request
  }
}

