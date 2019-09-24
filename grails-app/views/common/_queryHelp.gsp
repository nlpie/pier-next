	
	<div class="modal-header">
        <h3 class="modal-title">
			Search terms syntax
		</h3>
			
        </div>
   <div></div>
        <div class="modal-body">
            <div>
				NLP-PIER leverages elasticsearch for indexing unstructured EMR notes. Queries/searches utilize the Lucene query syntax, a powerful and 
				flexible syntax for finding that surgical needle in the clinical notes haystack. 
				Example search terms (below) can be pasted as templates 
				into the search input box by clicking on the example itself. 
				Modify as necessary according to your needs.
			</div>
			
			<br>
			<h4 class="modal-title">Example Search Terms (NLP-PIER defaults to logical AND queries; logical operator case matters)</h4>

			<div>
				<div ng-repeat="xq in hCtrl.queries" >
        			<div class="spirit-accordion-heading"
        				ng-mouseenter="explanationClass='spirit-hover'"
        				ng-mouseleave="explanationClass='spirit-collapse'">
						<a style="cursor:pointer"
							ng-click="hCtrl.currentSearch.appendHelp(xq.query);hCtrl.currentSearch.inputChange();hCtrl.modalService.helpOk()" data-container="body" data-toggle="tooltip" data-placement="top" title="click to paste into search box">
							{{xq.query}}
						</a>
					</div>
					<div style=";margin-left:10px;margin-bottom:10px">
		  				<div class="spirit-accordion-inner" ng-bind-html="xq.explanation" />
					</div>
    			</div>
    		</div>
			
			<div style="margin-top:25px">
				Query syntax <a href="query_string_query_reference.pdf">guide</a> (edited) from Elasticsearch
        	</div>
        	<div class="modal-footer">
            	<!--  <button class="btn btn-primary" type="button" ng-click="hCtrl.modalService.helpOk()">OK</button>-->
            	<button class="btn btn-warning" type="button" ng-click="hCtrl.modalService.helpCancel()">Cancel</button>
        	</div>
		</div>
		
		