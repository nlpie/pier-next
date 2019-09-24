import grails.util.Environment

dataSource {	
	pooled = true
    jmxExport = true
    //logSql=true
    //formatSql=true
	properties {
		// See http://grails.org/doc/latest/guide/conf.html#dataSource for documentation
		//dataSource in each env will "inherit" these properties
		jmxEnabled = true
		initialSize = 5
		maxActive = 50
		minIdle = 5
		maxIdle = 25
		maxWait = 10000
		maxAge = 100 * 60000
		timeBetweenEvictionRunsMillis = 5000
		minEvictableIdleTimeMillis = 60000
		validationQuery = "SELECT 1"
		validationQueryTimeout = 3
		validationInterval = 15000
		testOnBorrow = true
		testWhileIdle = true
		testOnReturn = false
		jdbcInterceptors = "ConnectionState"
		defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
	 }
}

if ( Environment.current.name!="fvdev" || Environment.current.name!="fvtest" ) {
	dataSource_notes {
		readOnly=true
		dbCreate = "none"
		//logSql=true
		//formatSql=true
		pooled = true
		driverClassName = "oracle.jdbc.OracleDriver"
		username = ""	//see config in ds_<env>.groovy
		password = ""	//see config in ds_<env>.groovy
		//dialect = org.hibernate.dialect.Oracle11gDialect
		dialect = org.hibernate.dialect.Oracle10gDialect	//no support for Oracle12c dialect in this version of Hibernate v4.x
		url = "jdbc:oracle:thin:@//tideprdp.ahc.umn.edu:1521/tideprdp.ahc.umn.edu"
		properties {
			maxActive = 10
			maxIdle = 10
			minIdle = 5
			initialSize = 5
			testOnBorrow=true
			testWhileIdle=true
			testOnReturn=true
			validationQuery="select 1 from dual"
		}
	}
}

hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
//    cache.region.factory_class = 'org.hibernate.cache.SingletonEhCacheRegionFactory' // Hibernate 3
    cache.region.factory_class = 'org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory' // Hibernate 4
    singleSession = true // configure OSIV singleSession mode
    flush.mode = 'manual' // OSIV session flush mode outside of transactional context
}

// environment specific settings
environments {
	development {
		dataSource {
			dbCreate = "update"
			driverClassName = "com.mysql.jdbc.Driver"
			dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
			username = ""	//see config in ds_<env>.groovy
			password = ""	//see config in ds_<env>.groovy
			url = "jdbc:mysql://127.0.0.1:3306/notes_next"
			properties {
				validationQuery="SELECT 1"
			}
		}
	}
	test {
		dataSource {
			dbCreate = "update"
			driverClassName = "com.mysql.jdbc.Driver"
			dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
			username = ""	//see config in ds.groovy
			password = ""	//see config in ds.groovy
			url = "jdbc:mysql://nlpql.ahc.umn.edu:3306/pier_next"
			properties {
				validationQuery="SELECT 1"
			}
		}
	}
	production {
		dataSource {
			dbCreate = "update"
			driverClassName = "com.mysql.jdbc.Driver"
			dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
			username = ""	//see config in ds.groovy
			password = ""	//see config in ds.groovy
			url = "jdbc:mysql://nlpql.ahc.umn.edu:3306/pier_next"
			properties {
				validationQuery="SELECT 1"
			}
		}
	}
	fvdev {
		dataSource {
			dbCreate = "create-drop"
			driverClassName = "com.mysql.jdbc.Driver"
			dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
			username = ""	//see config in ds_<env>.groovy
			password = ""	//see config in ds_<env>.groovy
			url = "jdbc:mysql://127.0.0.1:3306/fvdev"
			properties {
				validationQuery="SELECT 1"
			}
			//logSql=true
			//formatSql=true
		}
		dataSource_notes {
			readOnly = false
			dbCreate = "create-drop"
			//logSql=true
			//formatSql=true
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			username = ""	//see config in ds_<env>.groovy
			password = ""	//see config in ds_<env>.groovy
			//dialect = org.hibernate.dialect.Oracle11gDialect
			dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
			username = ""	//see config in ds_<env>.groovy
			password = ""	//see config in ds_<env>.groovy
			url = "jdbc:mysql://127.0.0.1:3306"	//no schema, it's specified in domain classes that use this dataSource
			properties {
				defaultCatalog = "notes"
				validationQuery="SELECT 1"
				testOnBorrow=true
				testWhileIdle=true
				testOnReturn=true
			}
		}
	}
	
	fvtest {
		dataSource {
			dbCreate = "create-drop"
			driverClassName = "com.mysql.jdbc.Driver"
			dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
			username = ""	//see config in ds_<env>.groovy
			password = ""	//see config in ds_<env>.groovy
			url = "jdbc:mysql://127.0.0.1:3306/notes_test"
			properties {
				validationQuery="SELECT 1"
			}
			//logSql=true
			//formatSql=true
		}
		dataSource_notes {
			readOnly = false
			dbCreate = "create-drop"
			//logSql=true
			//formatSql=true
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			username = ""	//see config in ds_<env>.groovy
			password = ""	//see config in ds_<env>.groovy
			//dialect = org.hibernate.dialect.Oracle11gDialect
			dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
			username = ""	//see config in ds_<env>.groovy
			password = ""	//see config in ds_<env>.groovy
			url = "jdbc:mysql://127.0.0.1:3306"	//no schema, it's specified in domain classes that use this dataSource
			properties {
				defaultCatalog = "notes"
				validationQuery="SELECT 1"
				testOnBorrow=true
				testWhileIdle=true
				testOnReturn=true
			}
		}
	}
}

// orig environment specific settings
/*
environments {
    development {
		dataSource {
			dbCreate = "create-drop"
			driverClassName = "com.mysql.jdbc.Driver"
			dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
			username = "root"	//see config in ds_<env>.groovy
			password = ""	//see config in ds_<env>.groovy
			url = "jdbc:mysql://127.0.0.1:3306/elastic_plugin?autoReconnect=true"
			properties {
				validationQuery="SELECT 1"
			}
		}
    }
}
*/
