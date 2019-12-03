import edu.umn.nlpie.pier.elastic.search.ElasticService
import edu.umn.nlpie.pier.marshaller.JsonMarshallerRegistrar
import grails.util.Environment

// Place your Spring DSL code here
beans = {
	jsonMarshallerRegistrar(JsonMarshallerRegistrar)
	
	elasticService(ElasticService) {
		def UMN = "http://nlp05.ahc.umn.edu:9200/umls_2013/entry/_search"
		def FV = "http://nlp02.fairview.org:9200/umls_2013/entry/_search"
		switch(Environment.current.name) {
			case [ 'development', 'test', 'production' ]:
				umlsIndexSearchUrl = UMN
				break
			case [ 'fvdev', 'fvtest', 'fvprod' ]:
				umlsIndexSearchUrl = FV				
				break
			default:
				umlsIndexSearchUrl = UMN
				break
		}
	}
}