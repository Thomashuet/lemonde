/*
* main method for extracting mentions from text
*/

package fr.lemonde

import fr.lemonde.util.Trie
import fr.lemonde.ner.Tag
import scala.io.Source
import java.io.File
import java.io.PrintWriter

object Main {

  /*
  * function to process one article
  * takes a confidence threshold, the trie, the id of the article and the article itself
  * outputs mentions in outM
  */
  def process(conf : Double)(trie : Trie[String])
    (id : Int, src : Source)
    (outM : PrintWriter) {
    val text = src.mkString
    val entities = Tag.tagger(conf)(trie, text)
    entities.foreach(e => outM.println(id + "\t" + e._1 + "\t" + e._2))
  }

  def main(args : Array[String]) {
    if (args.size != 4) println("all <trie> <input_dir> <output_mentions> <conf>") else {
    val trie = Trie.read(Source fromFile args(0))
      val outM = new PrintWriter(args(2))
      val conf = args(3).toDouble
      for (f <- (new File(args(1))).listFiles) {
        process(conf)(trie)(f.getName.split("\\.")(0).toInt, Source fromFile f)(outM)
      }
      outM.close
  }}
}
