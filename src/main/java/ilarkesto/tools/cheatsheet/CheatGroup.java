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
package ilarkesto.tools.cheatsheet;

import java.util.ArrayList;
import java.util.List;

public class CheatGroup {

	private String label;
	private List<Cheat> cheats = new ArrayList<Cheat>();

	public CheatGroup(String label) {
		super();
		this.label = label;
	}

	public List<Cheat> getCheats() {
		return cheats;
	}

	public void addCheat(Cheat cheat) {
		cheats.add(cheat);
	}

	@Override
	public String toString() {
		return label;
	}

}
