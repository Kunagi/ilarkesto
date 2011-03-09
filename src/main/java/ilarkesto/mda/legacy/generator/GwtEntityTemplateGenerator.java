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
package ilarkesto.mda.legacy.generator;

import ilarkesto.mda.legacy.model.BeanModel;

public class GwtEntityTemplateGenerator extends ABeanGenerator<BeanModel> {

	public GwtEntityTemplateGenerator(BeanModel bean) {
		super(bean);
	}

	@Override
	protected final String getName() {
		return bean.getName();
	}

	@Override
	protected final boolean isInterface() {
		return false;
	}

	@Override
	protected void writeContent() {
		toString();
	}

	@Override
	protected final String getSuperclass() {
		return "G" + bean.getName();
	}

	@Override
	protected final boolean isAbstract() {
		return bean.isAbstract();
	}

	@Override
	protected boolean isOverwrite() {
		return false;
	}

	@Override
	protected String getPackage() {
		return super.getPackage().replaceAll(".server.", ".client.");
	}

}
