/**
 * Copyright (C) 2015 Dr. David H. Akehurst (http://dr.david.h.akehurst.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.akehurst.build.gradle.resolver.p2;

import java.net.URL;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.internal.classloader.MutableURLClassLoader;

class P2ResolverPlugin implements Plugin<Project> {
	 private final static Logger LOG = Logging.getLogger(P2ResolverPlugin.class);

	public void apply(Project project) {
		LOG.trace("apply");

		//The plugin class loader is an isolated descendant of the classloader that
		// does the dependency resolution. So we need to mess with the classloaders
		// in order to get the P2 resolver classes to load appropriately
		
		//get the current class loader
		Thread thread = Thread.currentThread();
		ClassLoader contextClassLoader = thread.getContextClassLoader();
		//access ancestor class loader that can validly access the classes needed for repository resolving
		// i.e 'org.apache.ivy.core.module.id.ModuleRevisionId'
		ClassLoader toUse = contextClassLoader.getParent().getParent().getParent();
		while(!(toUse instanceof MutableURLClassLoader)) {
			toUse = toUse.getParent();
		}
		MutableURLClassLoader cl = (MutableURLClassLoader)toUse;
		try {

			java.net.URLClassLoader plCl = (java.net.URLClassLoader)contextClassLoader;
			for(URL location: plCl.getURLs()) {
				cl.addURL(location );
				LOG.trace("Adding URL to class loader: "+location);				
			}
			
//			//add url of this plugin to the jars searchable by the 'toUse' classLoader
//			URL location = this.getClass().getProtectionDomain().getCodeSource().getLocation();
//			cl.addURL(location );
//			LOG.error("Adding URL to class loader: "+location);

			Class<?> cls = toUse.loadClass("net.akehurst.build.gradle.resolver.p2.DefaultResolverHandler");
		    project.getExtensions().create("resolvers", cls, project);
		    
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
		
	}

}