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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleId;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.gradle.api.internal.artifacts.ivyservice.NamespaceId;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.internal.component.external.model.IvyModuleResolveMetaData;
import org.gradle.internal.component.external.model.MavenModuleResolveMetaData;
import org.gradle.internal.component.external.model.MutableModuleComponentResolveMetaData;
import org.gradle.internal.component.model.DependencyMetaData;
import org.gradle.internal.component.model.ModuleSource;

//import org.gradle.internal.component.external.model.AbstractModuleComponentResolveMetaData; 
import net.akehurst.build.gradle.p2resolver.gradle.component.model.AbstractModuleComponentResolveMetaData;

//Must implement either IvyModuleResolveMetaData or MavenModuleResolveMetaData
//otherwise gradle rejects the repository type
public class P2ModuleResolveMetaData extends AbstractModuleComponentResolveMetaData implements MutableModuleComponentResolveMetaData, MavenModuleResolveMetaData {

	private final static Logger LOG = Logging.getLogger(P2RemoteRepositoryAccess.class);
	
	public P2ModuleResolveMetaData(ModuleDescriptor moduleDescriptor) {
		super(moduleDescriptor);
		LOG.trace("P2ModuleResolveMetaData");
	}

	@Override
	public AbstractModuleComponentResolveMetaData copy() {
		LOG.trace("copy");
		ModuleId moduleId = new ModuleId(this.getComponentId().getGroup(), this.getComponentId().getModule());
		String revision = this.getComponentId().getVersion();
		ModuleRevisionId id = new ModuleRevisionId(moduleId, revision);
		String status = "release";
		Date pubDate = new Date();
		DefaultModuleDescriptor moduleDescriptor = new DefaultModuleDescriptor(id, status, pubDate);
        P2ModuleResolveMetaData copy = new P2ModuleResolveMetaData(moduleDescriptor);
        copyTo(copy);
        //copy.snapshotTimestamp = snapshotTimestamp;
        return copy;
	}
	
	//---- IvyModuleResolveMetaData ----
//	@Override
//	public String getBranch() {
//		LOG.debug("This is not really an IvyModuleResolveMetaData, it is a "+this.getClass().getSimpleName());
//		return "<no branch>";
//	}

//	@Override
//	public Map<NamespaceId, String> getExtraInfo() {
//		LOG.debug("This is not really an IvyModuleResolveMetaData, it is a "+this.getClass().getSimpleName());
//		return new HashMap<>();
//	}
//
	
	@Override
	public ModuleSource getSource() {
		LOG.trace("P2ModuleResolveMetaData.getSource "+super.getSource());
		return super.getSource();
	}

	@Override
	public String getPackaging() {
		return "jar";
	}

	@Override
	public String getSnapshotTimestamp() {
		return "";
	}

	@Override
	public boolean isKnownJarPackaging() {
		return true;
	}

	@Override
	public boolean isPomPackaging() {
		return false;
	}

	@Override
	public boolean isRelocated() {
		return false;
	}
	
}
