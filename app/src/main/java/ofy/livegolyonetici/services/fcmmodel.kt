package ofy.livegolyonetici.services


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class fcmmodel {
    @Expose
    @SerializedName("data")
    var data: Data? = null
    @Expose
    @SerializedName("to")
    var to: String? = null
    constructor(data:Data,to:String){
        this.data=data
        this.to=to
    }
    class Data {
        @Expose
        @SerializedName("saat")
        var saat: String? = null
        @Expose
        @SerializedName("mesaj")
        var mesaj: String? = null
        @Expose
        @SerializedName("baslik")
        var baslik: String? = null
        @Expose
        @SerializedName("katagori")
        var katagori: String? = null
        constructor(saat:String){
            this.saat=saat
        }
        constructor(mesaj:String,baslik:String){
            this.baslik=baslik
            this.mesaj=mesaj
        }
        constructor(mesaj:String,baslik:String,katagori:String){
            this.katagori=katagori
            this.baslik=baslik
            this.mesaj=mesaj
        }
    }
}