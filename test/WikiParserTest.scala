import org.junit._
import org.junit.Assert._

import org.openstreetmap.wiki.tttbot.wiki.WikiParser

class WikiParserTest {

	@Test
	def testTttTableRegex() {
		
		val tableString = """{| <!--TTTtable--> class="wikitable sortable" style="font-size: 85%; text-align: center;"
|-
! scope="col" | Name
! scope="col" | Customizable log interval
! scope="col" | GPX format
! scope="col" | KML format
! scope="col" | NMEA format
! scope="col" | All formats
! scope="col" | Geo-tagged notes
! scope="col" | Geo-tagged photos 
! scope="col" | Geo-tagged audio
! scope="col" | Fast POI buttons
|-
| [[OsmAnd|OsmAnd]]
| {{?}}
| {{yes}}
| {{no}}
| {{no}}
| gpx
| {{?}}
| {{?}}
| {{?}}
| {{?}}
|}"""
		
		assertTrue(tableString.matches(WikiParser.tttTableRegex))
		
	}
	
}