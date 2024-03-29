import edu.umn.nlpie.pier.audit.UserAuthenticationEvent
import grails.util.Environment

// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

grails.config.locations = [
	"file:/usr/share/tomcat-notes/app-config/ds_${Environment.current.name}.groovy",
	"file:/usr/share/tomcat-notes_test/app-config/ds_${Environment.current.name}.groovy", 
	"file:/Users/rmcewan/nlppier/ds_${Environment.current.name}.groovy",
	"file:/usr/share/tomcat-notes_test/conf/ds_${Environment.current.name}.groovy"
]
println "Config.groovy ENV: ${Environment.current.name} "

ENV = Environment.current.name.toString()

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.use.accept.header = true
grails.mime.types = [ // the first one is the default format
    all:           '*/*', // 'all' maps to '*' or the first available format in withFormat
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    hal:           ['application/hal+json','application/hal+xml'],
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        // filteringCodecForContentType.'text/html' = 'html'
    }
}


grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

// configure passing transaction's read-only attribute to Hibernate session, queries and criterias
// set "singleSession = false" OSIV mode in hibernate configuration after enabling
grails.hibernate.pass.readonly = false
// configure passing read-only to OSIV session by default, requires "singleSession = false" OSIV mode
grails.hibernate.osiv.readonly = false

username.placeholder = "UofM Internet ID"
password.placeholder = "Internet password"

environments {
    development {
        grails.logging.jul.usebridge = true
		//TODO issue ssh -N -f rmcewan@nlp05.ahc.umn.edu -L 9200:nlp05.ahc.umn.edu:9200
		//LDAP config used by Spring Security LDAP plugin for LDAP authentication
		grails.plugin.springsecurity.ldap.context.managerDn = ''
		grails.plugin.springsecurity.ldap.context.managerPassword = ''
		grails.plugin.springsecurity.ldap.authenticator.useBind = true
		grails.plugin.springsecurity.ldap.context.server = 'ldaps://ldapauth.umn.edu:636'
		grails.plugin.springsecurity.ldap.search.derefLink=true
		grails.plugin.springsecurity.ldap.authorities.groupSearchBase ='ou=People'
		grails.plugin.springsecurity.ldap.search.base = 'o=University of Minnesota,c=US'
		grails.plugin.springsecurity.ldap.search.filter='(uid={0})'
		
		grails.assets.minifyJs = false
		grails.assets.minifyCss = false
		grails.assets.enableSourceMaps = false
		/*grails.assets.configOptions = [babel: [
			enabled: true,
			processJsFiles: true  // add if you want to transpile '*.js'
		]]
		grails.assets.minifyOptions = [
			languageMode: 'ES6',
			targetLanguage: 'ES5', //Can go from ES6 to ES5 for those bleeding edgers
			optimizationLevel: 'SIMPLE',
			angularPass: true // Can use @ngInject annotation for Angular Apps
		]*/
				
    }
	test {
		grails.logging.jul.usebridge = true
		grails.assets.minifyJs = false
		grails.assets.minifyCss = false
		/*enableSourceMaps = true
		configOptions = [babel: [
            enabled: true,
            processJsFiles: true  // add if you want to transpile '*.js'
        ]]
		grails.assets.minifyOptions = [
			languageMode: 'ES6',
			targetLanguage: 'ES5', //Can go from ES6 to ES5 for those bleeding edgers
			optimizationLevel: 'SIMPLE',
			angularPass: true // Can use @ngInject annotation for Angular Apps
		]*/
		
		//LDAP config used by Spring Security LDAP plugin for LDAP authentication
		grails.plugin.springsecurity.ldap.context.managerDn = ''
		grails.plugin.springsecurity.ldap.context.managerPassword = ''
		grails.plugin.springsecurity.ldap.authenticator.useBind = true
		grails.plugin.springsecurity.ldap.context.server = 'ldaps://ldapauth.umn.edu:636'
		grails.plugin.springsecurity.ldap.search.derefLink=true
		grails.plugin.springsecurity.ldap.authorities.groupSearchBase ='ou=People'
		grails.plugin.springsecurity.ldap.search.base = 'o=University of Minnesota,c=US'
		grails.plugin.springsecurity.ldap.search.filter='(uid={0})'
	}
    production {
        grails.logging.jul.usebridge = true
		
		grails.assets.minifyJs = false
		grails.assets.minifyCss = false
		
		//LDAP config used by Spring Security LDAP plugin for LDAP authentication
		grails.plugin.springsecurity.ldap.context.managerDn = ''
		grails.plugin.springsecurity.ldap.context.managerPassword = ''
		grails.plugin.springsecurity.ldap.authenticator.useBind = true
		grails.plugin.springsecurity.ldap.context.server = 'ldaps://ldapauth.umn.edu:636'
		grails.plugin.springsecurity.ldap.search.derefLink=true
		grails.plugin.springsecurity.ldap.authorities.groupSearchBase ='ou=People'
		grails.plugin.springsecurity.ldap.search.base = 'o=University of Minnesota,c=US'
		grails.plugin.springsecurity.ldap.search.filter='(uid={0})'
		
        grails.serverURL = "https://nlppier.ahc.umn.edu"
    }
	fvdev {
		//sudo /usr/lib/jvm/jre/bin/keytool -import -alias fv-ldap -keystore /usr/lib/jvm/jre/lib/security/cacerts -file ~/fv-ldap.cer 
		//TODO issue ssh -N -f rmcewan1@nlp02.fairview.org -L 9200:localhost:9200 prior to spinning up this env, then the FV ES cluster is available on localhost:9200
		//TODO fire a script to do this after exchanging keys
		//https://confluence.atlassian.com/kb/unable-to-connect-to-ssl-services-due-to-pkix-path-building-failed-779355358.html
		
		//And now it just seems to work (for ssh and scp) if I reissue the command after the 'ssh-dss' message.
		//rmcewan$ scp -P 49595 /Users/rmcewan/Documents/workspace-nlp2/pier-next/target/pier-next-2.0.0.war rmcewan1@127.0.0.1:~/
		//on nlp01.fairview.org
		//sudo mv pier-next-2.0.0.war /usr/share/tomcat-notes_test/webapps/notes_test.war
		//sudo service tomcat@notes_test start
		//sudo tail -f test-log 

		
		disable.auto.recompile=false
		grails.gsp.enable.reload=true
		grails.logging.jul.usebridge = false
		grails.assets.minifyJs = false
		
		//LDAP config used by Spring Security LDAP plugin for LDAP authentication
		grails.plugin.springsecurity.ldap.context.managerDn = ''
		grails.plugin.springsecurity.ldap.context.managerPassword = ''
		grails.plugin.springsecurity.ldap.authenticator.useBind = true
		grails.plugin.springsecurity.ldap.context.server = 'ldaps://ldap-ad.fairview.org:636'
		grails.plugin.springsecurity.ldap.search.derefLink=true
		grails.plugin.springsecurity.ldap.authorities.groupSearchBase ='ou=Users'
		grails.plugin.springsecurity.ldap.search.base = 'DC=fairview,DC=org'
		grails.plugin.springsecurity.ldap.search.filter='(sAMAccountName={0})'
		
		username.placeholder = "Fairview User ID"
		password.placeholder = "Password"
	}
	fvtest {
		//sudo /usr/lib/jvm/jre/bin/keytool -import -alias fv-ldap -keystore /usr/lib/jvm/jre/lib/security/cacerts -file ~/fv-ldap.cer 
		//TODO issue ssh -N -f rmcewan1@nlp02.fairview.org -L 9200:nlp02.fairview.org:9200 prior to spinning up this env, then the FV ES cluster is available on localhost:9200
		//TODO fire a script to do this after exchanging keys
		disable.auto.recompile=false
		grails.gsp.enable.reload=true
		grails.logging.jul.usebridge = false
		grails.assets.minifyJs = false
		
		//LDAP config used by Spring Security LDAP plugin for LDAP authentication
		grails.plugin.springsecurity.ldap.context.managerDn = ''
		grails.plugin.springsecurity.ldap.context.managerPassword = ''
		grails.plugin.springsecurity.ldap.authenticator.useBind = true
		grails.plugin.springsecurity.ldap.context.server = 'ldaps://ldap-ad.fairview.org:636'
		grails.plugin.springsecurity.ldap.search.derefLink=true
		grails.plugin.springsecurity.ldap.authorities.groupSearchBase ='ou=Users'
		grails.plugin.springsecurity.ldap.search.base = 'DC=fairview,DC=org'
		grails.plugin.springsecurity.ldap.search.filter='(sAMAccountName={0})'
		
		username.placeholder = "Fairview User ID"
		password.placeholder = "Password"
	}
}

// log4j configuration
log4j.main = {
    // Example of changing the log pattern for the default console appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
           'org.codehaus.groovy.grails.web.pages',          // GSP
           'org.codehaus.groovy.grails.web.sitemesh',       // layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping',        // URL mapping
           'org.codehaus.groovy.grails.commons',            // core / classloading
           'org.codehaus.groovy.grails.plugins',            // plugins
           'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'edu.umn.nlpie.pier.springsecurity.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'edu.umn.nlpie.pier.springsecurity.UserRole'
grails.plugin.springsecurity.authority.className = 'edu.umn.nlpie.pier.springsecurity.Role'
grails.plugin.springsecurity.providerNames = ['ldapAuthProvider','daoAuthenticationProvider','anonymousAuthenticationProvider']
grails.plugin.springsecurity.ldap.search.searchSubtree = true
grails.plugin.springsecurity.ldap.authorities.retrieveGroupRoles = false  	//don't try to get authorities (isMemberOf) from LDAP
grails.plugin.springsecurity.ldap.authorities.retrieveDatabaseRoles = true	//retrieve authorities/roles from DB

grails.plugin.springsecurity.roleHierarchy = '''
   	ROLE_SUPERADMIN > ROLE_ADMIN
   	ROLE_ADMIN > ROLE_ANALYST
	ROLE_ADMIN > ROLE_CARDIOLOGY
	ROLE_ADMIN > ROLE_CANCER
   	ROLE_ANALYST > ROLE_USER
'''

grails.plugin.springsecurity.controllerAnnotations.staticRules = [
'/':						['permitAll'],
'/**':						['permitAll'],
'authz/**':					['permitAll'],
'/aclClass/**': 			['ROLE_SUPERADMIN'],
'/aclSid/**': 				['ROLE_SUPERADMIN'],
'/aclObjectIdentity/**': 	['ROLE_SUPERADMIN'],
'/aclEntry/**': 			['ROLE_SUPERADMIN'],
'/persistentLogin/**': 		['ROLE_SUPERADMIN'],
'/requestmap/**': 			['ROLE_SUPERADMIN'],
'/securityInfo/**': 		['ROLE_SUPERADMIN'],
'/registrationCode/**': 	['ROLE_SUPERADMIN'],
'/role/**': 				['ROLE_SUPERADMIN'],
'/user/**': 				['ROLE_SUPERADMIN'],
'/console/**': 				['ROLE_SUPERADMIN'],
'/register/**': 			['IS_AUTHENTICATED_ANONYMOUSLY']
]
//security logout
grails.plugin.springsecurity.logout.postOnly = false
grails.plugin.springsecurity.logout.afterLogoutUrl = '/'
//grails.plugin.springsecurity.successHandler.defaultTargetUrl = '/search/login'

grails.plugin.springsecurity.useSecurityEventListener = true
grails.plugin.springsecurity.onInteractiveAuthenticationSuccessEvent = { e, appCtx ->
	println e.toString()
	//println e.authentication.authorities.join(" ")
	def auth = e.authentication
	def deets = auth.details
	//println details.toString()
	def remoteAddress = deets.remoteAddress
	def session =  deets.sessionId
	println auth.principal.username
	UserAuthenticationEvent.withNewSession {
		new UserAuthenticationEvent( 
			username: auth.principal.username,
			roles: auth.authorities.join(" "),
			remoteAddress: deets.remoteAddress,
			session: deets.sessionId,
			eventInfo: e.toString()
		).save(failOnError:true)
	}
}






