package org.dbpedia.quad.formatters

import org.dbpedia.quad.formatters.TriXBuilder._
import org.dbpedia.quad.utils.XmlUtils
import org.dbpedia.quad.formatters.TripleBuilder._

object TriXBuilder {
  private val spaces = (2 to (6, step = 2)).map(" " * _)
}

/**
 * Formats statements according to the TriX format.
 * See: http://www.hpl.hp.com/techreports/2004/HPL-2004-56.html
 * 
 * Objects of this class are not re-usable - create a new object for each triple.
 *
 */
class TriXBuilder(quads: Boolean)
extends UriTripleBuilder() {
  
  private var depth = 0
  
  private val sb = new java.lang.StringBuilder
  
  // public methods implementing TripleBuilder
  
  override def start(context: String): Unit = { 
    this startTag "graph"
    if (quads) uri(context, CONTEXT)
    this startTag "triple"
  }
  
  override def uri(value: String, pos: Int): Unit = {
    this add spaces(depth) add "<uri>" escapeUri(value, pos) add "</uri>\n"
  }
  
  /**
   * @param value must not be null
   * @param lang may be null
   */
  override def plainLiteral(value: String, lang: String): Unit = {
    this add spaces(depth) add "<plainLiteral" 
    if (lang != null) this add " xml:lang=" add '"' add(lang) add '"' 
    this add '>' escape value add "</plainLiteral>\n"
  }
  
  /**
   * @param value must not be null
   * @param datatype must not be null
   */
  override def typedLiteral(value: String, datatype: String): Unit = {
    this add spaces(depth) add "<typedLiteral"
    this add " datatype=" add '"' escapeUri(datatype, DATATYPE) add '"'
    this add '>' escape value add "</typedLiteral>\n"
  }
  
  override def end(context: String): Unit = {
    this endTag "triple"
    this endTag "graph" 
  }
  
  override def result = sb.toString
  
  // private helper methods
  
  private def escape(str: String): TriXBuilder = {
    XmlUtils.escape(sb, str)
    this
  }
  
  private def escapeUri(str: String, pos: Int): TriXBuilder = {
    val uri = parseUri(str, pos)
    // TODO: check if uri starts with BadUri. If yes, wrap the whole triple in <!-- and --> 
    // (but take care that we do it only once). But currently this class is only used during 
    // testing, so it's probably better to have these errors visible.
    escape(uri)
  }
  
  /**
   * print spaces, print tag, increase depth
   */
  private def startTag(name: String): Unit = {
    this add spaces(depth) add ('<') add(name) add (">\n")
    depth += 1
  }
  
  /**
   * decrease depth, print spaces, print tag
   */
  private def endTag(name: String): Unit = {
    depth -= 1
    this add spaces(depth) add ("</") add(name) add (">\n")
  }
  
  private def add(s: String): TriXBuilder = { 
    sb append s
    this 
  }
  
  private def add(c: Char): TriXBuilder = { 
    sb append c
    this 
  }
  
}
