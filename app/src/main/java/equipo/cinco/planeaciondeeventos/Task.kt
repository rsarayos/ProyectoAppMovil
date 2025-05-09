package equipo.cinco.planeaciondeeventos

class Task (var id: String? = null,
            var name: String?=null,
            var desc: String?=null,
            var estimated_cost: Float?=null,
            var subtareas: Any? = null,
            var estado: Boolean = false){
    constructor() : this(null,null, null,null,null,false)
}