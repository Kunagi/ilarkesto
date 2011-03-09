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
package ilarkesto.core.navig;

import ilarkesto.testng.ATest;

import org.testng.annotations.Test;

public class NavigatorTest extends ATest {

	@Test
	public void goBack() {
		Navigator n = new Navigator();
		Page p = new Page(null);
		n.goNext(p);
		n.goBack();
		assertSame(n.getPage(), n.getRootPage());
	}

	@Test
	public void goNext() {
		Navigator n = new Navigator();
		Page p = new Page(null);
		n.goNext(p);
		assertSame(n.getPage(), p);
	}

	@Test
	public void goBackToRoot() {
		Navigator n = new Navigator();
		n.goBackToRoot();
		assertSame(n.getPage(), n.getRootPage());
	}

}
