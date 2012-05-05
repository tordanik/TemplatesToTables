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

import org.openstreetmap.wiki.tttbot.convert.ExtractedEntry
import org.openstreetmap.wiki.tttbot.convert.TableCell
import org.openstreetmap.wiki.tttbot.convert.Mapping
import scala.util.matching._
import java.util.regex.Pattern

class ColumnConfig (
	
	val id : String,
	
	val label : String,
	
	val labelTranslated : Seq[(String, String)],
		
	/**
	 * Determines whether the column is visible in the table.
	 * Some columns exist only for filtering purposes and will not need to be displayed.
	 */
	val visible : Boolean,
	
	/** 
	 * Primary columns will cause the generation of individual HTML table pages:
	 * If there is a single primary column with values a b and c,
	 * 3 HTML pages will be created (in addition to the default page)
	 * which will only contain lines for entries where that column would
	 * have the value a/b/c and will omit that column.   
	 */
	val primary : Boolean,
	
	/**
	 * determines whether the column should be prepared for sorting
	 */
	val sortable : Boolean,
	
	/**
	 * values that are legal for this column; null if there are infinitely many. 
	 * Must not be null if this is a primary column because it isn't possible to
	 * create infinitely many files...
	 */
	val values : Seq[String],
	
	/**
	 * attributes of the column
	 */
	val attributes : Seq[(String,String)],
	
	/**
	 * extracts the value for a data field in this column from an ExtractedEntry.
	 * This is used for implementing readValue
	 */
	private val readValueF : (String, ExtractedEntry) => TableCell
	
) { 
	
	require(!(primary && values == null))
	
	/** 
	 * alternative constructor that provides a default implementation for readData: ColumnConfig.readDataBasic
	 */
	def this (name : String, label : String, labelTranslated : Seq[(String, String)],
	    visible : Boolean, primary : Boolean, sortable : Boolean,
	    values : Seq[String], attributes : Seq[(String,String)]) = 
				this(name, label, labelTranslated, visible, primary, sortable,
						values, attributes, ColumnConfig.readDataBasic)
	
	override def toString = id
	
	def readValue (entry : ExtractedEntry) : TableCell = readValueF(id, entry)
	
}

object ColumnConfig extends XMLLoader[ColumnConfig] {

	override protected def fromXMLNode_internal(node : xml.Node, root : xml.Node)
		: ColumnConfig = {
			
			val values = for (value <- node \ "value") yield {
				(value \ "@string").text
			}
			
			val mappings = (node \ "mapping").map(Mapping.fromXMLNode)
			
			val name = (node \ "@id").toString
			val labelEl = node \ "@label"
					
			new ColumnConfig (
				name,
				if (labelEl.size > 0) labelEl.first.text else name,
				(node \ "label").map(l => ((l \ "@lang").text, (l \ "@text").text)),
				(node \ "@visible").text == "true",
				(node \ "@primary").text == "true",
				(node \ "@sortable").text == "true",
				values,
				(node \ "attribute").map(a => ((a \ "@key").text, (a \ "@value").text)), 
				if (mappings.isEmpty) readDataBasic else readDataMapping(mappings))
				
	}
	
	/**
	 * maps entry keys to column names and entry values to column values.
	 */
	private def readDataBasic(columnId : String, entry : ExtractedEntry) : TableCell = {
				
		if (entry.data.contains(columnId)) {
			return new TableCell(entry.data.get(columnId).get, Seq())
		} else {
			return null
		}
		
	}
	
	/** 
	 * Using currying, it is possible to set this as the readDataF member of a ColumnConfig instance.
	 * 
	 * @param mappings, in the order they are tested.
	 */
	private def readDataMapping (mappings : Seq[Mapping])
		(columnName : String, entry : ExtractedEntry) : TableCell = {
		
		//TODO: move some of this code to Mapping
		
		for (mapping <- mappings) {
			
			val groups = new scala.collection.mutable.ListBuffer[String]
			var matches = true		
			
			for ((key, value) <- mapping.kvPairs) {
				if (entry.data.contains(key)) {
					val extractedValue = entry.data.get(key).get
					val matcher = value.pattern.matcher(extractedValue)
					if (matcher.matches()) {
						for (group <- 1 to matcher.groupCount) {
							groups += matcher.group(group)
						}
					} else {
						matches = false
					}
				} else {
					matches = false
				}
			}
			
			if (matches) {
				var text = mapping.cellText 
				for (group <- 1 to groups.size) {
					text = text.replace("$" + group, groups(group-1))
				}
				var result = mapping.applyPostprocessingOperations(text)
				return new TableCell(result, mapping.cellAttributes)
			}
			
		}
		
		//fallback if no matching worked
		
		return readDataBasic(columnName, entry)
		
	}
	
}