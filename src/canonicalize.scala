package fr.lemonde

import scala.io.Source
import scala.collection.mutable.HashMap
import scala.collection.mutable.HashSet
import java.io.File
import java.io.PrintWriter
import java.util.regex._

/*
* Several classes to convert TSV files
*/

object Canonicalize {
  val t = Pattern compile "<.*?>"
  val sp = Pattern compile "\\s*$|^\\s*"

  def text(s : String) =
    sp.matcher(
    t.matcher(s.replace("&amp;", "&").
                replace("&#039;", "'").
                replace("&#39;", "'").
                replace("\\'", "'").
                replace("\\\"", "\"").
                replace("&quot;", "\"").
                replace("&nbsp;", " ").
                replace("&ensp;", " ").
                replace("&emsp;", " ").
                replace("&thinsp;", " ").
                replace("&amp;", "&").
                replace("&eacute;", "é").
                replace("&Eacute;", "É").
                replace("&#224;", "à").
                replace("&#233;", "é").
                replace("&#248;", "ø").
                replace("&shy;", "").
                replace("{{", "").
                replace("}}", "").
                replace("&lt;", "<").
                replace("&gt;", ">")) replaceAll ""
    ) replaceAll ""

  // Canonicalize Wikipedia pages titles
  def canonicalize(s : String) = text(s.capitalize).replace(" ", "_")

  /*
  * takes a redirection table, a list of stopwords and a list of links (origin, text, target)
  * returns the list of links with canonicalized pages titles
  */
  def main(args : Array[String]) {
    if (args.length < 2 || args.length > 3) {
      println("usage: canonicalize <redirections> <stopwords> [<input>]")
    } else {
      val redirections = HashMap[String, String]()
      for (l <- Source.fromFile(args(0)).getLines) {
        val a = l split "\t"
        if (a.length >= 2) redirections += (canonicalize(a(0)) -> canonicalize(a(1)))
      }
      // follow redirections recursively
      def redirect(s : String) : String = {
        if (redirections contains s) redirect(redirections(s))
        else s
      }
      val commonWords = HashSet[String]()
      Source.fromFile(args(1)).getLines.foreach(commonWords += _)
      val src = if (args.length == 3) Source.fromFile(args(2)) else Source.stdin
      for (l <- src.getLines) {
        val a = l split "\t"
        if ((a.length >= 3) && (a(1) != a(1).toLowerCase) && !(commonWords contains a(1)) && !(a(2) contains '#'))
          println(canonicalize(a(0))+"\t"+text(a(1))+"\t"+redirect(canonicalize(a(2))))
      }
    }
  }
}

/*
* Converts the table of lang links to have pages names instead of ids on the French side and YAGO entities (i.e. with <>) instead of pages names on the English side
*/
object Convert {
  def main(args : Array[String]) {
    if (args.length == 0 || args.length > 2) {
      println("usage: convert <dictionary> [<input>]")
    } else {
      val d = HashMap[String, String]()
      for (l <- Source.fromFile(args(0)).getLines) {
        val a = l split "\t"
        if (a.length >= 2) d += (a(0) -> Canonicalize.canonicalize(a(1)))
      }

      val src = if (args.length == 2) Source.fromFile(args(1)) else Source.stdin
      for (l <- src.getLines) {
        val a = l split "\t"
        if (a.length >= 2 && (d contains a(0)))
          println(d(a(0))+"\t<"+Canonicalize.canonicalize(a(1))+">")
      }
    }
  }
}

/*
* convert values in a trie using a dictionary
*/
object Frwiki2Yago {
  def main(args : Array[String]) {
    if (args.length == 0 || args.length > 2) {
      println("usage: convert <dictionary> [<input>]")
    } else {
      val d = HashMap[String, String]()
      d += ("" -> "") // "" has a special meaning in our trie, we don't want to lose it
      for (l <- Source.fromFile(args(0)).getLines) {
        val a = l split "\t"
        if (a.length >= 2) d += (a(0) -> a(1))
      }

      val src = if (args.length == 2) Source.fromFile(args(1)) else Source.stdin
      for (l <- src.getLines) {
        val a = l split "\t"
        if (a.length >= 3 && (d contains a(1)))
          println(a(0)+"\t"+d(a(1))+"\t"+a(2))
      }
    }
  }
}
