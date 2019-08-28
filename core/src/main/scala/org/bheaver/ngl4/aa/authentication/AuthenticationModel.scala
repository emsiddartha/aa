package org.bheaver.ngl4.aa.authentication

class AuthenticationModel

case class AuthenticationRequest(libCode: String,
                                 userName: String,
                                 password: String,
                                 requestId: Option[String])

case class AuthenticationSuccessResponse(jwtToken: String,
                                         patronId: String,
                                         fname: String,
                                         mname: String,
                                         lname: String,
                                         department: String,
                                         patronCategory: String)
