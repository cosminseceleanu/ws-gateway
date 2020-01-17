package common

import java.security.MessageDigest

object Sha {
  def hash(s: String): String = MessageDigest
    .getInstance("SHA-256")
    .digest(s.getBytes("UTF-8"))
    .map("%02x".format(_))
    .mkString("")
}
