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

import groovy.lang.Closure;

public interface ResolverHandler {
	
    P2ArtifactRepository p2(Closure closure);

    /**
     * Adds and configures a p2 repository.
     *
     * @param action The action to use to configure the repository.
     * @return The added repository.
     */
    P2ArtifactRepository p2(Action<? super P2ArtifactRepository> action);
}
