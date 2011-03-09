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
package ilarkesto.mda.legacy;

import ilarkesto.auth.AUser;
import ilarkesto.base.Str;
import ilarkesto.concurrent.TaskManager;
import ilarkesto.core.logging.Log;
import ilarkesto.di.BeanProvider;
import ilarkesto.di.Context;
import ilarkesto.di.app.AApplication;
import ilarkesto.mda.legacy.generator.ApplicationGenerator;
import ilarkesto.mda.legacy.generator.BeanTemplateGenerator;
import ilarkesto.mda.legacy.generator.ComponentGenerator;
import ilarkesto.mda.legacy.generator.DaoGenerator;
import ilarkesto.mda.legacy.generator.DaoTepmplateGenerator;
import ilarkesto.mda.legacy.generator.DatobGenerator;
import ilarkesto.mda.legacy.generator.EntityGenerator;
import ilarkesto.mda.legacy.model.ApplicationModel;
import ilarkesto.mda.legacy.model.BeanModel;
import ilarkesto.mda.legacy.model.ComponentModel;
import ilarkesto.mda.legacy.model.DatobModel;
import ilarkesto.mda.legacy.model.EntityModel;
import ilarkesto.mda.legacy.model.GwtServiceModel;
import ilarkesto.persistence.AEntity;
import ilarkesto.persistence.AStructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class AGeneratorApplication extends AApplication {

	private static final Log LOG = Log.get(AGeneratorApplication.class);

	protected void onGeneration() {
		BeanProvider beanProvider = Context.get().getBeanProvider();

		for (BeanModel beanModel : beanProvider.getBeansByType(BeanModel.class)) {
			if (beanModel.getPackageName().startsWith("ilarkesto")) continue;
			onBeanGeneration(beanModel);
		}

		// for (UiComponentModel uiComponentModel : beanProvider.getBeansByType(UiComponentModel.class)) {
		// onUiComponentGeneration(uiComponentModel);
		// }
	}

	protected void onBeanGeneration(BeanModel beanModel) {
		if (beanModel instanceof EntityModel) {
			EntityModel em = (EntityModel) beanModel;
			new EntityGenerator(em).generate();
			new DaoGenerator(em).generate();
			new BeanTemplateGenerator(em).generate();
			new DaoTepmplateGenerator(em).generate();
		} else if (beanModel instanceof ApplicationModel) {
			ApplicationModel am = (ApplicationModel) beanModel;
			new ApplicationGenerator(am).generate();
		} else if (beanModel instanceof ComponentModel) {
			ComponentModel cm = (ComponentModel) beanModel;
			if (cm.isGwt()) return;
			new ComponentGenerator(cm).generate();
			new BeanTemplateGenerator(cm).generate();
		} else {
			new DatobGenerator((DatobModel) beanModel).generate();
		}
	}

	@Override
	public final void onStart() {}

	@Override
	protected void scheduleTasks(TaskManager tm) {}

	public final AGeneratorApplication generateClasses() {
		try {
			onGeneration();
		} catch (Throwable ex) {
			LOG.fatal("Generation failed.", ex);
		}
		return this;
	}

	@Override
	protected final void onShutdown() {
		System.exit(0);
	}

	// --------------
	// --- models ---
	// --------------

	protected abstract String getBasePackageName();

	private EntityModel userModel;

	public EntityModel getUserModel() {
		if (userModel == null) {
			userModel = new EntityModel(AUser.class.getSimpleName(), AUser.class.getPackage().getName());
			userModel.setUserModel(userModel);
			Context.get().autowire(userModel);
		}
		return userModel;
	}

	private EntityModel entityModel;

	public EntityModel getEntityModel() {
		if (entityModel == null) {
			entityModel = new EntityModel(AEntity.class.getSimpleName(), AEntity.class.getPackage().getName());
			entityModel.setAbstract(true);
			Context.get().autowire(entityModel);
		}
		return entityModel;
	}

	private EntityModel abstractEntityModel;

	private EntityModel getAbstractEntityModel() {
		if (abstractEntityModel == null) {
			abstractEntityModel = new EntityModel(AEntity.class.getSimpleName(), AEntity.class.getPackage().getName());
			abstractEntityModel.setAbstract(true);
			Context.get().autowire(abstractEntityModel);
		}
		return abstractEntityModel;
	}

	private DatobModel abstractValueObjectModel;

	private DatobModel getAbstractStructureModel() {
		if (abstractValueObjectModel == null) {
			abstractValueObjectModel = new DatobModel(AStructure.class.getSimpleName(), AStructure.class.getPackage()
					.getName());
			Context.get().autowire(abstractValueObjectModel);
		}
		return abstractValueObjectModel;
	}

	public final List<EntityModel> getEntityModels(boolean excludeUser) {
		List<EntityModel> entityModels = new ArrayList<EntityModel>();
		for (Iterator iterator = Context.get().getBeanProvider().getBeansByType(EntityModel.class).iterator(); iterator
				.hasNext();) {
			EntityModel entityModel = (EntityModel) iterator.next();
			if (excludeUser && entityModel == getUserModel()) continue;
			if (entityModel == getEntityModel()) continue;
			entityModels.add(entityModel);
		}
		Collections.sort(entityModels);
		return entityModels;
	}

	public final List<EntityModel> getFinalEntityModels(boolean excludeUser) {
		List<EntityModel> result = new ArrayList<EntityModel>();
		for (EntityModel em : getEntityModels(excludeUser)) {
			if (!em.isAbstract()) result.add(em);
		}
		return result;
	}

	// --------------
	// --- helper ---
	// --------------

	protected GwtServiceModel createGwtServiceModel(String name) {
		return new GwtServiceModel(name, getBasePackageName());
	}

	protected ApplicationModel createSwingApplicationModel(String name) {
		ApplicationModel model = new ApplicationModel(ApplicationModel.Type.SWING, Str.uppercaseFirstLetter(name),
				getBasePackageName());
		return model;
	}

	protected ApplicationModel createWebApplicationModel(String name) {
		ApplicationModel model = new ApplicationModel(ApplicationModel.Type.WEB, Str.uppercaseFirstLetter(name),
				getBasePackageName());
		return model;
	}

	protected ComponentModel createComponentModel(String name, String packageName) {
		ComponentModel model = new ComponentModel(name, getBasePackageName() + "." + packageName);
		return model;
	}

	protected EntityModel createEntityModel(String name, String packageName) {
		EntityModel model = new EntityModel(name, getBasePackageName() + "." + packageName);
		model.setSuperbean(getAbstractEntityModel());
		if (!name.equals("User")) model.setUserModel(getUserModel());
		return model;
	}

	protected DatobModel createStructureModel(String name, String packageName) {
		DatobModel model = new DatobModel(name, getBasePackageName() + "." + packageName);
		model.setSuperbean(getAbstractStructureModel());
		return model;
	}

	// --- dependencies ---

}
