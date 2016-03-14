P2 Dependency Resolver
======================

This is a gradle plugin to enable resolving dependencies
against a p2 update site.

Usage
-----

    plugin {
        id 'net.akehurst.build.gradle.resolver.pTwo'
    }

    resolvers {
        p2 {
  		    group 'eclipse-mars'
  		    uri 'http://download.eclipse.org/releases/mars/201506241002'
        }
    }


Limitations
-----------

It currently does not support composite repositories.

It is not a particularly clean implementation, I had to use much non-API.
Contributions for improvement are welcome.

Tested on gradle 2.7 and 2.8