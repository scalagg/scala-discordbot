package gg.scala.discord.verification

import com.lamergameryt.lamercaptcha.Captcha
import com.lamergameryt.lamercaptcha.backgrounds.GradiatedBackgroundProducer
import com.lamergameryt.lamercaptcha.text.producer.TextProducer
import gg.scala.discord.embed
import gg.scala.discord.role
import gg.scala.discord.ticketing.Tickets
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Service
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.utils.FileUpload
import java.awt.Color
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/**
 * @author GrowlyX
 * @since 2/6/2022
 */
@Service
object ScalaDiscordVerificationService
{
    @Inject
    lateinit var jda: JDA

    val registry = mutableMapOf<String, Captcha>()

    fun startVerification(
        member: Member, event: ButtonInteractionEvent
    )
    {
        if (
            member.roles.contains(
                event.guild!!.role("Default")
            )
        )
        {
            event.reply("You're already verified in our discord!")
                .setEphemeral(true).queue()
            return
        }

        if (registry[member.id] != null)
        {
            event.reply("We already sent you a message! If you did not receive it, please rejoin the discord server.")
                .setEphemeral(true)
                .queue()
            return
        }

        val privateChannel = try
        {
            member.user.openPrivateChannel()
                .complete()
        } catch (ignored: Exception)
        {
            event.reply("We were unable to send you a private message! Are your DMs open?")
                .setEphemeral(true)
                .queue()
            return
        }

        val embed = embed(
            "It's me again! \uD83D\uDC4B",
            "Please respond with the following code to verify yourself as a",
            "human and gain access to the Discord server."
        )
        try
        {
            privateChannel.sendMessageEmbeds(embed.build())
                .queue {
                    val captcha = captcha()
                    registry[member.id] = captcha

                    val stream = ByteArrayOutputStream()

                    ImageIO.write(
                        captcha.image, "png", stream
                    )

                    privateChannel
                        .sendFiles(
                            FileUpload
                                .fromData(
                                    stream.toByteArray(),
                                    "captcha.png"
                                )
                        )
                        .queue()
                }
        } catch (ignored: Exception)
        {
            event.reply("We were unable to send you a private message! Are you allowing private messages?")
                .setEphemeral(true).queue()
            return
        }

        event.reply("We sent you a message, check your private messages!")
            .setEphemeral(true).queue()
    }

    private val textProducer = TextProducer {
        Tickets.generateRandomString()
    }

    private val gradient = GradiatedBackgroundProducer(
        Color.decode("#27aae1"), Color.decode("#014bb0")
    )

    private fun captcha(): Captcha
    {
        return Captcha.Builder(300, 70)
            .addBackground(gradient)
            .addText(textProducer)
            .addNoise().addNoise().build()
    }
}
