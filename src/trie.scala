package fr.lemonde.util

import scala.io.Source
import java.io.PrintWriter

/*
* The trie class
* a trie can be seen as a Map[String, Map[T, Int]] (see the toMap function)
* It maps strings to a set of values (of type T), each of these values is associated with a count, that's why this set is encoded as a Map[T, Int]
*
* The trie is immutable (the function add does not modify the trie but creates a new copy with the new element)
*/

class Trie[T](
  val children : Map[Char, Trie[T]],
  val entities : Map[T, Int],
  val size : Int
) {
//  val size : Int = entities.aggregate(0)(_+_._2, _+_) + children.aggregate(0)(_+_._2.size, _+_)

  def containsFrom(s : String, i : Int) : Boolean = {
    if (i >= s.length) true
    else (children contains s(i)) && (children(s(i)).containsFrom(s, i + 1))
  }

  // is the string s a prefix of a key in this trie ?
  def contains(s : String) =
    containsFrom(s, 0)

  def branchFrom(s : String, i : Int) : Trie[T] = {
    if (i >= s.length) this
    else children(s(i)).branchFrom(s, i + 1)
  }

  // get the subtree obtained by following the path labelled by s
  def branch(s : String) =
    branchFrom(s, 0)

  def getLongestFrom(s : String, i : Int) : (Int, Map[T, Int]) = {
    if (i >= s.length) (i, entities)
    else if (children contains s(i)) {
      val (n, ans) = children(s(i)).getLongestFrom(s, i + 1)
      if (ans.isEmpty) (i, entities) else (n, ans)
    } else (i, entities)
  }

  /*
  * get the values corresponding to the longest prefix of s that is a key in the trie
  * returns a pair (length of this prefix, values)
  */
  def getLongest(s : String) =
    getLongestFrom(s, 0)

  def getAllFrom(s : String, i : Int) : List[(Int, Map[T, Int])] = {
    if (i >= s.length || ! (children contains s(i))) {
      if (entities.isEmpty) List()
      else List((i, entities))
    } else {
      if (entities.isEmpty) children(s(i)).getAllFrom(s, i + 1)
      else (i, entities) :: children(s(i)).getAllFrom(s, i + 1)
    }
  }

  def getAll(s : String) =
    getAllFrom(s, 0)

  def getFrom(s : String, i : Int) : Map[T, Int] = {
    if (i >= s.length) entities
    else if (children contains s(i)) children(s(i)).getFrom(s, i + 1)
    else Map()
  }

  // get the values corresponding to the key s
  def apply(s: String) =
    getFrom(s, 0)

  def addFrom(s : String, entity : T, i : Int, w : Int = 1) : Trie[T] = {
    def add(m : Map[T, Int], e : T) = {
      if (m contains e) m + (e -> (m(e) + w))
      else m + (e -> w)
    }

    if (i >= s.length)
      new Trie(children, add(entities, entity), size + w)
    else if (children contains s(i))
      new Trie(children + (s(i) -> children(s(i)).addFrom(s, entity, i + 1, w)),
               entities,
               size + w)
    else
      new Trie(children + (s(i) -> (Trie.empty[T]).addFrom(s, entity, i + 1, w)),
               entities,
               size + w)
  }

  // add a new pair (key, value) to the trie
  def add(s : String, entity : T, w : Int = 1) =
    addFrom(s, entity, 0, w)

  def +(s : String, entity : T, w : Int = 1) =
    addFrom(s, entity, 0, w)

  // convert a trie to Map[String, Map[T, Int]]
  def toMap : Map[String, Map[T, Int]] = {
    def concat(c : Char)(m : Map[String, Map[T, Int]], p : (String, Map[T, Int])) = m + ((c.toString + p._1) -> p._2)
    def agreg(m : Map[String, Map[T, Int]], p : (Char, Trie[T])) = p._2.toMap.foldLeft(m)(concat(p._1))
    if (entities.isEmpty) children.foldLeft(Map[String, Map[T, Int]]())(agreg)
    else children.foldLeft(Map("" -> entities))(agreg)
  }

  /*
  * remove values that have a count below the threshold
  * other must be a value never used elsewhere (we use "")
  */
  def clean(other : T, threshold : Int = 5) : Trie[T] = {
    def cleanChildren(m : Map[Char, Trie[T]], c : (Char, Trie[T])) = {
      val t = c._2.clean(other, threshold)
      if (t.size == 0) m
      else m + (c._1 -> t)
    }
    def cleanEntities(m : Map[T, Int], e : (T, Int)) = {
      if (e._2 < threshold) m + (other -> (m(other) + e._2))
      else m + (other -> (m(other) + 1)) + (e._1 -> (e._2 - 1))
    }
    val nChildren = children.foldLeft(Map[Char, Trie[T]]())(cleanChildren)
    val nEntities = entities.foldLeft(Map(other -> 0))(cleanEntities)
    if (nEntities.size > 1) {
      val nSize = nEntities.aggregate(0)(_+_._2, _+_) + nChildren.aggregate(0)(_+_._2.size, _+_)
      new Trie(nChildren, nEntities, nSize)
    } else {
      val nSize = nChildren.aggregate(0)(_+_._2.size, _+_)
      new Trie(nChildren, Map[T, Int](), nSize)
    }
  }

  // print a trie to a file
  def print(out : PrintWriter, base : String = "") {
    entities.foreach(e => out.println(base+"\t"+e._1+"\t"+e._2))
    children.foreach(c => c._2.print(out, base+c._1))
  }
}

object Trie {
  // the empty trie
  def empty[T] = new Trie[T](Map[Char, Trie[T]](), Map[T, Int](), 0)

  // read a trie from a file
  def read(src : Source) = {
    src.getLines.foldLeft(empty[String])((m, l) => {
      val a = l split "\t"
      m.add(a(0), a(1), a(2).toInt)
    })
  }

  // build a trie from a list of links
  def build(src : Source) = {
   src.getLines.foldLeft(Trie.empty[String])(
     (t, l) => {
       val a = l split "\t"
//       if (a.size == 3 && a(1).size > 3) t + (a(1).toLowerCase, a(2)) else t
       if (a.size == 3 && a(1).size > 3) t + (a(1), a(2)) else t
      }
    )
  }

  // build a trie, clean it and save it to a file
  def main(args : Array[String]) {
    build(Source fromFile args(0)).clean("").print(new PrintWriter(args(1)))
  }
}
