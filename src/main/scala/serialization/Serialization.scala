package serialization

import io.circe.{Decoder, Encoder, Json}
import io.circe.syntax._
import logger.ApplicationLogger

object Serialization {
  def encode[A](data: A)(implicit m: Encoder[A]): Either[String, Json] = try {
    Right(data.asJson)
  }
  catch {
    case ex: Exception => ApplicationLogger.errorLeft(ex.getMessage)
  }

  def decode[A <: AnyRef](data: String)(implicit d: Decoder[A]): Either[String, A] = try {
    io.circe.parser.decode[A](data) match {
      case Left(error) => ApplicationLogger.errorLeft(error.getMessage)
      case Right(instance) => Right(instance)
    }
  }
  catch {
    case ex: Exception => ApplicationLogger.errorLeft(ex.toString)
  }
}
