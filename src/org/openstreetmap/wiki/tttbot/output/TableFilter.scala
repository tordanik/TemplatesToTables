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

import org.openstreetmap.wiki.tttbot.config.ColumnConfig
import org.openstreetmap.wiki.tttbot.convert.TableData
import org.openstreetmap.wiki.tttbot.convert.TableLine

/**
 * a filter that accepts only those lines of TableData
 * which have a value matching 'valueRegex' in the column 'column'.
 * This is used for defining partial views of TableData.
 */
class TableFilter (
	val columnRegex : String,
	val valueRegex  : String
) {
	def accepts(line : TableLine) : Boolean =
		line.values.exists(entry
			=> entry._1.id.matches(columnRegex)
			&& entry._2.text.matches(valueRegex))
		
}

object TableFilter {
		
	def filterTable (data : TableData, filters : Seq[TableFilter]) : TableData = {
				
		val newColumns = data.columns.filter(
				c => !filters.exists(f => c.id.matches(f.columnRegex)))
				
		val acceptedLines = data.getLines().filter(l => filters.forall(_.accepts(l)))
		val acceptedLineSeqs = acceptedLines.map(_.values.toSeq)
		val newLineSeqs = acceptedLineSeqs.map(_.filter(e => newColumns.contains(e._1)))
		
		val result = new TableData(newColumns)
		newLineSeqs.foreach(result.addLine)
		
		return result
		
	}
	
	def fromXMLNode (node : xml.Node) : TableFilter = 		 
		new TableFilter(
			(node \ "@column").text,
			(node \ "@value").text
		)
	
}