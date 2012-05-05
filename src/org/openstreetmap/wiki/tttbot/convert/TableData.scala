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

package org.openstreetmap.wiki.tttbot.convert

import org.openstreetmap.wiki.tttbot.config._
import scala.collection.mutable

/** mutable class that represents table data */
class TableData (

	val columns : Seq[ColumnConfig]
	
) {
	
	private val lines = mutable.ListBuffer[TableLine]()
	
	/** 
	 * adds a complete line of data.
	 * It doesn't need to contain entries for all columns (some may be unknown),
	 * but it must not contain any columns that don't exist in the columns member 
	 */
	def addLine(line : Seq[(ColumnConfig, TableCell)]) {
		
		if (line.exists(e => !columns.contains(e._1))) throw new IllegalArgumentException
		
		val lineMap = Map() ++ line
		lines += new TableLine(lineMap)
		
	}
	
	def getLines() : Seq[TableLine] = {
		return lines
	}

}