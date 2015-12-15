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

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ModuleComponentRepositoryAccess;
import org.gradle.api.internal.component.ArtifactType;
import org.gradle.internal.component.external.model.ModuleComponentResolveMetaData;
import org.gradle.internal.component.model.ComponentArtifactMetaData;
import org.gradle.internal.component.model.ComponentOverrideMetadata;
import org.gradle.internal.component.model.ComponentResolveMetaData;
import org.gradle.internal.component.model.ComponentUsage;
import org.gradle.internal.component.model.DependencyMetaData;
import org.gradle.internal.component.model.ModuleSource;
import org.gradle.internal.resolve.result.BuildableArtifactResolveResult;
import org.gradle.internal.resolve.result.BuildableArtifactSetResolveResult;
import org.gradle.internal.resolve.result.BuildableModuleComponentMetaDataResolveResult;
import org.gradle.internal.resolve.result.BuildableModuleVersionListingResolveResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.akehurst.build.resolver.p2.OsgiP2ResolverException;

public class P2LocalRepositoryAccess extends AbstractP2RepositoryAccess
{

	private final static Logger LOG = LoggerFactory.getLogger(P2LocalRepositoryAccess.class);
	
	
	public P2LocalRepositoryAccess(DefaultP2Resolver p2Resolver) {
		LOG.trace("P2LocalRepositoryAccess");
		this.p2Resolver = p2Resolver;
	}
	DefaultP2Resolver p2Resolver;
	
	public URI getRepositoryUri() {
		return this.p2Resolver.getP2RepositoryUri(); //LocalCacheUri();
	}
	
	public Set<String> getVersions(String artifactId) throws OsgiP2ResolverException {
		LOG.trace("P2LocalRepositoryAccess.getVersions");
		return this.p2Resolver.getOsgiP2Resolver().fetchVersions(this.getRepositoryUri(), artifactId);
	}
	
	public URI fetchArtifact(String artifactId, String versionRangeString) throws OsgiP2ResolverException {
		LOG.trace("P2LocalRepositoryAccess.fetchArtifact "+artifactId+":"+versionRangeString);
		return this.p2Resolver.getOsgiP2Resolver().resolve(this.getRepositoryUri(), artifactId, versionRangeString);
	}
	
//	@Override
//	public void listModuleVersions(DependencyMetaData dependency, BuildableModuleVersionListingResolveResult result) {
//		result.attempted("xx");
//	}
	
	@Override
	public void resolveComponentMetaData(ModuleComponentIdentifier moduleComponentIdentifier, ComponentOverrideMetadata requestMetaData,
			BuildableModuleComponentMetaDataResolveResult result) {
	}
	
	@Override
	public void resolveModuleArtifacts(ComponentResolveMetaData component, ArtifactType artifactType, BuildableArtifactSetResolveResult result) {
	}

	@Override
	public void resolveArtifact(ComponentArtifactMetaData arg0, ModuleSource arg1, BuildableArtifactResolveResult arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resolveModuleArtifacts(ComponentResolveMetaData arg0, ComponentUsage arg1, BuildableArtifactSetResolveResult arg2) {
		// TODO Auto-generated method stub
		
	}
}
