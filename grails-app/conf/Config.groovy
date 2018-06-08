import grails.util.Environment

// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

grails.config.locations = [
	"file:/usr/share/tomcat-notes/app-config/ds_${Environment.current.name}.groovy",
	"file:/usr/share/tomcat-notes_test/app-config/ds_${Environment.current.name}.groovy", 
	"file:/Users/rmcewan/nlppier/ds_${Environment.current.name}.groovy"
]
println "env: ${Environment.current.name} "

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

environments {
    development {
        grails.logging.jul.usebridge = true
		
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
	test {
		grails.serverURL = "https://nlp01.ahc.umn.edu/notes_test"
		grails.assets.minifyJs = false
		grails.assets.minifyCss = false
	}
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
	fvdev {
		//TODO issue ssh -N -f rmcewan1@nlp02.fairview.org -L 9200:nlp02.fairview.org:9200 prior to spinning up this env, then the FV ES cluster is available on localhost:9200
		//TODO fire a script to do this after exchanging keys
		disable.auto.recompile=false
		grails.gsp.enable.reload=true
		grails.logging.jul.usebridge = false
		grails.assets.minifyJs = false
		// TODO: grails.serverURL = "http://www.changeme.com"
		
		//LDAP config used by Spring Security LDAP plugin for LDAP authentication
		grails.plugin.springsecurity.ldap.context.managerDn = 'nlp-pier-svc@fairview.org'
		grails.plugin.springsecurity.ldap.context.managerPassword = '79Rb99@6$qaG73GbXZ5U'
		grails.plugin.springsecurity.ldap.authenticator.useBind = true
		grails.plugin.springsecurity.ldap.context.server = 'ldaps://ldap-ad.fairview.org:636'
		grails.plugin.springsecurity.ldap.search.derefLink=true
		grails.plugin.springsecurity.ldap.authorities.groupSearchBase ='ou=Users'
		grails.plugin.springsecurity.ldap.search.base = 'DC=fairview,DC=org'
		grails.plugin.springsecurity.ldap.search.filter='(sAMAccountName={0})'
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
   	ROLE_ANALYST > ROLE_USER
	ROLE_ADMIN > ROLE_BETA_USER
	ROLE_SUPERADMIN > ROLE_BETA_USER
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





