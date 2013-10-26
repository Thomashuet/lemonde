package fr.lemonde

import scala.io.Source
import scala.collection.mutable.HashMap

/*
* reads the mentions.tsv file and computes the locations.tsv file
*/
object Locate {
  def main(args : Array[String]) {
    if (args.length == 0 || args.length > 2)
      println("usage: locate <isInCountry> [<mentions>]")
    else {
      val isInCountry = HashMap[String, String]()
      for (l <- Source.fromFile(args(0)).getLines) {
        val a = l split "\t"
        if (a.length >= 2) isInCountry += (a(0) -> a(1))
      }
      val src = if (args.length == 2) Source.fromFile(args(1)) else Source.stdin
      val countries = HashMap[String, Double]() // score of each country
      var curId = -1 // id of the article currently processed (lines for same article are consecutive)
      for (l <- src.getLines) {
        val a = l split "\t"
        if (a(0).toInt != curId) {
          if (countries.nonEmpty) {
            val c = countries.maxBy(_._2)
            println(curId+"\t"+c._1+"\t"+c._2)
          }
          countries.clear
          curId = a(0).toInt
        }
        if (isInCountry contains a(1)) {
          val c = isInCountry(a(1))
          countries += (c -> (a(2).toDouble + (if (countries contains c) countries(c) else 0)))
        }
      }
    }
  }
}
