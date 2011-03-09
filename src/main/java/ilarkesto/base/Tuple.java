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
package ilarkesto.base;

/**
 * Tuple of two objects.
 */
public final class Tuple<A, B> {

	private A a;
	private B b;

	public Tuple(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public A getA() {
		return a;
	}

	public B getB() {
		return b;
	}

	public void setA(A a) {
		this.a = a;
	}

	public void setB(B b) {
		this.b = b;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Tuple)) return false;
		Tuple<A, B> other = (Tuple<A, B>) obj;
		return Sys.equals(a, other.a) && Sys.equals(b, other.b);
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + (a == null ? 0 : a.hashCode());
		hash = hash * 31 + (b == null ? 0 : b.hashCode());
		return hash;
	}

	@Override
	public String toString() {
		return new StringBuilder().append('<').append(a).append(',').append(b).append('>').toString();
	}

}
