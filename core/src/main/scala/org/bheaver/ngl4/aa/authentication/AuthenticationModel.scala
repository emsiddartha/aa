package org.bheaver.ngl4.aa.authentication

class AuthenticationModel

case class AuthenticationRequest(val libCode: String,
                                 val userName: String,
                                 val password: String,
                                 val requestId: Option[String])

case class AuthenticationSuccessResponse(val jwtToken: String,
                                         val patronId: String,
                                         val fname: String,
                                         val mname: String,
                                         val lname: String,
                                         val department: String,
                                         val patronCategory: String)
