December 4, 2021

When I tried to use org.mockito.Mockito.mock, I got a runtime
exception (see below).  Using Maven Central [1], I found I was
missing three dependencies:

	net.bytebuddy:byte-buddy:1.12.1 (compile scope)
	net.bytebuddy:byte-buddy-agent:1.12.1 (compile scope)
	org.objenesis:objenesis:3.2 (runtime scope)

[1] https://search.maven.org/artifact/org.mockito/mockito-core/4.1.0/jar

Which got me curious about what a package called Byte Buddy does.

	Byte Buddy creates and modifies Java classes during the
	runtime of a Java application and without the help of a
	compiler.  For example:

		Class<?> dynamicType = new ByteBuddy()
		  .subclass(Object.class)
		  .method(ElementMatchers.named("toString"))
		  .intercept(FixedValue.value("Hello World!"))
		  .make()
		  .load(getClass().getClassLoader())
		  .getLoaded();
		 
		assertThat(dynamicType.newInstance().toString(), is("Hello World!")

	Oracle awarded this project a "Duke's Choice Award" in 2015,
	and the project is downloaded over 75 million times a year.

	Who knew?


While debugging, I came across some references to the mockito-inline
package.  The notes I found were conflicting and my tests worked
fine with the normal includes with Java 17, so I did not use this
package.

	experimental features like mocking final classes and methods,
	mocking static methods etc.
	-- StackOverflow, Feb 2021
	-- https://stackoverflow.com/a/66256832

	mockito-inline is the Java 16 ready version of mockito-core.
	It also support mocking of static methods and final classes.
	-- Vogella, Aug 2021
	-- https://www.vogella.com/tutorials/Mockito/article.html



This was the runtime exception I got running the tests:

Failures (5):
  JUnit Jupiter:SignUpCommandHandlerSignUpTest:testCheckEmailUnique()
    MethodSource [className = 'com.acsredux.auth.services.SignUpCommandHandlerSignUpTest', methodName = 'testCheckEmailUnique', methodParameterTypes = '']
    => java.lang.IllegalStateException: Could not initialize plugin: interface org.mockito.plugins.MockMaker (alternate: null)
       org.mockito.internal.configuration.plugins.PluginLoader$1.invoke(PluginLoader.java:88)
       jdk.proxy3/jdk.proxy3.$Proxy19.isTypeMockable(Unknown Source)
       org.mockito.internal.util.MockUtil.typeMockabilityOf(MockUtil.java:33)
       org.mockito.internal.util.MockCreationValidator.validateType(MockCreationValidator.java:22)
       org.mockito.internal.creation.MockSettingsImpl.validatedSettings(MockSettingsImpl.java:250)
       [...]
     Caused by: java.lang.IllegalStateException: Internal problem occurred, please report it. Mockito is unable to load the default implementation of class that is a part of Mockito distribution. Failed to load interface org.mockito.plugins.MockMaker
       org.mockito.internal.configuration.plugins.DefaultMockitoPlugins.create(DefaultMockitoPlugins.java:85)
       org.mockito.internal.configuration.plugins.DefaultMockitoPlugins.getDefaultPlugin(DefaultMockitoPlugins.java:59)
       org.mockito.internal.configuration.plugins.PluginLoader.loadPlugin(PluginLoader.java:79)
       org.mockito.internal.configuration.plugins.PluginLoader.loadPlugin(PluginLoader.java:54)
       org.mockito.internal.configuration.plugins.PluginRegistry.<init>(PluginRegistry.java:28)
       [...]
     Caused by: java.lang.reflect.InvocationTargetException
       java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
       java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:78)
       java.base/jdk.internal.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
       java.base/java.lang.reflect.Constructor.newInstanceWithCaller(Constructor.java:499)
       java.base/java.lang.reflect.Constructor.newInstance(Constructor.java:480)
       [...]
     Caused by: org.mockito.exceptions.base.MockitoInitializationException: 
It seems like you are running Mockito with an incomplete or inconsistent class path. Byte Buddy could not be loaded.
