package ofy.livegolyonetici.services


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

class soccerlivemodel {

    @SerializedName("league_id")
    var league_id : Int?=null
    @SerializedName("name")
    var name : String?=null
    @SerializedName("type")
    var type : String?=null
    @SerializedName("country")
    var country : String?=null
    @SerializedName("country_code")
    var country_code : String?=null
    @SerializedName("season")
    var season : Int?=null
    @SerializedName("season_start")
    var season_start : Date?=null
    @SerializedName("season_end")
    var season_end : Date?=null
    @SerializedName("logo")
    var logo : String?=null
    @SerializedName("flag")
    var flag : String?=null
    @SerializedName("standings")
    var standings : Int?=null
    @SerializedName("is_current")
    var is_current : Int?=null

}
class  sovcerlige{
    @Expose
    @SerializedName("leagues")
    var leagues: ArrayList<soccerlivemodel>? = null
    @Expose
    @SerializedName("results")
    var results: Int? = null
}
class apisoccer{
    @Expose
    @SerializedName("api")
    var api:sovcerlige? = null
}
