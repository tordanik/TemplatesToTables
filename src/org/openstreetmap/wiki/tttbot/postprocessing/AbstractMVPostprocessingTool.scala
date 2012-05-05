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

package org.openstreetmap.wiki.tttbot.postprocessing

/** 
 * operation that views a table cell as a list of independent strings
 * (multivalue or MV string) that are edited individually
 */
abstract class AbstractMVPostprocessingTool extends PostprocessingTool {

	/** 
	 * template method that splits the string at a separator string
	 * and passes the individual parts to processPart.
	 * 
	 * @param arguments  needs to contain "sep" key; is passed to processPart
	 */
	override def process (arguments : Map[String, String]) (input : String) : String = {		
		
		require(arguments.get("sep") != None)
		
		val sep = arguments("sep")
		val parts = input.split(sep)
				
		val outputSeq : Seq[String] = for (
				part <- parts;
				outputPart = processPart(arguments)(part);
				if outputPart != None)
			yield outputPart.get
			
		return outputSeq.mkString(sep)
		
	}
	
	/**
	 * operation performed on the individual parts of the list
	 * 
	 * @return  output string for the part; 
	 *          None if the part was filtered away by the operation
	 */
	def processPart (arguments : Map[String, String]) (input : String) : Option[String]
	
}