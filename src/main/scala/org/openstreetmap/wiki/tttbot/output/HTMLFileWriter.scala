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
import org.openstreetmap.wiki.tttbot.config.XMLLoader
import org.openstreetmap.wiki.tttbot.convert.ExtractedData

class HTMLFileWriter (
		
	targetFile : File,
	
	filters : Seq[TableFilter],
	
	/** nodes for the head of the HTML document */
	head : xml.NodeSeq,
	
	/** nodes before the filter list */
	bodyIntro : xml.NodeSeq,
	
	/** nodes between filter list and table */
	bodyCenter : xml.NodeSeq,
	
	/** nodes after the table */
	bodyOutro : xml.NodeSeq,
	
	/** summary for the table */
	tableSummary : String
		
) extends Writer[Unit] {

	def write(data : ExtractedData) {
		
		val tableWriter = new HTMLTableWriter(filters, tableSummary)
		val table = tableWriter.write(data)
		
		val xmlDoc =
		<html>
			<head>
				{ head }
				<meta name="generator" content="TemplatesToTables"/>
			</head>
			<body>
				{ bodyIntro }
				{ bodyCenter }
				{ table }
				{ bodyOutro }	
			</body>
		</html>

		new FileOutput(targetFile).write(xmlDoc)
		
	}
	
}

object HTMLFileWriter extends XMLLoader[HTMLFileWriter] {
	
	protected override def fromXMLNode_internal (node : xml.Node, root : xml.Node)
		: HTMLFileWriter = {
			
		val filters = (node \ "filter").map(TableFilter.fromXMLNode)
		
		return new HTMLFileWriter(
			new File((node \ "@file").text),
			filters,
			node \ "html" \ "head" \ "_",
			node \ "html" \ "bodyIntro" \ "_",
			node \ "html" \ "bodyCenter" \ "_",
			node \ "html" \ "bodyOutro" \ "_",
			(node \ "html" \ "@tableSummary").text
		);
		
	}
	
}