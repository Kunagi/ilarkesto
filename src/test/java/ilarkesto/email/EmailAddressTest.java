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
package ilarkesto.email;

import ilarkesto.testng.ATest;

import java.util.List;

import org.testng.annotations.Test;

public class EmailAddressTest extends ATest {

	@Test
	public void parse() {
		assertEmail(new EmailAddress("wi@koczewski.de"), null, "wi@koczewski.de");
		assertEmail(new EmailAddress("Witoslaw Koczewski <wi@koczewski.de>"), "Witoslaw Koczewski", "wi@koczewski.de");
		assertEmail(new EmailAddress("\"Witoslaw Koczewski\" <wi@koczewski.de>"), "Witoslaw Koczewski",
			"wi@koczewski.de");
		assertEmail(new EmailAddress("'Witoslaw Koczewski' <wi@koczewski.de>"), "Witoslaw Koczewski", "wi@koczewski.de");
	}

	@Test
	public void parseList() {
		List<EmailAddress> addresses = EmailAddress
				.parseList("Witoslaw Koczewski <wi@koczewski.de>; support@kunagi.org");
		assertEquals(addresses.size(), 2);
		assertEmail(addresses.get(0), "Witoslaw Koczewski", "wi@koczewski.de");
		assertEmail(addresses.get(1), null, "support@kunagi.org");
	}

	private static void assertEmail(EmailAddress ea, String label, String address) {
		assertEquals(ea.getLabel(), label);
		assertEquals(ea.getAddress(), address);
	}

}
