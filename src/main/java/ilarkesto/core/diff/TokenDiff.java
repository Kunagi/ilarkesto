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

import java.util.List;

public class TokenDiff {

	private List<String> left;
	private List<String> right;
	private DiffMarker marker;
	private DiffTokenizer tokenizer;
	private DiffTokenizer subTokenizer;

	private List<String> lcs;
	private int lcsLen;
	private int leftLen;
	private int rightLen;

	private StringBuilder out = new StringBuilder();
	private String chLcs;
	private String chLeft;
	private String chRight;

	private boolean skipBurn;
	private String removed;

	public static String combinedDiff(String left, String right, DiffMarker marker) {
		return new TokenDiff(left, right, marker, new LineTokenizer(), new WordTokenizer()).diff().toString();
	}

	public TokenDiff(String left, String right, DiffMarker marker, DiffTokenizer tokenizer) {
		this(left, right, marker, tokenizer, null);
	}

	public TokenDiff(String left, String right, DiffMarker marker, DiffTokenizer tokenizer, DiffTokenizer subTokenizer) {
		this.left = tokenizer.tokenize(left);
		this.right = tokenizer.tokenize(right);
		this.marker = marker;
		this.tokenizer = tokenizer;
		this.subTokenizer = subTokenizer;
	}

	public TokenDiff diff() {
		lcs = LongestCommonSubsequenceList.execute(left, right);
		updateLengths();

		while (skipBurn || (lcsLen > 0 && leftLen > 0 && rightLen > 0)) {
			burnNext();
		}
		if (removed != null) {
			out.append(marker.removed(removed));
			removed = null;
		}
		if (leftLen == 0 && rightLen == 0) return this;
		if (leftLen == 0) {
			out.append(marker.added(tokenizer.concat(right)));
			return this;
		}
		if (rightLen == 0) {
			out.append(marker.removed(tokenizer.concat(left)));
			return this;
		}
		if (lcsLen == 0) {
			outReplaced(tokenizer.concat(left), tokenizer.concat(right));
			return this;
		}

		return this;
	}

	private void burnNext() {
		if (!nextChar()) return;
		if (chLcs.equals(chLeft) && chLcs.equals(chRight)) {
			if (removed != null) {
				out.append(marker.removed(removed));
				removed = null;
			}
			burnSame();
			return;
		}
		if (chLcs.equals(chLeft)) {
			burnAdded();
			return;
		}
		burnRemoved();
	}

	private void burnRemoved() {
		StringBuilder sb = new StringBuilder();
		while (!chLcs.equals(chLeft)) {
			sb.append(chLeft);
			if (!nextCharLeft()) break;
		}
		removed = sb.toString();
		skipBurn = true;
	}

	private void burnAdded() {
		StringBuilder sb = new StringBuilder();
		while (chLcs.equals(chLeft) && !chLcs.equals(chRight)) {
			sb.append(chRight);
			if (!nextCharRight()) break;
		}
		String added = sb.toString();
		if (removed != null) {
			outReplaced(removed, added);
			removed = null;
		} else {
			out.append(marker.added(added));
		}
		skipBurn = true;
	}

	private void outReplaced(String removed, String added) {
		if (subTokenizer == null) {
			out.append(marker.replaced(removed, added));
			return;
		}
		TokenDiff diff = new TokenDiff(removed, added, marker, subTokenizer);
		diff.diff();
		out.append(diff.toString());
	}

	private void burnSame() {
		StringBuilder sb = new StringBuilder();
		boolean nextCharAvailable = false;
		while (chLcs.equals(chLeft) && chLcs.equals(chRight)) {
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

		chLcs = lcs.get(0);
		chLeft = left.get(0);
		chRight = right.get(0);

		lcs.remove(0);
		left.remove(0);
		right.remove(0);

		lcsLen--;
		leftLen--;
		rightLen--;

		return true;
	}

	private boolean nextCharRight() {
		if (rightLen == 0) return false;

		chRight = right.get(0);

		right.remove(0);

		rightLen--;

		return true;
	}

	private boolean nextCharLeft() {
		if (leftLen == 0) return false;

		chLeft = left.get(0);

		left.remove(0);

		leftLen--;

		return true;
	}

	private void updateLengths() {
		leftLen = left.size();
		rightLen = right.size();
		lcsLen = lcs.size();
	}

	@Override
	public String toString() {
		return out.toString();
	}

}
