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
import scala.collection.mutable
import scala.util.matching._

object WikiTemplateParser {

	/** 
	 * returns all pages form the wiki that contain a template 
	 * as described by templateStructure
	 */
	def getPages(templateName  : String, articlesOnly : Boolean) : Seq[Article] = {
		
		val wikiInterface = WikiConnection.getWikiInterface
		
		val pageTitleIt = 
			if (articlesOnly) { new TemplateUserTitles(wikiInterface, templateName, 0).iterator() }
			else              { new TemplateUserTitles(wikiInterface, templateName).iterator() }
		
		val pageTitleBuffer = new mutable.ArrayBuffer[String]()
		
		var pageCount = 0
		
		while (pageTitleIt.hasNext() /* && pageCount < 20 */) {
			pageTitleBuffer += pageTitleIt.next()
			pageCount += 1
		}
		
		//eliminates duplicates //TODO: should not be necessary. Bug in lib or in use of lib?
		var pageTitleSet = pageTitleBuffer.toSet
		
		return pageTitleSet.map(wikiInterface.readContent).toSeq
		
	}
	
}