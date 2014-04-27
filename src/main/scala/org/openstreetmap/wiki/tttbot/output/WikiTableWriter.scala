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
import org.openstreetmap.wiki.tttbot.config.TableOutputConfig
import org.openstreetmap.wiki.tttbot.convert.TableData
import org.openstreetmap.wiki.tttbot.convert.TableCreator
import org.openstreetmap.wiki.tttbot.convert.ExtractedData
import scala.xml

/**
 * writes table data in MediaWiki syntax to a String
 */
class WikiTableWriter (

	val config : TableOutputConfig

) extends Writer[String] {
	
	def write(extractedData : ExtractedData) : String = {
		
		val columnConfigs = config.columnSets.map(_.columnConfigs).flatten[ColumnConfig]
		val tableData = TableCreator.convert(extractedData, columnConfigs, config.filters)
		
		var s = new String();
		
		s += "{| <!--TTTtable--> " + config.tableParams + "\n"
		s += "|-\n"
		
		for (column <- tableData.columns.filter(_.visible)) {
			s += "! scope=\"col\" "
			for (a <- column.attributes) {
				s += a._1 + "=\"" + a._2 + "\" "
			}
			s += "| " + localizeLabel(column) + "\n"
		}
				
		for (line <- tableData.getLines()) {
			s += "|-\n"
			for (column <- tableData.columns.filter(_.visible);
				 cellOpt = line.values.get(column)) {
				s += "| "
				if (config.withColumnTitles) {
					s += "title=\"" + localizeLabel(column) + "\" "
					if (cellOpt == None ||
							!(cellOpt.get.text.startsWith("{{yes")
								|| cellOpt.get.text.startsWith("{{no")
								|| cellOpt.get.text.startsWith("{{free")
								|| cellOpt.get.text.startsWith("{{nonfree"))){
						//TODO: remove hard-coded reliance on Yes/No templates from the condition
						s += "| "
					}
				}
				if (cellOpt != None) {
					 s += cellOpt.get.text
				}
				s += "\n"
			}			
		}
		
		s += "|}"
		
		return s
		
	}
	
	private def localizeLabel(column : ColumnConfig) : String = {
	  
		if (config.lang != None && 
		    column.labelTranslated.exists(_._1 == config.lang.get)) {
			return column.labelTranslated.find(_._1 == config.lang.get).get._2
		} else {
			return column.label		  
		}
		
	}
	
}