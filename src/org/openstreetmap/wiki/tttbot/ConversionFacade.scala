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

package org.openstreetmap.wiki.tttbot

import java.io.File
import org.openstreetmap.wiki.tttbot.convert._
import org.openstreetmap.wiki.tttbot.config._
import org.openstreetmap.wiki.tttbot.output._
import org.openstreetmap.wiki.tttbot.postprocessing._
import org.openstreetmap.wiki.tttbot.wiki._

object ConversionFacade {

	def performConversion(configFile : File, args : Array[String]) {
		
		/* create default toolbox */
		
		PostprocessingToolbox.addTool("replace", ReplaceTool)
		PostprocessingToolbox.addTool("multivalueFilter", MVFilterTool)
		
		/* read configuration file and options */
		
		val xmlRoot = xml.XML.loadFile(configFile.toString)
		
		WikiConnection.configureWithXML(xmlRoot)
		WikiConnection.configureWithCommandLine(args)
		
		val extractedData = ExtractedData.fromXMLDocument(xmlRoot)
		val outputConfig = OutputConfig.fromXMLDocument(xmlRoot)
		
		
		/* perform conversion */
		
		for (target <- outputConfig.targets) target.write(extractedData)
		
	}
	
}