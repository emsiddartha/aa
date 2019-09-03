package org.bheaver.ngl4.aa.authentication

class AuthenticationModel

case class AuthenticationRequest(libCode: String,
                                 userName: String,
                                 password: String,
                                 requestId: String)

case class AuthenticationSuccessResponse(jwtToken: String,
                                         patronId: String,
                                         fname: String,
                                         mname: String,
                                         lname: String,
                                         department: String,
                                         departmentId: String,
                                         patronCategory: String,
                                         patronCategoryId: String,
                                         requestId: String)
