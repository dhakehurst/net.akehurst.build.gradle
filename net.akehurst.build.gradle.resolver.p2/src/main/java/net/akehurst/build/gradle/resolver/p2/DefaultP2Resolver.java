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
import java.nio.file.Path;
import java.nio.file.Paths;

import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ConfiguredModuleComponentRepository;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ModuleComponentRepositoryAccess;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import net.akehurst.build.resolver.p2.osgi.SimpleOsgi;
import net.akehurst.build.resolver.p2.SimpleOsgiP2Resolver;

public class DefaultP2Resolver implements ConfiguredModuleComponentRepository {

	private final static Logger LOG = Logging.getLogger(DefaultP2Resolver.class);

	public DefaultP2Resolver(String groupString, URI p2RepositoryUri, URI localCacheUri) {
		LOG.trace("DefaultP2Resolver");
		this.groupString = groupString;
		this.p2RepositoryUri = p2RepositoryUri;
		this.localCacheUri = localCacheUri;
	}

	String groupString;
	URI p2RepositoryUri;
	public URI getP2RepositoryUri() {
		return this.p2RepositoryUri;
	}
	
	URI localCacheUri;
	public URI getLocalCacheUri() {
		return this.localCacheUri;
	}
	
	static SimpleOsgi osgi;
	static SimpleOsgi getOsgi(Path dataArea) {
		if (null==osgi) {
			
			osgi = SimpleOsgiP2Resolver.createRequiredOsgi(dataArea);
		}
		return osgi;
	}
	
	SimpleOsgiP2Resolver osgiP2Resolver;
	public SimpleOsgiP2Resolver getOsgiP2Resolver() {
		if (null==this.osgiP2Resolver) {
			LOG.trace("Create OsgiP2Resolver "+this.localCacheUri);
			this.osgiP2Resolver = new SimpleOsgiP2Resolver(getOsgi(Paths.get(this.getLocalCacheUri())), this.localCacheUri); //Paths.get("build/osgi"));
		}
		return this.osgiP2Resolver;
	}

	public String getId() {
		LOG.trace("getId");
		return "p2-" + this.groupString;
	}

	public ModuleComponentRepositoryAccess getLocalAccess() {
		LOG.trace("DefaultP2Resolver.getLocalAccess");
		return new P2LocalRepositoryAccess(this);
	}

	public String getName() {
		LOG.trace("getName");
		return "p2("+this.getP2RepositoryUri()+")";
	}

	public ModuleComponentRepositoryAccess getRemoteAccess() {
		LOG.trace("DefaultP2Resolver.getRemoteAccess");
		return new P2RemoteRepositoryAccess(this);
	}

	public boolean isDynamicResolveMode() {
		LOG.trace("isDynamicResolveMode");
		return false;
	}

	public boolean isLocal() {
		LOG.trace("isLocal");
		return false;
	}

}