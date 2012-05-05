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

package org.openstreetmap.wiki.tttbot.config

import org.openstreetmap.wiki.tttbot.convert.EntryFilter

/** configuration for a single instance of a table */
class TableOutputConfig (
    
	val columnSets : Seq[ColumnSetConfig],
	
	val filters : Seq[EntryFilter],
	
	val withColumnTitles : Boolean,
	
    val lang : Option[String],
	
	/** string that will be added after the {| of the wiki table */
	val tableParams : String
	
) {}

object TableOutputConfig extends XMLLoader[TableOutputConfig] {
	
	protected override def fromXMLNode_internal(node : xml.Node, root : xml.Node) : TableOutputConfig = {
		
		val columnSets = for (child <- node.child
			     if (child.label == "columns"|| child.label == "col")) yield {
			child.label match {
				case "columns" => ColumnSetConfig.fromXMLNode(child, root)
				case "col"     => new ColumnSetConfig(None, Seq(ColumnConfig.fromXMLNode(child, root)))
			}
		}
		
		return new TableOutputConfig(
		    
			columnSets,
			(node \ "filter").map(EntryFilter.fromXMLNode),
			(node \ "@columnTitles").text == "true",
			None,
			(node \ "tableParams").text
		);
		
	}	
	
}