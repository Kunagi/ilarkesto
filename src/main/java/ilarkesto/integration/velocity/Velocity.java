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
package ilarkesto.integration.velocity;

import ilarkesto.base.Str;
import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;

import java.io.File;
import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class Velocity {

	public static final String LIB_TEMPLATE_NAME = "VM_global_library.vm";

	private static Log log = Log.get(Velocity.class);

	private File templateDir;
	private VelocityEngine velocityEngine;

	public Velocity(File templateDir) {
		this.templateDir = templateDir;
		velocityEngine = createEngine(templateDir);
	}

	public static void processDir(File templateDir, File outputDir, ContextBuilder context) {
		processDir(templateDir, outputDir, context.toVelocityContext());
	}

	public static void processDir(File templateDir, File outputDir, VelocityContext velocityContext) {
		Velocity velocity = new Velocity(templateDir);

		File[] files = templateDir.listFiles();
		if (files == null) return;
		IO.createDirectory(outputDir);
		for (File templateFile : files) {
			String name = templateFile.getName();
			if (name.equals(LIB_TEMPLATE_NAME)) continue;
			log.debug("   ", name);
			boolean velocityTemplate = name.endsWith(".vm");
			if (velocityTemplate) {
				File outputFile = new File(outputDir.getAbsolutePath() + "/" + Str.removeSuffix(name, ".vm"));
				velocity.processTemplate(name, outputFile, velocityContext);
			} else {
				IO.copyFile(templateFile, new File(outputDir.getPath() + "/" + name));
			}
		}
	}

	public void processTemplate(String name, File outputFile, ContextBuilder context) {
		processTemplate(name, outputFile, context.toVelocityContext());
	}

	public void processTemplate(String name, File outputFile, VelocityContext velocityContext) {
		log.debug("Processing", templateDir.getAbsolutePath() + "/" + name, "->", outputFile.getAbsolutePath());
		IO.createDirectory(outputFile.getParentFile());
		StringWriter out = new StringWriter();
		try {
			Template template = velocityEngine.getTemplate(name);
			template.merge(velocityContext, out);
			out.close();
		} catch (Exception ex) {
			throw new RuntimeException("Processing velocity template failed: " + name, ex);
		}
		IO.writeFileIfChanged(outputFile, out.toString(), IO.UTF_8);
	}

	public static VelocityContext createContext(Map<String, ?> context) {
		VelocityContext velocityContext = new VelocityContext();
		for (Map.Entry<String, ?> entry : context.entrySet()) {
			velocityContext.put(entry.getKey(), entry.getValue());
		}
		return velocityContext;
	}

	private static VelocityEngine createEngine(File templateDir) {
		VelocityEngine velocityEngine = new VelocityEngine();
		String encoding = IO.UTF_8;
		velocityEngine.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, VelocityLogging.class.getName());
		velocityEngine.setProperty(VelocityEngine.ENCODING_DEFAULT, encoding);
		velocityEngine.setProperty(VelocityEngine.INPUT_ENCODING, encoding);
		velocityEngine.setProperty(VelocityEngine.OUTPUT_ENCODING, encoding);
		velocityEngine.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, templateDir.getAbsolutePath());
		try {
			velocityEngine.init();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return velocityEngine;
	}
}
