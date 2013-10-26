/*
* Class to represent articles
*
* The constructor takes a JSON file and parses it using the standard library
*/

package fr.lemonde

import fr.lemonde.util.Date

import scala.io.Source
import scala.util.parsing.json._
import scala.xml.parsing.XhtmlParser

class Article(
val id : Int,
val title : String,
val description : scala.xml.NodeSeq,
val date : Date,
val link : String,
val author : String,
val text : scala.xml.NodeSeq
) {}

object Article {
  def apply(source : Source) = {
    val o = JSON parseRaw source.mkString match {
      case Some(JSONObject(o)) => o
      case _ => null
    }

    def canon(s : String) = if (s == null) "" else s

    def html(s : String) =
      XhtmlParser(Source fromString ("<html>"+s+"</html>"))

    new Article(
    o("id").asInstanceOf[Double].toInt,
    canon(html(o("titre").asInstanceOf[String]).text),
    html(canon(o("description").asInstanceOf[String])),
    Date(o("date_publication").asInstanceOf[String]),
    canon(o("link").asInstanceOf[String]),
    canon(o("auteur").asInstanceOf[String]),
    html(canon(o("texte").asInstanceOf[String]))
    )
  }
}
