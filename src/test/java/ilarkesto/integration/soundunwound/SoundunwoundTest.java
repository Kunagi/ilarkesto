/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.integration.soundunwound;

import ilarkesto.testng.ATest;

import org.testng.annotations.Test;

public class SoundunwoundTest extends ATest {

	@Test
	public void determineIdByTitle() {
		assertEquals(Soundunwound.determineIdByTitle("Ohrbooten - Babylon Bei Boot", false), "7360478");
	}

	@Test
	public void determineIdByTitleGuess() {
		assertEquals(Soundunwound.determineIdByTitle("Kaiser Chiefs - Yours Truly Angry Mob [2007]", true), "6929991");
	}

	@Test
	public void loadRecord() {
		SoundunwoundRecord r = Soundunwound.loadRecord("7360478");
		assertEquals(r.getArtist(), "Ohrbooten");
		assertEquals(r.getTitle(), "Babylon Bei Boot");
		assertEquals(r.getYear(), new Integer(2007));
		assertEquals(r.getCoverId(), "516z8jFoywL");
	}

}
