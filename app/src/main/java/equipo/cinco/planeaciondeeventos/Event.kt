package equipo.cinco.planeaciondeeventos

class Event (var name: String?=null,
             var desc: String?=null,
             var estimated_cost: Float?=null,
             var id: String? = null,
             var tasks: Any? = null,
             var estado: Boolean?= null){
    constructor() : this(null, null,null,null,null,null)
}