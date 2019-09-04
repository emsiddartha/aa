package org.bheaver.ngl4.aa.authentication.exceptions

import org.bheaver.ngl4.util.exceptions.HTTPException

class InvalidTokenException(val message: String) extends HTTPException{
  override def statusCode = 403
}