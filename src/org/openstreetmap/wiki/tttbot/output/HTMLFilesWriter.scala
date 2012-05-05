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

import java.io.File
import org.openstreetmap.wiki.tttbot.convert.TableData
import org.openstreetmap.wiki.tttbot.config.ColumnConfig
import scala.xml

/* TODO reactivate

/**
 * writes table data to HTML files.
 * Depending on the number of primary attributes, multiple files may be produced.
 * It does not produce any return values, only side effects (IO).
 */
class HTMLFilesWriter (

	/** directory where the files will be created */
	val rootDir : File,
	
	/** additional content for the generated HTML document */
	val htmlConfig : HTMLConfig
		
) extends Writer[Unit] {

	def write(data : TableData) {
		
		createHTMLFile(data, List.empty)
		
		val allFilters = 
			for (primaryColumn <- data.columns.filter(_.primary);
			     value <- primaryColumn.values)
			yield new TableFilter(primaryColumn, value)
		
		for(filter <- allFilters) {
			val filters = List(filter)
			createHTMLFile(data, filters)
		}
			
	}
	
	/**
	 * creates the entire XML for a document, ready to write to a file.
	 * It's possible to create different documents for a single TableData object
	 * by defining filters that remove lines (and make some columns unnecessary).
	 * 
	 * @param filters  list of filters for this document,
	 *                 consisting of the column and the value it is set to 
	 */
	private def generateXMLForDocument (data : TableData,
			filters : Seq[TableFilter]) : xml.Elem = {
		
		<html>
			<head>
				{ htmlConfig.head }
				<meta name="generator" content="TemplatesToTables"/>
			</head>
			<body>
				{ htmlConfig.bodyIntro }
				{ xmlForFilters(data, filters) }
				{ htmlConfig.bodyCenter }
				{ new HTMLTableWriter(filters, htmlConfig.tableSummary).write(data) }
				{ htmlConfig.bodyOutro }	
			</body>
		</html>
		
	}
	
	/**
	 * returns the information about the active filters
	 * as it will be included on the generated page
	 */
	private def xmlForFilters(data : TableData,
			filters : Seq[TableFilter]) : xml.Elem = {
		
		<table class="filters" summary="List of active filters with controls to add and remove them.">
			<tr> {
				for (filter <- filters) yield {
					val relpath = relativeFilePath(data, filters.filter(_ != filter))
					<th>{filter.column} <a
						href={relpath.toString} class="delfilter"
						title="Remove filter"></a></th>
				}
			} </tr>
			<tr> {
				for (filter <- filters) 
					yield <td>{filter.value}</td> //TODO create drop-down menu with column values
			} </tr>
		</table>
		
	}
	
	/** returns the file path depending on active filters; relative to rootDir */
	private def relativeFilePath(data : TableData, 
			filters : Seq[TableFilter]) : File = {
		
		val path = new StringBuilder("index")
		
		for (
				column <- data.columns;
				if filters.exists(_.column == column);
				value = filters.find(_.column == column).get.value
		) {
				val columnIndex = data.columns.indexOf(column)
				val valueIndex = column.values.indexOf(value)
				path.append("_" + columnIndex + "_" + valueIndex)			
		}
		
		return new File(path.toString + ".html")
			
	}
	
	/** returns the full file path depending on active filters */
	private def fullFilePath(data : TableData, 
			filters : Seq[TableFilter]) : File = {
		
		val relPath = relativeFilePath(data, filters);
		return new File(rootDir.getAbsolutePath + File.separator + relPath);
		
	}
	
	private def createHTMLFile(data : TableData, filters : Seq[TableFilter]) {
		
		val rootElem = generateXMLForDocument(data, filters)
		val path = fullFilePath(data, filters)
		
		xml.XML.saveFull(path.toString, rootElem, "UTF-8", true, null)
		
	}
	
}

*/