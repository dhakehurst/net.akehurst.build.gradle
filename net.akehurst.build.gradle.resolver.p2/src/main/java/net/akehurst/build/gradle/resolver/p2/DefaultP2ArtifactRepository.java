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
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ConfiguredModuleComponentRepository;
import org.gradle.api.internal.artifacts.repositories.AbstractArtifactRepository;
import org.gradle.api.internal.artifacts.repositories.ResolutionAwareRepository;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;


public class DefaultP2ArtifactRepository extends AbstractArtifactRepository implements ArtifactRepository, ResolutionAwareRepository, P2ArtifactRepository {
	private final static Logger LOG = Logging.getLogger(DefaultP2ArtifactRepository.class);
	
	public DefaultP2ArtifactRepository() {
		LOG.trace(this.getClass().getSimpleName());
	}
	
	String group;
	public String getGroup() {
		return this.group;
	}
	public void setGroup(String value) {
		this.group = value;
	}
	public void group(String value) {
		this.setGroup(value);
	}
	
	URI uri;
	public URI getUri() {
		LOG.trace("getUri = {}",this.uri);
		return this.uri;
	}
	public void uri(String value) {
		this.setUri(value);
	}
	public void setUri(String value) {
		LOG.trace("setUri({})",value);
		try {
			this.uri = new URI(value);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Cannot create URI for "+value,e);
		}	
	}
	
	URI localCacheUri;
	public URI getLocalCacheUri() {
		LOG.trace("getUri = {}",this.uri);
		if (null==localCacheUri) {
			//use default value
			String userHome = System.getProperty("user.home");
			String defaultValue = userHome+"/.gradle-p2/";
			this.localCacheUri = Paths.get(defaultValue).toUri();
		}
		return this.localCacheUri;
	}
	public void localCacheUri(String value) {
		this.setLocalCacheUri(value);
	}
	public void setLocalCacheUri(String value) {
		LOG.trace("setUri({})",value);
		try {
			this.uri = new URI(value);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Cannot create URI for "+value,e);
		}	
	}
	
	@Override
	public String getName() {
		LOG.trace("getName");
		return "p2("+this.group+")";
	}
	
	public ConfiguredModuleComponentRepository createResolver() {
		LOG.trace("createResolver");
		URI p2Uri = this.getUri();
		URI lcUri = this.getLocalCacheUri();
		LOG.trace(p2Uri.toString());
		LOG.trace(lcUri.toString());
		return new DefaultP2Resolver(group, p2Uri, lcUri);
	}

}
