package gg.scala.discord.listener.modal

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.modals.Modal

/**
 * @author GrowlyX
 * @since 1/11/2024
 */
abstract class ButtonActivatedModal(private val id: String) : ListenerAdapter()
{
    abstract fun modal(): Modal
    abstract fun onSubmit(event: ModalInteractionEvent)

    override fun onModalInteraction(event: ModalInteractionEvent)
    {
        if (event.modalId == id)
        {
            onSubmit(event)
        }
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent)
    {
        if (event.button.id == id)
        {
            event.replyModal(modal()).queue()
        }
    }
}
