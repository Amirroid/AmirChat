package ir.amirroid.amirchat.data.events

sealed class MessageEvents{
    class SeekExo(val position:Long) : MessageEvents()
    class Reply(val id:String) : MessageEvents()
}
