package org.bheaver.ngl4.aa.protocol.model

final case class DecodeRequest(jwtToken: String, patronId: String, libCode: String, renewToken: Option[Boolean], requestId: Option[String])

final case class JWTRenewTokenResponse(token: String, requestId: String)
