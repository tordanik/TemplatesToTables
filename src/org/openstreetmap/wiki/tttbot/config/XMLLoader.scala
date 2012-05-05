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

package org.openstreetmap.wiki.tttbot.config

import scala.xml

abstract class XMLLoader[ResultType] {

	/**
	 * loads an object from an xml node.
	 * Also 
	 * @param node
	 * @param xmlFile
	 * @return
	 */
	def fromXMLNode(node : xml.Node, root : xml.Node) : ResultType = {
		
		node.attribute("idref") match {
		
			case Some(idref) => 
				val idNodes = (root \\ "_").filter(_.attribute("id") != None)
				idNodes.find(n => (n \ "@id").text == idref.text) match {
					case Some(refdNode) => return fromXMLNode(refdNode, root)
					case None           => throw new Error("referenced id doesn't exist: " + idref.text)
				}
			
			case None =>				
				return fromXMLNode_internal(node : xml.Node, root : xml.Node) : ResultType
			
		}
		
	}
	
	/**
	 * method used by fromXMLNode for reading the result;
	 * doesn't need to deal with id/idref
	 */
	protected def fromXMLNode_internal(node : xml.Node, root : xml.Node) : ResultType
	
	
	
}