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
package net.akehurst.build.gradle.equinox

import java.util.*

class EquinoxConfiguration {

	EquinoxConfiguration() {
		this.startInfo = new HashMap<>()
		this.properties = new HashMap<>()
	}
	
	String layout; //'gradle' or 'eclipse'

	Map<String, String> startInfo

	void layout(String value) {
		this.layout = value;
	}
	
	void start(String bundleName) {
		this.startInfo.put(bundleName,"")	
	}

	void start(String bundleName, String level) {
		this.startInfo.put(bundleName,level)	
	}

	Map<String,String> properties
	void properties(Map<String,String> properties) {
		this.properties = properties
	}
	void property(String name, String value) {
		this.properties.put(name,value)
	}
	
	OsgiInfo osgi;
	OsgiInfo getOsgi() {
		if (null==this.osgi) {
			this.osgi = new OsgiInfo();
		}
		return this.osgi;
	}
}