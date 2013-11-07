class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{ constraints {
				// apply constraints here
			} }

		"/" {
			controller = "hello"
			action = "index"
		}
		"/set" {
			controller = "hello"
			action = "set"
		}
		"/get" {
			controller = "hello"
			action = "get"
		}
		"/doGet" {
			controller = "hello"
			action = "doGet"
		}
		"/env" {
			controller = "hello"
			action = "env"
		}
	}
}
