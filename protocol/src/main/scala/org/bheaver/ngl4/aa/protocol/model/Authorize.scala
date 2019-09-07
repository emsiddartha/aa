package org.bheaver.ngl4.aa.protocol.model

case class DecodeRequest(jwtToken: String, patronId: String, libCode: String, renewToken: Boolean = true, requestId: String = null)

case class JWTRenewTokenResponse(token: String, requestId: String)
