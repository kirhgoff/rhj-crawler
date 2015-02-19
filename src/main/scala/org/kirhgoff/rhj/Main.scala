package org.kirhgoff.rhj

import java.net.URL
import java.time.LocalDate

import org.apache.commons.io.IOUtils

import scala.Option
import scala.util.matching.Regex

// \1 - url, \2 - name
//<TD><B><A HREF="([a-z]+\.html)" target="_top">(.*)</A></TD>

case class Musician (name:String, url:String, born:String, died:String, birthLocation:String)

object Main {

  val Row = """<TR>\s*<TD>((?s).*?)</TD>\s*<TD>((?s).*?)</TD>\s*<TD>((?s).*?)</TD>\s*<TD>((?s).*?)</TD>\s*</TR>""".r
  val NameUrl = """<A HREF="(.*\.html)" target="_top">(.*)</A>""".r

  def main(args: Array[String]) {
    println ("Starting Red Hot Jazz crawler")
    val url = new URL("http://www.redhotjazz.com/musicians.html")
    val musiciansContent = IOUtils.toString(url.openStream())
    val musicians:List[Musician] = extractMusicians (musiciansContent)
    println ("Found:")
    musicians.map(println)
  }

  def extractMusicians(content: String): List[Musician] = {
    for (Row(nameUrl, location, born, died) <- Row.findAllIn(content)) {
      val option:Option[Musician] = nameUrl match {
        case NameUrl(url, name) => Musician(name, url, born, died, location)
        case _ =>
      }


    }
  }

}
