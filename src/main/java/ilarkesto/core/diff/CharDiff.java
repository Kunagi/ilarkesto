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
package ilarkesto.core.diff;


public class CharDiff {

	private String left;
	private String right;
	private DiffMarker marker;

	private String lcs;
	private int lcsLen;
	private int leftLen;
	private int rightLen;

	private StringBuilder out = new StringBuilder();
	private char chLcs;
	private char chLeft;
	private char chRight;

	private boolean skipBurn;

	public CharDiff(String left, String right, DiffMarker marker) {
		super();
		this.left = left == null ? "" : left;
		this.right = right == null ? "" : right;
		this.marker = marker;
	}

	public CharDiff diff() {
		lcs = LongestCommonSubsequenceString.execute(left, right);
		updateLengths();

		while (skipBurn || (lcsLen > 0 && leftLen > 0 && rightLen > 0)) {
			burnNext();
		}
		if (leftLen == 0 && rightLen == 0) return this;
		if (leftLen == 0) {
			out.append(marker.added(right));
			return this;
		}
		if (rightLen == 0) {
			out.append(marker.removed(left));
			return this;
		}
		if (lcsLen == 0) {
			out.append(marker.removed(left));
			out.append(marker.added(right));
			return this;
		}

		return this;
	}

	private void burnNext() {
		if (!nextChar()) return;
		if (chLcs == chLeft && chLcs == chRight) {
			burnSame();
			return;
		}
		if (chLcs == chLeft) {
			burnAdded();
			return;
		}
		burnRemoved();
	}

	private void burnRemoved() {
		StringBuilder sb = new StringBuilder();
		while (chLcs != chLeft) {
			sb.append(chLeft);
			if (!nextCharLeft()) break;
		}
		out.append(marker.removed(sb.toString()));
		skipBurn = true;
	}

	private void burnAdded() {
		StringBuilder sb = new StringBuilder();
		while (chLcs == chLeft && chLcs != chRight) {
			sb.append(chRight);
			if (!nextCharRight()) break;
		}
		out.append(marker.added(sb.toString()));
		skipBurn = true;
	}

	private void burnSame() {
		StringBuilder sb = new StringBuilder();
		boolean nextCharAvailable = false;
		while (chLcs == chLeft && chLcs == chRight) {
			sb.append(chLcs);
			nextCharAvailable = nextChar();
			if (!nextCharAvailable) break;
		}
		out.append(marker.same(sb.toString()));
		if (nextCharAvailable) skipBurn = true;
	}

	private boolean nextChar() {
		if (skipBurn) {
			skipBurn = false;
			return true;
		}

		if (lcsLen == 0) return false;
		if (leftLen == 0) return false;
		if (rightLen == 0) return false;

		chLcs = lcs.charAt(0);
		chLeft = left.charAt(0);
		chRight = right.charAt(0);

		lcs = lcs.substring(1);
		left = left.substring(1);
		right = right.substring(1);

		lcsLen--;
		leftLen--;
		rightLen--;

		return true;
	}

	private boolean nextCharRight() {
		if (rightLen == 0) return false;

		chRight = right.charAt(0);

		right = right.substring(1);

		rightLen--;

		return true;
	}

	private boolean nextCharLeft() {
		if (leftLen == 0) return false;

		chLeft = left.charAt(0);

		left = left.substring(1);

		leftLen--;

		return true;
	}

	private void updateLengths() {
		leftLen = left.length();
		rightLen = right.length();
		lcsLen = lcs.length();
	}

	@Override
	public String toString() {
		return out.toString();
	}

}
