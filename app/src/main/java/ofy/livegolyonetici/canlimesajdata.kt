package ofy.livegol.datas

class canlimesajdata{
    var res_mesaj:String?=null
    var reslow_mesaj:String?=null
    var nick_mesaj:String?=null
    var mesaj_mesaj:String?=null
    var engellimi_mesaj:Boolean?=null
    var hesaptipi:Int?=null
    constructor(res_mesaj:String,reslow_mesaj:String,nick_mesaj:String,mesaj_mesaj:String,engellimi_mesaj:Boolean){
       this.mesaj_mesaj=mesaj_mesaj
        this.res_mesaj=res_mesaj
        this.reslow_mesaj=reslow_mesaj
        this.nick_mesaj=nick_mesaj
        this.engellimi_mesaj=engellimi_mesaj
    }
    constructor(res_mesaj:String,reslow_mesaj:String,nick_mesaj:String,mesaj_mesaj:String,engellimi_mesaj:Boolean,hesaptipi:Int){
        this.mesaj_mesaj=mesaj_mesaj
        this.res_mesaj=res_mesaj
        this.reslow_mesaj=reslow_mesaj
        this.nick_mesaj=nick_mesaj
        this.engellimi_mesaj=engellimi_mesaj
        this.hesaptipi=hesaptipi
    }
    constructor(){}
}