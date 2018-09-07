package com.scalaProj

import spray.json.{DefaultJsonProtocol, JsonFormat}

case class Multiply(operand1: String, operand2: String)
case class Results(count: Integer)

case class MultiplyResult[T](result: String)

object MultiplicationJsonProtocol extends DefaultJsonProtocol {
  implicit val multFormat = jsonFormat2(Multiply)


  implicit def multResultFormat[T: JsonFormat] = jsonFormat1(MultiplyResult.apply[T])
}