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

package org.openstreetmap.wiki.tttbot.output

import collection.mutable
import net.sourceforge.jwbf.core.contentRep.Article
import org.openstreetmap.wiki.tttbot.config.XMLLoader
import org.openstreetmap.wiki.tttbot.config.ColumnConfig
import org.openstreetmap.wiki.tttbot.convert.ExtractedData
import org.openstreetmap.wiki.tttbot.wiki.WikiParser._
import org.openstreetmap.wiki.tttbot.wiki.WikiTemplateParser._
import org.openstreetmap.wiki.tttbot.wiki.WikiHookUtil._

class ColumnDocumentationWriter (
	
	/** object whose column selection will be documented */
	documentedObject : WikiHookWriter,
	
	templateNames : Seq[String]

) extends Writer[Unit] {

	override def write(data : ExtractedData) : Unit = {
		
		// collect individual columns
		
		val columnsWithoutId = documentedObject.columnSets.filter(_.id == None)
		val individualColumns = columnsWithoutId.map(_.columnConfigs).flatten[ColumnConfig]
		
		// generate the list
		
		val s = new StringBuffer()
		
		if (!individualColumns.isEmpty) {			
			s.append("* ''<nowiki>(individual columns)</nowiki>''\n")
			for (col <- individualColumns) {
				s.append("** ")
				s.append(col.id)
				s.append(" (\"" + col.label + "\")")
				s.append('\n')				
			}
		}
		
		for (colSet <- documentedObject.columnSets if colSet.id != None) {			
			s.append("* <nowiki>" + colSet.id.get + "</nowiki>\n")
			for (col <- colSet.columnConfigs) {
				s.append("** ")
				s.append(col.id)
				s.append(" (\"" + col.label + "\")")
				s.append('\n')				
			}			
		}
				
		// write the list to those wiki pages where it is needed 
		
		updateHooks(templateNames,
				tttListRegex.r,
				_ => "<!--TTTlist-->\n" + s.toString)
		
		// println(s.toString)	
		
	}
		
}

object ColumnDocumentationWriter extends XMLLoader[ColumnDocumentationWriter] {
	
	protected override def fromXMLNode_internal (node : xml.Node, root : xml.Node)
		: ColumnDocumentationWriter = {
						
		return new ColumnDocumentationWriter(
			(node \ "target").map(n => WikiHookWriter.fromXMLNode(n, root)).first,
			(node \ "template" \ "@name").map(_.text)
		)
		
	}
	
}