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
import org.openstreetmap.wiki.tttbot.config.TableOutputConfig
import org.openstreetmap.wiki.tttbot.config.XMLLoader
import org.openstreetmap.wiki.tttbot.convert.TableData
import org.openstreetmap.wiki.tttbot.convert.ExtractedData

class WikiPageWriter (
		
	targetFile : File,
	
	val targetPage : String,
	
	/**
	 * sequence of String and OutputTableConfig instances
	 * that describes the page being built
	 */
	val structure : Seq[Object]
	
) extends Writer[Unit] {

	def write(data : ExtractedData) {
		
		val result = new StringBuilder()
		
		for (element <- structure) {
			result.append(element match {
				case s : String => s
				case t : TableOutputConfig => new WikiTableWriter(t).write(data)
			})
		}
		
		if (targetFile != null) {
			new FileOutput(targetFile).write(result.toString)
		} 
		if (targetPage != null) {
			new WikiOutput(targetPage).write(result.toString)
		}
		
	}
	
}

object WikiPageWriter extends XMLLoader[WikiPageWriter] {
	
	protected override def fromXMLNode_internal (node : xml.Node, root : xml.Node)
		: WikiPageWriter = {
		
		val targetFile =
			if (node.attribute("file") == None) null 
			else new File((node \ "@file").text)
		
		val targetPage =
			if (node.attribute("page") == None) null 
			else (node \ "@page").text
		
		val structure = for (child <- (node \ "_")) yield {
			child.label match {
				case "text" => child.text
				case "table" => TableOutputConfig.fromXMLNode(child, root)
				case _ => throw new Error("unknown child: " + child)
			}
		}
				
		return new WikiPageWriter(
			targetFile,
			targetPage,
			structure
		);
		
	}
	
}