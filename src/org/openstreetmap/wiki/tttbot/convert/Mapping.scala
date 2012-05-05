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

import org.openstreetmap.wiki.tttbot.postprocessing.PostprocessingToolbox
import scala.xml
import scala.util.matching.Regex

/**
 * a set of key/value conditions that, if fulfilled,
 * will lead to a certain value being set to a cell 
 */
class Mapping (
	
	/** 
	 * pairs of string and regular expression that needs to match 
	 * a key-value pair in the extracted data.
	 * Can capture groups referenced (using $1, $2 ...)
	 * in the text field.
	 * Order is relevant for group numbering.
	 */
	val kvPairs : Seq[(String, Regex)],
	
	val cellText : String,
	
	val cellAttributes : Seq[(String, String)],
	
	postprocessingOperations : Seq[String => String]
			
) { 
	
	def applyPostprocessingOperations(input : String) : String = {
		
		var text = input
		
		for (op <- postprocessingOperations) {
			text = op(text)
		}
		
		return text
		
	}
	
	
}

object Mapping {
	
	def fromXMLNode(node : xml.Node) : Mapping = {
		
		var kvPairs = for (and <- (node \ "and"))
				yield ((and \ "@key").text, (and \ "@from").text.r)
	
		if (node.attribute("key") != None && node.attribute("from") != None) {
			val firstKVPair = ((node \ "@key").text, (node \ "@from").text.r)
			kvPairs = List(firstKVPair) ++ kvPairs
		}
		
		return new Mapping(
			kvPairs,
			(node \ "@to").text,
			Seq() /* TODO cell attributes */,
			(node \ "postprocessing").map(PostprocessingToolbox.getOperationForXmlNode))
		
	}
	
}