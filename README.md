P2 Dependency Resolver
======================

This is a gradle plugin to enable resolving dependencies
against a p2 update site.

Usage
-----

    resolvers {
        p2 {
  		    group 'eclipse-mars'
  		    uri 'http://download.eclipse.org/releases/mars/201506241002'
        }
    }


Limitations
-----------

It currently does not support composite repositories.
It does not work with the new plugin mechanism

It is not a particularly clean implementation, I had to use much non-API.

Contributions for improvement are welcome.

Tested on gradle 2.7 and 2.8 (does not work on > 2.8)

Example - Single-project
------------------------

    buildscript {
        repositories {
            maven { url "https://plugins.gradle.org/m2/"}
        }
        dependencies {
          classpath "gradle.plugin.net.akehurst.build.gradle:net.akehurst.build.gradle.resolver.p2:1.0.3"
        }
    }

    apply plugin: 'net.akehurst.build.gradle.resolver.pTwo'
    apply plugin: 'java'

    resolvers {
        p2 {
            group 'eclipse-mars'
            uri 'http://download.eclipse.org/releases/mars/201506241002'
        }
    }

    dependencies {
        compile 'eclipse-mars:org.eclipse.core.runtime:+'
    }
    
Example - Multi-project
------------------------

Top Level

    buildscript {
        repositories {
            maven { url "https://plugins.gradle.org/m2/"}
        }
        dependencies {
          classpath "gradle.plugin.net.akehurst.build.gradle:net.akehurst.build.gradle.resolver.p2:1.0.3"
        }
    }

    subprojects {

        apply plugin: 'net.akehurst.build.gradle.resolver.pTwo'
        apply plugin: 'java'
    
        resolvers {
            p2 {
                group 'eclipse-mars'
                uri 'http://download.eclipse.org/releases/mars/201506241002'
            }
        }

    }
    
Sub Project

    dependencies {
        compile 'eclipse-mars:org.eclipse.core.runtime:+'
    }