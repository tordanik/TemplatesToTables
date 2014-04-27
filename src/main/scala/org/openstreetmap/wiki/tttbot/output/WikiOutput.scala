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

import net.sourceforge.jwbf.mediawiki.bots._
import net.sourceforge.jwbf.mediawiki.actions.queries._
import net.sourceforge.jwbf.core.contentRep._
import org.openstreetmap.wiki.tttbot.wiki.WikiConnection

class WikiOutput (
	
	val wikiPage : String

) extends Output {

	def write(s : String) =
		WikiOutput.writeIfChanged(wikiPage, s, "updated auto-generated tables")
		
}

object WikiOutput {
	
	def writeIfChanged(wikiPage : String, newText : String, editSummary : String) {
		
		val wikiInterface = WikiConnection.getWikiInterface
		
		val currentText = wikiInterface.readData(wikiPage).getText
		
		if (currentText == newText) {
			println(wikiPage + ": no update necessary")
		} else {
		
			val article = new SimpleArticle()
			
			article.setLabel(wikiPage)
			article.setText(newText)
			article.setEditSummary(editSummary)
			article.setMinorEdit(false)
			
			wikiInterface.writeContent(article)
//			println(newText)
			
			println(wikiPage + ": updated!")
			
		}
		
	}	
	
}