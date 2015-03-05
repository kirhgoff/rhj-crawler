package org.kirhgoff.rhj

import java.io.File
import java.net.URL

import org.apache.commons.io.{FileUtils, IOUtils}

import scala.collection.mutable

// \1 - url, \2 - name
//<TD><B><A HREF="([a-z]+\.html)" target="_top">(.*)</A></TD>

case class Musician(name: String, url: String, born: String, died: String, birthLocation: String)
case class DateTuple(year:Integer, month:Integer, day:Integer)

object Main {

  val Row = """<TR>\s*<TD><B>((?s).*?)</TD>\s*<TD><B>((?s).*?)</TD>\s*<TD><B>((?s).*?)</TD>\s*<TD><B>((?s).*?)</TD>\s*</TR>"""
    .r
  val NameUrl = """<A HREF="(.*\.html)" target="_top">(.*)</A>""".r
  val NameUrl2 = """<[A|a] HREF="(.*\.html)" target="_top">(.*)</A></[B|A]+>""".r
  val NameUrl4 = """<A HREF="(.*\.html)" target="_top">([\w|\s|-]*)""".r
  val Name2 = """([\w\.]+\s+\w+)(?:</A>)*""".r
  val Date = """(?:<NOBR>)*(\d+)-(\d+)-(\d\d\d\d)\?*(?:</NOBR>)*""".r
  val DateMonth = """(\d+)-(\d\d\d\d)""".r
  val Year = """(\d\d\d\d)\s*\?*""".r

  def main(args: Array[String]) {
    println("Starting Red Hot Jazz crawler")
    val url = new URL("http://www.redhotjazz.com/musicians.html")
    val musiciansContent = IOUtils.toString(url.openStream())
    val musicians = extractMusicians(musiciansContent)
    val json = toJson(musicians)

    FileUtils.writeStringToFile(new File("c:/Tmp/musicians.json"), json, "UTF-8")
    println ("Done.");
  }

  def extractMusicians(content: String): List[Musician] = {
    val result = for (Row(nameUrl, location, born, died) <- Row.findAllIn(content)) yield {
      nameUrl match {
        case NameUrl(url, name) => Right(Musician(name, url, born, died, location))
        case NameUrl4(url, name) => Right(Musician(name, url, born, died, location))
        case NameUrl2(url, name) => Right(Musician(name, url, born, died, location))
        case Name2(name) => Right(Musician(name, null, born, died, location))
        //case Name(name, _) => Right(Musician(name, null, born, died, location))
        case _ => Left(s"Error: [$nameUrl]")
      }
    }
    val list = result.toList
    //Split errors and results
    val (lefts, rights) = list.partition(_.isLeft)
    val musicians = rights.map(either => either.right.get).toList

    //Print error results
    println("==================\nErrors found")
    val errors = lefts.map(_.left.get)
    errors.map(println)

    println(s"Found ${musicians.size} results, ${errors.size} errors")
    musicians
  }

  def extract(dateString: String) = dateString match {
    case Date(day, month, year) => DateTuple(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day))
    case DateMonth(month, year) => DateTuple(Integer.valueOf(year), Integer.valueOf(month), null)
    case Year(year) => DateTuple(Integer.valueOf(year), null, null)
    case "1-1-18945" => DateTuple(1845, 1, 1)
    case x => println(s"Error: [$x]"); DateTuple(null, null, null)
  }

  def toString(dateTuple:DateTuple) = {
    val list = new mutable.MutableList[String]()
    if (dateTuple.year != null) list += s"""{\"year\":\"${dateTuple.year}\"}"""
    if (dateTuple.month != null) list += s"""{\"month\":\"${dateTuple.month}\"}"""
    if (dateTuple.day != null) list += s"""{\"day\":\"${dateTuple.day}\"}"""

    list.mkString("[", ", ", "]")
  }

  def toJson(musicians: List[Musician]) = {
    musicians.map {
      m => {
        val born = extract(m.born)
        val died = extract(m.died)
        val list = new mutable.MutableList[String]()
        list += s"""\"name\":\"${m.name.replace("\"", "\\\"")}\""""
        list += s"""\"birthLocation\":\"${m.birthLocation}\""""
        list += s"""\"pageUrl\":\"${m.url}\""""
        list += s"""\"born\":""" + toString(born)
        list += s"""\"died\":""" + toString(died)

        list.mkString("{", ", ", "}")
      }
    }.mkString("[\n", ",\n", "\n]")
  }

}
