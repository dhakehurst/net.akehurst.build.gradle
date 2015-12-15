package net.akehurst.build.gradle.resolver.p2.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.BundleException;

public class ManifestReader {

	public ManifestReader(File file) throws FileNotFoundException, IOException {
		this.manifest = new Manifest(new FileInputStream(file));
	}
	
	public ManifestReader(Manifest manifest) {
		this.manifest = manifest;
	}
	
	Manifest manifest;
	
	public Set<DependencyInfo> getDependencies() throws BundleException {

		Set<DependencyInfo> result = new HashSet<>();
		
		Attributes atts = this.manifest.getMainAttributes();
		String s = atts.getValue("Require-Bundle");
		ManifestElement[] els = ManifestElement.parseHeader("Require-Bundle", s);
		if (null != els) {
			for (ManifestElement me : els) {
				String bundleName = me.getValue();
				String bundleVersion = me.getAttribute("bundle-version");
				if (null == bundleVersion) {
					bundleVersion = "+";
				} else {
					//use version as read from file;
				}
				result.add(  new DependencyInfo(bundleName, bundleVersion) );
			}
		} else {
			//no required bundles listed
		}
		
		return result;
	}
	
	public Set<DependencyInfo> getTransitiveDependencies() throws BundleException {
		//TODO: handle transitive dependencies and reexported dependencies
		return this.getDependencies();
	}
	
	public static class DependencyInfo {
		public DependencyInfo(String bundleName, String version) {
			this.bundleName = bundleName;
			this.version = version;
		}
		String bundleName;
		public String getBundleName() {
			return this.bundleName;
		}
		
		String version;
		public String getVersion() {
			return this.version;
		}
		@Override
		public int hashCode() {
			// TODO Auto-generated method stub
			return super.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			return super.equals(obj);
		}
		
		@Override
		public String toString() {
			return ":"+this.getBundleName()+":"+this.getVersion();
		}
	}
}
