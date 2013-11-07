package mongograilsquickstart


class Car {
		String name
		String brand
		int ps
		Date buildDate
		Date dateCreated
		Date lastUpdated
    static constraints = {
		brand nullable:true
		ps min: 30,max: 1001
    }
}
