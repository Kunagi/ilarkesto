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
package ilarkesto.mda.model.processor;

import ilarkesto.core.logging.Log;
import ilarkesto.mda.generator.GwtComponentBaseGenerator;
import ilarkesto.mda.generator.GwtComponentReflectorGenerator;
import ilarkesto.mda.generator.GwtComponentTemplateGenerator;
import ilarkesto.mda.generator.GwtComponentsReflectorGenerator;
import ilarkesto.mda.generator.GwtEventGenerator;
import ilarkesto.mda.generator.GwtEventHandlerGenerator;
import ilarkesto.mda.generator.GwtServiceAsyncInterfaceGenerator;
import ilarkesto.mda.generator.GwtServiceCallGenerator;
import ilarkesto.mda.generator.GwtServiceImplGenerator;
import ilarkesto.mda.generator.GwtServiceInterfaceGenerator;
import ilarkesto.mda.generator.GwtTextBundleGenerator;
import ilarkesto.mda.model.Model;
import ilarkesto.mda.model.ModelProcessor;
import ilarkesto.mda.model.Node;
import ilarkesto.mda.model.NodeTypes;

public class GwtClassesGenerator implements ModelProcessor, NodeTypes {

	private static Log log = Log.get(GwtClassesGenerator.class);

	private String genSrcPath;
	private String implSrcPath;

	private GwtComponentsReflectorGenerator componentsReflectorGenerator;

	public GwtClassesGenerator() {
		genSrcPath = "src/generated/java";
		implSrcPath = "src/main/java";
	}

	@Override
	public void processModel(Model model) {

		for (Node module : model.getRoot().getChildrenByType(GwtModule)) {
			processModule(module);
		}

	}

	private void processModule(Node module) {
		log.info(module);
		componentsReflectorGenerator = new GwtComponentsReflectorGenerator(genSrcPath, module);
		for (Node package_ : module.getChildrenByType(Package)) {
			processPackage(package_);
		}
		componentsReflectorGenerator.generate();
		new GwtServiceInterfaceGenerator(genSrcPath, module).generate();
		new GwtServiceAsyncInterfaceGenerator(genSrcPath, module).generate();
		new GwtServiceImplGenerator(genSrcPath, module).generate();
		for (Node textBundle : module.getChildrenByType(TextBundle)) {
			new GwtTextBundleGenerator(genSrcPath, textBundle).generate();
		}
	}

	private void processPackage(Node package_) {
		log.info(package_);
		for (Node component : package_.getChildrenByType(Component)) {
			processComponent(component);
		}
		for (Node event : package_.getChildrenByType(Event)) {
			processEvent(event);
		}
		for (Node call : package_.getChildrenByType(ServiceCall)) {
			processServiceCall(call);
		}
	}

	private void processServiceCall(Node call) {
		log.info("ServiceCall:", call);
		new GwtServiceCallGenerator(genSrcPath, call).generate();
	}

	private void processEvent(Node event) {
		log.info("Event:", event);
		new GwtEventHandlerGenerator(genSrcPath, event).generate();
		new GwtEventGenerator(genSrcPath, event).generate();
	}

	private void processComponent(Node component) {
		log.info("Component:", component);
		new GwtComponentBaseGenerator(genSrcPath, component).generate();
		new GwtComponentReflectorGenerator(genSrcPath, component).generate();
		new GwtComponentTemplateGenerator(implSrcPath, component).generate();
		componentsReflectorGenerator.addComponent(component);
	}

}
