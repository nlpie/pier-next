package edu.umn.nlpie.pier.marshaller

import javax.annotation.PostConstruct

import org.codehaus.groovy.grails.web.converters.configuration.DefaultConverterConfiguration

import edu.umn.nlpie.pier.api.QueryInfo
import edu.umn.nlpie.pier.context.AuthorizedContext
import edu.umn.nlpie.pier.elastic.Cluster
import edu.umn.nlpie.pier.elastic.Field
import edu.umn.nlpie.pier.elastic.Index
import edu.umn.nlpie.pier.elastic.Type
import edu.umn.nlpie.pier.ui.CorpusType
import edu.umn.nlpie.pier.ui.FieldPreference
import grails.converters.JSON

class JsonMarshallerRegistrar {
	
	@PostConstruct
	void registerMarshallers() {
		
		JSON.registerObjectMarshaller(FieldPreference) { fpr ->
			[
				"id": fpr.id,
				"label": fpr.label,
				"fieldName": fpr.field.fieldName,
				"fieldDescription": fpr.field.description,
				"ontology": fpr.ontology.name,
				"username": fpr.user.username,
				"displayOrder": fpr.displayOrder,
				"displayAsFilter": fpr.displayAsFilter,
				"numberOfFilterOptions": fpr.numberOfFilterOptions,
				"includeInExport": fpr.includeInExport
			]
		}
			
		JSON.createNamedConfig ('authorized.context') { DefaultConverterConfiguration<JSON> cfg ->
			cfg.registerObjectMarshaller (AuthorizedContext) { c ->
				[
					//"id": c.id,
					"requestId": c.requestId,
					"label": c.label,
					"contextFilterValue": c.filterValue,
					"description": c.description?:"description unavailable",
					"username": c.username,
					"candidateCorpora": c.annotatedCorpusTypes()
				]
			}
			cfg.registerObjectMarshaller (CorpusType) { ct ->
				[
					id: ct.id,
					name: ct.name,
					glyph: ct.glyph,
					queryInfo: ct.queryInfo,
					queryFilters: null	//placeholder to be filled by subsequent, client-side initiated request
				]
			}
			cfg.registerObjectMarshaller (QueryInfo) { qi ->
				[
					tooltip: qi.tooltip,
					searchable: qi.searchable,
					url: qi.url,
					defaultSearchField: qi.defaultSearchField,
					contextFilterField: qi.contextFilterField,
				]
			}
			
			cfg.registerObjectMarshaller (Field) { f ->
				[
					"fieldName": f.fieldName,
					"defaultSearchField": f.defaultSearchField,
					"dataTypeName": f.dataTypeName,
					"description": f.description,
					"defaultFilter": f.defaultPreference
				]
			}
			
			
			
			
			/*
			cfg.registerObjectMarshaller (Type) { t ->
			[
			 id: t.id,
			 name: t.typeName,
			 environment: t.environment,
			 index: t.index
			 ]
			}
			
			
			
			cfg.registerObjectMarshaller (Index) { i ->
				[
					//"id": i.id,
					"name": i.indexName,
					"commonName": i.commonName,
					"alias": i.alias,
					"defaultSearchField": i.types[0].fields.find { it.defaultSearchField==true },
					"cluster": i.cluster
				]
			}
			cfg.registerObjectMarshaller (Cluster) { c ->
				[
					"id": c.id,
					"uri": c.uri,
					"commonName": c.commonName
				]
			}
			*/
		}//authorized.context
		
		JSON.createNamedConfig ('available.corpora') { DefaultConverterConfiguration<JSON> cfg ->
			cfg.registerObjectMarshaller (Type) { t ->
				[
					id: t.id,
					name: t.typeName,
					environment: t.environment,
					index: t.index
				]
			}
			cfg.registerObjectMarshaller (Index) { i ->
				[
					id: i.id,
					name: i.indexName,
					cluster: i.cluster
				]
			}
			cfg.registerObjectMarshaller (Cluster) { cl ->
				[
					id: cl.id,
					name: cl.uri
				]
			}
				
		}//available.corpora
		
		JSON.registerObjectMarshaller(CorpusType) { cp ->
			[ 
				id: cp.id,
				name: cp.name,
				enabled: cp.enabled,
				description: cp.description,
				glyph: cp.glyph
				
			]
		}
		
		
		JSON.registerObjectMarshaller(Field) { f ->
			[ "${f.fieldName}": f.dataType ]
		}
		
		JSON.registerObjectMarshaller(Type) { Type type ->
			Map propertiesMap = [:]
			type.fields.each { f ->
				propertiesMap.put(f.fieldName, f.dataType)
			}
			[ "${type.typeName}": propertiesMap ]
		}
		
		
		//this config returns JSON capable of being fed into a create index statement,
		//e.g., curl -XPOST http://nlp05.ahc.umn.edu:9200/<index_name> --data-binary <mapping json>
		JSON.createNamedConfig ('index.mapping') { DefaultConverterConfiguration<JSON> cfg ->
			cfg.registerObjectMarshaller (Type) { Type type ->
				Map propertiesMap = [:]
						type.fields.each { f ->
						propertiesMap.put(f.fieldName, f.dataType)
				}
				propertiesMap
			}
			cfg.registerObjectMarshaller (Index) { Index index ->
				
				Map typesMap = [:]
				index.types.each { ty ->
					typesMap.put( ty.typeName, [
							"dynamic": "strict",
							"_timestamp": [ "enabled": true ],
							"properties": ty
					] )
				}
				[
					aliases: [:],
					mappings: typesMap,
					settings: [index: [ number_of_shards:index.numberOfShards, number_of_replicas:index.numberOfReplicas ] ],
					warmers: [:]
				]
			}
		}
		
		def marshallers = []
		marshallers << "authorized.context [named config]"
		marshallers << "CorpusType"
		marshallers << "Field"
		marshallers << "Type"
		marshallers << "index.mapping [named config]"
		println "registering ${marshallers} json marshallers"
		
	}//registerMarshallers()

}
