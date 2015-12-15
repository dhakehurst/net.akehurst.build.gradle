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

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.internal.ClosureBackedAction;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import groovy.lang.Closure;

public class DefaultResolverHandler implements ResolverHandler {
	private final static Logger LOG = Logging.getLogger(DefaultResolverHandler.class);
	
	public DefaultResolverHandler(final Project project) {
		LOG.trace(this.getClass().getSimpleName());
		this.project = project;
	}

	Project project;

	@Override
	public P2ArtifactRepository p2(Closure closure) {
		LOG.trace("p2(Closure)");
		return p2(new ClosureBackedAction<P2ArtifactRepository>(closure));
	}

	@Override
	public P2ArtifactRepository p2(Action<? super P2ArtifactRepository> configureAction) {
		LOG.trace("p2(Action)");
		P2ArtifactRepository p2Repo = new DefaultP2ArtifactRepository();
		configureAction.execute(p2Repo);
		if (null==p2Repo.getUri()) {
			LOG.error("Cannot add a P2 repo with no 'uri' value");
		} else {
			project.getRepositories().add( p2Repo );
		}
		return p2Repo;
	}
}
