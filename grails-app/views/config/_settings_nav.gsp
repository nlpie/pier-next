<!-- getting expanded search bar in navbar form based on http://stackoverflow.com/questions/18552714/bootstrap-3-how-to-maximize-input-width-inside-navbar http://www.bootply.com/t7O3HSGlbc -->
<nav class="navbar navbar-default">
  <div class="container-fluid">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <!-- <a class="navbar-brand" href="#">Brand</a> -->
    </div>

    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">

		<ul class="nav navbar-nav navbar-right">
			<!-- configure links in NavCtrl links property -->
			<li>
				<a><i class="fa fa-expand fa-lg" data-container="body" data-toggle="tooltip" data-placement="bottom" data-html="true" title="expand query using semantic similarity"></i></a>
			</li>
			<li>
				<a><i class="fa fa-language fa-lg" data-container="body" data-toggle="tooltip" data-placement="bottom" data-html="true" title="expand query using UMLS CUIs"></i></a>
			</li>
			<li>
				<a><i class="fa fa-download fa-lg" data-container="body" data-toggle="tooltip" data-placement="bottom" title="download query results"></i></a>
			</li>
			<li>
				<a><i class="fa fa-floppy-o fa-lg" data-container="body" data-toggle="tooltip" data-placement="bottom" title="save query for later use"></i></a>
			</li>
		</ul>

		<form class="navbar-form">
       		<div class="form-group" style="display:inline;">
       			<div class="input-group" style="display:table;">
					<div class="input-group-btn" class="input-group-addon" style="width:1%;">
						<button type="button" class="btn btn-default" data-toggle="dropdown" style="border-right:none">
                            <span class="label-icon" title="{{sc.uiState.currentContext.description}}" >{{sc.uiState.currentContext.icsRequest}}</span>
                            <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu" role="menu">
                            <li role="presentation" class="dropdown-header pull-left">Authorized Search Contexts</li>
                        	<li ng-repeat="c in sc.uiState.authorizedContexts" class="pull-left">
                        		<a>
		                        	<i class="fa fa-file-text-o pier-li-icon" ng-click="sc.uiState.changeContext(c)" title="search only notes"></i>
		                        	<i class="fa fa-file-image-o pier-li-left-padded-icon" ng-click="sc.uiState.changeContext(c)" title="search only imaging reports"></i>
                        			
                        			<i class="pier-li-left-padded-icon">
                        				<svg id="pathology-svg" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" version="1.1" x="0px" y="0px" viewBox="0 0 392.667 490.83374999999995" enable-background="new 0 0 392.667 392.667" xml:space="preserve"><g><g><path fill="#000000" d="M103.492,84.779c-0.792,0-1.579-0.317-2.159-0.94c-0.812-0.872-1.638-1.605-2.456-2.179    c-0.754-0.529-1.218-1.381-1.253-2.302c-0.035-0.921,0.363-1.805,1.075-2.39l0.688-0.554c0.656-0.532,1.511-0.758,2.345-0.616    c0.834,0.14,1.567,0.633,2.014,1.351c0.582,0.936,1.263,1.883,2.026,2.816c1.008,1.232,0.856,3.041-0.343,4.087    C104.871,84.538,104.181,84.779,103.492,84.779z"/></g><g><path fill="#000000" d="M202.164,104.171c-1.357,0-2.552-0.932-2.867-2.271c-0.662-2.807-4.718-4.478-8.065-4.289    c-1.286,0.067-2.464-0.689-2.924-1.888c-0.459-1.199-0.09-2.557,0.913-3.358c2.062-1.647,3.642-3.864,3.758-5.271    c0.034-0.419-0.078-0.635-0.269-0.849c-0.002-0.002-0.005-0.005-0.007-0.007c-2.788-3.104-9.766-16.027-20.741-38.413    c-0.415-0.846-0.4-1.84,0.039-2.673c0.44-0.834,1.252-1.407,2.184-1.542c16.964-2.463,34.328-2.091,51.08,1.061    c0.899,0.17,1.67,0.748,2.084,1.566c1.834,3.622,0.448,8.599-1.461,12.049c-0.073,0.132-0.155,0.257-0.248,0.377    c-6.562,8.483-13.642,18.765-15.857,23.519c-0.07,0.275-0.183,0.545-0.339,0.8c-0.007,0.012-0.014,0.022-0.021,0.034    c-0.191,0.488-0.306,0.887-0.335,1.186c0.342,2.45,3.283,4.977,8.093,6.945c1.473,0.603,2.202,2.267,1.646,3.759    c-0.556,1.491-2.194,2.274-3.705,1.763c-3.267-1.101-5.762-1.105-7.421-0.012c-2.242,1.482-2.594,4.764-2.597,4.797    c-0.113,1.449-1.264,2.598-2.712,2.71C202.316,104.168,202.24,104.171,202.164,104.171z M196.985,92.552    c1.591,0.531,3.097,1.312,4.379,2.338c0.745-1.148,1.746-2.266,3.088-3.153c0.523-0.345,1.07-0.64,1.642-0.886    c-0.838-0.932-1.544-1.962-2.054-3.097c-1.771,0.706-3.609,0.858-5.304,0.694C198.452,89.948,197.783,91.338,196.985,92.552z     M196.763,81.907c1.455,0.654,5.283,1.87,7.648-2c0.051-0.082,0.105-0.161,0.161-0.237c3.141-6.931,10.985-17.603,16.258-24.429    c0.95-1.78,1.5-3.737,1.492-5.112c-14.218-2.49-28.821-2.91-43.255-1.214C187.848,66.688,194.324,78.764,196.763,81.907z"/></g><g><path fill="#000000" d="M112,174.772c-1.103,0-2.146-0.622-2.651-1.658c-1.262-2.593-5.576-3.34-8.811-2.412    c-1.231,0.357-2.555-0.131-3.267-1.199c-0.711-1.069-0.649-2.475,0.153-3.477c1.65-2.06,2.704-4.57,2.508-5.968    c-0.059-0.416-0.215-0.602-0.449-0.768c-1.195-0.846-1.588-2.44-0.924-3.744c0.655-1.286,2.141-1.908,3.514-1.474    c0.497,0.138,4.553,1.005,8.307-5.81c0.735-1.333,2.363-1.888,3.756-1.282c1.396,0.606,2.1,2.174,1.628,3.62    c-0.673,2.058-0.731,2.915-0.731,3.147c0.901,2.29,4.313,4.089,9.401,4.946c1.57,0.265,2.646,1.728,2.43,3.306    c-0.215,1.578-1.658,2.698-3.227,2.533c-3.429-0.358-5.865,0.186-7.241,1.618c-1.863,1.938-1.484,5.218-1.48,5.25    c0.208,1.438-0.662,2.813-2.051,3.24C112.579,174.73,112.287,174.772,112,174.772z M105.04,164.502    c1.669,0.169,3.309,0.6,4.785,1.319c0.474-1.285,1.205-2.594,2.321-3.755c0.434-0.451,0.903-0.86,1.407-1.225    c-1.287-0.914-2.406-2.025-3.231-3.375c-1.567,1.195-3.11,1.868-4.5,2.227C105.963,161.362,105.6,163.018,105.04,164.502z     M101.187,156.884h0.012H101.187z"/></g><g><path fill="#000000" d="M293.843,315.256c-0.569,0-1.14-0.165-1.638-0.496c-0.533-0.357-0.951-0.615-1.275-0.79    c-0.261-0.142-0.5-0.321-0.708-0.533c-4.04-4.134-6.336-11.308-2.632-16.464c4.409-6.138,20.025-12.697,32.348-17.119    c1.209-0.433,2.556-0.035,3.336,0.982c0.78,1.017,0.813,2.423,0.081,3.475c-7.822,11.256-17.114,21.435-27.616,30.254    C295.194,315.023,294.519,315.256,293.843,315.256z M312.499,289.006c-10.729,4.383-18.019,8.481-20.121,11.407l0,0    c-1.509,2.1-0.547,5.655,1.366,8.103C300.577,302.592,306.856,296.059,312.499,289.006z M289.984,298.693h0.012H289.984z"/></g><g><path fill="#000000" d="M236.063,61.622c-0.919,0-1.815-0.43-2.383-1.22c-1.684-2.343-6.062-2.351-9.093-0.889    c-1.157,0.558-2.541,0.302-3.422-0.63c-0.882-0.933-1.059-2.33-0.438-3.452c1.367-2.469,1.965-5.338,1.36-6.53    c-0.507-1.001-0.402-2.204,0.273-3.102c0.674-0.898,1.792-1.335,2.903-1.127c4.281,0.806,8.557,1.8,12.707,2.954    c0.934,0.26,1.68,0.962,1.997,1.878c0.317,0.917,0.161,1.93-0.413,2.71c-1.661,2.255-0.678,5.551-0.668,5.584    c0.423,1.38-0.213,2.875-1.506,3.516C236.959,61.521,236.507,61.622,236.063,61.622z M229.057,52.605    c1.316,0,2.626,0.169,3.866,0.53c0.044-0.234,0.094-0.472,0.154-0.709c-1.627-0.411-3.27-0.796-4.919-1.154    c-0.041,0.457-0.107,0.914-0.193,1.371C228.328,52.618,228.693,52.605,229.057,52.605z"/></g><g><path fill="#000000" d="M218.176,329.757c-1.172,0-2.274-0.705-2.735-1.849c-0.292-0.651-2.184-4.019-8.609-1.6    c-1.481,0.556-3.146-0.157-3.758-1.62c-0.613-1.464,0.043-3.15,1.482-3.815c0.527-0.244,0.81-0.418,0.948-0.516    c0.663-2.343-0.848-5.841-4.187-9.657c-1.049-1.198-0.957-3.012,0.207-4.097c1.165-1.087,2.981-1.05,4.102,0.078    c2.415,2.431,4.629,3.551,6.598,3.33c2.713-0.307,4.493-3.123,4.512-3.152c0.761-1.225,2.304-1.73,3.641-1.175    c1.333,0.554,2.075,1.983,1.743,3.389c-0.663,2.807,2.204,6.115,5.298,7.442c1.179,0.506,1.895,1.717,1.771,2.995    c-0.126,1.278-1.063,2.328-2.318,2.596c-2.581,0.552-4.985,1.829-5.717,3.035c-0.218,0.359-0.214,0.603-0.138,0.88    c0.417,1.506-0.416,3.077-1.897,3.578C218.808,329.706,218.489,329.757,218.176,329.757z M211.557,319.557    c2.119,0.092,3.867,0.69,5.27,1.528c0.894-1.085,2.023-1.943,3.184-2.615c-1.185-1.187-2.183-2.558-2.872-4.049    c-1.18,0.696-2.575,1.247-4.173,1.441c-0.622,0.075-1.24,0.096-1.863,0.059C211.43,317.112,211.602,318.331,211.557,319.557z"/></g><g><path fill="#000000" d="M226.256,322.173c-0.397,0-0.793-0.081-1.163-0.24c-5.311-2.279-10.169-8.036-8.711-14.214    c1.877-7.953,9.982-32.818,13.379-43.139c0.074-0.225,0.175-0.439,0.299-0.639c1.935-3.1,4.468-4.055,6.253-4.31    c4.945-0.704,9.651,3.027,10.704,3.926c3.497,2.357,27.87,19.05,31.092,28.53c1.726,5.077,2.918,12.079-1.211,16.598    c-3.351,3.672-32.805,9.992-50.065,13.432C226.642,322.155,226.449,322.173,226.256,322.173z M235.24,266.795    c-2.769,8.42-11.271,34.452-13.118,42.279c-0.607,2.571,1.747,5.562,4.524,7.067c20.556-4.124,43.023-9.48,45.978-11.522    c1.601-1.877,1.567-5.739-0.098-10.635c-1.985-5.842-18.811-18.824-28.95-25.636c-0.113-0.075-0.22-0.158-0.32-0.248    c-0.884-0.781-3.919-2.933-6.117-2.632C236.665,265.539,235.97,265.754,235.24,266.795z"/></g><g><path fill="#000000" d="M280.233,325.446c-0.609,0-1.217-0.189-1.733-0.563c-0.996-0.724-1.437-1.992-1.107-3.178    c1.276-4.565,1.581-6.192,1.65-6.756v-0.001c0.238-1.954,1.214-3.612,2.821-4.795c3.723-2.742,9.36-1.975,10.996-1.677    c0.305,0.055,0.599,0.159,0.871,0.305c0.496,0.268,1.051,0.609,1.749,1.076c0.772,0.516,1.255,1.363,1.306,2.29    c0.051,0.926-0.337,1.822-1.048,2.418c-4.423,3.715-9.09,7.21-13.874,10.388C281.37,325.282,280.801,325.446,280.233,325.446z     M286.572,314.356c-0.495,0.138-0.91,0.321-1.214,0.545c-0.32,0.235-0.417,0.45-0.456,0.714    C285.461,315.199,286.018,314.78,286.572,314.356z"/></g><g><path fill="#000000" d="M220.147,348.838c-0.667,0-1.32-0.227-1.846-0.649c-0.647-0.519-1.044-1.287-1.096-2.115    c-0.818-13.15-1.524-17.223-1.87-18.48c-0.525-1.897-0.256-3.802,0.78-5.508c2.398-3.953,7.897-5.395,9.526-5.743    c20.853-4.157,43.985-9.649,46.983-11.724c1.601-1.877,1.567-5.739-0.098-10.635c-0.512-1.507,0.265-3.148,1.755-3.709    c1.484-0.557,3.155,0.164,3.762,1.638c1.306,3.17,2.932,5.052,4.831,5.595c2.626,0.751,5.343-1.175,5.371-1.195    c1.172-0.841,2.793-0.72,3.815,0.302c1.021,1.021,1.162,2.626,0.319,3.799c-1.682,2.342-0.292,6.494,2.061,8.9    c0.897,0.919,1.097,2.312,0.494,3.445c-0.602,1.134-1.874,1.743-3.132,1.515c-2.598-0.474-5.306-0.211-6.443,0.626    c-0.339,0.249-0.428,0.476-0.463,0.76l0,0c-0.103,0.839-0.403,2.545-1.823,7.63c-0.191,0.681-0.62,1.27-1.208,1.662    c-18.618,12.376-39.23,20.4-61.261,23.85C220.451,348.827,220.299,348.838,220.147,348.838z M270.536,311.7    c-10.382,3.516-30.573,7.799-43.704,10.417c-2.54,0.542-4.945,1.819-5.677,3.026c-0.218,0.359-0.214,0.603-0.138,0.88    c0.658,2.378,1.269,7.765,1.861,16.421c19.63-3.506,38.048-10.83,54.809-21.798c0.861-3.147,1.186-4.652,1.307-5.364    c-0.314-1.351-2.351-3.555-8.258-3.576C270.668,311.706,270.601,311.704,270.536,311.7z M278.106,307.055    c1.84,0.721,3.232,1.695,4.262,2.753c1.374-0.863,2.953-1.308,4.477-1.513c-0.644-1.55-1.043-3.198-1.111-4.839    c-1.355,0.192-2.855,0.17-4.406-0.259c-0.603-0.168-1.185-0.387-1.746-0.656C279.385,304.135,278.927,305.67,278.106,307.055z"/></g><g><path fill="#000000" d="M196.542,350.674c-17.691,0-35.058-2.98-51.621-8.857c-0.99-0.351-1.719-1.203-1.912-2.236    c-0.193-1.033,0.178-2.09,0.974-2.776c0.352-0.303,0.704-0.598,1.054-0.883c-3.557-0.793-6.836-2.27-8.706-4.369    c-0.961-1.096-23.192-26.478-27.806-31.95c-1.269-1.505-1.819-3.349-1.591-5.332c0.21-1.826,1.032-3.497,2.031-4.91    c-1.573-0.584-3.052-1.413-4.3-2.48c-0.782,1.124-1.819,2.208-3.19,3.052c-3.321,2.038-7.548,2.121-12.569,0.244    c-0.058-0.022-0.115-0.046-0.172-0.071c-1.977-0.886-6.781-2.667-18.447-6.126c-0.654-0.193-1.219-0.608-1.602-1.172    c-17.326-25.569-26.484-55.471-26.484-86.473c0-21.984,4.531-43.227,13.469-63.137c0.411-0.914,1.258-1.558,2.25-1.708    c2.507-0.377,11.187-1.286,17.567,2.925c0.043,0.028,0.086,0.058,0.127,0.089c3.637,2.685,22.043,16.27,27.277,19.977    c1.606,1.137,2.603,2.782,2.881,4.758c0.256,1.822-0.12,3.646-0.731,5.265c1.669,0.169,3.309,0.6,4.785,1.319    c0.474-1.285,1.205-2.594,2.321-3.755c0.434-0.451,0.903-0.86,1.407-1.225c-1.849-1.313-3.348-3.031-4.134-5.28    c-0.534-1.532-0.314-3.559,0.737-6.779c0.001-0.004,0.003-0.008,0.004-0.013c2.993-9.163,12.146-25.597,12.534-26.292    c1.372-2.513,0.524-10.338-1.429-13.115c-0.761-0.992-14.29-18.625-20.055-25.664c-0.925-1.13-1.756-2.286-2.471-3.437    c-0.784-1.262-0.507-2.91,0.647-3.846c21.559-17.488,47.424-28.833,74.798-32.806c1.266-0.181,2.508,0.47,3.07,1.62    c10.138,20.678,17.553,34.537,19.835,37.071c1.317,1.462,1.928,3.287,1.765,5.276c-0.151,1.833-0.918,3.531-1.87,4.976    c1.591,0.531,3.097,1.312,4.379,2.338c0.745-1.148,1.746-2.266,3.088-3.153c0.523-0.345,1.07-0.64,1.642-0.886    c-1.517-1.687-2.601-3.693-2.873-6.059c-0.592-5.141,10.871-20.838,17.755-29.738c0.28-0.362,0.64-0.653,1.051-0.852    c3.228-1.555,7.346-2.103,10.896-1.067c0.272-1.456,0.834-2.992,1.885-4.418c0.725-0.986,1.985-1.419,3.163-1.092    c27.456,7.633,52.27,22.777,71.758,43.793c19.581,21.115,32.823,47.142,38.297,75.266c0,0.002,0.001,0.005,0.001,0.007    c1.895,9.74,2.856,19.713,2.856,29.643c0,7.531-0.556,15.136-1.652,22.601c0,0.003-0.001,0.008-0.001,0.012    c-3.429,23.343-12.376,45.946-25.874,65.365c-0.35,0.503-0.848,0.886-1.424,1.093c-15.835,5.681-26.882,11.292-29.552,15.009    c-0.177,0.246-0.393,0.465-0.637,0.647c-0.202,0.15-4.993,3.635-10.413,2.137c-3.755-1.043-6.695-4.083-8.737-9.04    c-0.023-0.058-0.045-0.115-0.066-0.174c-1.986-5.846-18.812-18.827-28.95-25.636c-0.113-0.075-0.219-0.158-0.32-0.248    c-0.884-0.781-3.919-2.933-6.117-2.632c-0.473,0.07-1.169,0.286-1.899,1.327c-2.771,8.422-11.271,34.452-13.118,42.279    c-0.07,0.297-0.187,0.58-0.343,0.842c-0.129,0.215-3.228,5.27-8.813,5.945c-0.622,0.075-1.24,0.093-1.863,0.059    c0.328,1.192,0.5,2.411,0.455,3.637c5.38,0.235,8.379,3.725,9.354,6.152c0.041,0.102,0.077,0.207,0.106,0.314    c0.737,2.664,1.415,9.103,2.073,19.685c0.094,1.519-0.982,2.86-2.486,3.095C212.675,350.044,204.58,350.674,196.542,350.674z     M152.161,338.019c14.328,4.486,29.23,6.758,44.381,6.758c6.836,0,13.715-0.473,20.488-1.408    c-0.701-10.402-1.302-14.185-1.639-15.557c-0.472-0.918-2.484-3.759-8.466-1.538c-1.484,0.675-3.577,1.407-6.953,2.436    c-1.559,0.476-3.205-0.403-3.679-1.961c-0.087-0.287-0.128-0.576-0.128-0.859c-0.001-1.264,0.818-2.433,2.089-2.82    c2.472-0.752,4.202-1.332,5.419-1.818c0.145-0.092,0.301-0.172,0.465-0.238c0.166-0.067,0.321-0.121,0.478-0.174l0.056-0.02    c0.454-0.215,0.704-0.372,0.831-0.462c0.663-2.343-0.848-5.841-4.187-9.657c-1.049-1.198-0.957-3.012,0.207-4.097    c1.165-1.087,2.981-1.05,4.102,0.078c2.415,2.431,4.629,3.551,6.598,3.33c2.161-0.245,3.73-2.081,4.288-2.833    c2.095-8.482,9.924-32.5,13.247-42.598l1.013-3.07c0.203-0.864,0.439-2.403,0.438-4.038c0-2.257-0.45-4.697-2.059-5.802    c-1.147-0.691-13.599-8.252-18.989-14.265c-0.506-0.564-0.754-1.268-0.753-1.97c0.001-0.807,0.332-1.611,0.98-2.193    c1.214-1.087,3.077-0.986,4.163,0.227c3.784,4.222,12.086,9.671,15.817,12.021c0.198,0.076,0.39,0.174,0.575,0.296    c5.129,3.394,13.25,1.851,14.109,1.674c1.256-0.37,14.649-4.374,27.658-10.722c1.466-0.713,3.228-0.107,3.943,1.357    c0.203,0.418,0.299,0.859,0.299,1.294c-0.001,1.091-0.61,2.137-1.656,2.649c-11.939,5.826-23.856,9.644-27.555,10.773    c-0.647,1.238-1.714,3.677-1.711,6.302c0.001,1.289,0.259,2.622,0.95,3.881c0.13,0.236,0.223,0.481,0.283,0.731    c4.249,2.878,27.521,18.965,30.81,28.261c1.3,3.125,2.915,4.983,4.8,5.521c2.118,0.61,4.296-0.53,5.074-1.002    c4.724-5.853,19.241-12.029,31.098-16.329c11.989-17.485,20.163-37.602,23.769-58.446c-10.526,0.767-18.694,1.322-24.314,1.651    c-1.644,0.091-3.021-1.147-3.116-2.771c-0.003-0.058-0.005-0.114-0.005-0.17c-0.002-1.552,1.208-2.854,2.775-2.946    c5.828-0.341,14.421-0.927,25.566-1.742c0.841-6.433,1.267-12.955,1.267-19.42c0-8.933-0.808-17.901-2.403-26.689    c-10.177-2.567-19.546-4.927-22.219-5.585c-1.742-0.429-3.436-0.093-5.033,0.998c-2.043,1.397-3.082,3.432-3.184,4.21    l-0.219,1.487c-0.001,0.101-0.007,0.199-0.016,0.297c-0.018,0.192-0.055,0.378-0.108,0.556    c-0.859,5.849-3.285,22.542-4.612,33.216c-0.34,2.733-0.585,4.903-0.73,6.453c-0.152,1.62-1.575,2.795-3.211,2.66    c-1.526-0.143-2.67-1.426-2.673-2.927c0-0.093,0.003-0.188,0.013-0.283c0.151-1.609,0.403-3.839,0.751-6.629    c1.375-11.082,3.926-28.572,4.706-33.881c0.003-0.071,0.006-0.144,0.006-0.216c-0.001-2.06-1.361-4.531-3.543-6.389    c-1.943-1.655-3.477-1.954-3.809-1.874c-0.263,0.077-0.527,0.115-0.788,0.119c-9.075,2.355-27.517,7.079-37.864,9.28    c-1.593,0.335-3.159-0.677-3.497-2.27c-0.044-0.206-0.064-0.411-0.066-0.612c-0.001-1.364,0.949-2.59,2.335-2.885    c13.888-2.955,42.65-10.536,42.939-10.612c4.818-1.255,5.309-5.131,5.309-6.661c0-0.199-0.008-0.358-0.017-0.467    c-0.006-0.082-0.009-0.176-0.009-0.267c0-0.056,0.001-0.112,0.005-0.162l0.001-0.031c-0.003-0.099-0.006-0.196-0.006-0.292    c0-0.34,0.023-0.662,0.07-0.974c0.228-4.397,0.798-15.07,1.438-24.658c0.108-1.625,1.538-2.844,3.137-2.745    c1.558,0.104,2.752,1.4,2.752,2.939c0,0.066-0.002,0.132-0.007,0.199c-0.686,10.303-1.294,21.874-1.48,25.487    c0.372,3.295,7.005,10.229,9.064,11.27c2.688,0.664,10.449,2.617,19.254,4.838c-5.76-25.289-18.1-48.647-35.866-67.806    c-18.183-19.608-41.183-33.901-66.644-41.44c-0.329,1.412-0.102,2.825,0.035,3.453c6.088,9.218,16.632,44.582,17.086,46.11    c0.083,0.28,0.123,0.564,0.123,0.842c-0.001,1.271-0.829,2.444-2.11,2.824c-1.559,0.463-3.202-0.426-3.665-1.986    c-3.069-10.331-12.024-38.523-16.55-44.826c-1.575-2.191-5.512-2.339-8.496-1.15c-7.645,9.944-15.821,22.084-16.096,24.949    c0.342,2.45,3.283,4.977,8.093,6.945c0.871,0.357,1.838,0.805,2.875,1.324c8.19,4.098,20.735,12.611,24.763,15.386    c0.051,0.035,0.099,0.069,0.146,0.101c0.061,0.041,0.119,0.082,0.175,0.12c0.061,0.043,0.121,0.083,0.177,0.123    c0.026,0.018,0.052,0.036,0.077,0.054c0.015,0.011,0.03,0.021,0.045,0.031c0.098,0.068,0.187,0.129,0.264,0.183    c0.199,0.138,0.329,0.229,0.381,0.266c0.821,0.574,1.26,1.491,1.259,2.422c0,0.582-0.173,1.17-0.532,1.684    c-0.934,1.335-2.773,1.659-4.105,0.725c-0.192-0.134-19.267-13.448-27.673-16.927c-3.206-1.066-5.662-1.066-7.304,0    c-1.837,1.194-2.414,3.562-2.573,4.47c2.087,10.577-5.077,29.439-5.389,30.251c-0.584,1.519-2.291,2.275-3.808,1.696    c-1.172-0.45-1.893-1.567-1.893-2.752c0-0.351,0.063-0.708,0.196-1.055c1.687-4.402,5.398-15.944,5.396-23.719    c0-1.34-0.111-2.567-0.362-3.627c-0.641-2.723-4.439-4.402-7.761-4.302l-8.687,2.489c-1.564,0.45-3.197-0.457-3.646-2.022    c-0.078-0.272-0.115-0.546-0.115-0.816c0.001-1.281,0.844-2.46,2.137-2.83l8.428-2.415c1.847-1.598,3.218-3.604,3.325-4.911    c0.034-0.419-0.078-0.635-0.269-0.849c-2.703-3-9.36-15.27-19.794-36.478c-24.642,3.945-47.944,14.112-67.676,29.524    c0.173,0.225,0.351,0.449,0.534,0.673c5.575,6.807,18.264,23.325,20.006,25.595c2.141,1.645,10.57,4.473,13.892,3.814    c0.244-0.083,0.477-0.158,0.698-0.223c0.025-0.011,0.054-0.022,0.085-0.036c0.002-0.001,0.005-0.002,0.008-0.003    c0.814-0.34,3.634-1.518,7.497-3.123c0,0,0,0,0.001,0c0.018-0.007,0.037-0.016,0.056-0.023c0.007-0.003,0.013-0.006,0.021-0.009    c0.022-0.009,0.045-0.019,0.067-0.028c0.001,0,0.001,0,0.002-0.001c0.006-0.002,0.013-0.005,0.019-0.007    c0.007-0.003,0.015-0.006,0.023-0.01c0.003-0.001,0.006-0.002,0.009-0.004c0.002-0.001,0.005-0.002,0.007-0.003    c0.052-0.021,0.105-0.043,0.157-0.065c0.005-0.002,0.008-0.003,0.013-0.005c0.045-0.019,0.091-0.038,0.136-0.057    c0.001-0.001,0.003-0.001,0.004-0.002c0.041-0.017,0.082-0.033,0.123-0.051c0.013-0.005,0.026-0.011,0.039-0.016    c0.006-0.002,0.012-0.005,0.018-0.007c0.058-0.024,0.115-0.048,0.173-0.071c0.011-0.005,0.021-0.009,0.032-0.013    c0.025-0.011,0.051-0.021,0.077-0.032c1.502-0.625,3.228,0.089,3.853,1.593c0.154,0.37,0.226,0.754,0.226,1.131    c-0.001,1.155-0.685,2.251-1.818,2.722c-5.004,2.077-8.519,3.547-8.694,3.62c-0.004,0.002-0.007,0.003-0.012,0.005    c-0.338,0.14-0.699,0.257-1.079,0.35c-4.723,1.602-11.643,6.03-12.65,8.186c-0.048,0.103-0.101,0.202-0.159,0.297    c-0.199,0.674-0.446,1.293-0.746,1.842c-0.104,0.185-8.952,16.073-11.934,24.806c-0.047,0.241-0.124,0.48-0.235,0.711    c-0.599,1.883-0.652,2.681-0.652,2.904c0.901,2.289,4.311,4.087,9.396,4.946c0.002,0,0.003,0.001,0.005,0.001    c6.002,1.013,14.765,0.774,19.839,0.51c0.005,0,0.009-0.001,0.014-0.001c0.002,0,0.005-0.001,0.007-0.001    c0.069-0.003,0.136-0.007,0.204-0.01c1.652-0.088,2.885-0.176,3.461-0.221c0.01-0.001,0.019-0.001,0.029-0.002    c0.072-0.006,0.133-0.01,0.182-0.014c0.029-0.003,0.054-0.005,0.075-0.006c0.043-0.004,0.067-0.007,0.073-0.006    c1.622-0.135,3.049,1.067,3.186,2.689c0.007,0.086,0.01,0.171,0.01,0.255c-0.002,1.514-1.162,2.801-2.699,2.931    c-0.638,0.053-15.665,1.297-25.264-0.295c-3.364-0.337-5.763,0.201-7.13,1.604c-1.907,1.954-1.504,5.261-1.5,5.294    c0.185,1.433-0.693,2.8-2.076,3.214c-1.385,0.415-2.862-0.224-3.494-1.522c-1.21-2.484-5.218-3.273-8.398-2.52    c-2.94,2.005-7.08,5.754-13.692,12.389c-1.15,1.152-3.017,1.155-4.169,0.007c-0.579-0.577-0.868-1.335-0.867-2.092    c0.001-0.751,0.287-1.503,0.86-2.077c7.006-7.031,11.297-10.895,14.608-13.14c1.495-1.989,2.427-4.302,2.241-5.624    c-0.059-0.416-0.215-0.602-0.449-0.768c-5.249-3.717-23.51-17.193-27.306-19.996c-3.752-2.438-9.051-2.39-11.805-2.156    c-8.147,18.703-12.276,38.61-12.276,59.195c0,29.467,8.602,57.899,24.884,82.296c8.965,2.675,15.042,4.711,18.074,6.056    c3.19,1.179,5.655,1.255,7.332,0.225c1.854-1.14,2.507-3.5,2.691-4.375c-1.513-9.815,5.404-34.367,7.27-40.721    c0.159-0.542,0.282-0.951,0.359-1.208c0.007-0.024,0.014-0.048,0.022-0.071v-0.001c0.001-0.001,0.001-0.001,0.001-0.002    c0.089-0.272,0.195-0.549,0.316-0.831c0.069-0.268,0.104-0.562,0.104-0.877c-0.001-0.957-0.318-2.112-0.936-3.343    c-1.656-3.298-4.714-5.8-6.131-6.109c-1.254-0.197-13.647-2.15-25.236-4.18c-5.275-0.925-10.388-1.866-14.204-2.664    c-0.003,0-0.005-0.001-0.008-0.001c-0.125-0.026-0.25-0.053-0.373-0.078c-0.107-0.023-0.212-0.045-0.316-0.068    c-0.05-0.01-0.098-0.021-0.146-0.031c-0.089-0.018-0.176-0.038-0.263-0.056c-1.38-0.299-2.323-1.519-2.324-2.875    c0-0.207,0.022-0.418,0.067-0.63c0.345-1.591,1.915-2.606,3.505-2.257c10.991,2.38,36.687,6.46,39.939,6.974    c3.903-0.33,9.284-1.916,9.284-4.067c0-0.341,0.058-0.667,0.164-0.972c-0.085-0.425-0.139-0.853-0.161-1.285l-0.413-5.115    c-0.007-0.079-0.01-0.158-0.01-0.236c-0.002-1.522,1.168-2.816,2.711-2.94c1.615-0.105,3.045,1.079,3.175,2.702l0.42,5.206    c0.075,1.488,1.46,3.92,3.974,5.869c1.832,1.42,3.805,2.175,5.034,1.928c0.013-0.003,0.026-0.006,0.04-0.008    c0.001,0,0.001,0,0.001,0c0.002-0.001,0.004-0.001,0.006-0.001c0.234-0.045,0.467-0.06,0.695-0.048    c1.289,0.035,25.43,0.67,42.944-3.001c1.594-0.333,3.156,0.687,3.49,2.281c0.043,0.205,0.064,0.41,0.063,0.611    c-0.002,1.365-0.956,2.588-2.344,2.879c-17.33,3.632-39.975,3.234-43.853,3.136c-5.09,1.842-11.674,7.765-13.353,11.29    c-0.086,0.259-0.183,0.512-0.293,0.76c-2.566,8.562-8.656,31.479-7.393,39.149c0.017,0.104,0.036,0.204,0.055,0.302    c0.548,2.719,4.255,4.496,7.531,4.552c11.376-3.45,52.639-4.118,54.415-4.146c1.57-0.022,2.968,1.275,2.992,2.903    c0.001,0.016,0.001,0.031,0.001,0.047c-0.001,1.606-1.291,2.92-2.903,2.946c-11.806,0.18-43.833,1.221-52.651,3.847    c-1.932,1.545-3.401,3.536-3.553,4.855c-0.048,0.418,0.056,0.637,0.241,0.857c4.577,5.43,27.485,31.582,27.716,31.846    c1.856,2.081,8.754,3.813,13.511,2.765c1.501-0.329,3.001,0.549,3.45,2.018c0.449,1.469-0.304,3.035-1.731,3.602    C154.75,336.521,153.474,337.185,152.161,338.019z M237.573,259.542c0.882,0,1.747,0.126,2.574,0.332    c-0.012-0.248-0.017-0.495-0.017-0.74c0-1.93,0.36-3.744,0.841-5.302c-1.292,0.045-2.734,0.021-4.24-0.136    c0.268,1.305,0.364,2.616,0.364,3.828c0,0.722-0.033,1.408-0.086,2.036C237.196,259.548,237.385,259.542,237.573,259.542z     M110.058,225.972c1.343,1.429,2.541,3.1,3.424,4.861c0.073,0.144,0.143,0.289,0.211,0.433c1.75-1.795,3.796-3.521,5.917-4.984    c-0.456-0.292-0.909-0.613-1.355-0.959c-0.893-0.692-1.811-1.538-2.661-2.497C114.102,224.288,112.07,225.288,110.058,225.972z     M306.456,159.225c1.034,0.901,2.057,2.008,2.934,3.268c0.778-0.865,1.664-1.655,2.614-2.304c0.614-0.42,1.244-0.78,1.886-1.081    c-1.093-1.138-2.179-2.402-3.152-3.713C309.745,156.858,308.357,158.208,306.456,159.225z M128.779,113.368    c0.092,0.51,0.17,1.027,0.235,1.547c0.477-0.338,0.968-0.668,1.469-0.99C129.905,113.749,129.334,113.563,128.779,113.368z"/></g><g><path fill="#000000" d="M145.906,341.987c-0.33,0-0.663-0.055-0.984-0.169c-1.779-0.631-3.349-1.214-4.8-1.785    c-1.075-0.423-1.804-1.435-1.865-2.589c-0.131-2.483-0.775-4.587-1.734-5.682c-0.066-0.069-0.13-0.139-0.193-0.21    c-1.068-1.198-0.981-3.03,0.195-4.121c1.176-1.093,3.009-1.042,4.125,0.112c0.067,0.07,0.133,0.141,0.197,0.211    c2.026,2.043,8.745,3.68,13.412,2.66c1.5-0.328,3.001,0.549,3.449,2.018c0.449,1.469-0.304,3.035-1.731,3.602    c-2.542,1.01-5.283,2.772-8.148,5.24C147.287,341.74,146.601,341.987,145.906,341.987z"/></g><g><path fill="#000000" d="M141.201,340.237c-0.364,0-0.73-0.067-1.078-0.204c-18.46-7.254-35.164-17.881-49.648-31.588    c-0.577-0.546-0.91-1.302-0.921-2.097c-0.012-0.795,0.298-1.56,0.858-2.123c2.672-2.684,4.549-4.792,5.578-6.266    c0.105-0.15,0.223-0.29,0.355-0.418c7.229-7.081,13.934-4.389,16.467-1.974c0.078,0.075,0.151,0.153,0.221,0.235    c4.483,5.318,26.55,30.516,27.673,31.798c2.704,2.848,3.316,7.19,3.439,9.535c0.052,1.001-0.407,1.96-1.221,2.546    C142.415,340.048,141.811,340.237,141.201,340.237z M96.718,306.195c11.982,10.902,25.48,19.671,40.218,26.131    c-0.163-0.264-0.339-0.494-0.525-0.686c-0.033-0.035-0.066-0.07-0.097-0.106c-0.934-1.066-22.703-25.92-27.635-31.749    c-0.763-0.609-3.662-2.356-8.026,1.793C99.741,302.844,98.442,304.369,96.718,306.195z"/></g><g><path fill="#000000" d="M102.991,160.055c-1.578,0-2.626-0.337-2.748-0.378c-0.271-0.092-0.527-0.222-0.76-0.387    c-5.279-3.738-23.721-17.349-27.37-20.043c-0.001,0-0.001-0.001-0.001-0.001l-1.025-0.757c-6.749-4.788-3.663-15.931-3.529-16.403    c0.043-0.15,0.098-0.297,0.164-0.439l1.085-2.338c1.761-3.815,6.271-13.659,10.103-22.679c0.118-0.279,0.279-0.538,0.476-0.768    C85.252,89.027,91.75,82.671,98.7,76.968c1.024-0.838,2.479-0.894,3.563-0.135c1.154,0.81,2.294,1.815,3.386,2.989    c0.043,0.045,0.083,0.092,0.123,0.14c5.963,7.281,20.063,25.67,20.205,25.855c3.073,4.284,4.526,14.663,1.878,19.511    c-0.105,0.189-9.275,16.653-12.09,25.272c-0.058,0.175-0.131,0.345-0.22,0.507C111.349,158.725,106.126,160.055,102.991,160.055z     M102.43,154.151c1.062,0.154,4.542,0.134,7.821-5.658c3.083-9.214,12.058-25.328,12.442-26.016    c1.378-2.522,0.516-10.401-1.453-13.149c-0.085-0.108-14.032-18.297-19.971-25.556c-0.229-0.244-0.458-0.476-0.689-0.697    c-5.872,4.982-11.388,10.449-16.426,16.282c-3.676,8.631-7.828,17.725-9.997,22.422l0,0l0,0l0,0l-0.986,2.126    c-0.552,2.144-1.344,7.881,1.371,9.808l0.979,0.721c0.162,0.098,0.286,0.176,0.405,0.261c0.136,0.097,0.262,0.203,0.377,0.317    C81.01,138.486,96.968,150.257,102.43,154.151z M71.483,120.544h0.012H71.483z"/></g><g><path fill="#000000" d="M92.502,309.253c-0.728,0-1.457-0.268-2.026-0.807c-8.17-7.732-15.502-16.358-21.791-25.639    c-0.698-1.031-0.674-2.389,0.062-3.395c0.735-1.005,2.022-1.441,3.217-1.085c9.615,2.85,16.068,5.003,19.181,6.398    c4.894,2.193,10.833,6.087,11.334,12.188c0.105,1.286-0.388,2.608-1.651,4.417c-0.001,0.002-0.002,0.003-0.003,0.006    c-1.241,1.777-3.28,4.082-6.234,7.049C94.015,308.962,93.259,309.253,92.502,309.253z M78.638,286.531    c4.223,5.509,8.84,10.735,13.797,15.612c1.029-1.089,1.886-2.054,2.566-2.886c0.132-0.345,0.332-0.67,0.6-0.957    c0.179-0.19,0.357-0.373,0.534-0.55c0.222-0.333,0.351-0.56,0.423-0.701c-0.428-2.383-3.249-4.894-7.823-6.944    C87.317,289.47,84.447,288.375,78.638,286.531z M96.641,296.862L96.641,296.862L96.641,296.862z"/></g><g><path fill="#000000" d="M66.949,126.167c-1.201,0-2.465-0.253-3.766-0.768c-0.791-0.313-1.407-0.951-1.693-1.751    c-0.285-0.8-0.211-1.685,0.203-2.427c5.027-9.006,10.979-17.538,17.694-25.359c0.947-1.103,2.556-1.351,3.791-0.584    c1.235,0.767,1.727,2.319,1.158,3.657c-3.719,8.755-7.972,18.071-10.176,22.845c-0.066,0.141-0.142,0.277-0.229,0.406    C72.175,124.805,69.733,126.167,66.949,126.167z M71.483,120.544h0.012H71.483z"/></g><g><path fill="#000000" d="M98.407,302.595c-0.675,0-1.353-0.231-1.905-0.698c-1.161-0.983-1.383-2.69-0.512-3.937    c0.313-0.449,0.481-0.743,0.567-0.912c-0.428-2.383-3.249-4.894-7.823-6.944c-1.453-0.651-2.126-2.338-1.521-3.81    c0.605-1.473,2.269-2.197,3.76-1.641c3.211,1.203,5.697,1.295,7.386,0.272c2.337-1.412,2.779-4.714,2.783-4.748    c0.181-1.433,1.375-2.535,2.818-2.588c1.435-0.084,2.714,0.936,2.999,2.351c0.57,2.828,4.553,4.604,7.922,4.551    c1.268-0.063,2.44,0.769,2.86,1.983c0.42,1.213,0.006,2.559-1.023,3.327c-2.114,1.578-3.766,3.741-3.927,5.144    c-0.048,0.418,0.056,0.637,0.241,0.857c1.009,1.196,0.907,2.973-0.233,4.047c-1.141,1.073-2.921,1.066-4.053-0.012    c-0.525-0.461-3.568-2.697-8.276,1.916C99.899,302.313,99.154,302.595,98.407,302.595z M99.805,290.762    c0.792,0.941,1.453,1.971,1.924,3.095c1.968-0.801,3.809-0.986,5.437-0.805c0.36-1.354,1.027-2.6,1.798-3.692    c-1.573-0.584-3.052-1.413-4.3-2.48c-0.782,1.124-1.819,2.208-3.19,3.052C100.941,290.259,100.384,290.536,99.805,290.762z"/></g><g><path fill="#000000" d="M73.86,139.822c-0.559,0-1.122-0.158-1.621-0.488c-4.747-3.132-12.028-2.23-13.44-2.015    c-1.065,0.159-2.136-0.273-2.788-1.133c-0.652-0.859-0.783-2.006-0.341-2.99c1.802-4.013,3.828-8.042,6.023-11.976    c0.721-1.289,2.285-1.85,3.658-1.304c1.985,0.783,2.854,0.222,3.684-1.015c0.856-1.278,2.551-1.681,3.892-0.928    c1.341,0.753,1.878,2.411,1.233,3.806l-0.986,2.126c-0.552,2.144-1.344,7.881,1.371,9.808l1.067,0.787    c1.286,0.949,1.582,2.749,0.667,4.06C75.707,139.38,74.791,139.822,73.86,139.822z M63.149,131.139    c1.188,0.013,2.489,0.09,3.836,0.276c-0.313-1.843-0.288-3.69-0.144-5.249c-0.359-0.004-0.723-0.031-1.092-0.081    C64.848,127.758,63.979,129.447,63.149,131.139z"/></g></g><g><g><path fill="#000000" d="M178.137,349.605c-0.115,0-0.231-0.007-0.347-0.021c-6.403-0.758-12.811-1.928-19.046-3.48    c-0.8-0.199-1.481-0.724-1.876-1.449c-0.395-0.724-0.469-1.582-0.202-2.363c8.128-23.852,26.488-82.277,21.432-109.571    c-3.316-17.896-10.595-26.575-24.342-29.023c-25.24-4.49-72.967-1.112-108.561,2.515c-0.804,0.088-1.612-0.172-2.225-0.701    c-0.614-0.53-0.982-1.29-1.019-2.099c-0.113-2.507-0.168-4.823-0.168-7.081c0-3.018,0.094-6.066,0.288-9.318    c0.095-1.597,1.465-2.815,3.042-2.771c33.072,1.12,77.091,0.701,98.977-7.665c15.909-6.081,22.246-16.643,20.545-34.244    c-2.038-21.083-19.991-56.198-34.693-81.943c-0.409-0.716-0.499-1.572-0.249-2.358c0.251-0.786,0.819-1.431,1.568-1.779    c4.618-2.142,9.391-4.073,14.19-5.74c1.477-0.514,3.102,0.218,3.696,1.668c12.083,29.536,29.361,68.115,43.059,84.174    c10.448,12.25,23.051,14.78,37.454,7.514c22.046-11.117,50.112-37.793,69.774-58.214c0.544-0.565,1.29-0.89,2.074-0.903    c0.781-0.031,1.541,0.286,2.103,0.832c3.874,3.76,7.578,7.751,11.012,11.863c0.508,0.608,0.75,1.395,0.67,2.183    c-0.078,0.788-0.471,1.511-1.088,2.007c-20.631,16.548-48.02,40.123-61.338,58.462c-9.384,12.92-7.37,25.776,6.158,39.302    c10.815,10.813,39.999,25.309,82.173,40.817c1.486,0.547,2.274,2.168,1.787,3.675c-1.81,5.593-3.951,11.11-6.361,16.397    c-0.339,0.745-0.973,1.315-1.749,1.573c-0.776,0.258-1.626,0.185-2.343-0.207c-24.665-13.447-69.67-36.711-93.969-41.45l0,0    c-14.125-2.753-24.453,2.499-33.474,17.044c-14.739,23.762-21.706,84.617-24.017,109.677c-0.073,0.792-0.462,1.52-1.08,2.021    C179.464,349.376,178.808,349.605,178.137,349.605z M163.285,341.123c4.024,0.907,8.108,1.651,12.203,2.221    c2.613-27.066,9.772-85.311,24.59-109.2c10.219-16.478,23.174-22.926,39.613-19.725c23.097,4.505,62.67,24.187,92.849,40.532    c1.43-3.329,2.748-6.739,3.938-10.19c-29.981-11.139-67.429-26.999-81.623-41.19c-15.483-15.479-17.82-31.708-6.76-46.935    c13.191-18.164,39.453-41.044,60.053-57.691c-2.098-2.41-4.288-4.768-6.547-7.049c-19.849,20.439-47.28,46.142-69.288,57.241    c-16.912,8.526-32.33,5.428-44.595-8.952c-13.796-16.174-30.774-53.595-42.959-83.16c-2.725,1.006-5.433,2.097-8.098,3.263    c14.662,25.951,31.778,60.139,33.84,81.48c1.946,20.134-6.005,33.322-24.308,40.318c-22.159,8.47-65.132,9.154-98.385,8.144    c-0.088,2.087-0.131,4.104-0.131,6.104c0,1.207,0.016,2.433,0.05,3.7c35.594-3.538,81.892-6.617,107.06-2.137    c20.946,3.728,26.558,19.996,29.106,33.753C188.993,259.166,171.977,315.197,163.285,341.123z"/></g></g></svg>
                        			</i>
                        			
                        			<span class="pier-li-left-padded-content" ng-click="sc.uiState.changeContext(c)" title="{{c.description}}">{{c.icsRequest}}</span>
                        		</a>
                            </li>
                        </ul>
                    </div>
					
					<input type="text" name="query" class="form-control" placeholder="Search words and/or phrases anywhere in the note text" 
						style="border-right:none;-webkit-box-shadow: none !important;-moz-box-shadow: none !important;box-shadow: none !important;"
						ng-model-options="{
    						'updateOn': 'default blur',
    						'debounce': {
      							'default': 250,
      							'blur': 0
    						}
    					}" />
					
					<div class="input-group-btn" class="input-group-addon" style="width:1%;">
						
						<button type="button" class="btn btn-default recent-queries" data-toggle="dropdown" style="border-left:none" title="query history">
							<i class="fa fa-caret-down recent-queries" aria-hidden="true"></i>
						</button>
						<ul class="dropdown-menu pull-right">
							<li role="presentation" class="dropdown-header">Query History</li>
							<li>
				          		<a href ng-click="">query n</a>
							</li>
							<li>
				          		<a href ng-click="">query n-1</a>
							</li>
							<li>
				          		<a href ng-click="">query n-2</a>
							</li>
						</ul>
						<button class="btn btn-default" type="submit">
							<i class="fa fa-search" aria-hidden="true"></i>
						</button>
					</div>
				</div>
			</div>
		</form>
       
      <!-- 
      <form class="navbar-form navbar-left">
        <div class="form-group">
          <input type="text" class="form-control" placeholder="Search">
        </div>
        <button type="submit" class="btn btn-default">Submit</button>
      </form>
       --> 
      
      
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>