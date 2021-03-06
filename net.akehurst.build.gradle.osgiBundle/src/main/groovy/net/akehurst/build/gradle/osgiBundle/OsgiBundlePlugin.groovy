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
package net.akehurst.build.gradle.osgiBundle

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.File
import java.nio.file.Files;
import java.util.jar.JarFile;

import org.gradle.api.*
import org.gradle.api.plugins.*
import org.gradle.api.tasks.*

import net.akehurst.build.gradle.resolver.p2.utils.ManifestReader;

class OsgiBundlePlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {
	project.apply plugin: 'osgi'
	project.apply plugin: 'java'
	

	project.jar {
		manifest {
			from 'META-INF/MANIFEST.MF'
		}
	}
	
	
	def t = project.tasks.create(name:'addManifestDependencies') {
		ManifestReader mr = new ManifestReader(project.file('META-INF/MANIFEST.MF'));
		for(ManifestReader.DependencyInfo di: mr.getTransitiveDependencies()) {
			String dep = ":"+di.getBundleName()+":"+di.getVersion();
			def prj = project.rootProject.findProject(di.getBundleName());
			if (null==prj) {
				project.dependencies {
					delegate.'compile'(dep)
				}
			} else {
				project.dependencies {
					delegate.'compile'(prj)
				}
			}
		}
	}
	
	project.tasks.build.dependsOn(t)

  }
 
}
