package gg.scala.discord.language.impl

import gg.scala.discord.language.Language

object English : Language
{
    override val locale = mutableMapOf<String, String>()

    override fun configure()
    {
        locale["invalid"] = "That's an invalid response, please try again!"
        // default
        locale["username"] = "What is your in-game username?"
        locale["category"] = "What is this ticket categorized under?\n`%s`"
        locale["reason"] = "Why did you create this ticket?"
        // extended stuff
        locale["punishment"] = "Please enter your punishment ID."
        locale["transaction"] = "Please enter your transaction ID."
    }

    override fun getId() = "English"
}

object Spanish : Language
{
    override val locale = mutableMapOf<String, String>()

    override fun configure()
    {
        locale["invalid-response"] = "Esa no es una respuesta válida, aquí hay algunas a continuación."
        locale["ign"] = "¿Cuál es tu nombre de usuario en el juego?"
        locale["why"] = "¿En qué categoría entraría este boleto?\n`%s`"
        locale["reason"] = "¿Por qué creaste este ticket de soporte?"
        locale["punish"] = "Proporcione su identificación de castigo (N/A si no está relacionado con el castigo)."
        locale["transact"] = "Proporcione su ID de transacción (N/A si no está relacionado con la tienda)."
    }

    override fun getId() = "Español"
}

object Italian : Language
{
    override val locale = mutableMapOf<String, String>()

    override fun configure()
    {
        locale["invalid-response"] = "Non è una risposta valida, eccone alcune qui sotto"
        locale["ign"] = "Qual è il tuo nome utente nel gioco?"
        locale["why"] = "In quale categoria rientrerebbe questo biglietto?\n`%s`"
        locale["reason"] = "Perché hai creato questo ticket di supporto?"
        locale["punish"] = "Fornisci il tuo ID di punizione (N/A se non è correlato alla punizione)."
        locale["transact"] = "Fornisci il tuo ID transazione (N/A se non è correlato al negozio)."
    }

    override fun getId() = "Italiano"
}

object French : Language
{
    override val locale = mutableMapOf<String, String>()

    override fun configure()
    {
        locale["invalid-response"] = "Ce n'est pas une réponse valide, en voici quelques-unes ci-dessous"
        locale["ign"] = "Quel est votre nom d'utilisateur dans le jeu?"
        locale["why"] = "Dans quelle catégorie appartiendrait ce billet?\n`%s`"
        locale["reason"] = "Pourquoi avez-vous créé ce ticket de support?"
        locale["punish"] = "Fournissez votre identifiant de punition (N/A s'il n'est pas lié à la punition)."
        locale["transact"] = "Fournissez votre identifiant de transaction (N/A si ce n'est pas lié au magasin)."
    }

    override fun getId() = "Italiano"
}
