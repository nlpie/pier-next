<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="pier" />
		<title><g:meta name="admin.title"/></title>
	</head>
		<body>
		
			<div style="text-align:center">
				<div style="margin:100px"></div>
				<div style="margin: 0 auto;width:20%">
					<g:if test="${flash.message}">
						<div style="color:red">
						    ${flash.message}
						</div>
					</g:if>
					<form action='${request.contextPath}/j_spring_security_check' method='POST' id='loginForm' autocomplete='off'>
						<div class="form-group">
						  
						  <input type="text" name='j_username' class="form-control" id="username" placeholder="${grailsApplication.config.username.placeholder}">
						</div>
						<div class="form-group">
						  
						  <input type='password' name='j_password' id='password' class="form-control" placeholder="${grailsApplication.config.password.placeholder}">
						</div>
						<button type="submit" class="btn btn-primary">Login</button>
					</form>
		
		<!-- 
				<form action='/pier-next/j_spring_security_check' method='POST' id='loginForm' autocomplete='off'>
					<p>
						<label for='username'>Username:</label>
						<input type='text' name='j_username' id='username'/>
					</p>
		
					<p>
						<label for='password'>Password:</label>
						<input type='password' name='j_password' id='password'/>
					</p>
		
					<p>
						<input type='submit' id="submit" value='Login'/>
					</p>
				</form>
				
		 -->		
			</div>
		</div>
	</body>

</html>