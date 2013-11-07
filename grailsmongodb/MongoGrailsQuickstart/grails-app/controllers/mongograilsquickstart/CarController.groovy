package mongograilsquickstart

import org.springframework.dao.DataIntegrityViolationException

class CarController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [carInstanceList: Car.list(params), carInstanceTotal: Car.count()]
    }

    def create() {
        [carInstance: new Car(params)]
    }

    def save() {
        def carInstance = new Car(params)
        if (!carInstance.save(flush: true)) {
            render(view: "create", model: [carInstance: carInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'car.label', default: 'Car'), carInstance.id])
        redirect(action: "show", id: carInstance.id)
    }

    def show(Long id) {
        def carInstance = Car.get(id)
        if (!carInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'car.label', default: 'Car'), id])
            redirect(action: "list")
            return
        }

        [carInstance: carInstance]
    }

    def edit(Long id) {
        def carInstance = Car.get(id)
        if (!carInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'car.label', default: 'Car'), id])
            redirect(action: "list")
            return
        }

        [carInstance: carInstance]
    }

    def update(Long id, Long version) {
        def carInstance = Car.get(id)
        if (!carInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'car.label', default: 'Car'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (carInstance.version > version) {
                carInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'car.label', default: 'Car')] as Object[],
                          "Another user has updated this Car while you were editing")
                render(view: "edit", model: [carInstance: carInstance])
                return
            }
        }

        carInstance.properties = params

        if (!carInstance.save(flush: true)) {
            render(view: "edit", model: [carInstance: carInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'car.label', default: 'Car'), carInstance.id])
        redirect(action: "show", id: carInstance.id)
    }

    def delete(Long id) {
        def carInstance = Car.get(id)
        if (!carInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'car.label', default: 'Car'), id])
            redirect(action: "list")
            return
        }

        try {
            carInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'car.label', default: 'Car'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'car.label', default: 'Car'), id])
            redirect(action: "show", id: id)
        }
    }
}
