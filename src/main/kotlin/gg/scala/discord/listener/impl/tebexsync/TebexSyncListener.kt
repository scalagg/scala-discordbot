package gg.scala.discord.listener.impl.tebexsync

import gg.scala.discord.ScalaDiscordApp
import gg.scala.discord.listener.modal.ButtonActivatedModal
import kong.unirest.Unirest
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal
import net.evilblock.cubed.serializers.Serializers

/**
 * @author GrowlyX
 * @since 2/6/2022
 */
object TebexSyncListener : ButtonActivatedModal(id = "tebexsync")
{
    override fun modal() = Modal
        .create(
            "tebexsync", "Claim Early Access"
        )
        .addComponents(
            ActionRow.of(
                TextInput
                    .create(
                        "transactionid",
                        "Transaction ID",
                        TextInputStyle.SHORT
                    )
                    .setPlaceholder("Your Tebex transaction ID.")
                    .build()
            )
        )
        .build()

    override fun onSubmit(event: ModalInteractionEvent)
    {
        event.deferReply(true)
            .queue {
                val betaWhitelist = event.guild!!
                    .getRolesByName("Early Access", true)
                    .first()

                data class Package(val id: Int, val name: String)

                data class Response(
                    val status: String,
                    val packages: List<Package>
                )

                val response = Serializers
                    .gson
                    .fromJson(
                        Unirest.get("https://plugin.tebex.io/payments/${event.getValue("transactionid")!!.asString}")
                            .header("accept", "application/json")
                            .header("X-Tebex-Secret", ScalaDiscordApp.settings.tebexApiKey)
                            .asString()
                            .body,
                        Response::class.java
                    )

                if (response.status != "Complete")
                {
                    event.hook
                        .sendMessage(
                            "We have received your purchase, however, it is still being processed. Please try again later!"
                        )
                        .queue()
                    return@queue
                }

                if (response.packages.none { pkg -> (pkg.name == "Basic Bundle" || pkg.name == "Premium Bundle" || pkg.name == "Buddy Bundle") })
                {
                    event.hook
                        .sendMessage(
                            "Sorry, but your purchase does not include an invitation to the Early Access program."
                        )
                        .queue()
                    return@queue
                }

                event.hook
                    .sendMessage(
                        "**Your purchase contains an Early Access program invitation.** You should be able to log on the server using the address `scala.gg`. You will be receiving the `Early Access` role in our Discord server shortly."
                    )
                    .queue()

                event.guild!!
                    .addRoleToMember(
                        event.user,
                        betaWhitelist
                    )
                    .queue()
            }
    }
}
