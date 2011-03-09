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

import ilarkesto.core.event.EventBus;
import ilarkesto.core.logging.Log;
import ilarkesto.core.scope.In;
import ilarkesto.mda.model.processor.GwtClassesGenerator;

import java.util.ArrayList;
import java.util.List;

public class ModellingSession {

	private static Log log = Log.get(ModellingSession.class);

	@In
	protected EventBus eventBus;

	private ModelSource source;
	private Model model = new Model();
	private RuleSet ruleSet = new RuleSet();
	private List<ModelProcessor> processors = new ArrayList<ModelProcessor>();

	public ModellingSession() {
		addProcessor(new GwtClassesGenerator());
	}

	public Model getModel() {
		return model;
	}

	public RuleSet getRuleSet() {
		return ruleSet;
	}

	public void addProcessor(ModelProcessor processor) {
		processors.add(processor);
		log.debug("Processor added:", processor);
	}

	public void process() {
		for (ModelProcessor processor : processors) {
			processor.processModel(model);
		}
	}

	public void load(ModelSource source) {
		model = new Model();
		this.source = source;
		source.load(model);
		eventBus.fireEvent(new ModelChangedEvent());
	}

	public void save(ModelSource source) {
		this.source = source;
		save();
	}

	public void save() {
		if (source == null) throw new IllegalStateException("source == null");
		source.save(model);
	}

}
