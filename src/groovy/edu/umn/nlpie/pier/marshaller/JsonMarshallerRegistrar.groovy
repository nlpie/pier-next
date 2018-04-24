package edu.umn.nlpie.pier.marshaller

import javax.annotation.PostConstruct

import org.codehaus.groovy.grails.web.converters.configuration.DefaultConverterConfiguration

import edu.umn.nlpie.pier.api.CorpusMetadata
import edu.umn.nlpie.pier.audit.DistinctCount
import edu.umn.nlpie.pier.audit.Query
import edu.umn.nlpie.pier.audit.SearchRegistration
import edu.umn.nlpie.pier.context.AuthorizedContext
import edu.umn.nlpie.pier.elastic.Cluster
import edu.umn.nlpie.pier.elastic.Field
import edu.umn.nlpie.pier.elastic.Index
import edu.umn.nlpie.pier.elastic.Type
import edu.umn.nlpie.pier.ui.Corpus
import edu.umn.nlpie.pier.ui.FieldPreference
import edu.umn.nlpie.pier.ui.Ontology
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
				"aggregate": fpr.aggregate,
				"countDistinct": fpr.computeDistinct,
				"count": null,	//filled in client-side if countDistinct is true
				"numberOfFilterOptions": fpr.numberOfFilterOptions,
				"export": fpr.export,
				"isTemporal": ["DATE","DATETIME"].contains(fpr.field.dataTypeName) ? true: false,
				"isNumeric": ["LONG","INTEGER"].contains(fpr.field.dataTypeName) ? true: false
			]
		}
		
		JSON.createNamedConfig ('fieldpreference') { DefaultConverterConfiguration<JSON> cfg ->
			cfg.registerObjectMarshaller (FieldPreference) { fpr ->
				[
					"id": fpr.id,
					"label": fpr.label,
					"corpus": fpr.field.type.index,
					"ontology": fpr.ontology,
					"field": fpr.field,
					"displayOrder": fpr.displayOrder,
					"numberOfFilterOptions": fpr.numberOfFilterOptions,
					"isTemporal": ["DATE","DATETIME"].contains(fpr.field.dataTypeName) ? true: false,
					"isNumeric": ["LONG","INTEGER"].contains(fpr.field.dataTypeName) ? true: false,
					"countDistinct": fpr.computeDistinct,
					//"count": null,	//filled in client-side if countDistinct is true
					"aggregate": fpr.aggregate,
					"export": fpr.export,
					"filters": [:],	//TODO move to client side object as an extenstion of data returned by API
					"min": null,		//TODO move to client side object as an extenstion of data returned by API
					"max": null		//TODO move to client side object as an extenstion of data returned by API			
				]
			}
			cfg.registerObjectMarshaller (Field) { f ->
				[
					id: f.id,
					fieldName: f.fieldName,
					aggregatable: f.aggregatable,
					significantTermsAggregatable: f.significantTermsAggregatable,
					exportable: f.exportable,
					contextFilterField: f.contextFilterField,
					dataTypeName: f.dataTypeName,
					description: f.description
				]
			}
			cfg.registerObjectMarshaller (Index) { i ->
				[
					id: i.id,
					name: i.commonName
				]
			}
			cfg.registerObjectMarshaller (Ontology) { o ->
				[
					id: o.id,
					name: o.name,
					description: o.description
				]
			}
		}
		
		JSON.registerObjectMarshaller(DistinctCount) { dc ->
			[
				"id": dc.id,
				"countType": dc.countType,
				"label": dc.label,
				"count": dc.count, 
				"cardinalityEstimate": dc.cardinalityEstimate,
				"took": dc.took,
				"httpStatus": dc.httpStatus
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
					"corpora": c.annotatedCorpora()
				]
			}
			cfg.registerObjectMarshaller (Corpus) { ct ->
				[
					id: ct.id,
					name: ct.name,
					glyph: ct.glyph,
					metadata: ct.metadata,
					status: [ 
						searchingDocs:false, 
						computingAggs:false, 
						active: false,
						dirty:false,
						userSelectedFilters: false,
						showBan: false 
					],
					resultsOpacity: [
						dimmed: [ opacity: 0.2 ],
						bright: [ opacity: 1 ]
					],
					opacity: null
				]
			}
			cfg.registerObjectMarshaller (CorpusMetadata) { cm ->
				[
					tooltip: cm.tooltip,
					searchable: cm.searchable,
					filtered: cm.filtered,
					url: cm.url,
					scrollUrl: cm.scrollUrl,
					defaultSearchField: cm.defaultSearchField,
					contextFilterField: cm.contextFilterField
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
		
		JSON.registerObjectMarshaller(SearchRegistration) { sr ->
			[
				id: sr.id,
				authorizedContext: sr.authorizedContext,
				username: sr.username,
				uuid: sr.uuid,
				queries: sr.queries
			]
		}
		
		JSON.registerObjectMarshaller(Query) { qq ->
			[
				id: qq.id,
				query: qq.query,
				type: qq.type,
				url: qq.url,
				corpus: qq.corpus
			]
		}
		
		JSON.registerObjectMarshaller(Corpus) { cp ->
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
		marshallers << "Corpus"
		marshallers << "Field"
		marshallers << "Type"
		marshallers << "index.mapping [named config]"
		println "registering ${marshallers} json marshallers"
		
	}//registerMarshallers()

}
