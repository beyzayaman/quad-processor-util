package org.dbpedia.quad.formatters

import org.dbpedia.quad.utils.TurtleUtils
import org.dbpedia.quad.formatters.TripleBuilder._

/**
 * Helps to build one triple/quad line in Turtle/Turtle-Quads/N-Triples/N-Quads format.
 *
*/
class TerseBuilder(quads: Boolean, turtle: Boolean)
extends UriTripleBuilder() {
  
  // Scala's StringBuilder doesn't have appendCodePoint
  private var sb = new java.lang.StringBuilder()
  
  override def start(context: String): Unit = {
    sb = new java.lang.StringBuilder()
  }
  
  override def uri(str: String, pos: Int): Unit = {
    val uri = parseUri(str, pos)
    // If URI is bad, comment out whole triple (may happen multiple times)
    if (uri.startsWith(BadUri)) sb.insert(0, "# ")
    this add '<' escape uri add ">"
    this add ' '
  }
  
  /**
   * @param value must not be null
   * @param lang may be null
   */
  override def plainLiteral(value: String, lang: String): Unit = {
    this add '"' escape value add '"'
    if (lang != null) this add '@' add lang
    this add ' '
  }
  
  /**
   * @param value must not be null
   * @param datatype must not be null
   */
  override def typedLiteral(value: String, datatype: String): Unit = {
    this add '"' escape value add '"'
    // do not write xsd:string datatype
    if (datatype != "http://www.w3.org/2001/XMLSchema#string")
      this add "^^" uri(datatype, DATATYPE)
    else
      this add ' '
  }
  
  override def end(context: String): Unit = {
    if (quads && context != null)
      uri(context, CONTEXT)

    // use UNIX EOL. N-Triples and Turtle don't care:
    // http://www.w3.org/TR/rdf-testcases/#eoln and http://www.w3.org/TR/turtle/#term-turtle2-WS
    // and it's probably better to be consistent instead of using the EOL of the platform
    // where the file was generated. These files are moved around a lot anyway.
    this add ".\n"
  }
  
  override def result = sb.toString
  
  private def add(s: String): TerseBuilder = { 
    sb append s
    this 
  }
  
  private def add(c: Char): TerseBuilder = { 
    sb append c
    this 
  }
  
  /**
   * Escapes a Unicode string according to N-Triples / Turtle format.
   */
  private def escape(input: String): TerseBuilder = {
    TurtleUtils.escapeTurtle(sb, input, turtle)
    this
  }
  
}
