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

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.ivy.core.module.descriptor.Configuration;
import org.apache.ivy.core.module.descriptor.DefaultDependencyArtifactDescriptor;
import org.apache.ivy.core.module.descriptor.DefaultDependencyDescriptor;
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor;
import org.apache.ivy.core.module.descriptor.DependencyDescriptor;
import org.apache.ivy.core.module.descriptor.MDArtifact;
import org.apache.ivy.core.module.id.ModuleId;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.eclipse.osgi.util.ManifestElement;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.CachingModuleComponentRepository;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ModuleComponentRepositoryAccess;
import org.gradle.api.internal.component.ArtifactType;
import org.gradle.internal.component.external.model.ModuleComponentArtifactMetaData;
import org.gradle.internal.component.external.model.ModuleComponentResolveMetaData;
import org.gradle.internal.component.model.ComponentArtifactMetaData;
import org.gradle.internal.component.model.ComponentOverrideMetadata;
import org.gradle.internal.component.model.ComponentResolveMetaData;
import org.gradle.internal.component.model.ComponentUsage;
import org.gradle.internal.component.model.ConfigurationMetaData;
import org.gradle.internal.component.model.DependencyMetaData;
import org.gradle.internal.component.model.ModuleSource;
import org.gradle.internal.resolve.ArtifactResolveException;
import org.gradle.internal.resolve.ModuleVersionResolveException;
import org.gradle.internal.resolve.result.BuildableArtifactResolveResult;
import org.gradle.internal.resolve.result.BuildableArtifactSetResolveResult;
import org.gradle.internal.resolve.result.BuildableModuleComponentMetaDataResolveResult;
import org.gradle.internal.resolve.result.BuildableModuleVersionListingResolveResult;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions.Version;

import com.google.common.collect.ImmutableSet;

import net.akehurst.build.resolver.p2.OsgiP2ResolverException;

abstract public class AbstractP2RepositoryAccess implements ModuleComponentRepositoryAccess {

	private final static Logger LOG = LoggerFactory.getLogger(AbstractP2RepositoryAccess.class);

	abstract public URI getRepositoryUri();

	abstract public Set<String> getVersions(String artifactId) throws OsgiP2ResolverException;

	abstract public URI fetchArtifact(String artifactId, String versionRangeString) throws OsgiP2ResolverException;
	
	
	void addDependencies(DefaultModuleDescriptor moduleDescriptor, String group, Manifest manifest) {
		//can't resolve dependencies because the resolution of dependencies doesn't seem to look in the p2 resolver!
		
//		try {
//			List<DependencyDescriptor> list = new ArrayList<>();
//			Attributes atts = manifest.getMainAttributes();
//			String s = atts.getValue("Require-Bundle");
//			ManifestElement[] els = ManifestElement.parseHeader("Require-Bundle", s);
//			if (null != els) {
//				for (ManifestElement me : els) {
//					String name = me.getValue();
//					String bundleVersion = me.getAttribute("bundle-version");
//					if (null == bundleVersion) {
//						bundleVersion = "+";
//					}
//					String scope = "default";
//					String revision = bundleVersion;
//					ModuleRevisionId mrid = ModuleRevisionId.newInstance(group, name, revision);
//					DefaultDependencyDescriptor dependency = new DefaultDependencyDescriptor(moduleDescriptor, mrid, false, false, true);
//					DefaultDependencyArtifactDescriptor depArtifact = new DefaultDependencyArtifactDescriptor(dependency, dependency.getDependencyId().getName(), "jar","jar", null, new HashMap());
//					dependency.addDependencyArtifact(scope, depArtifact);
//					LOG.error(moduleDescriptor.getModuleRevisionId().getName() + " has dependency " + group+ ":"+name + ":" + revision);
//					moduleDescriptor.addDependency(dependency);
//				}
//			}
//
//		} catch (BundleException ex) {
//			LOG.error(ex.getMessage());
//		}
	}

	void addArtifact(DefaultModuleDescriptor moduleDescriptor, String name, String type, String extension, Map<String, String> extraAttributes, String configuration) {
		LOG.trace("addArtifact {} ", name);
		moduleDescriptor.addArtifact(configuration, new MDArtifact(moduleDescriptor, name, type, extension, null, extraAttributes));
	}

	@Override
	public void listModuleVersions(DependencyMetaData dependency, BuildableModuleVersionListingResolveResult result) {
		String artifactId = dependency.getRequested().getName();
		LOG.trace("listModuleVersions {} ", artifactId);
		try {
			Set<String> versions = this.getVersions(artifactId);
			LOG.debug("Versions for " + artifactId + " " + Arrays.toString(versions.toArray()));
			result.listed(versions);
		} catch (OsgiP2ResolverException e) {
			LOG.error("Unable to resolve artifact " + artifactId);
			result.attempted(this.getRepositoryUri().toString());
			result.failed(new ModuleVersionResolveException(dependency.getRequested(), e));
		}
	}

	@Override
	public void resolveArtifact(ComponentArtifactMetaData artifact, ModuleSource moduleSource, BuildableArtifactResolveResult result) {
		LOG.trace("resolveArtifact");

		String artifactId = artifact.getName().getName();
		String versionRangeString = ((ModuleComponentIdentifier) artifact.getComponentId()).getVersion();
		// TODO: convert gradle version ranges into p2 version ranges
		if ("+".equals(versionRangeString)) {
			versionRangeString = "0.0.0";
		}
		try {
			URI fileUri = this.fetchArtifact(artifactId, versionRangeString);
			if (null != fileUri) {
				result.resolved(Paths.get(fileUri).toFile());
			} else {
				LOG.error("Unable to find artifact " + artifactId);
				result.notFound(artifact.getId());
			}
		} catch (OsgiP2ResolverException e) {
			LOG.error("Unable to resolve artifact " + artifactId);
			result.attempted(this.getRepositoryUri().toString());
			result.failed(new ArtifactResolveException(artifact.getId(), e));
		}
	}

	@Override
	public void resolveComponentMetaData(ModuleComponentIdentifier moduleComponentIdentifier, ComponentOverrideMetadata requestMetaData,
			BuildableModuleComponentMetaDataResolveResult result) {
		LOG.trace("resolveComponentMetaData");
		try {
			ModuleId moduleId = new ModuleId(moduleComponentIdentifier.getGroup(), moduleComponentIdentifier.getModule());
			String revision = moduleComponentIdentifier.getVersion();
			ModuleRevisionId id = new ModuleRevisionId(moduleId, revision);
			String status = "release";
			Date pubDate = null;
			String config = "default";
			LOG.trace("configuration {}", config);
			String artifactId = moduleComponentIdentifier.getModule();
			String versionRangeString = moduleComponentIdentifier.getVersion();
			URI fileUri = this.fetchArtifact(artifactId, versionRangeString);

			JarFile bundle = new JarFile(new File(fileUri));

			DefaultModuleDescriptor moduleDescriptor = new DefaultModuleDescriptor(id, status, pubDate);
			moduleDescriptor.addConfiguration(new Configuration(config));
			this.addArtifact(moduleDescriptor, moduleComponentIdentifier.getModule(), "jar", "jar", Collections.<String, String> emptyMap(), config);
			this.addDependencies(moduleDescriptor, moduleComponentIdentifier.getGroup(), bundle.getManifest());

			LOG.trace("Status {}", moduleDescriptor.getStatus());
			P2ModuleResolveMetaData metaData = new P2ModuleResolveMetaData(moduleDescriptor);
			
			metaData.setArtifacts(Arrays.asList( metaData.artifact("jar", "jar", null)) );
			ModuleSource source = new P2ModuleSource();
			metaData.setSource(source);
			
			
			LOG.info("Resolved {} to {}", artifactId, fileUri);
			if (null != fileUri) {
				result.attempted(this.getRepositoryUri().toString());

				result.resolved(metaData);

				if (!result.hasResult()) {
					result.failed(new ModuleVersionResolveException(metaData.getComponentId(),
							String.format("Cannot locate %s for '%s' in repository '%s'", "jar", artifactId, this.getRepositoryUri())));
				}
			}

			LOG.trace("result.getState() {}", result.getState());
			LOG.trace("resolveComponentMetaData - resolved");
		} catch (Throwable e) {
			LOG.error("Cannont resolveComponentMetaData for "+moduleComponentIdentifier.getModule()+":"+moduleComponentIdentifier.getVersion(), e);
			result.attempted(this.getRepositoryUri().toString());
			result.failed(new ModuleVersionResolveException(moduleComponentIdentifier,
							"Cannont resolveComponentMetaData for "+moduleComponentIdentifier.getModule()+":"+moduleComponentIdentifier.getVersion()));
		}

		LOG.trace("resolveComponentMetaData - end");
	}

	@Override
	public void resolveModuleArtifacts(ComponentResolveMetaData component, ArtifactType artifactType, BuildableArtifactSetResolveResult result) {
		LOG.trace("resolveModuleArtifacts");
		ModuleComponentResolveMetaData moduleMetaData = (ModuleComponentResolveMetaData) component;

		if (artifactType == ArtifactType.JAVADOC) {
			resolveJavadocArtifacts(moduleMetaData, result);
		} else if (artifactType == ArtifactType.SOURCES) {
			resolveSourceArtifacts(moduleMetaData, result);
			// } else if (isMetaDataArtifact(artifactType)) {
			// resolveMetaDataArtifacts(moduleMetaData, result);
		} else {
			int i = 0;
		}
		LOG.trace("resolveModuleArtifacts - end");
	}

	@Override
	public void resolveModuleArtifacts(ComponentResolveMetaData component, ComponentUsage componentUsage, BuildableArtifactSetResolveResult result) {
		resolveConfigurationArtifacts((ModuleComponentResolveMetaData) component, componentUsage, result);
		LOG.trace("resolveModuleArtifacts2 - end");
	}

	protected void resolveConfigurationArtifacts(ModuleComponentResolveMetaData module, ComponentUsage usage, BuildableArtifactSetResolveResult result) {
		LOG.trace("resolveConfigurationArtifacts");
        ModuleComponentArtifactMetaData artifact = module.artifact("jar", "jar", null);
        result.resolved(ImmutableSet.of(artifact));
        
	}

	protected void resolveMetaDataArtifacts(ModuleComponentResolveMetaData module, BuildableArtifactSetResolveResult result) {
		LOG.trace("resolveMetaDataArtifacts");
		result.resolved(Collections.emptySet());

	}

	protected void resolveJavadocArtifacts(ModuleComponentResolveMetaData module, BuildableArtifactSetResolveResult result) {
		LOG.trace("resolveJavadocArtifacts");
		result.resolved(Collections.emptySet());

	}

	protected void resolveSourceArtifacts(ModuleComponentResolveMetaData module, BuildableArtifactSetResolveResult result) {
		LOG.trace("resolveSourceArtifacts");
		result.resolved(Collections.emptySet());
		
	}
}
