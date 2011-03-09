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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

public final class EmailAddress {

	public static void main(String[] args) {
		System.out.println(EmailAddress.formatAddress("<duke@hell.org> Crap", false));
	}

	private String address;
	private String label;

	public EmailAddress(String emailAddress) {
		if (emailAddress == null) throw new IllegalArgumentException("emailAddress == null");
		this.address = formatAddress(emailAddress, true);
		validatePlainAddress(this.address);
		int idx = emailAddress.indexOf("<");
		if (idx >= 0) {
			label = emailAddress.substring(0, idx).trim();
			if (label.length() >= 2) {
				if (label.startsWith("\"") || label.startsWith("'")) label = label.substring(1);
				if (label.endsWith("\"") || label.endsWith("'")) label = label.substring(0, label.length() - 1);
			}
			if (label.length() == 0) label = null;
		}
	}

	public EmailAddress(String email, String label) {
		if (email == null) throw new IllegalArgumentException("email == null");
		validatePlainAddress(email);
		this.address = email;
		this.label = label;
	}

	public String getAddress() {
		return address;
	}

	public String getLabel() {
		return label;
	}

	public boolean isLabelSet() {
		return label != null;
	}

	private int hashCode;

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode = 23;
			hashCode = hashCode * 37 + address.hashCode();
			// if (label != null) hashCode = hashCode * 37 + label.hashCode();
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof EmailAddress)) return false;
		EmailAddress other = (EmailAddress) obj;
		return address.equals(other.address); // && Utl.equals(label, other.label);
	}

	@Override
	public String toString() {
		if (!isLabelSet()) {
			return address;
		} else {
			return label + " <" + address + ">";
		}
	}

	public static String formatAddress(String email, boolean removeText) {
		if (email == null) return null;
		email = email.trim();
		if (email.endsWith(">")) {
			int idx = email.indexOf("<");
			if (idx < 0) throw new RuntimeException("Illegal EmailAddress: " + email);
			if (removeText) {
				email = email.substring(idx);
				email = formatPlainAddress(email);
			} else {
				if (idx == 0) {
					email = formatPlainAddress(email);
				} else {
					String label = email.substring(0, idx).trim();
					if (label.length() >= 2) {
						if (label.startsWith("\"")) label = label.substring(1);
						if (label.endsWith("\"")) label = label.substring(0, label.length() - 1);
					}
					String address = formatPlainAddress(email.substring(idx));
					email = label + " <" + address + ">";
				}
			}
		} else {
			email = formatPlainAddress(email);
		}
		return email;
	}

	public static final String formatPlainAddress(String email) {
		if (email == null) return null;
		email = email.trim();
		email = email.toLowerCase();
		if (email.startsWith("<")) email = email.substring(1);
		int closerIdx = email.indexOf('>');
		if (closerIdx > 0) email = email.substring(0, closerIdx);
		email = email.trim();
		int idx = email.indexOf(' ');
		if (idx > 0) email = email.substring(0, idx);
		return email;
	}

	public static final void validatePlainAddress(String email) {
		String msg = "Illegal email address: " + email;
		if (email.length() < 5) throw new RuntimeException(msg);
		if (email.contains(" ") || email.contains("\n") || email.contains("\t")) throw new RuntimeException(msg);
		int idx = email.indexOf('@');
		if (idx < 1) throw new RuntimeException(msg);
		if (idx >= email.length() - 3) throw new RuntimeException(msg);
		int idx2 = email.lastIndexOf('.');
		if (idx2 < 3) throw new RuntimeException(msg);
		if (idx2 <= idx) throw new RuntimeException(msg);
		if (idx2 >= email.length() - 1) throw new RuntimeException(msg);
	}

	public static final List<EmailAddress> parseList(String s) {
		if (s == null) return null;
		List<EmailAddress> result = new ArrayList<EmailAddress>();
		StringTokenizer st = new StringTokenizer(s, ";");
		while (st.hasMoreTokens()) {
			result.add(new EmailAddress(st.nextToken()));
		}
		return result;
	}

	public static String getAddress(EmailAddress emailAddress) {
		return emailAddress == null ? null : emailAddress.getAddress();
	}

	public static final List<String> toStringList(Collection<EmailAddress> addresses) {
		List<String> ret = new ArrayList<String>(addresses.size());
		for (EmailAddress address : addresses) {
			ret.add(address.toString());
		}
		return ret;
	}

	public static final String toString(Collection<EmailAddress> addresses) {
		if (addresses == null) return null;
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (EmailAddress ea : addresses) {
			if (ea == null) continue;
			if (first) {
				first = false;
			} else {
				sb.append("; ");
			}
			sb.append(ea);
		}
		return sb.toString();
	}

}
