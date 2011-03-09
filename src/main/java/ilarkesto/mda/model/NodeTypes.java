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
package ilarkesto.mda.model;

public interface NodeTypes {

	public static final String Root = "root";
	public static final String GwtModule = "GwtModule";
	public static final String ServiceCall = "ServiceCall";
	public static final String Package = "Package";
	public static final String EntitySet = "EntitySet";
	public static final String Entity = "Entity";
	public static final String Datastruct = "Datastruct";
	public static final String Event = "Event";
	public static final String Type = "Type";
	public static final String Flag = "Flag";
	public static final String TextProperty = "TextProperty";
	public static final String IntegerProperty = "IntegerProperty";
	public static final String FloatProperty = "FloatProperty";
	public static final String BooleanProperty = "BooleanProperty";
	public static final String DateAndTimeProperty = "DateAndTimeProperty";
	public static final String DateProperty = "DateProperty";
	public static final String TimeProperty = "TimeProperty";
	public static final String ReferenceProperty = "ReferenceProperty";

	public static final String Component = "Component";
	public static final String Dependency = "Dependency";
	public static final String Inject = "Inject";
	public static final String Parameter = "Parameter";
	public static final String Dispensable = "Dispensable";
	public static final String InitializationProcedure = "InitializationProcedure";

	public static final String JavaClass = "JavaClass";
	public static final String PackageName = "PackageName";
	public static final String Import = "Import";

	public static final String Index = "Index";

	public static final String TextBundle = "TextBundle";
	public static final String Text = "Text";
	public static final String EN = "EN";
	public static final String DE = "DE";

}
