package common

import play.api.libs.json.{Json, Reads, Writes}

trait JsonResource {
  def toJson[T](resource: T)(implicit writes: Writes[T]): String = {
    Json.stringify(Json.toJson(resource))
  }

  def fromJson[T](json: String)(implicit reads: Reads[T]): T = {
    Json.parse(json).asOpt.get
  }
}
