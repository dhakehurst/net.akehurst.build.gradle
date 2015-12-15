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
import java.util.Set;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import net.akehurst.build.resolver.p2.OsgiP2ResolverException;

public class P2RemoteRepositoryAccess extends AbstractP2RepositoryAccess {

	private final static Logger LOG = Logging.getLogger(P2RemoteRepositoryAccess.class);
	
	
	public P2RemoteRepositoryAccess(DefaultP2Resolver p2Resolver) {
		LOG.trace("P2RemoteRepositoryAccess");
		this.p2Resolver = p2Resolver;
	}
	DefaultP2Resolver p2Resolver;
	
	public URI getRepositoryUri() {
		return this.p2Resolver.getP2RepositoryUri();
	}
	
	public Set<String> getVersions(String artifactId) throws OsgiP2ResolverException {
		LOG.trace("P2RemoteRepositoryAccess.getVersions");
		return this.p2Resolver.getOsgiP2Resolver().fetchVersions(this.getRepositoryUri(), artifactId);
	}
	
	public URI fetchArtifact(String artifactId, String versionRangeString) throws OsgiP2ResolverException {
		LOG.trace("P2RemoteRepositoryAccess.fetchArtifact "+artifactId+":"+versionRangeString);
		return this.p2Resolver.getOsgiP2Resolver().resolve(this.getRepositoryUri(), artifactId, versionRangeString);
	}
	

}
