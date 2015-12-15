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
package net.akehurst.build.gradle.equinox;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.*;
import org.slf4j.Logger;

import org.gradle.api.logging.*;

public class Converter {

	public static void osgify(File jarFile, String symbolicName, String outputDir) throws Exception {
		Logger log = Logging.getLogger(Converter.class);
		
		try {
			JarFile jar = new JarFile(jarFile);
			Manifest m = jar.getManifest();
			if (null==m) {
				m = new Manifest();
			}
			
			Path destinationPath = Paths.get(outputDir).resolve(jarFile.getName());
			if (destinationPath.toFile().exists()) {
				destinationPath.toFile().delete();
			}
			Attributes att = m.getMainAttributes();
			if (att.containsKey(new Attributes.Name("Bundle-SymbolicName"))) {
				//already an osgi bundle
				//just copy it to desination
				log.error("Copying osgi jar "+destinationPath.getFileName());
				Files.copy(jarFile.toPath(), destinationPath);
			} else {
				log.error("Converting jar to osgi "+destinationPath.getFileName());
				//create osgi manifest entries
				att.putValue("Manifest-Version", "1.0");
				att.putValue("Bundle-SymbolicName", symbolicName);
				
				String exportPackages = "";
				Enumeration<JarEntry> entries = jar.entries();
				while(entries.hasMoreElements()){
					JarEntry je = entries.nextElement();
					if (je.getName().endsWith(".class")) {
						exportPackages += fetchPackageName(je) + ",";
					}
				}
				if (exportPackages.endsWith(",")) {
					exportPackages = exportPackages.substring(0, exportPackages.length()-1);
				}
				att.putValue("Export-Package", exportPackages);

				//create new jar
				
				FileOutputStream out = new FileOutputStream(destinationPath.toFile());
				JarOutputStream jarOut = new JarOutputStream(out, m);
				entries = jar.entries();
				while(entries.hasMoreElements()){
					JarEntry je = entries.nextElement();
					if (je.getName().startsWith("META-INF/")) { //exclude existing Manifest file and any other META-INF, e.g. signature files
						//don't copy
					} else {
						InputStream ins = jar.getInputStream(je);
						jarOut.putNextEntry(je);
						copy(ins,jarOut);
					}
				}
				
				
				jarOut.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
	}
	
	static String fetchPackageName(JarEntry je) {
		String n = je.getName();
		int i = n.lastIndexOf('/');
		n = n.substring(0, i);
		return n;
	}
	
	static void copy(InputStream ins, OutputStream outs) {
	    try{
            byte[] bytes = new byte[10*1024];
            int length = ins.read(bytes);
            while (length!=-1) {
            	outs.write(bytes, 0, length);
            	outs.flush();
            	length = ins.read(bytes);
            }
        } catch (FileNotFoundException ex){
            ex.printStackTrace();
        } catch (IOException ex){
            ex.printStackTrace();
        }
	}
	
}
