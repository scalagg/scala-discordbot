package gg.scala.discord.ticketing.response.extended.models

import java.util.concurrent.CompletableFuture

interface Savable {

    fun save(): CompletableFuture<Void>

}
