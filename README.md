thundr-contrib-freemarker [![Build Status](https://travis-ci.org/atomicleopard/thundr-contrib-freemarker.svg)](https://travis-ci.org/atomicleopard/thundr-contrib-freemarker)
=================

A thundr module for rendering views using [FreeMarker](http://freemarker.org/).

You can read more about thundr [here](http://3wks.github.io/thundr/)

Include the thundr-contrib-freemarker dependency using maven or your favourite dependency management tool.
    
    <dependency>
  		<groupId>com.atomicleopard.thundr</groupId>
		<artifactId>thundr-contrib-freemarker</artifactId>
		<version>2.0.0-alpha</version>
		<scope>compile</scope>
	</dependency>
    
Add a dependency on the velocity module in your ApplicationModule file:

	@Override
	public void requires(DependencyRegistry dependencyRegistry) {
		super.requires(dependencyRegistry);
		...
		dependencyRegistry.addDependency(FreemarkerModule.class);
	}

You can return FreemarkerView from controller methods which will render the supplied velocity view with the given model
    
    public FreemarkerView get(){
        Map<String, Object> model = new HashMap<String, Object>();
        return new FreemarkerView("/ftl/view.ftl", model);
    }
    
Views can be located as a classpath resource, or inside /WEB-INF/ in your war file.
    
--------------    
thundr-contrib-freemarker - Copyright (C) 2016 Atomic Leopard Pty Ltd    