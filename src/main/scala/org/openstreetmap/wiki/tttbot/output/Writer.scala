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

import org.openstreetmap.wiki.tttbot.config.XMLLoader
import org.openstreetmap.wiki.tttbot.convert.ExtractedData

/* any class that can take table data and produce some output format */
abstract trait Writer[ResultType] {

	def write(data : ExtractedData) : ResultType
	
}

object Writer extends XMLLoader[Writer[_]] {

	protected override def fromXMLNode_internal (node : xml.Node, root : xml.Node) : Writer[_] = {
		
		(node \ "@type").text match {
			case "wikipage"    => return WikiPageWriter.fromXMLNode(node, root)
			case "wikihook"    => return WikiHookWriter.fromXMLNode(node, root)
			case "wikihookDoc" => return ColumnDocumentationWriter.fromXMLNode(node, root)
			case "htmlpage"    => return HTMLFileWriter.fromXMLNode(node, root)
			case _  => throw new Error("unknown output target type: " + (node \ "@type").text)
		}
		
	}
	
}