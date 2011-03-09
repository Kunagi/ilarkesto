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
package ilarkesto.integration.xstream;

import ilarkesto.io.IO;
import ilarkesto.persistence.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XStreamSerializer extends Serializer {

	private XStream xstream;
	private String encoding;

	public XStreamSerializer() {
		this(IO.UTF_8);
	}

	public XStreamSerializer(String encoding) {
		this.encoding = encoding;
		xstream = new XStream(new DomDriver(encoding));
		registerConverter(DateConverter.class);
		registerConverter(TimeConverter.class);
		registerConverter(DateAndTimeConverter.class);
		registerConverter(TimePeriodConverter.class);
		registerConverter(MoneyConverter.class);
		registerConverter(EmailAddressConverter.class);
	}

	private final void registerConverter(Class<? extends Converter> type) {
		Converter converter;
		try {
			converter = type.newInstance();
		} catch (InstantiationException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
		xstream.registerConverter(converter);
		xstream.alias(type.getSimpleName(), type);
	}

	@Override
	public void setAlias(String alias, Class clazz) {
		xstream.alias(alias, clazz);
	}

	@Override
	public void serialize(Object bean, OutputStream out) {
		try {
			Writer writer = new OutputStreamWriter(out, encoding);
			writer.write("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
			xstream.toXML(bean, writer);
			writer.flush();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Object deserialize(InputStream in) {
		try {
			return xstream.fromXML(new InputStreamReader(in, encoding));
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
	}

}
