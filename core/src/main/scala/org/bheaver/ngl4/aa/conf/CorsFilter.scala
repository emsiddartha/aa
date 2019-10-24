package org.bheaver.ngl4.aa.conf

import javax.servlet.{Filter, FilterChain, ServletRequest, ServletResponse}
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.core.Ordered

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class CorsFilter extends Filter{
  override def doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain): Unit = {
    import javax.servlet.http.HttpServletRequest
    import javax.servlet.http.HttpServletResponse
    val response = res.asInstanceOf[HttpServletResponse]
    response.setHeader("Access-Control-Allow-Origin", "*")
    response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE")
    response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type")
    response.setHeader("Access-Control-Max-Age", "3600")
    if ("OPTIONS".equalsIgnoreCase(req.asInstanceOf[HttpServletRequest].getMethod)) response.setStatus(HttpServletResponse.SC_OK)
    else chain.doFilter(req, res)
  }
}
