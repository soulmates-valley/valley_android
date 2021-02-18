package com.soulmates.valley.data.model.post

data class Language(
    val langIdx: String,
    val langName: String
)

object LanguageList{
    val languageMap = mapOf(
        1 to Language("1", "C"),
        2 to Language("2", "C++"),
        3 to Language("3", "C#"),
        4 to Language("4", "HTML"),
        5 to Language("5", "Java"),
        6 to Language("6", "JavaScript"),
        7 to Language("7", "Kotlin"),
        8 to Language("8", "Python"),
        9 to Language("9", "XML"),
        10 to Language("10", "Markdown"),
        11 to Language("11", "Shell"),
        12 to Language("12", "SQL"),
        13 to Language("13", "Visual Basic"),
        14 to Language("14", "기타")
    )
}