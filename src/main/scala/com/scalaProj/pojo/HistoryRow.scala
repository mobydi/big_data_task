package com.scalaProj.pojo

class HistoryRow(var operand1  : Double,  operand2  : Double, result : Double) {
  override def toString: String =  operand1 + " * " + operand2 + " = " + result;
}
