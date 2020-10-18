package ofy.livegolyonetici

class Kupondata{
    var Kuponismi:String?=null
    var Kupondurumu:String?=null
    var maclar:ArrayList<macdata>?=null
    constructor(Kuponismi:String, Kupondurumu:String,maclar:ArrayList<macdata>){
        this.Kuponismi=Kuponismi
        this.Kupondurumu=Kupondurumu
        this.maclar=maclar
    }
    constructor(){}
}