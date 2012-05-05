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
import net.sourceforge.jwbf.core.contentRep._

/** 
 * singleton that is used throughout the program
 * for accessing a MediaWiki installation
 */
object WikiConnection {
	
	private var wikiURL = None : Option[String]	
	private var wikiUser = None : Option[String]
	private var wikiPassword = None : Option[String]
	
	private var wikiInterface = None : Option[MediaWikiBot]
	
	/**
	 * reads configuration from xml file.
	 * overwrites existing information.
	 */
	def configureWithXML(root : xml.Node) {
		
		wikiURL = Some((root \ "@wikiURL").text)
		wikiUser = Some((root \ "@wikiUser").text)
		
		updateWikiInterface()
		
	}
	
	/**
	 * reads configuration from command line params.
	 * overwrites existing information.
	 */
	def configureWithCommandLine(args : Array[String]) {
		
		for (paramPair <- paramPairs(args)) {
			paramPair match {
				case ("--wikiUrl", url)      => this.wikiURL = Some(url)
				case ("--wikiUser", user)    => this.wikiUser = Some(user)
				case ("--wikiPassword", pwd) => this.wikiPassword = Some(pwd)
				case _                       => //skip
			}
		}
		
		updateWikiInterface()
		
	}	
	
	private def paramPairs (args : Array[String]) : Seq[(String, String)] = 		
		for (index <- 0 to args.size - 2) yield
			(args(index), args(index+1))
				
	private def updateWikiInterface () {
		if (wikiURL != None) {
			wikiInterface = Some(new MediaWikiBot(WikiConnection.wikiURL.get))
			if (wikiUser != None && wikiPassword != None) {
				wikiInterface.get.login(wikiUser.get, wikiPassword.get)
			}
		}
	}
	
	def getWikiInterface() : MediaWikiBot = {
		if (wikiInterface == None) throw new IllegalStateException("wiki interface not yet created")
		return wikiInterface.get
	}
			
}