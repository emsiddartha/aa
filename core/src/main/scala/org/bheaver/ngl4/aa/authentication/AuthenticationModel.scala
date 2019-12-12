package org.bheaver.ngl4.aa.authentication

import org.bheaver.ngl4.util.model.HTTPResponse

class AuthenticationModel

final case class AuthenticationRequest(libCode: String,
                                 userName: String,
                                 password: String,
                                 requestId: Option[String])

final case class AuthenticationSuccessResponse(jwtToken: String,
                                         patronId: String,
                                         fname: String,
                                         mname: String,
                                         lname: String,
                                         department: String,
                                         departmentId: String,
                                         patronCategory: String,
                                         patronCategoryId: String,
                                         requestId: String) extends HTTPResponse
