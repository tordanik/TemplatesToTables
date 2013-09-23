/*
Copyright (c) 2010, Tobias Knerr
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.openstreetmap.wiki.tttbot.convert

import net.sourceforge.jwbf.mediawiki.bots._
import net.sourceforge.jwbf.mediawiki.actions.queries._
import net.sourceforge.jwbf.core.contentRep.Article;
import org.openstreetmap.wiki.tttbot.config._
import org.openstreetmap.wiki.tttbot.wiki.WikiConnection
import org.openstreetmap.wiki.tttbot.wiki.WikiTemplateParser._
import org.openstreetmap.wiki.tttbot.wiki.WikiParser
import scala.util.matching._
import scala.collection.mutable

/** information that was extracted from wiki templates */
class ExtractedData (

	templates : Seq[TemplateStructure],
	sorting : Option[ExtractSorting]
		
) {
	
	var keys = null : Set[String]
	var entries = null : Seq[ExtractedEntry]
	
	{ //data is extracted at construction time
		
		val pages = mutable.ListBuffer[Article]()
		for (template <- templates) { pages ++= getPages(template.name, true) }
				
		val extractedKeys = mutable.ListBuffer[String]()
		for (template <- templates) { extractedKeys ++= template.extractedKeys }
		keys = Set() ++ extractedKeys
		
		val extractedEntries = for (page <- pages) yield {
			WikiParser.parseText(page.getTitle, page.getText(), templates);
		}
		
		entries = sorting match {
			case None    => extractedEntries
			case Some(s) => s.sort(extractedEntries)
		}
		
	}
		
	override def toString = entries.toString
		
}

object ExtractedData {
		
	def fromXMLDocument (root : xml.Node) : ExtractedData = {
		
		return fromXMLNode ((root \ "extract").head, root)
		
	}
	
	def fromXMLNode (node : xml.Node, root : xml.Node) : ExtractedData = {
		
		val sorting = if (!(node \ "sorting").isEmpty) {
			Some(ExtractSorting.fromXMLNode(node, root))
		} else {
			None
		}			
		
		return new ExtractedData(
			(node \ "template").map(n => TemplateStructure.fromXMLNode(n, root)),
			sorting)
		
	}
	
}