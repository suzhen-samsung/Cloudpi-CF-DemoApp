import org.codehaus.groovy.grails.commons.*
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import com.samsung.cloudpi.service.CloudpiService
import com.samsung.cloudpi.service.MongoDBService
class BootStrap {
def config = ConfigurationHolder.config

    def init = { servletContext ->
    ConfigurationHolder.flatConfig.each{
		key,value -> println(key+value)
		}
		}
 //println(ApplicationAttributes.APPLICATION_CONTEXT)
		//println(ServletContextHolder.servletContext.getAttribute(ApplicationAttributes.APPLICATION_CONTEXT))
		//println(ServletContextHolder.servletContext.getAttribute(ApplicationAttributes.APPLICATION_CONTEXT).mongodb)
   
    def destroy = {
			println "##### stop"
    }
}
