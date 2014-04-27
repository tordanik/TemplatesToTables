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

import collection.mutable
import java.util.regex.Pattern
import net.sourceforge.jwbf.core.contentRep.Article
import org.openstreetmap.wiki.tttbot.wiki.WikiTemplateParser._
import org.openstreetmap.wiki.tttbot.output.WikiOutput
import scala.util.matching.Regex

object WikiHookUtil {

	/**
	 * creates/updates tables or lists in a wiki
	 * at those places marked by a "hook" template
	 */
	def updateHooks(templateNames : Seq[String],
			contentRegex : Regex,
			createContentForParams : (Seq[(String, String)] => String)) {
	
		// find all pages using any of the templates in templateNames
		
		val pageBuffer = mutable.ListBuffer[Article]()
		for (template <- templateNames) { pageBuffer ++= getPages(template, false) }
		val pageSet = pageBuffer.toSet
		
		// update each page
		for (page <- pageSet) {
						
			var pageContent = page.getText
			
			for (template <- templateNames) {
								
				// write content above template uses
				
				val templateRegex = WikiParser.createTemplateRegex(template)				
				for (templateUse <- templateRegex.findAllIn(pageContent)) {
					
					val params = WikiParser.getTemplateParameters(templateRegex, templateUse)
					
					val content = createContentForParams(params)
					
					//remove existing list/table above the template (if present)
					val templateUseQuoted = Pattern.quote(templateUse)
					pageContent = pageContent.replaceAll(
							contentRegex + "\n+" + templateUseQuoted,
							templateUse)
					
					//write new list/table above the template					
					pageContent = pageContent.replace(
							templateUse,
							content + "\n" + templateUse)
					
					//TODO: using the same template call multiple times on the same page will cause problems!
					
				}
				
			}
			
			// write the new content to the page
			
			WikiOutput.writeIfChanged(page.getTitle, pageContent, "updated auto-generated tables")
			
		}
		
	}
		
}