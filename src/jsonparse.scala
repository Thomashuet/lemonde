/*
* main method for extracting raw text and dates from articles in JSON
*/

package fr.lemonde

import scala.io.Source
import java.io.File
import java.io.PrintWriter

object JsonParse {
  def main(args : Array[String]) {
    val l = (new File(args(0))).listFiles
    val table = new PrintWriter(args(2))
    for (f <- l) {
      try {
	val a = Article(Source fromFile f)
	val out = new PrintWriter(args(1) + f.getName.split("\\.")(0) + ".txt")
	out.println(a.title)
	out.println(a.description(0).child.map(_.text) mkString "\n")
	out.println(a.text(0).child.map(_.text) mkString "\n")
	out.close
        table.println(a.id+"\t"+a.date.year+"-"+a.date.month+"-"+a.date.day)
      } catch {
	case _ => ()
      }
    }
    table.close
  }
}
