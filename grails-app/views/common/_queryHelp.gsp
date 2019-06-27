	
	<div class="modal-header">
        <h3 class="modal-title">
			Query string syntax
			<!-- <span class="pull-right btn-sm" ng-click="ok()"><i class="glyphicon glyphicon-remove"></i></span> -->
		</h3>
			
        </div>
   <div></div>
        <div class="modal-body">
            <div>
				NLP-PIER leverages elasticsearch for indexing unstructured EMR notes. Queries utilize the Lucene query syntax, a powerful and 
				flexible syntax for finding that surgical needle in the clinical notes haystack. 
				Commonly used query types are listed below. Examples can be pasted as templates 
				into the search box by clicking on the example itself. 
				Modify as necessary according to your needs.
			</div>
			
			<br>
			<!--accordians-->
			<h4 class="modal-title">Example Queries (NLP-PIER defaults to logical AND queries; case of logical operator matters)</h4>

			<div>
				<div ng-repeat="xq in hCtrl.queries" >
        			<div class="spirit-accordion-heading"
        				ng-mouseenter="explanationClass='spirit-hover'"
        				ng-mouseleave="explanationClass='spirit-collapse'">
						<a style="cursor:pointer"
							ng-click="hCtrl.currentSearch.userInput=xq.query;hCtrl.currentSearch.dirty();hCtrl.modalService.helpOk()" data-container="body" data-toggle="tooltip" data-placement="top" title="click to paste into search box">
							{{xq.query}}
						</a>
					</div>
					<div style=";margin-left:10px;margin-bottom:10px">
		  				<div class="spirit-accordion-inner" ng-bind-html="xq.explanation" />
					</div>
    			</div>
    		</div>
			
			<div style="margin-top:25px">
				Query syntax <a style="color:#428BCA" href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html#query-string-syntax">pointers</a> from elasticsearch. Or consult the Lucene reference 
				<a style="color:#428BCA" href="http://lucene.apache.org/core/4_7_2/queryparser/org/apache/lucene/queryparser/classic/package-summary.html#package_description">query syntax</a>
				documentation directly from the Lucene API site.
        	</div>
        	<div class="modal-footer">
            	<!--  <button class="btn btn-primary" type="button" ng-click="hCtrl.modalService.helpOk()">OK</button>-->
            	<button class="btn btn-warning" type="button" ng-click="hCtrl.modalService.helpCancel()">Cancel</button>
        	</div>
		</div>
		
		