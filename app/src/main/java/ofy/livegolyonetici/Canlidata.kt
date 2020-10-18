package ofy.livegolyonetici

class Canlidata{
    var Kuponismi:String?=null
    var Kupondurumu:String?=null
    var maclar:ArrayList<macdata>?=null
    var Kupondali:String?=null
    constructor(Kuponismi:String, Kupondurumu:String,maclar:ArrayList<macdata>,Kupondali:String){
        this.Kuponismi=Kuponismi
        this.Kupondurumu=Kupondurumu
        this.maclar=maclar
        this.Kupondali=Kupondali
    }
    constructor(){}
}