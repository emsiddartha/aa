package org.bheaver.ngl4.aa.protocol.exceptions

import org.bheaver.ngl4.util.exceptions.HTTPException

class BadRequestException(val message: String) extends HTTPException{
  override def statusCode = 400
}