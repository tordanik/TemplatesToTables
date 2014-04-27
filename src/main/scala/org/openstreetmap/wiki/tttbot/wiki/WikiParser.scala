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

package org.openstreetmap.wiki.tttbot.wiki

import net.sourceforge.jwbf.mediawiki.bots._
import net.sourceforge.jwbf.mediawiki.actions.queries._
import net.sourceforge.jwbf.core.contentRep.Article;
import org.openstreetmap.wiki.tttbot.config._
import org.openstreetmap.wiki.tttbot.convert.ExtractedEntry
import scala.util.matching._
import scala.collection.mutable

object WikiParser {
	
	val tttTableRegex = """\{\|\s*<!--TTTtable-->[^\n]*\n(?:(?:!|\|-|\| )[^\n]*\n)*?\|\}"""
	
	val tttListRegex = """<!--TTTlist-->\n(?:\*[^\n]*\n)*?"""
	
	private val keyRegex = """\s*([^=\s]+)\s*"""
	private val valueRegex = """\s*(.*[^\s])\s*"""

	private val parameterRegex = ("" + keyRegex + "=" + valueRegex + "").r
		
	def parseText(title : String, text : String, templates : Seq[TemplateStructure]) : ExtractedEntry = {
		
		var cleanedText = prepareText(title, text)
		
		var entryData = Seq[(String, String)]()
		
		for (template <- templates) {
		
			var templateRegex = createTemplateRegex(template.name)
			
			for (templateOcc <- templateRegex.findAllIn(cleanedText)) {
							
				val templateRegex(parameterText) = templateOcc
							
				val data = mutable.ListBuffer.empty[(String, String)]
								
				for ((pKey, pValue) <- getTemplateParameters(template.name, templateOcc)) {
					val param = template.parameters.find(_.name == pKey);
					val key = if (param != None) param.get.targetKey else pKey
					data += ((key, pValue))
				}
				
				entryData ++= data
							
			}
			
		}
		
		return new ExtractedEntry(Map() ++ entryData)
		
	}
	
	/** returns the parameter list of a single call to a template */
	def getTemplateParameters (templateRegex : Regex, templateUse : String)
		: Seq[(String, String)] = {
		
		val templateRegex(parameterText) = templateUse
		
		val paramStrings = parameterText.split("\\|")
		
		val data = mutable.ListBuffer.empty[(String, String)]
		
		return for (
			paramString : String <- paramStrings			
			if parameterRegex.pattern.matcher(paramString).matches;
			parameterRegex(pKey, pValue) = paramString
		) yield (pKey, pValue)
		
	}
	
	def getTemplateParameters (templateName : String, templateUse : String)
		: Seq[(String, String)]
		= getTemplateParameters(createTemplateRegex(templateName), templateUse)
	
	/** removes comments, links etc. from the text */
	private def prepareText(title : String, text : String) : String = {
		
		var t = text
		
		// remove comments
		t = t.replaceAll("<!--.*?-->", "")
		
		// remove {{PAGENAME}} references
		t = t.replaceAll("""\{\{PAGENAME\}\}""", title)
		
		// remove wikilinks
		t = t.replaceAll("""\[\[[^\|\]]*?\|(.*?)\]\]""", "$1")
		t = t.replaceAll("""\[\[(.*?)\]\]""", "$1")
		
		return t
		
	}
	
	/** creates the regex that matches the template with a given name */
	def createTemplateRegex(templateName : String) : Regex = {
		
		val templateCall = templateName
			// underscores can be written as spaces when using a template		
			.replaceAll("_", "[ _]")		
			// the "<Namespace>:" prefix can be left off when using a template 
			.replaceAll("([^:]*?:)", "(?:$1)?")
		
		return ("\\{\\{\\s*" + templateCall + "\\s*([^\\}]*)\\}\\}").r
				
	}

}