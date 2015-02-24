package org.kirhgoff.rhj

import java.net.URL

import org.apache.commons.io.IOUtils

// \1 - url, \2 - name
//<TD><B><A HREF="([a-z]+\.html)" target="_top">(.*)</A></TD>

case class Musician(name: String, url: String, born: String, died: String, birthLocation: String)

object Main {

  val Row = """<TR>\s*<TD><B>((?s).*?)</TD>\s*<TD><B>((?s).*?)</TD>\s*<TD><B>((?s).*?)</TD>\s*<TD><B>((?s).*?)</TD>\s*</TR>""".r
  val NameUrl = """<A HREF="(.*\.html)" target="_top">(.*)</A>""".r
  val NameUrl2 = """<A HREF="(.*\.html)" target="_top">(.*)</A></[B|A]+>""".r
  val NameUrl4 = """<A HREF="(.*\.html)" target="_top">([\w|\s|-]*)""".r
  val Name = """((?s).*?)([\w|\s|-]*)</A>""".r
  val Name2 = """(\w+\s+\w+)""".r
  val Date = """(\d+)-(\d+)-(\d\d\d\d)""".r
  val Year = """(\d\d\d\d)""".r

  def main(args: Array[String]) {
    println("Starting Red Hot Jazz crawler")
    val url = new URL("http://www.redhotjazz.com/musicians.html")
    val musiciansContent = IOUtils.toString(url.openStream())
    extractMusicians(musiciansContent)
  }

  def extractMusicians(content: String): List[Musician] = {
    val result = for (Row(nameUrl, location, born, died) <- Row.findAllIn(content)) yield {
      nameUrl match {
        case NameUrl(url, name) => Right(Musician(name, url, born, died, location))
        case NameUrl4(url, name) => Right(Musician(name, url, born, died, location))
        case NameUrl2(url, name) => Right(Musician(name, url, born, died, location))
        case Name2(name, _) => Right(Musician(name, null, born, died, location))
        //case Name(name, _) => Right(Musician(name, null, born, died, location))
        case _ => Left(s"Error: $nameUrl")
      }
    }
    val list = result.toList
    //Split errors and results
    val (lefts, rights) = list.partition(_.isLeft)

    //Collect results
    println ("==================\nResults are")
    val musicians = rights.map(either => either.right.get).toList
    musicians.map(println)

    //Print error results
    println ("==================\nErrors found")
    val errors = lefts.map(_.left.get)
    errors.map (println)

    println (s"Found ${musicians.size} results, ${errors.size} errors")
    musicians
  }

}
