package edu.umn.nlpie.pier.marshaller

import javax.annotation.PostConstruct

import org.codehaus.groovy.grails.web.converters.configuration.DefaultConverterConfiguration

import edu.umn.nlpie.pier.api.CorpusMetadata
import edu.umn.nlpie.pier.audit.DistinctCount
import edu.umn.nlpie.pier.audit.Query
import edu.umn.nlpie.pier.elastic.Field
import edu.umn.nlpie.pier.elastic.Index
import edu.umn.nlpie.pier.elastic.Type
import edu.umn.nlpie.pier.context.AuthorizedPierContext
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
			cfg.registerObjectMarshaller (Ontology) { o ->
				[
					id: o.id,
					name: o.name,
					description: o.description,
					aggregations: o.fieldPreferences
				]
			}
			cfg.registerObjectMarshaller (FieldPreference) { fpr ->
				//fp returned has 2 client-side use cases: 1) display/update user settings for corpus metadata fields, 2) as set of aggregations to be queried for each corpus
				[
					"id": fpr.id,
					"label": fpr.label,
					"field": fpr.field,
					"displayOrder": fpr.displayOrder,
					"numberOfFilterOptions": fpr.numberOfFilterOptions,
					"isTemporal": ["DATE","DATETIME"].contains(fpr.field.dataTypeName) ? true: false,
					"isNumeric": ["LONG","INTEGER"].contains(fpr.field.dataTypeName) ? true: false,
					"countDistinct": fpr.computeDistinct,
					"aggregate": fpr.aggregate,
					"export": fpr.export,
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
			//TODO can this AuthorizedContext config piggyback on the standalone config above?
			cfg.registerObjectMarshaller (AuthorizedPierContext) { ct ->
				[
					"requestId": ct.requestId,
					"label": ct.label,
					"description": ct.description?:"description unavailable",
					//"username": ct.username,
					"corpusName": ct.corpusName,
					"filteredContext": ct.filteredContext,
					"contextFilterValue": ct.contextFilterValue,
					"corpus": ct.corpus()
				]
			}
			cfg.registerObjectMarshaller (Corpus) { c ->
				[
					id: c.id,
					name: c.name,
					glyph: c.glyph,
					metadata: c.metadata
				]
			}
			cfg.registerObjectMarshaller (CorpusMetadata) { cm ->
				[
					tooltip: cm.tooltip,
					searchable: cm.searchable,
					//filtered: cm.filtered,
					url: cm.url,
					scrollUrl: cm.scrollUrl,
					defaultSearchField: cm.defaultSearchField,
					contextFilterField: cm.contextFilterField
					//aggregations: cm.aggregations
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
		
		JSON.registerObjectMarshaller(Query) { qq ->
			[
				id: qq.id,
				userInput: qq.userInput,
				query: qq.query,
				type: qq.type,
				url: qq.url,
				corpus: qq.corpus,
				uuid: qq.uuid,
				inputExpansion: qq.inputExpansion,
				expansionTerms: qq.expansionTerms,
				distinctCounts: qq.distinctCounts,
				saved: qq.saved
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
		
		JSON.createNamedConfig ('recent.query') { DefaultConverterConfiguration<JSON> cfg ->
			cfg.registerObjectMarshaller (Query) { Query q ->
				[ 	id: q.id,
					userInput: q.userInput,
					query: q.query,
					terms: q.terms,
					type: q.type,
					authorizedContext: q.authorizedContext,
					uuid: q.uuid,
					inputExpansion: q.inputExpansion,
					expansionTerms: q.expansionTerms,
					distinctCounts: q.distinctCounts
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
