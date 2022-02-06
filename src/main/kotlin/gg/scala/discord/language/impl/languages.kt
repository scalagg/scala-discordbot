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
        locale["invalid"] = "Esa es una respuesta inválida, ¡inténtelo de nuevo!"
        // default
        locale["username"] = "¿Cuál es tu nombre de usuario en el juego?"
        locale["category"] = "¿En qué categoría está clasificado este ticket?\n`%s`"
        locale["reason"] = "¿Por qué creó este ticket?"
        // extended stuff
        locale["punishment"] = "Ingrese su ID de castigo"
        locale["transaction"] = "Ingrese su ID de transacción"
    }

    override fun getId() = "Español"
}

object Italian : Language
{
    override val locale = mutableMapOf<String, String>()

    override fun configure()
    {
        locale["invalid"] = "Questa è una risposta non valida, per favore riprova!"
        // default
        locale["username"] = "Qual è il tuo nome utente di gioco?"
        locale["category"] = "In cosa è classificato questo ticket?\n`%s`"
        locale["reason"] = "Perché hai creato questo ticket?"
        // extended stuff
        locale["punishment"] = "Inserisci il tuo ID punizione."
        locale["transaction"] = "Inserisci il tuo ID transazione."
    }

    override fun getId() = "Italiano"
}

object French : Language
{
    override val locale = mutableMapOf<String, String>()

    override fun configure()
    {
        locale["invalid"] = "C'est une réponse invalide, veuillez réessayer !"
        // default
        locale["username"] = "Quel est votre nom d'utilisateur dans le jeu?"
        locale["category"] = "Dans quelle catégorie ce ticket est-il classé?\n`%s`"
        locale["reason"] = "Pourquoi avez-vous créé ce ticket?"
        // extended stuff
        locale["punishment"] = "Veuillez entrer votre identifiant de punition."
        locale["transaction"] = "Veuillez entrer votre ID de transaction."
    }

    override fun getId() = "Français"
}

object Dutch : Language
{
    override val locale = mutableMapOf<String, String>()

    override fun configure()
    {
        locale["invalid"] = "Dat is een ongeldig antwoord, probeer het opnieuw!"
        // default
        locale["username"] = "Wat is je in-game gebruikersnaam?"
        locale["category"] = "Waaronder valt dit ticket?\n`%s`"
        locale["reason"] = "Waarom heb je dit ticket gemaakt?"
        // extended stuff
        locale["punishment"] = "Voer uw straf-ID in."
        locale["transaction"] = "Voer uw transactie-ID in."
    }

    override fun getId() = "Nederlands"
}
