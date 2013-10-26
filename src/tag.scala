/*
* methods for spotting entity mentions in a text
*/

package fr.lemonde.ner

import fr.lemonde.util.Trie
import scala.io.Source
import scala.collection.mutable.HashMap
import java.util.regex._
import java.io.File
import java.io.PrintWriter

object Tag {
  // get the values from the trie corresponding to the longest substring of the text starting at index i that is a key in the trie
  def tagFrom(t : Trie[String])(m : Matcher, i : Int) : (Int, Map[String, Int]) = {
    if (m find i) {
      val w = m.group
      if (t contains w) {
        val k = m.end
        val (j, ans) = tagFrom(t branch w)(m, k)
        if (ans.isEmpty) (i, t.entities)
        else (j, ans)
      } else (i, t.entities)
    } else (i, t.entities)
  }

  val b = Pattern compile ".+?\\b" // regexp to match words
  val c = Pattern compile "\\p{Lu}[\\p{L}-]*( \\p{Lu}[\\p{L}-]*)*" // regexp to match capitalized phrases

  /*
  * find mentions in the text
  * returns a HashMap[String, Double] which maps entities found to their weight
  */
  def tagger(threshold : Double)(trie : Trie[String], text : String) = {
    // add some weight to an entity if it has already been found or add it if it is new
    def add(m : HashMap[String, Double], e : (String, Double)) = {
      if (e._1.length > 0)
        m += (e._1 -> (e._2 + (if (m contains e._1) m(e._1) else 0)))
    }
    val entities = HashMap[String, Double]()
    val m = b matcher text
    val cap = c matcher text
    var i = 0
    var right = 0
    var left = 0
    var tagged = ("", 0.)
    var capStart = 0
    var capEnd = 0
    while (m find i) {
      if (i >= capEnd) {
        if (cap find i) {
          capStart = cap.start
          capEnd = cap.end
        } else capEnd = text.size
        // words Me , Mme  and Mgr  are capitalized but are not part of the name
        try {
         if (text.substring(capStart, capStart+3) == "Me ") 
           capStart += 3
         else if ((text.substring(capStart, capStart+4) == "Mme ")
               || (text.substring(capStart, capStart+4) == "Mgr "))
           capStart += 4
        } catch {case _ => ()}
      }
      val k = i
      if (m.end > capStart) i = capEnd // if a phrase is capitalized, we want to match all of it or none of it
      else i = m.end
      val l = m.start
      val (j, tags) = tagFrom(trie)(m, l)
      val total = tags.aggregate(0)(_+_._2, _+_)
      if (j > right && j >= capEnd) {
        left = l
        right = j
        val (tag, count) = {
          val seenTags = tags.filterKeys(entities contains _)
          if (seenTags.isEmpty) tags.maxBy(_._2)
          else seenTags.maxBy(_._2)
        }
        val conf = count.toDouble / total
        if (conf < threshold) tagged = ("", 0.)
        else tagged = (tag, conf)
      }
      if (i >= right) {
        add(entities, tagged)
        tagged = ("", 0.)
        right = i
        left = i
      }
    }
    entities
  }
}
