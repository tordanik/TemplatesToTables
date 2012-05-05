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
import java.util.regex.Pattern
import net.sourceforge.jwbf.core.contentRep.Article;
import org.openstreetmap.wiki.tttbot.convert.ExtractedData
import org.openstreetmap.wiki.tttbot.convert.EntryFilter
import org.openstreetmap.wiki.tttbot.config._
import org.openstreetmap.wiki.tttbot.wiki.WikiTemplateParser._
import org.openstreetmap.wiki.tttbot.wiki.WikiHookUtil._
import org.openstreetmap.wiki.tttbot.wiki.WikiParser
import org.openstreetmap.wiki.tttbot.wiki.WikiParser._
import scala.util.matching._

class WikiHookWriter (
		
	templateNames : Seq[String],
	
	tableParams : String,
	
	withColumnTitles : Boolean,
	
	val columnSets : Seq[ColumnSetConfig]
	
) extends Writer[Unit] {
		
	/** map from ids of individual columns or column groups to column sequences */
	private val columnMap : Map[String, Seq[ColumnConfig]]
		= WikiHookWriter.createColumnMap(columnSets)
	
	/** templateNames from constructor wrapped in TemplateStructure objects */
	private val templateStructures = templateNames.map(n
			=> new TemplateStructure(n, Seq()))
	
	def write(data : ExtractedData) {
		
		updateHooks(templateNames,
				tttTableRegex.r,
				createTableForParams(data))
						
	}
	
	private def createTableForParams (data : ExtractedData) (params : Seq[(String, String)])
			: String = {
		
		val colsParam = params.find(_._1 == "cols")
		
		if (colsParam != None) {
			
			val colIds = colsParam.get._2.split(",") : Seq[String]
			val columnSeqs = for (id <- colIds) yield
				columnMap.get(id) match { case Some(cols) => cols; case _ => Seq() }
			val columns = columnSeqs.flatten[ColumnConfig]
			
			val filterParams = params.filterNot(p => p._1 == "cols" || p._1 == "lang")
			val filters = for (p <- filterParams) yield {
					new EntryFilter(p._1,
							p._2.replaceAll("/", "|")) //allow to use slash instead of pipe
			}
			
			val tableConfig = new TableOutputConfig(
					Seq(new ColumnSetConfig(None, columns)),
					filters,
					withColumnTitles,
					tableParams)
			
			return new WikiTableWriter(tableConfig).write(data)
			
		} else {
			return "" //TODO: change to exception (with exception type dedicated to template parsing)
		}
			
	}
		
}

object WikiHookWriter extends XMLLoader[WikiHookWriter] {
	
	protected override def fromXMLNode_internal (node : xml.Node, root : xml.Node)
		: WikiHookWriter = {
		
		val columnSets = for (child <- node.child
			     if (child.label == "columns"|| child.label == "col")) yield {
			child.label match {
				case "columns" => ColumnSetConfig.fromXMLNode(child, root)
				case "col"     => new ColumnSetConfig(None, 
						               Seq(ColumnConfig.fromXMLNode(child, root)))
			}
		}
				
		return new WikiHookWriter(
			(node \ "template" \ "@name").map(_.text),
			(node \ "tableParams").text,
			(node \ "@columnTitles").text == "true",
			columnSets
		);
		
	}
	
	/**
	 * skips n matches of a regex by moving to the text after the regex;
	 * then applies the replacement function. 
	 * Recursively repeats this for all larger n.
	 * 
	 * @param n  number of matches to skip
	 * @return   text with replacements
	 */
	private def replaceAllMatchesAfterNth(text : String, n : Int,
			regex : Regex, f : Regex.Match => String) : String = {
		
		var scannedText = text
		for (i <- 0 to n) {
			
		}
		
		val repText = "TODO"
		
		return replaceAllMatchesAfterNth(repText, n+1, regex, f)
		
	}
	
	private def createColumnMap(columnSets : Seq[ColumnSetConfig]) 
			: Map[String, Seq[ColumnConfig]] = {
		
		val setsWithId = columnSets.filter(_.id != None)
		val pairsFromSets = setsWithId.map(set => (set.id.get, set.columnConfigs))
		
		val cols = columnSets.map(_.columnConfigs).flatten[ColumnConfig]
		val pairsFromCols = cols.map(col => (col.id, Seq(col)))
		
		val allPairs = pairsFromSets ++ pairsFromCols		
		
		return allPairs.toMap[String, Seq[ColumnConfig]]
		
	}
	
}