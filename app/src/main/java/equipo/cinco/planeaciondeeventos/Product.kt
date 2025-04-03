package equipo.cinco.planeaciondeeventos

class Product (var id: String? = null,
               var nombre: String?=null,
               var lugar: String?=null,
               var precio: Float?=null,
               var seleccionado: Boolean = false
){
    constructor() : this(null,null, null,null,false)
}