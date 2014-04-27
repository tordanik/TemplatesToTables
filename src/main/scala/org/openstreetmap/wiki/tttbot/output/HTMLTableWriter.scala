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
import org.openstreetmap.wiki.tttbot.convert.ExtractedData
import scala.xml

/**
 * writes table data to a single HTML table element
 */
class HTMLTableWriter (

	val filters : Seq[TableFilter],
	
	val tableSummary : String

) extends Writer[xml.Elem] {
	
	def write(extractedData : ExtractedData) : xml.Elem = {
		
		throw new Error("unimplemented")
		
//		//TODO create tableData from extractedData
//		
//		val data = TableFilter.filterTable(tableData, filters)
//		val visibleColumns = data.columns.filter(_.visible)
//		
//		return <table class="main sortable" summary={tableSummary}> 
//			<thead> <tr> {			
//				for (column <- visibleColumns) yield 
//					<th class={if (column.sortable) "unsorted" else "sorttable_nosort"}>{column.label}</th>
//			} </tr> </thead>
//			<tbody> {
//				for (
//					line <- data.getLines()
//				) yield 
//					<tr> {
//						for (column <- visibleColumns; cell = line.values.get(column).get) 
//							yield if (cell == null) <td class="unknown">unknown</td>
//							      else <td>{ cell.text }</td>						
//					}</tr>
//			} </tbody>
//		</table>
		
	}
	
}