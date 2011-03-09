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
package ilarkesto.mda.legacy.model;

public class IntegerPropertyModel extends SimplePropertyModel {

	private int min = 0;
	private int max = Integer.MAX_VALUE;

	public IntegerPropertyModel(BeanModel entityModel, String name) {
		super(entityModel, name, false, false, Integer.class.getName());
	}

	public IntegerPropertyModel setMin(int min) {
		this.min = min;
		return this;
	}

	public IntegerPropertyModel setMax(int max) {
		this.max = max;
		return this;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

}
