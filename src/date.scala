/*
* Simple date parser
*/

package fr.lemonde.util

import java.util.regex._

class Date (
val year : Int,
val month : Int,
val day : Int
) {
  val bissextile = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
}

object Date {
  def apply(date : String) = {
    val m = Pattern compile "(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d)" matcher date
    m.find
    new Date(m.group(1).toInt, m.group(2).toInt, m.group(3).toInt)
  }
}
