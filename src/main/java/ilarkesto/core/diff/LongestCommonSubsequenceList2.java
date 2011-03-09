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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LongestCommonSubsequenceList2 {

	public static List<String> executeRecursive(List<String> a, List<String> b) {
		int aLen = a.size();
		int bLen = b.size();
		if (aLen == 0 || bLen == 0) {
			return new ArrayList<String>();
		} else if (a.get(aLen - 1).equals(b.get(bLen - 1))) {
			LinkedList<String> newList = new LinkedList<String>(b.subList(0, bLen - 1));
			newList.add(a.get(aLen - 1));
			return executeRecursive(a.subList(0, aLen - 1), newList);
		} else {
			List<String> x = executeRecursive(a, b.subList(0, bLen - 1));
			List<String> y = executeRecursive(a.subList(0, aLen - 1), b);
			return (x.size() > y.size()) ? x : y;
		}
	}

	public static List<String> executeProc(List<String> x, List<String> y) {
		int M = x.size();
		int N = y.size();

		// opt[i][j] = length of LCS of x[i..M] and y[j..N]
		int[][] opt = new int[M + 1][N + 1];

		// compute length of LCS and all subproblems via dynamic programming
		for (int i = M - 1; i >= 0; i--) {
			for (int j = N - 1; j >= 0; j--) {
				if (x.get(i).equals(y.get(j)))
					opt[i][j] = opt[i + 1][j + 1] + 1;
				else opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
			}
		}

		List<String> ret = new ArrayList<String>();
		// recover LCS itself and print it to standard output
		int i = 0, j = 0;
		while (i < M && j < N) {
			if (x.get(i).equals(y.get(j))) {
				ret.add(x.get(i));
				i++;
				j++;
			} else if (opt[i + 1][j] >= opt[i][j + 1])
				i++;
			else j++;
		}
		return ret;
	}
}
